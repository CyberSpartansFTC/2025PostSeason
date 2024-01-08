package org.firstinspires.ftc.teamcode.Components;

import static org.firstinspires.ftc.teamcode.Robots.BasicRobot.LOGGER;
import static org.firstinspires.ftc.teamcode.Robots.BasicRobot.isTeleop;
import static org.firstinspires.ftc.teamcode.Robots.BasicRobot.time;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.Components.RFModules.Devices.RFServo;
import org.firstinspires.ftc.teamcode.Components.RFModules.System.RFLogger;

/** Harry Class to contain all Arm functions */
@Config
public class Arm extends RFServo {
  public static double DROP_POS = 0.1, HOVER_POS = 0.88, GRAB_POS = 0.72;
  private double lastTime = 0, FLIP_TIME = 0.9;

  /** constructs arm servo, logs to general with CONFIG severity */
  public Arm() {
    super("armServo", 1.0);
    lastTime = -100;
    super.setLastTime(-100);
    if (!isTeleop) {
      super.setPosition(GRAB_POS);
      super.setFlipTime(FLIP_TIME);
      ArmStates.GRAB.setStateTrue();
      ArmTargetStates.GRAB.setStateTrue();
    }
    else{
      super.setPosition(HOVER_POS);
      super.setFlipTime(FLIP_TIME);
      ArmStates.HOVER.setStateTrue();
      ArmTargetStates.HOVER.setStateTrue();
    }
    lastTime = -100;
    super.setLastTime(-100);
  }

  /** enum for arm servo states, built in function to update states */
  public enum ArmStates {
    HOVER(false, HOVER_POS),
    GRAB(true, GRAB_POS),
    DROP(false, DROP_POS);

    boolean state;
    double pos;

    ArmStates(boolean p_state, double p_pos) {
      state = p_state;
      pos = p_pos;
    }

    void setStateTrue() {
      if (!this.state) {
        for (int i = 0; i < Arm.ArmStates.values().length; i++) {
          if (Arm.ArmStates.values()[i].state) {
            LOGGER.log("assigned false to position state: " + (Arm.ArmStates.values()[i].name()));
          }
          Arm.ArmStates.values()[i].state = false;
        }
        this.state = true;
        LOGGER.log("assigned true to position state: " + this.name());
      }
    }

    public boolean getState() {
      return this.state;
    }
  }

  public enum ArmTargetStates {
    HOVER(false, HOVER_POS),
    GRAB(true, GRAB_POS),
    DROP(false, DROP_POS);

    boolean state;
    double pos;

    ArmTargetStates(boolean p_state, double p_pos) {
      state = p_state;
      pos = p_pos;
    }

    void setStateTrue() {
      if (!this.state) {
        for (int i = 0; i < Arm.ArmTargetStates.values().length; i++) {
          if (Arm.ArmTargetStates.values()[i].state) {
            LOGGER.log(
                "assigned false to target state: " + (Arm.ArmTargetStates.values()[i].name()));
          }
          Arm.ArmTargetStates.values()[i].state = false;
        }
        this.state = true;
        LOGGER.log("assigned true to target state: " + this.name());
      }
    }

    public boolean getState() {
      return this.state;
    }
  }

  public void flipTo(ArmStates p_state) {
    if (!p_state.state && time - lastTime > FLIP_TIME) {
      if ((p_state == ArmStates.DROP || p_state == ArmStates.HOVER)
          && super.getPosition() != p_state.pos) {
        if (p_state == ArmStates.DROP) {
          ArmTargetStates.DROP.setStateTrue();
          if (ArmStates.GRAB.getState()) {
            flipTo(ArmStates.HOVER);
            return;
          }
          if (!(Lift.LiftMovingStates.AT_ZERO.state || Lift.LiftPositionStates.AT_ZERO.state)) {
            super.setPosition(DROP_POS);
            Wrist.WristTargetStates.DROP.setStateTrue();
            Twrist.twristTargetStates.DROP.setStateTrue();
            LOGGER.log(RFLogger.Severity.INFO, "flipping to DROP");
            lastTime = time;
          }else{
            Lift.LiftMovingStates.LOW.state=true;
          }
        } else {
          if (!(Lift.LiftMovingStates.AT_ZERO.state || Lift.LiftPositionStates.AT_ZERO.state)
              || ArmStates.GRAB.getState()) {

            super.setPosition(HOVER_POS);
            ArmTargetStates.HOVER.state=true;
            LOGGER.log(RFLogger.Severity.INFO, "flipping to HOVER");
            Wrist.WristTargetStates.GRAB.setStateTrue();
            Twrist.twristTargetStates.GRAB.setStateTrue();
            lastTime = time;
          }else{
            Lift.LiftMovingStates.LOW.state=true;
          }
        }
      }
      if (p_state == ArmStates.GRAB && super.getPosition() != p_state.pos) {
        if (ArmStates.HOVER.state) {
          super.setPosition(GRAB_POS);
          LOGGER.log("flipping to GRAB");
          lastTime = time;
        } else {
          ArmTargetStates.HOVER.state = true;
        }
      }
    }
    ArmTargetStates.values()[p_state.ordinal()].state = true;
  }

  /**
   * void, sets arm servo position to input position logs to general with highest verbosity
   *
   * @param p_target target position
   */
  public void setPosition(double p_target) {
    super.setPosition(p_target);
  }

  /**
   * void, updates state machine according to where servo is logs to general inside the enum
   * functions
   */
  public void update() {
    for (var i : ArmStates.values()) {
      if (super.getPosition() == i.pos && time > lastTime + FLIP_TIME) i.setStateTrue(); ArmTargetStates.values()[i.ordinal()].state=false;
    }
    for (var i : ArmTargetStates.values()) {
      if (i.state && super.getTarget() != i.pos) {
        flipTo(ArmStates.values()[i.ordinal()]);
      }
    }
  }
}
