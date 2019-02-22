package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;



import edu.spa.ftclib.internal.activator.ServoActivator;
import edu.spa.ftclib.internal.drivetrain.MecanumDrivetrain;

//@Disabled
@TeleOp(name = "State Teleop", group = "State")
public class Teleop extends OpMode {
    // drive train and motors
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public boolean isMoriah = false;

    public MecanumDrivetrain drivetrain;


    public DcMotor rotateMotor;
    public DcMotor liftMotor;
    public DcMotor extenderMotor;

    public Servo teamMarkerServo;
    public CRServo spinnerServo;
    public Servo hookServo;
    public Servo bucketServo;
    public Servo doorServo;
    public ServoActivator teamMarkerServoActivator;
    public ServoActivator hookServoActivator;
    public double bucketPosition = 0.5;
    public long endgameTime = 0;
    public boolean endgame = false;

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void init() {
        // Drive train initialization
        frontLeft = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "driveFrontRight");
        backLeft = hardwareMap.get(DcMotor.class, "driveBackLeft");
        backRight = hardwareMap.get(DcMotor.class, "driveBackRight");
        drivetrain = new MecanumDrivetrain(new DcMotor[]{frontLeft, frontRight, backLeft, backRight});

        // The motor that rotates our collection arm
        rotateMotor = hardwareMap.get(DcMotor.class, "rotateMotor");
        rotateMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // motor that extends the arm in and out
        extenderMotor = hardwareMap.get(DcMotor.class, "extenderMotor");
        extenderMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // The motor that lifts and lower the robot from the lander
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");

        // servo that drops the team marker into the landing zone
        teamMarkerServo = hardwareMap.get(Servo.class, "teamMarkerServo");
        teamMarkerServoActivator = new ServoActivator(teamMarkerServo, RoverConstants.TEAM_MARKER_DOWN_POSITION, RoverConstants.TEAM_MARKER_UP_POSITION);
        teamMarkerServoActivator.setActivated(true);

        // servo that hooks the servo onto the handle
        hookServo = hardwareMap.get(Servo.class, "hookServo");
        hookServoActivator = new ServoActivator(hookServo, RoverConstants.HOOK_CLOSE_POSITION, RoverConstants.HOOK_OPEN_POSITION);
        hookServoActivator.setActivated(false);

        // servo that rotates collection box
        bucketServo = hardwareMap.get(Servo.class, "bucketServo");
        // continuously rotating servo
        spinnerServo = hardwareMap.get(CRServo.class, "spinnerServo");
        spinnerServo.setDirection(DcMotorSimple.Direction.REVERSE);
        // servo that controls allen wrench for back of collection box
        doorServo = hardwareMap.get(Servo.class, "doorServo");
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loop() {
        double course, velocity, rotation =0.0;

        // Does driver want to trigger end game?

        if (gamepad1.a || gamepad1.b || gamepad1.x || gamepad1.y) {
            long now = System.currentTimeMillis();
            // has the endgame button been toggled in the last second
            if (now - endgameTime > 1000) {
                // toggle endgame
                endgame = !endgame;
                // update last time endgame was tirgged
                endgameTime = now;
            }
        }

        if (isMoriah) {
            // Moriah always wants phone to be front
            course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x) - Math.PI/2;
            velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
            rotation = -gamepad1.left_stick_x;
            if (gamepad1.right_trigger > 0.2) {
                course = Math.atan2(gamepad1.right_stick_y, -gamepad1.right_stick_x) - Math.PI/2;
                velocity = Math.hypot(-gamepad1.right_stick_x, -gamepad1.right_stick_y);
                rotation = gamepad1.left_stick_x;
            }
        } else {
            if (!endgame) {
                // collection bucket side is front
                course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x);// - Math.PI/2;
                velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
                rotation = -gamepad1.left_stick_x;
                if (gamepad1.right_trigger > 0.2) {
                    course = Math.atan2(gamepad1.right_stick_y, -gamepad1.right_stick_x);// - Math.PI/2;
                    velocity = Math.hypot(-gamepad1.right_stick_x, -gamepad1.right_stick_y);
                    rotation = gamepad1.left_stick_x;
                }
            } else {
                // endgame hook end is now the front
                course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x) + Math.PI / 2;
                velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
                rotation = -gamepad1.left_stick_x;
                if (gamepad1.right_trigger > 0.2) {
                    course = Math.atan2(gamepad1.right_stick_y, -gamepad1.right_stick_x) + Math.PI / 2;
                    velocity = Math.hypot(-gamepad1.right_stick_x, -gamepad1.right_stick_y);
                    rotation = gamepad1.left_stick_x;
                }
            }
        }
        drivetrain.setCourse(course);
        drivetrain.setVelocity(velocity);
        drivetrain.setRotation(rotation);

        telemetry.addData("course", course);
        telemetry.addData("velocity", velocity);
        telemetry.addData("rotation", rotation);
        telemetry.addData("Lift", liftMotor.getCurrentPosition());
        telemetry.update();

        // Spinner
        double spinnerPower = gamepad2.right_trigger > 0.2 ? -gamepad2.right_trigger : 0;
        spinnerPower = gamepad2.left_trigger > 0.2 ? gamepad2.left_trigger : spinnerPower;
        spinnerPower = .74 * spinnerPower;
        spinnerServo.setPower(spinnerPower);

        // Lift
        double stickValue = gamepad2.right_stick_y;
        double liftPower = stickValue > 0.2 ? stickValue : 0;
        liftPower = stickValue < -0.2 ? stickValue : liftPower;
        liftMotor.setPower(liftPower);

        // arm extension
        double extenderPower = gamepad2.left_stick_y > 0.2 ? gamepad2.left_stick_y : 0;
        extenderPower = gamepad2.left_stick_y < -0.2 ? gamepad2.left_stick_y : extenderPower;
        extenderMotor.setPower(extenderPower);

        // arm rotation
        stickValue = gamepad2.right_stick_x;
        liftPower = stickValue > 0.2 ? stickValue / 2.0 : 0;
        liftPower = stickValue < -0.2 ? stickValue / 3.0 : liftPower;
        rotateMotor.setPower(liftPower);

        // never move team marker arm
        teamMarkerServoActivator.setActivated(false);

        // Hook Servo
        if (gamepad2.y) {
            hookServoActivator.setActivated(true);
        }
        if (gamepad2.a) {
            hookServoActivator.setActivated(false);
        }

        // Door Servo
        if (gamepad2.x) {
            doorServo.setPosition(RoverConstants.DOOR_OPEN_CUBE_POSITION);
        } else if (gamepad2.b) {
            doorServo.setPosition(RoverConstants.DOOR_OPEN_BALL_POSITION);
        } else {
            doorServo.setPosition(RoverConstants.DOOR_CLOSE_POSITION);
        }

        // bucket adjustment
        if (gamepad2.right_bumper) {
            bucketPosition = bucketPosition + 0.01;
        }
        if (gamepad2.left_bumper) {
            bucketPosition = bucketPosition - 0.01;
        }
        bucketPosition = Range.clip(bucketPosition, 0, 1);
        bucketServo.setPosition(bucketPosition);

    }
}

