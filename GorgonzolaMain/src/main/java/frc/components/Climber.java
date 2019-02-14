package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.Globals;
import frc.talonmanager.ClimbTalonManager;

public class Climber implements Component {
    private ClimbTalonManager talon1, talon2;
    private InputManager im;
    public Climber() {
        //talon1=new ClimbTalonManager(idx);
        //talon2=new ClimbTalonManager(idx);
        talon2.follow(talon1);
    }
    public void init(){
        im = Globals.im;
    }
    public void tick(){
        talon1.set(ControlMode.PercentOutput, im.getShoulderHeight()*2.0-1.0);
    }
}