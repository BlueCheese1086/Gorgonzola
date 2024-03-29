package frc.components;

import edu.wpi.first.wpilibj.Joystick;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.ButtonMap;
import frc.robot.Globals;

/**
 * A component that parses input from the driverstation (joysticks, etc)
 */
public class InputManager implements Component {
    private LogInterface logger;
    private Joystick left, right, aux;
    private ArmControlState state;

    public InputManager() {
        state = ArmControlState.AUTO;
        left = new Joystick(ButtonMap.LEFT_STICK);
        right = new Joystick(ButtonMap.RIGHT_STICK);
        aux = new Joystick(ButtonMap.AUX_STICK);
    }

    public void init() {
        logger = Globals.logger;
        try {
            logger.inputManager = LogInterface.table("Input_Manager",
                    new String[] { "forward", "turn", "safetyButton" },
                    new Type[] { new Decimal(), new Decimal(), new Bool() },
                    new Loggable[] { () -> getForward(), () -> getTurn(), () -> getDriveSafetyButton() });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean shoulderManual = false, wristManual = false;

    /**
     * Updates the robot's arm state so that getArmPosition() can always return the proper position, regardless of overrides.
     */
    public void tick() {
        if (aux.getRawButtonPressed(ButtonMap.SHOULDER_MANUAL_OVERRIDE)) {
            shoulderManual = !shoulderManual;

        }
        if (aux.getRawButtonPressed(ButtonMap.WRIST_MANUAL_OVERRIDE)) {
            wristManual = !wristManual;
        }
    }

    /**
     * A method to get a number from -1 to 1 denoting the position of the primary
     * joystick
     * 
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         forward/backward axis
     */
    public double getForward() {
        return getDriveSafetyButton() ? left.getY() : 0;
    }

    public boolean getCameraEnable() {
        return right.getRawButton(ButtonMap.CAMERA_ENABLE);
    }

    public boolean imuOveridden = false;

    public boolean getIMUOverride() {
        if (aux.getPOV() == 270 & !imuOveridden) {
            imuOveridden = true;
            return true;
        } else if (aux.getPOV() != 270) {
            imuOveridden = false;
        }
        return false;
    }

    /**
     * Returns whether the hatch toggle button is being pressed
     * @return true if we should be holding a hatch, false otherwise.
     */
    public boolean getHatchIntakeButton() {
        return aux.getRawButton(ButtonMap.SUCC_TOGGLE_BUTTON);

    }

    public boolean getHatchOutputButton() {
        return aux.getRawButton(8);
    }

    public boolean getTipOverrideButton() {
        return getClimbKnives();
    }

    public boolean getTipEnableButton() {
        return right.getRawButton(ButtonMap.TIP_ENABLE);
    }

    /**
     * Returns a number from -1 to 1 denoting the amount the robot should be turning
     * @return a number [-1, 1], with -1 denoting full power left and 1 denoting full power right
     */
    public double getTurn() {
        return getDriveSafetyButton() ? right.getX() : 0;
    }

    /**
     * Returns the state of the drive safety button
     * @return true if the drive safety button is being pressed, and false otherwise.
     */
    public boolean getDriveSafetyButton() {
        return left.getRawButton(ButtonMap.DRIVE_SAFETY);
    }

    /**
     * Returns whether the gear shift button is being pressed
     * @return true if the button is being pressed, false otherwise
     */
    public boolean getGearSwitchButton() {
        return right.getRawButtonPressed(ButtonMap.GEAR_SHIFT);
    }

    /**
     * Returns whether the arm safety button is being pressed
     * @return true if the arm safety button is being pressed, false otherwise.
     */
    public boolean getArmSafetyButton() {
        return aux.getRawButton(ButtonMap.AUX_SAFETY);
    }

    /**
     * Returns the power with which the CAM should be moving.
     * @return a number [-1, 1], denoting the percent of backward/forward power to give to the CAM motor
     */
    public double getClimb() {
        return right.getY();
    }

    /**
     * Returns whether the manual (percent out) climb button is being pressed
     * @return true if the button is being pressed, false otherwise
     */
    public boolean getManualClimbButton() {
        return false;// right.getRawButton(ButtonMap.CAM_MANUAL);
    }

    /**
     * Returns whether the automatic (motion magic) climb button is being pressed
     * @return true if the button is being pressed, false otherwise
     */
    public boolean getAutoClimbButton() {
        return right.getRawButton(ButtonMap.CAM_AUTO);
    }

    /**
     * Returns a number denoting the position of the shoulder within the range of
     * the shoulder
     * @return -1 for the bottom of the shoulder's range, 0 for the middle, 1 for the top, etc
     */
    public double getShoulderManualHeight() {
        return aux.getRawAxis(ButtonMap.SHOULDER_STICK);
    }

    /**
    * Returns a number denoting the position of the wrist within a defined range of
    * the wrist
    * @return -1 for the bottom of the wrist's range, 0 for the middle, 1 for the top, etc
    */
    public double getWristManualPosition() {
        return aux.getRawAxis(ButtonMap.WRIST_STICK);
    }

    /**
     * Returns whether the climb knife deploy button is being pressed.
     * @return true if the button is being pressed, false otherwise.
     */
    public boolean getClimbKnives() {
        return left.getRawButton(ButtonMap.KNIFE_DEPLOY);
    }

    public ArmHeight getArmPosition() {
        if (getArmSafetyButton()) {
            if (state == ArmControlState.FULL_MANUAL)
                return ArmHeight.FULL_MANUAL;
            if (aux.getRawButton(ButtonMap.HATCH_BALL_SWITCH)) {
                if (aux.getRawButton(1)) {
                    return ArmHeight.GROUND_PICKUP;
                }
                if (aux.getRawButton(2)) {
                    return ArmHeight.BALL_LOW;
                }
                if (aux.getRawButton(3)) {
                    return ArmHeight.BALL_MEDIUM;
                }
                if (aux.getRawButton(4)) {
                    return ArmHeight.BALL_HIGH;
                }
                int pov = aux.getPOV();
                if (pov == ButtonMap.BALL_CARGO) {
                    return ArmHeight.BALL_CARGO;
                }
            } else {
                if (aux.getRawButton(1)) {
                    return ArmHeight.STOW;
                }
                if (aux.getRawButton(2)) {
                    return ArmHeight.HATCH_LOW;
                }
                if (aux.getRawButton(3)) {
                    return ArmHeight.HATCH_MEDIUM;
                }
                if (aux.getRawButton(4)) {
                    return ArmHeight.HATCH_HIGH;
                }
            }
            return ArmHeight.NO_CHANGE; //Nothing will be done
        } else {
            return ArmHeight.NO_MOVEMENT;
        }
    }

    public boolean getBallIntakeInButton() {
        return aux.getPOV() == ButtonMap.BALL_INTAKE;
    }

    public boolean getBallIntakeOutButton() {
        return aux.getPOV() == ButtonMap.BALL_OUTPUT;
    }

}