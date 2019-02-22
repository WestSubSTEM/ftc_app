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
@TeleOp(name = "Moriah Teleop", group = "State")
public class TeleopMoriah extends Teleop {

    /**
     * User defined init method
     * <p>
     * This method will be called once when the INIT button is pressed.
     */
    @Override
    public void init() {
        isMoriah = true;
        super.init();
    }

}

