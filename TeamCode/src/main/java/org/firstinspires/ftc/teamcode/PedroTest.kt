package org.firstinspires.ftc.teamcode

import com.bylazar.configurables.annotations.IgnoreConfigurable
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import java.lang.Thread.sleep

@Autonomous(name = "Example Auto", group = "Examples")
class PedroTest : OpMode() {

    @IgnoreConfigurable
    var panels: TelemetryManager? = null

    private lateinit var follower: Follower
    private lateinit var pathTimer: Timer
    private lateinit var actionTimer: Timer
    private lateinit var opmodeTimer: Timer

    private var pathState: Int = 0

    private val startPose =  Pose(72.0, 0.0, Math.toRadians(90.0))
    private val pickup1 =    Pose(116.0, 35.0, Math.toRadians(360.0))
    private val scoreBack =  Pose(70.0, 8.6, Math.toRadians(90.0))

    private lateinit var pickupPose1: Path
    private lateinit var returnPose: Path

    override fun init() {
        panels = PanelsTelemetry.telemetry

        pathTimer = Timer()
        actionTimer = Timer()
        opmodeTimer = Timer()
        opmodeTimer.resetTimer()

        follower = Constants.createFollower(hardwareMap)
        buildPaths()
        follower.setStartingPose(startPose)
    }

    override fun init_loop() {}

    override fun start() {
        opmodeTimer.resetTimer()
        setPathState(0)
    }

    override fun loop() {
        follower.update()
        autonomousPathUpdate()

        val pose = follower.pose

        // Optional numeric telemetry to Panels
        panels?.debug("path state", pathState)
        panels?.debug("x", pose.x)
        panels?.debug("y", pose.y)
        panels?.debug("heading", Math.toDegrees(pose.heading))
        panels?.debug("pathTimer", pathTimer.elapsedTimeSeconds)
        panels?.debug("actionTimer", actionTimer.elapsedTimeSeconds)
        panels?.debug("opmodeTimer", opmodeTimer.elapsedTimeSeconds)
        panels?.update(telemetry)
    }

    private fun buildPaths() {
        /*pickupPose1 = follower.pathBuilder()
            .addPath(BezierCurve(startPose, pickup1))
            .setLinearHeadingInterpolation(startPose.heading, pickup1.heading)
            .build()
        returnPose = follower.pathBuilder()
            .addPath(BezierCurve(pickup1, scoreBack))
            .setLinearHeadingInterpolation(pickup1.heading, scoreBack.heading)
            .build()*/
        pickupPose1 = Path(BezierCurve(startPose, pickup1))
        pickupPose1.setLinearHeadingInterpolation(startPose.heading, pickup1.heading)
        returnPose = Path(BezierCurve(pickup1, scoreBack))
        returnPose.setLinearHeadingInterpolation(pickup1.heading, scoreBack.heading)
    }

    private fun autonomousPathUpdate() {
        when (pathState) {
            0 -> {
                follower.followPath(pickupPose1, true)
                setPathState(1)
            }
            1 -> {
                if (!follower.isBusy) {
                    sleep(2500)
                    follower.followPath(returnPose, true)
                    setPathState(2)
                }
            }
        }
    }

    private fun setPathState(pState: Int) {
        pathState = pState
        pathTimer.resetTimer()
    }

    override fun stop() {}
}
