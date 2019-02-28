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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;


@Autonomous(name = "Auto Crater", group = "State")
//@Disabled
public class AutoCrater extends LinearOpMode  {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "AXRxi6H/////AAAAGXFBcOuXBkpfsXQl3RrsaWMs3rPkMqV94uKhxmHX5LE95IW4PqzCg3G44Uqx8hnsDvRnPQrbus1zvbgc+3sPBt4w08IbyebgwgnFN9221SFutmZ76ox5ctJ6+HhTKIyfyYJjSWUaxADTzTy5w8BNnu9KOk6GOiafGNqbDzFffECDcnfSkxQBSlvuTtioONy5dKrhUj6nFuIXIXFO9kb6vqhqjzS6ViKUcSbkYmQ8Pjrqb5W4cUd+wyeGMDqFQkEUlWdm/z/J+p774VeP9NquwDPUVfR4GLUEQsA8/EG0B8IoVG1VCeHZOJcpIiapQOPQ9eMpVaBr+Qj6E0kaEUR5vZ9QFXYDpk+1fpyB1RGGSmAm";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;


    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public DcMotor[] driveMotors;

    public DcMotor liftMotor;

    public Servo teamMarkerServo;
    public Servo hookServo;

    public final int BUMP_FORWARD = 40;
    public final int BLOCK_RIGHT = -40;
    public final int BLOCK_LEFT = 40;
    public final int SIDEWAYS_TO_WALL = -15;
    public final int SIDEWAYS_AWAY_FROM_WALL = 5;
    public final int MOVE_TO_WALL = 70;
    public final int LANDER_TO_CENTER_MINERAL = 30;
    public final int ROTATE_TO_ZONE = 90+40;// changed from 35 to 40
    public final int GO_TO_ZONE = 110;
    public final int ZONE_TO_PIT = 180;
//    public int turnToPit = (90+45);
//    public int moveToPit = 200;
//
    public boolean drop = true;

    public enum PossibleGoldPositions {
        LEFT,
        RIGHT,
        CENTER,
        NONE
    }
    public PossibleGoldPositions goldPosition = PossibleGoldPositions.NONE;


    @Override public void runOpMode() {
        initVuforia();


        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();

        frontLeft = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "driveFrontRight");

        backLeft = hardwareMap.get(DcMotor.class, "driveBackLeft");
        backRight = hardwareMap.get(DcMotor.class, "driveBackRight");
        driveMotors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        teamMarkerServo = hardwareMap.get(Servo.class, "teamMarkerServo");
        teamMarkerServo.setPosition(RoverConstants.TEAM_MARKER_UP_POSITION);
        hookServo  = hardwareMap.get(Servo.class, "hookServo");
        hookServo.setPosition(RoverConstants.HOOK_CLOSE_POSITION);

        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        if (tfod != null) {
            tfod.activate();
        }
        while (!isStarted()) {
            inferGold();
        }
        telemetry.addLine("Go GO GO!!!!");
        telemetry.update();

        if (drop) {
            liftMotor.setTargetPosition(RoverConstants.LIFT_TICS);
            liftMotor.setPower(1);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            while (liftMotor.isBusy()) {
                telemetry.addData("Lift", liftMotor.getCurrentPosition());
                telemetry.update();
            }
            hookServo.setPosition(RoverConstants.HOOK_OPEN_POSITION);
            sleep(500);
        } else {
            goldPosition = PossibleGoldPositions.LEFT;
        }
        // MIDDLE MINERAL
        goStraight(LANDER_TO_CENTER_MINERAL, .8);
        printWhileMoving();

        switch (goldPosition) {
            case CENTER:
                bumpGold();
                goSideways(BLOCK_LEFT + MOVE_TO_WALL, .8);
                break;
            case RIGHT:
                goSideways(BLOCK_RIGHT, .8);
                bumpGold();
                goSideways(-BLOCK_RIGHT +BLOCK_LEFT + MOVE_TO_WALL, .8);
                break;
            case LEFT:
                goSideways(BLOCK_LEFT, .8);
                bumpGold();
                goSideways(MOVE_TO_WALL, .8);
                break;
            case NONE:
                goSideways(BLOCK_LEFT + MOVE_TO_WALL, .8);
                break;
        }
        if (opModeIsActive()) {
            rotate(ROTATE_TO_ZONE, 0.8);
        }
        if (opModeIsActive()) {
            goSideways(SIDEWAYS_TO_WALL, 0.8);
            goSideways(SIDEWAYS_AWAY_FROM_WALL, 0.8);
        }
        if (opModeIsActive()) {
            goStraight(GO_TO_ZONE, 0.8);
        }
        if (opModeIsActive()) {
            goSideways(SIDEWAYS_AWAY_FROM_WALL * 2, 0.8);
        }
        if (opModeIsActive()) {
            teamMarkerServo.setPosition(RoverConstants.TEAM_MARKER_DOWN_POSITION);
            sleep(500);
        }
        if (opModeIsActive()) {
            teamMarkerServo.setPosition(RoverConstants.TEAM_MARKER_UP_POSITION);
            rotate(10, 0.8);
        }
        if (opModeIsActive()) {
            goStraight(-ZONE_TO_PIT, 1.0);
        }
    }

    public void printWhileMoving() {
        while(opModeIsActive() && frontRight.isBusy()) {
            telemetry.addData("motor fl", frontLeft.getCurrentPosition());
            telemetry.addData("motor fr", frontRight.getCurrentPosition());
            telemetry.addData("motor bl", backLeft.getCurrentPosition());
            telemetry.addData("motor br", backRight.getCurrentPosition());
            telemetry.update();
        }
    }

    public void goStraight(int cm, double power) {
        int ticks = (int) (cm / RoverConstants.CM_PER_TIC);
        for (DcMotor motor : driveMotors) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        for (DcMotor motor : driveMotors) motor.setTargetPosition(ticks);
        for (DcMotor motor : driveMotors) motor.setPower(power);
        for (DcMotor motor : driveMotors) motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        printWhileMoving();
    }

    public void goSideways(int cm, double power) {
        int ticks = (int) (cm / RoverConstants.CM_PER_TIC);
        frontRight.setTargetPosition(ticks);
        backRight.setTargetPosition(-ticks);
        frontLeft.setTargetPosition(-ticks);
        backLeft.setTargetPosition(ticks);
        for (DcMotor motor : driveMotors) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        for (DcMotor motor : driveMotors) motor.setPower(power);
        for (DcMotor motor : driveMotors) motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        printWhileMoving();
    }

    public void rotate(int degrees, double power) {
        int ticks = (int) (degrees / RoverConstants.DEG_PER_TIC);
        for (DcMotor motor : driveMotors) motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setTargetPosition(-ticks);
        frontLeft.setTargetPosition(-ticks);
        frontRight.setTargetPosition(ticks);
        backRight.setTargetPosition(ticks);
        for (DcMotor motor : driveMotors) motor.setPower(power);
        for (DcMotor motor : driveMotors) motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        printWhileMoving();
    }
    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public boolean seeGold() {
        int goldMineralX = -1;
        int silverMineral1X = -1;
        int silverMineral2X = -1;
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() > 0) {
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                            telemetry.addData("Gold L R T B", " " + (int) recognition.getLeft() + ", " + (int) recognition.getRight() + ", " + (int) recognition.getTop() + ", " + (int) recognition.getBottom());
                            telemetry.addData("Gold W H", " " + (int) recognition.getWidth() + ", " + (int) recognition.getHeight());
                            telemetry.addData("Gold conf", (int)(recognition.getConfidence() * 100));
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                            telemetry.addData("S1 L R T B", " " + (int) recognition.getLeft() + ", " + (int) recognition.getRight() + ", " + (int) recognition.getTop() + ", " + (int) recognition.getBottom());
                            telemetry.addData("S1 W H", " " + (int) recognition.getWidth() + ", " + (int) recognition.getHeight());
                            telemetry.addData("S1 conf", (int)(recognition.getConfidence() * 100));
                        } else {
                            silverMineral2X = (int) recognition.getLeft();
                            telemetry.addData("S2 Left", (int) recognition.getLeft());
                            telemetry.addData("S2 Right", (int) recognition.getRight());
                            telemetry.addData("S2 conf", (int)(recognition.getConfidence() * 100));
                        }
                    }
                    if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                        if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Left");
                        } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                            telemetry.addData("Gold Mineral Position", "Right");
                        } else {
                            telemetry.addData("Gold Mineral Position", "Center");
                        }
                    }
                }
                telemetry.update();
            }
        }
        if (goldMineralX > -1 && silverMineral1X == -1 && silverMineral2X == -1 && goldMineralX < 700) {
            return true;
        }
        if (goldMineralX > -1 && silverMineral1X > -1 && (goldMineralX < silverMineral1X)) {
            return true;
        }
        return false;
    }

    public void inferGold() {
        int goldMineralX = -1;
        int silverMineral1X = -1;
        int silverMineral2X = -1;
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                if (updatedRecognitions.size() > 0) {
                    for (Recognition recognition : updatedRecognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldMineralX = (int) recognition.getLeft();
                            telemetry.addData("Gold L R T B", " " + (int) recognition.getLeft() + ", " + (int) recognition.getRight() + ", " + (int) recognition.getTop() + ", " + (int) recognition.getBottom());
//                            telemetry.addData("Gold W H", " " + (int) recognition.getWidth() + ", " + (int) recognition.getHeight());
//                            telemetry.addData("Gold conf", (int)(recognition.getConfidence() * 100));
                        } else if (silverMineral1X == -1) {
                            silverMineral1X = (int) recognition.getLeft();
                            telemetry.addData("S1 L R T B", " " + (int) recognition.getLeft() + ", " + (int) recognition.getRight() + ", " + (int) recognition.getTop() + ", " + (int) recognition.getBottom());
//                            telemetry.addData("S1 W H", " " + (int) recognition.getWidth() + ", " + (int) recognition.getHeight());
//                            telemetry.addData("S1 conf", (int)(recognition.getConfidence() * 100));
                        } else {
                            silverMineral2X = (int) recognition.getLeft();
                            telemetry.addData("S2 L R T B", " " + (int) recognition.getLeft() + ", " + (int) recognition.getRight() + ", " + (int) recognition.getTop() + ", " + (int) recognition.getBottom());
//                            telemetry.addData("S2 Right", (int) recognition.getRight());
//                            telemetry.addData("S2 conf", (int)(recognition.getConfidence() * 100));
                        }
                    }
                    if (goldMineralX == -1) {
                       goldPosition = PossibleGoldPositions.RIGHT;
                       telemetry.addData("GOLD", "Right");
                    } else if (goldMineralX > silverMineral1X && silverMineral1X > -1) {
                        goldPosition = PossibleGoldPositions.CENTER;
                        telemetry.addData("GOLD", "Center");
                    } else if (goldMineralX < silverMineral1X && silverMineral1X > -1) {
                        goldPosition = PossibleGoldPositions.LEFT;
                        telemetry.addData("GOLD", "Left");
                    } else {
                        goldPosition = PossibleGoldPositions.NONE;
                        telemetry.addData("GOLD", "None");
                    }
                }
            }
        }
        telemetry.addData("gold?", goldPosition);
        telemetry.update();
    }


    public void bumpGold() {
        goStraight(BUMP_FORWARD, .7);
        goStraight(-(BUMP_FORWARD-10), .7);         //change 10 to 5-7ish to help the angle for the idol
    }

}
