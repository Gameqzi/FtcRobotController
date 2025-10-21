import cv2
import numpy as np
from networktables import NetworkTablesInstance

class ColorDetector:
    def __init__(self):
        # Initialize NetworkTables for Limelight communication
        self.nt_inst = NetworkTablesInstance.getDefault()
        self.nt_inst.startClientTeam(0000)  # Replace with your team number
        self.limelight_table = self.nt_inst.getTable("limelight")
        
        # Purple HSV range (adjust these values based on your lighting conditions)
        self.purple_lower = np.array([125, 50, 50])
        self.purple_upper = np.array([155, 255, 255])
        
        # Green HSV range (adjust these values based on your lighting conditions)
        self.green_lower = np.array([40, 50, 50])
        self.green_upper = np.array([80, 255, 255])
        
        # Minimum contour area to filter noise
        self.min_area = 100

    def detect_colors(self, frame):
        """
        Detect purple and green objects in the frame
        Returns: processed frame, purple contours, green contours
        """
        # Convert BGR to HSV
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        
        # Create masks for purple and green
        purple_mask = cv2.inRange(hsv, self.purple_lower, self.purple_upper)
        green_mask = cv2.inRange(hsv, self.green_lower, self.green_upper)
        
        # Apply morphological operations to reduce noise
        kernel = np.ones((5, 5), np.uint8)
        purple_mask = cv2.morphologyEx(purple_mask, cv2.MORPH_OPEN, kernel)
        purple_mask = cv2.morphologyEx(purple_mask, cv2.MORPH_CLOSE, kernel)
        green_mask = cv2.morphologyEx(green_mask, cv2.MORPH_OPEN, kernel)
        green_mask = cv2.morphologyEx(green_mask, cv2.MORPH_CLOSE, kernel)
        
        # Find contours
        purple_contours, _ = cv2.findContours(purple_mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        green_contours, _ = cv2.findContours(green_mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # Filter contours by area
        purple_contours = [c for c in purple_contours if cv2.contourArea(c) > self.min_area]
        green_contours = [c for c in green_contours if cv2.contourArea(c) > self.min_area]
        
        return frame, purple_contours, green_contours, purple_mask, green_mask

    def draw_detections(self, frame, purple_contours, green_contours):
        """
        Draw bounding boxes and information on detected objects
        """
        frame_height, frame_width = frame.shape[:2]
        center_x = frame_width / 2
        
        # Process purple objects
        for contour in purple_contours:
            # Get bounding rectangle
            x, y, w, h = cv2.boundingRect(contour)
            
            # Calculate center
            cx = x + w // 2
            cy = y + h // 2
            
            # Calculate angle offset from center (for targeting)
            angle_offset = (cx - center_x) / center_x * 30  # Approximate FOV adjustment
            
            # Draw bounding box
            cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 255), 2)
            
            # Draw center point
            cv2.circle(frame, (cx, cy), 5, (255, 0, 255), -1)
            
            # Add label
            label = f"Purple: {w}x{h}"
            cv2.putText(frame, label, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 255), 2)
            
            # Add angle offset
            cv2.putText(frame, f"Angle: {angle_offset:.1f}", (x, y + h + 20), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 255), 2)
        
        # Process green objects
        for contour in green_contours:
            # Get bounding rectangle
            x, y, w, h = cv2.boundingRect(contour)
            
            # Calculate center
            cx = x + w // 2
            cy = y + h // 2
            
            # Calculate angle offset from center
            angle_offset = (cx - center_x) / center_x * 30
            
            # Draw bounding box
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            
            # Draw center point
            cv2.circle(frame, (cx, cy), 5, (0, 255, 0), -1)
            
            # Add label
            label = f"Green: {w}x{h}"
            cv2.putText(frame, label, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
            
            # Add angle offset
            cv2.putText(frame, f"Angle: {angle_offset:.1f}", (x, y + h + 20), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
        
        # Draw center crosshair
        cv2.line(frame, (int(center_x), 0), (int(center_x), frame_height), (255, 255, 255), 1)
        cv2.line(frame, (0, frame_height // 2), (frame_width, frame_height // 2), (255, 255, 255), 1)
        
        return frame

    def publish_to_networktables(self, purple_contours, green_contours, frame_width):
        """
        Publish detection data to NetworkTables for robot code
        """
        center_x = frame_width / 2
        
        # Purple data
        if purple_contours:
            largest_purple = max(purple_contours, key=cv2.contourArea)
            x, y, w, h = cv2.boundingRect(largest_purple)
            cx = x + w // 2
            angle_offset = (cx - center_x) / center_x * 30
            
            self.limelight_table.putNumber("purple_detected", 1)
            self.limelight_table.putNumber("purple_x", cx)
            self.limelight_table.putNumber("purple_y", y + h // 2)
            self.limelight_table.putNumber("purple_area", cv2.contourArea(largest_purple))
            self.limelight_table.putNumber("purple_angle", angle_offset)
        else:
            self.limelight_table.putNumber("purple_detected", 0)
        
        # Green data
        if green_contours:
            largest_green = max(green_contours, key=cv2.contourArea)
            x, y, w, h = cv2.boundingRect(largest_green)
            cx = x + w // 2
            angle_offset = (cx - center_x) / center_x * 30
            
            self.limelight_table.putNumber("green_detected", 1)
            self.limelight_table.putNumber("green_x", cx)
            self.limelight_table.putNumber("green_y", y + h // 2)
            self.limelight_table.putNumber("green_area", cv2.contourArea(largest_green))
            self.limelight_table.putNumber("green_angle", angle_offset)
        else:
            self.limelight_table.putNumber("green_detected", 0)

    def run(self):
        """
        Main loop for color detection
        """
        # For Limelight 3A, you can use the camera stream
        # Option 1: Use USB camera (if running locally)
        cap = cv2.VideoCapture(0)
        
        # Option 2: Use Limelight stream (uncomment if needed)
        # cap = cv2.VideoCapture("http://limelight.local:5800")
        
        print("Starting color detection... Press 'q' to quit")
        
        while True:
            ret, frame = cap.read()
            if not ret:
                print("Failed to grab frame")
                break
            
            # Detect colors
            frame, purple_contours, green_contours, purple_mask, green_mask = self.detect_colors(frame)
            
            # Draw detections
            output_frame = self.draw_detections(frame.copy(), purple_contours, green_contours)
            
            # Publish to NetworkTables
            self.publish_to_networktables(purple_contours, green_contours, frame.shape[1])
            
            # Display results
            cv2.imshow("Color Detection", output_frame)
            cv2.imshow("Purple Mask", purple_mask)
            cv2.imshow("Green Mask", green_mask)
            
            # Print detection info
            print(f"Purple objects: {len(purple_contours)}, Green objects: {len(green_contours)}")
            
            # Break on 'q' key
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
        
        cap.release()
        cv2.destroyAllWindows()


if __name__ == "__main__":
    detector = ColorDetector()
    detector.run()