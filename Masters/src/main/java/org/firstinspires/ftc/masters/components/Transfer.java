package org.firstinspires.ftc.masters.components;

import static org.firstinspires.ftc.masters.CSCons.transferPush;
import static org.firstinspires.ftc.masters.CSCons.transferUp;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.masters.CSCons;

public class Transfer {

    private CSCons.TransferStatus currentTransferStatus;
    private static Transfer singleInstance= null;

    private final DigitalChannel frontBreakBeam;
    private final DigitalChannel backBreakBeam;

    private ElapsedTime elapsedTime = null;
    private HardwareMap hardwareMap;

    private final Servo transferServo;
    boolean pickupOverride = false;

    private Transfer(HardwareMap hardwareMap){
        currentTransferStatus = CSCons.TransferStatus.WAITING_FOR_PIXELS;
        this.hardwareMap = hardwareMap;
        transferServo = hardwareMap.servo.get("transfer");
        frontBreakBeam = hardwareMap.digitalChannel.get("breakBeam2");
        frontBreakBeam.setMode(DigitalChannel.Mode.INPUT);
        backBreakBeam = hardwareMap.digitalChannel.get("breakBeam1");
        backBreakBeam.setMode(DigitalChannel.Mode.INPUT);

    }

    public static Transfer getInstance(HardwareMap hardwareMap){
        if (singleInstance==null){
            singleInstance = new Transfer(hardwareMap);
        }
        return singleInstance;
    }

    public void update(Gamepad gamepad, CSCons.OuttakeState outtakeState){

        if ((has2Pixels() || pickupOverride) && outtakeState!= CSCons.OuttakeState.ReadyToDrop ) {

            if (currentTransferStatus == CSCons.TransferStatus.MOVE_ARM && elapsedTime != null && elapsedTime.milliseconds() > CSCons.TransferStatus.MOVE_ARM.getWaitTime()) {
                transferServo.setPosition(transferPush);
                currentTransferStatus = CSCons.TransferStatus.MOVE_OUTTAKE;
                elapsedTime = new ElapsedTime();
            }
            if (currentTransferStatus == CSCons.TransferStatus.MOVE_OUTTAKE && elapsedTime != null && elapsedTime.milliseconds() > CSCons.TransferStatus.MOVE_OUTTAKE.getWaitTime()) {
                currentTransferStatus = CSCons.TransferStatus.CLOSE_FINGERS;
                elapsedTime = new ElapsedTime();
            }
            if (currentTransferStatus == CSCons.TransferStatus.CLOSE_FINGERS && elapsedTime.milliseconds() > CSCons.TransferStatus.CLOSE_FINGERS.getWaitTime()) {
                transferServo.setPosition(transferUp);
                elapsedTime = null;
                gamepad.rumble(3000);
                currentTransferStatus= CSCons.TransferStatus.DONE;
                pickupOverride = false;
            }

        } else{
            currentTransferStatus= CSCons.TransferStatus.WAITING_FOR_PIXELS;
            transferServo.setPosition(transferUp);
            pickupOverride= false;
        }


    }

    public CSCons.TransferStatus getCurrentTransferStatus(){
        return currentTransferStatus;
    }

    public void setCurrentTransferStatus(CSCons.TransferStatus transferStatus) {
        if (this.currentTransferStatus == CSCons.TransferStatus.WAITING_FOR_PIXELS && transferStatus == CSCons.TransferStatus.MOVE_ARM){
            if (elapsedTime ==null){
                elapsedTime = new ElapsedTime();
            }
            transferServo.setPosition(transferUp);
        }

        this.currentTransferStatus = transferStatus;
    }

    public boolean has2Pixels(){
        return !frontBreakBeam.getState() && !backBreakBeam.getState();
    }

    public void setPickupOverride(boolean transferOverride) {
        this.pickupOverride = transferOverride;
    }

    public void setTransferServoUp(){
        this.transferServo.setPosition(transferUp);
    }
}
