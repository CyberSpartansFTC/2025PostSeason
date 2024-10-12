package org.firstinspires.ftc.teamcode.Robot;

import org.firstinspires.ftc.teamcode.Robot.systems.MecanumDriveTrain;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.resourses.PIDController;
import org.firstinspires.ftc.teamcode.resourses.Odometry;

@Config
public class Robot {
   
    
    //used for how fast the turning input is used.
    public static double turningInputConstant = 0.005;
    public static double pCon = 1;
    public static double dCon = 0;
    
    private double drive;
    private double slide;
    private double turn;
    //TODO Make more permanent system to detect turning
    public boolean turningBoolean;
    public MecanumDriveTrain driveTrain;
    public Odometry odometry;
    private double RobotX, RobotY;
    private ElapsedTime runtime = new ElapsedTime();
    //TODO Tune the pConstant and d Constant numbers, these are place holders.
    public PIDController anglePID = new PIDController(pCon,dCon, runtime);
    
    FtcDashboard dashboard;
    private double robotHeading;
    private double lastTimeAngle;
    private boolean CurrentAlign = true;
    private boolean DriverOrientationDriveMode = true;
    
    public double derivativeConstantAngle;
    public double proportionalConstantAngle;
    
    public Telemetry telemetry = null;
    
    private boolean resettingImu = false;
    private double AutoStartAngle = 0;
    
    private double previousLoopTime, secondPreviousLoopTime;
    
    public void init(HardwareMap hardwareMap, double x, double y, double angle){
        driveTrain = new MecanumDriveTrain(hardwareMap);
        odometry = new Odometry(x,y,angle,telemetry);
    }
    
    public double getDeltaTime(){
        double deltaTime = Math.abs(secondPreviousLoopTime-previousLoopTime);
        secondPreviousLoopTime = previousLoopTime;
        previousLoopTime = runtime.nanoseconds();
        return deltaTime;
    }
    
    public void IMUReset() {
        odometry.IMUReset();
        anglePID.setTarget(0);
    }
    
    private double lastErrorAngle;
    private boolean IsProgramAutonomous;
    
    public double radiansToDegrees(double radians){
        return radians*(180/Math.PI);
    }
    
    public double degreesToRadians(double degrees){
        return degrees*(Math.PI/180);
    }
    
    public void turnUpdate() {
        if (resettingImu){
            return;
        }
        anglePID.setTarget(odometry.getRobotAngle());
        anglePID.update(odometry.getRobotAngle());
        
    }
    
    double Wrap(double angle) {
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        return angle;
    }
    
    
    
}