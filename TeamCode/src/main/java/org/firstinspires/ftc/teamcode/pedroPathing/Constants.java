package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.OTOSConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(8.89041)

            // Primary XY position control
            //.translationalPIDFCoefficients(new PIDFCoefficients(0.12, 0.0, 0.0007, 0.0))
            // Secondary translational PIDF (for finer correction)
            //.secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0.010, 0.0, 0.0006, 0.0))
            // Primary heading control (rotation)
            //.headingPIDFCoefficients(new PIDFCoefficients(0.010, 0.0, 0.0006, 0.0))
            // Secondary heading control
            //.secondaryHeadingPIDFCoefficients(new PIDFCoefficients(0.009, 0.0, 0.0005, 0.0))
            // Drive PIDF (helps keep velocity smooth)
            //.drivePIDFCoefficients(new FilteredPIDFCoefficients(0.009, 0.0, 0.0005, 0.6, 0.0))
            // Secondary drive PIDF
            //.secondaryDrivePIDFCoefficients(new FilteredPIDFCoefficients(.008, 0.0, 0.0004, 0.6, 0.0))


            // Enable the secondary loops (these help smooth out motion)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .useSecondaryDrivePIDF(true);


    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 0.8, 1);

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(0.8)
            .rightFrontMotorName("frontRight")
            .rightRearMotorName("backRight")
            .leftRearMotorName("backLeft")
            .leftFrontMotorName("frontLeft")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD);


    /*public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .robotLength(16.5)
            .robotWidth(17)
            .forwardTicksToInches(0.006313782143400114)
            .strafeTicksToInches(0.006325054641343225)
            .turnTicksToInches(-0.11523000868339908)
            .rightFrontMotorName("frontRight")
            .rightRearMotorName("backRight")
            .leftRearMotorName("backLeft")
            .leftFrontMotorName("frontLeft")
            .leftFrontEncoderDirection(Encoder.REVERSE)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD);*/


    static SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(-2.875, 1.5, 0);
    public static OTOSConstants localizerConstants = new OTOSConstants()
            .hardwareMapName("otos")
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS)
            .offset(offset)
            .linearScalar(0.9571582267593028)
            .angularScalar(0.9962755890624266);


    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .OTOSLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
