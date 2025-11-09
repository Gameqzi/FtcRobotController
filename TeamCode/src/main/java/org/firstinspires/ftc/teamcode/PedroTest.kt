package org.firstinspires.ftc.teamcode

import com.bylazar.configurables.annotations.IgnoreConfigurable
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Autonomous(name = "Example Auto", group = "Examples")
class PedroTest : OpMode() {
    @IgnoreConfigurable
    var panels: TelemetryManager? = null

    private lateinit var follower: Follower
    private lateinit var pathTimer: Timer
    private lateinit var actionTimer: Timer
    private lateinit var opmodeTimer: Timer

    private var pathState: Int = 0

    // Step Two: Initializing poses
    private val startPose   = Pose(84.7, 8.0, Math.toRadians(90.0))
    private val pickup1Pose = Pose(84.7, 8.0, Math.toRadians(90.0))

    // Step Three: Path Initializing
    private lateinit var grabPickup1: PathChain

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
        panels?.addData("path state", pathState)
        panels?.addData("x", pose.x)
        panels?.addData("y", pose.y)
        panels?.addData("heading", pose.heading)
        panels?.update()
    }

    private fun buildPaths() {

        grabPickup1 = follower.pathBuilder()
            .addPath(BezierLine(startPose, pickup1Pose))
            .setLinearHeadingInterpolation(startPose.heading, pickup1Pose.heading)
            .build()
    }

    private fun autonomousPathUpdate() {
        when (pathState) {
            0 -> {
                follower.followPath(grabPickup1, true)
                setPathState(1)
            }
        }
    }

    private fun setPathState(pState: Int) {
        pathState = pState
        pathTimer.resetTimer()
    }

    override fun stop() {}
}
