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
@TeleOp(name = "winchSetup", group = "diagnostics")

public class WinchSetup extends OpMode {
    public DcMotor winchMotor;
    public Servo hookServo;
    public CRServo liftServo;

    public double HOOK_OPEN = 1;
    public double HOOK_CLOSE = 0;

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void init() {
        winchMotor = hardwareMap.get(DcMotor.class, "winchMotor");
        winchMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        winchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        winchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hookServo = hardwareMap.get(Servo.class, "hookServo");
        liftServo = hardwareMap.get(CRServo.class, "liftServo");
        hookServo.setPosition(HOOK_CLOSE);
        winchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    @Override
    public void loop() {

        double winchPower = -gamepad2.right_stick_y;


        winchMotor.setPower(winchPower);
        telemetry.addData("winch", winchPower);
        int position = winchMotor.getCurrentPosition();
        telemetry.addData("position:", position);
        if (gamepad2.y) {
            hookServo.setPosition(HOOK_CLOSE);
        } else if (gamepad2.a) {
            hookServo.setPosition(HOOK_OPEN);
        }
        telemetry.addData("hook", hookServo.getPosition());

            double servoPower = .3 * winchPower;
            if (gamepad2.right_bumper) {
                double leftStickY = -gamepad2.left_stick_y;
                if (Math.abs(leftStickY) < 0.2) {
                    servoPower = 0;
                } else {
                    servoPower = leftStickY;
                }
            }
                liftServo.setPower(servoPower);
        telemetry.addData("servoPower",servoPower);
        telemetry.update();
    }
}

