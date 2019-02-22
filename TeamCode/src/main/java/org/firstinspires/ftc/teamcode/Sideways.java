package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import edu.spa.ftclib.internal.activator.ServoActivator;
import edu.spa.ftclib.internal.drivetrain.MecanumDrivetrain;

/**
 * Created by Michaela on 1/3/2018.
 */

@Disabled
@TeleOp(name = "Sideways", group = "meet3")

public class Sideways extends OpMode {
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
   // ColorSensor sensorColor;
    //DistanceSensor sensorDistance;

    public MecanumDrivetrain drivetrain;

//    public DcMotor spinnerMotor;
//    public DcMotor armMotor;
//    public DcMotor liftMotor;
//    public DcMotor extenderMotor;
//
//    public Servo teamMarkerServo;
//    public Servo doorServo;
//    public Servo hookServo;
//    public ServoActivator teamMarkerServoActivator;
//    public ServoActivator doorServoActivator;
//    public ServoActivator hookServoActivator;

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "driveFrontRight");
        backLeft = hardwareMap.get(DcMotor.class, "driveBackLeft");
        backRight = hardwareMap.get(DcMotor.class, "driveBackRight");

        drivetrain = new MecanumDrivetrain(new DcMotor[]{frontLeft, frontRight, backLeft, backRight});

//        spinnerMotor = hardwareMap.get(DcMotor.class, "intakeMoter");
//        spinnerMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//        armMotor = hardwareMap.get(DcMotor.class, "ArmMotor");
//        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
//        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        extenderMotor = hardwareMap.get(DcMotor.class, "extenderMotor");

//        // get a reference to the color sensor.
//        sensorColor = hardwareMap.get(ColorSensor.class, "colorSensor");
//
//        // get a reference to the distance sensor that shares the same name.
//        sensorDistance = hardwareMap.get(DistanceSensor.class, "colorSensor");

//        teamMarkerServo = hardwareMap.get(Servo.class, "teamMarkerServo");
//        teamMarkerServoActivator = new ServoActivator(teamMarkerServo, RoverConstants.TEAM_MARKER_DOWN_POSITION, RoverConstants.TEAM_MARKER_UP_POSITION);
//        teamMarkerServoActivator.setActivated(true);
//        hookServo = hardwareMap.get(Servo.class, "hookServo");
//        hookServoActivator = new ServoActivator(hookServo, RoverConstants.HOOK_CLOSE_POSITION, RoverConstants.HOOK_OPEN_POSITION);
//        hookServoActivator.setActivated(false);
//        doorServo = hardwareMap.get(Servo.class, "doorServo");
//        doorServoActivator = new ServoActivator(doorServo, RoverConstants.DOOR_OPEN_POSITION, RoverConstants.DOOR_CLOSE_POSITION);
//        doorServoActivator.setActivated(false);
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loop() {
//        // hsvValues is an array that will hold the hue, saturation, and value information.
//        float hsvValues[] = {0F, 0F, 0F};
//
//        // values is a reference to the hsvValues array.
//        final float values[] = hsvValues;
//
//        // sometimes it helps to multiply the raw RGB values with a scale factor
//        // to amplify/attentuate the measured values.
//        final double SCALE_FACTOR = 255;
//
//        Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
//                (int) (sensorColor.green() * SCALE_FACTOR),
//                (int) (sensorColor.blue() * SCALE_FACTOR),
//                hsvValues);

        // send the info back to driver station using telemetry function.
//        telemetry.addData("Distance (cm)",
//                String.format(Locale.US, "%.02f", sensorDistance.getDistance(DistanceUnit.CM)));
//        telemetry.addData("Alpha", sensorColor.alpha());
//        telemetry.addData("Red  ", sensorColor.red());
//        telemetry.addData("Green", sensorColor.green());
//        telemetry.addData("Blue ", sensorColor.blue());
//        telemetry.addData("Hue", hsvValues[0]);
        double course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x) - Math.PI/2;
        double velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
        double rotation = -gamepad1.left_stick_x;

        if (gamepad1.right_trigger > 0.2){
            course = Math.atan2(gamepad1.right_stick_y, -gamepad1.right_stick_x) - Math.PI/2;
            velocity = Math.hypot(-gamepad1.right_stick_x, -gamepad1.right_stick_y);
            rotation = gamepad1.left_stick_x;
        } else if (gamepad1.left_trigger > 0.2) {
            course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x); // - Math.PI/2 + Math.PI/2;
            velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
            rotation = -gamepad1.left_stick_x;
        }

        drivetrain.setCourse(course);
        drivetrain.setVelocity(velocity);
        drivetrain.setRotation(rotation);

//        telemetry.addData("Lift", liftMotor.getCurrentPosition());
//        telemetry.update();
//
//        // Spinner
//        double spinnerPower = gamepad2.right_trigger > 0.2 ? -gamepad2.right_trigger : 0;
//        spinnerPower = gamepad2.left_trigger > 0.2 ? gamepad2.left_trigger : spinnerPower;
//        spinnerMotor.setPower(spinnerPower);
//
//        // Lift
//        double stickValue = gamepad2.right_stick_y;
//        double power = stickValue > 0.2 ? stickValue : 0;
//        power = stickValue < -0.2 ? stickValue : power;
//        liftMotor.setPower(power);
//
//        // extender
//        double extenderPower = gamepad2.left_stick_y > 0.2 ? gamepad2.left_stick_y : 0;
//        extenderPower = gamepad2.left_stick_y < -0.2 ? gamepad2.left_stick_y : extenderPower;
//        extenderMotor.setPower(extenderPower);
//
//        // arm
//        stickValue = gamepad2.right_stick_x;
//        power = stickValue > 0.2 ? stickValue : 0;
//        power = stickValue < -0.2 ? stickValue : power;
//        armMotor.setPower(power);
//
//        teamMarkerServoActivator.setActivated(false);
//
//        if (gamepad2.y) {
//            hookServoActivator.setActivated(true);
//        }
//        if (gamepad2.a) {
//            hookServoActivator.setActivated(false);
//        }
//        if (gamepad2.x) {
//            doorServoActivator.setActivated(true);
//        }
//        if (gamepad2.b) {
//            doorServoActivator.setActivated(false);
//        }

    }
}

