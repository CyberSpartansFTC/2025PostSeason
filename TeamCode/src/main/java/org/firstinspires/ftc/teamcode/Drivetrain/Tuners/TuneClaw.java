package org.firstinspires.ftc.teamcode.Drivetrain.Tuners;
import static org.firstinspires.ftc.teamcode.Claw.Claw.clawState.CLOSE;
import static org.firstinspires.ftc.teamcode.Claw.Claw.clawState.OPEN;

import org.checkerframework.checker.units.qual.C;
import org.firstinspires.ftc.teamcode.Claw.Claw;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Drivetrain.Drivetrain;
@Config
@Autonomous(name = "Test Claw", group = "Autonomous")
public class TuneClaw extends LinearOpMode {
    Drivetrain drivetrain = null;

    @Override
    public void runOpMode() {
        Claw claw = new Claw(hardwareMap);
        waitForStart();
        while (opModeIsActive()) {
            if (gamepad2.right_bumper){
                Actions.runBlocking(claw.servoClaw(OPEN));
            }
            if (gamepad2.left_bumper) {
                Actions.runBlocking(claw.servoClaw(CLOSE));
            }
        }
    }
}
