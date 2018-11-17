package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import edu.spa.ftclib.internal.drivetrain.MecanumDrivetrain;

/**
 * Created by Michaela on 1/3/2018.
 */

//@Disabled
@TeleOp(name = "Meet 1 Tele-op Zero", group = "sample")

public class Meet1Teleop extends OpMode {

    public static final double RIGHT_DOOR_OPEN = 0;
    public static final double LEFT_DOOR_OPEN = 1;
    public static final double RIGHT_DOOR_CLOSE = 1;
    public static final double LEFT_DOOR_CLOSE = 0;
    public double HOOK_OPEN = 1;
    public double HOOK_CLOSE = 0;

    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    public DcMotor winchMotor;
//    public DcMotor spinnerMotor;
//    public DcMotor linearMotor;
//    public DcMotor rotaterMotor;

    public Servo hookServo;
//    public Servo leftDoorServo;
//    public Servo rightDoorServo;
    public CRServo liftServo;

    public boolean endgameOverride = false;


    public MecanumDrivetrain drivetrain;

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "driveFrontLeft");   // Pink 1
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight = hardwareMap.get(DcMotor.class, "driveFrontRight"); // ORANGE 0driveFrontRight
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft = hardwareMap.get(DcMotor.class, "driveBackLeft");    //  GREEN 2
        backRight = hardwareMap.get(DcMotor.class, "driveBackRight");  // blue 3
        drivetrain = new MecanumDrivetrain(new DcMotor[]{frontLeft, frontRight, backLeft, backRight});

        winchMotor = hardwareMap.get(DcMotor.class, "winchMotor");
        winchMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hookServo = hardwareMap.get(Servo.class, "hookServo");
        liftServo = hardwareMap.get(CRServo.class, "liftServo");
        hookServo.setPosition(HOOK_OPEN);
        winchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loop() {
        double course = Math.atan2(-gamepad1.right_stick_y, gamepad1.right_stick_x) - Math.PI/2;
        double velocity = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
        double rotation = -gamepad1.left_stick_x;

        drivetrain.setCourse(course);
        drivetrain.setVelocity(velocity);
        drivetrain.setRotation(rotation);

        telemetry.addData("course", course);
        telemetry.addData("velocity", velocity);
        telemetry.addData("rotation", rotation);
        telemetry.update();

        // spinner
        /*
        double spinnerInSpeed = gamepad2.right_trigger;
        double spinnerOutSpeed = gamepad2.left_trigger;

        if (spinnerInSpeed > 0.2) {
            spinnerMotor.setPower(spinnerInSpeed);
        } else if (spinnerOutSpeed > 0.2) {
            spinnerMotor.setPower(-spinnerOutSpeed);
        } else {
            spinnerMotor.setPower(0);
        }
*/
        // Collection Doors
        /*
        boolean rightDoorOpen = gamepad2.right_bumper;
        boolean leftDoorOpen = gamepad2.left_bumper;

        if (rightDoorOpen) {
            rightDoorServo.setPosition(RIGHT_DOOR_OPEN);
        } else {
            rightDoorServo.setPosition(RIGHT_DOOR_CLOSE);
        }

        if (leftDoorOpen) {
            leftDoorServo.setPosition(LEFT_DOOR_OPEN);
        } else {
            leftDoorServo.setPosition(LEFT_DOOR_CLOSE);
        }
*/
        // HOOK
        if (gamepad2.y) {
            hookServo.setPosition(HOOK_CLOSE);
        } else if (gamepad2.a) {
            hookServo.setPosition(HOOK_OPEN);
        }

        if (gamepad2.x && gamepad2.b) {
            endgameOverride = true;
        }

        double liftPower = -gamepad2.right_stick_y;
        if (Math.abs(liftPower) < 0.2) {
            liftPower = 0;
        }
        liftServo.setPower(liftPower);

        double winchPower = -gamepad2.left_stick_y;
        if (Math.abs(winchPower) < 0.2) {
            winchPower = 0;
        }
        winchMotor.setPower(winchPower);
    }
}

