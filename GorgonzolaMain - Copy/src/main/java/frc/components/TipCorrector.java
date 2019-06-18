/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.components;

import java.awt.Color;

import frc.components.arm.Shoulder;
import frc.components.arm.Wrist;
import frc.robot.Constants;
import frc.robot.Globals;

/**
 * Corrects tipping robots in case of bad drivers; drive the robot backward if tipping backward and vice versa.
 * IMPORTANT: Defines Backward tipping as positive pitch.
 */
public class TipCorrector implements Component {
    private Drivetrain drivetrain;
    private Shoulder shoulder;
    private Wrist wrist;
    private boolean override;
    private Gyro gyro;
    private InputManager inputManager;
    private boolean correcting;
    private NetworkInterface robotDataTable;
    public TipCorrector() {
        override = false;
        correcting = false;
        
    }

    public void init() {
        inputManager = Globals.inputManager;
        shoulder = Globals.shoulder;
        drivetrain = Globals.drivetrain;
        gyro = Globals.gyro;
        robotDataTable=Globals.robotDataTable;
    }

    public void tick() {
        robotDataTable.setDouble("TipActive", override ? 0 : correcting ? 1 : 2);
        if (inputManager.getTipOverrideButton()) {
            override = true;
        } else if (inputManager.getTipEnableButton()) {
            override = false;
        }
        if (!override) {
            if (gyro.getNormalizedPitch() > Constants.TIP_PITCH_THRESHOLD) {
                //drivetrain.autoDrive(-1, 0);
                System.out.println("OVERRIDING PITCH " + gyro.getNormalizedPitch());
                correcting = true;

                //Globals.lightController.setColor(1, Color.RED);
                //Globals.lightController.setColor(2, Color.RED);
                Globals.lightController.setMastColor(Color.RED);
            } else if (gyro.getNormalizedPitch() < -Constants.TIP_PITCH_THRESHOLD) {
                correcting = true;
                //drivetrain.autoDrive(1, 0);
                System.out.println("OVERRIDING PITCH " + gyro.getNormalizedPitch());
                Globals.lightController.setMastColor(Color.RED);
                //Globals.lightController.setColor(1, Color.RED);
                //Globals.lightController.setColor(2, Color.RED);
            } else {
                correcting = false;
                Globals.lightController.setMastColor(Color.YELLOW);
                //Globals.lightController.setColor(1, Color.YELLOW);
                //Globals.lightController.setColor(2, Color.YELLOW);
            }

        } else {
            correcting = false;
            Globals.lightController.setMastColor(Color.BLUE);
            //Globals.lightController.setColor(1, Color.BLUE);
            //Globals.lightController.setColor(2, Color.BLUE);
        }
    }

    public boolean isCorrecting() {
        return correcting;
    }
}
