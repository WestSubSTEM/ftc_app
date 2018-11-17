/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import edu.spa.ftclib.internal.drivetrain.MecanumDrivetrain;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Basic Auto", group="Auto")
//@Disabled
public class AutoBase extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

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


    public Servo hookServo;
    public CRServo liftServo;

    public boolean endgameOverride = false;


    public MecanumDrivetrain drivetrain;


    @Override
    public void runOpMode() {
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
        hookServo.setPosition(HOOK_CLOSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();


        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        if (opModeIsActive()) {

            winchMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            winchMotor.setTargetPosition(32_000);
            winchMotor.setPower(1);
            liftServo.setPower(.2);

            while (winchMotor.getCurrentPosition() < 31_000) {
                sleep(100);
                telemetry.addData("winch Pos:", winchMotor.getCurrentPosition());
                telemetry.update();
            }
            if (opModeIsActive()) {
                liftServo.setPower(1);
                hookServo.setPosition(HOOK_OPEN);
                winchMotor.setTargetPosition(36_000);
                winchMotor.setPower(1);
                while (winchMotor.getCurrentPosition() < 35_000) {
                    sleep(100);
                    telemetry.addData("winch Pos:", winchMotor.getCurrentPosition());
                    telemetry.update();
                }
            }
            winchMotor.setPower(0);
            liftServo.setPower(0);
            if (opModeIsActive()) {
                double course = Math.atan2(1, 0) - Math.PI/2;
                double velocity = Math.hypot(0, .5);
                double rotation = 0;

                drivetrain.setCourse(course);
                drivetrain.setVelocity(velocity);
                drivetrain.setRotation(rotation);

                sleep(4000);
            }

        }
    }
}
