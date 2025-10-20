import cv2
import numpy as np

# Tuning constants
HSV_GREEN_LO = np.array([45, 120, 80])
HSV_GREEN_HI = np.array([85, 255, 255])

HSV_PURPLE_LO = np.array([125, 100, 80])
HSV_PURPLE_HI = np.array([160, 255, 255])

MIN_AREA = 300  # adjust after seeing image size
MAX_AREA = 20000  # adjust if very close

def _largest_valid_contour(mask, min_area=MIN_AREA, max_area=MAX_AREA):
    cnts, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    best = None
    best_area = 0
    for c in cnts:
        area = cv2.contourArea(c)
        if area < min_area or area > max_area:
            continue
        # optional: approximate shape and check circularity
        (x, y), radius = cv2.minEnclosingCircle(c)
        circularity = area / (np.pi * radius * radius)
        if circularity < 0.6:
            continue
        if area > best_area:
            best_area = area
            best = c
    return best, best_area

def runPipeline(image, llrobot):
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    hsv = cv2.GaussianBlur(hsv, (5,5), 0)

    mask_g = cv2.inRange(hsv, HSV_GREEN_LO, HSV_GREEN_HI)
    mask_p = cv2.inRange(hsv, HSV_PURPLE_LO, HSV_PURPLE_HI)

    gCnt, gArea = _largest_valid_contour(mask_g)
    pCnt, pArea = _largest_valid_contour(mask_p)

    # choose best detection
    if gCnt is not None and (pCnt is None or gArea > pArea):
        colour = 1
        cnt = gCnt
        area = gArea
    elif pCnt is not None:
        colour = 2
        cnt = pCnt
        area = pArea
    else:
        colour = 0
        cnt = None
        area = 0

    cx = cy = 0
    if cnt is not None:
        M = cv2.moments(cnt)
        if M["m00"] != 0:
            cx = int(M["m10"]/M["m00"])
            cy = int(M["m01"]/M["m00"])
        (xc, yc), radius = cv2.minEnclosingCircle(cnt)
        # draw
        colourBGR = (0,255,0) if colour==1 else (180,0,180)
        cv2.circle(image, (int(xc),int(yc)), int(radius), colourBGR, 2)
        cv2.circle(image, (cx, cy), 4, colourBGR, -1)
        cv2.putText(image, f"{'G' if colour==1 else 'P'}:{int(area)}", (cx+10, cy),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, colourBGR, 2)

    llpython = [1 if colour>0 else 0, colour, float(cx), float(cy), float(area)]
    return np.array([[]]), image, llpython
