package com.bfr.BuddyWebServer;


import android.os.RemoteException;
import android.util.Log;

import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;


public class Robot {

    final String  TAG = "BuddyWebServer";

    private int default_head_speed = 30;

    public void enable() {
        EnableYesMotor(1);
        EnableNoMotor(1);
    }
    public void disable() {
        EnableYesMotor(0);
        EnableNoMotor(0);
    }

    public void onSdkReady() {
        BuddySDK.UI.setFacialExpression(FacialExpression.LISTENING);

        BuddySDK.Speech.loadReadSpeaker();
        BuddySDK.Speech.setSpeakerVoice("roxane");
    }

    public void speak(String text) {
        if(!BuddySDK.Speech.isReadyToSpeak())
        {
            log("not ready to say : " + text);
            return;

        }
        BuddySDK.Speech.startSpeaking(
                text,
                new ITTSCallback.Stub() {
                    @Override
                    public void onSuccess(String s) throws RemoteException {
                        Log.i(TAG, "Message received : "+ s);
                    }

                    @Override
                    public void onPause() throws RemoteException {
                    }

                    @Override
                    public void onResume() throws RemoteException {
                    }

                    @Override
                    public void onError(String s) throws RemoteException {
                        Log.i(TAG, "Message received : "+ s);

                    }
                });
    }

    public void setFace(int data) {
        FacialExpression[] values = FacialExpression.values();
        log("face " + values[data % values.length].toString());
        BuddySDK.UI.setFacialExpression(values[data % values.length]);
    }
    public void setHeadPitch(int angle) {
        Log.i("TAG", "set head pitch " + angle);
        BuddySDK.USB.buddySayYes(default_head_speed, angle, new IUsbCommadRsp.Stub() { //function with speed, angle and stub callback
            @Override
            public void onSuccess(String success) {
                Log.i("TAG", "move head pitch success");
            }
            @Override public void onFailed(String error) {
                Log.i("TAG", "move head pitch failed");
            }
        });
    }
    public void setHeadYaw(int angle) {
        Log.i("TAG", "set head yaw " + angle);
        BuddySDK.USB.buddySayNo(default_head_speed, angle, new IUsbCommadRsp.Stub() { //function with speed, angle and stub callback
            @Override
            public void onSuccess(String success) {
                Log.i("TAG", "move head yaw success");
            }
            @Override public void onFailed(String error) {
                Log.i("TAG", "move head yaw failed");
            }
        });
    }
    public void setHeadPitchYaw(int pitch, int yaw) {
        Log.i("TAG", "set head pitch yaw " + pitch + " " + yaw);
        setHeadPitch(pitch);
        setHeadYaw(yaw);
    }

    private void EnableNoMotor(int state){
        Log.i(TAG,"State : " + state);
        // The motor for "no" move is enable
        BuddySDK.USB.enableNoMove(state, new IUsbCommadRsp.Stub() {
            @Override
            //if the motor succeeded to be enabled,we display motor is enabled
            public void onSuccess(String success) throws RemoteException {
                Log.i("TAG", "Motor Enabled");
            }

            @Override
            //if the motor did not succeed to be enabled,we display motor failed to be enabled
            public void onFailed(String error) throws RemoteException {
                Log.i("Motor No", "No motor Enabled Failed");
            }
        });
    }

    private void EnableYesMotor(int state){
        Log.i(TAG,"State : " + state);
        // The motor for "no" move is enable
        BuddySDK.USB.enableYesMove(state, new IUsbCommadRsp.Stub() {
            @Override
            //if the motor succeeded to be enabled,we display motor is enabled
            public void onSuccess(String success) throws RemoteException {
                Log.i("TAG", "Motor Enabled");
            }

            @Override
            //if the motor did not succeed to be enabled,we display motor failed to be enabled
            public void onFailed(String error) throws RemoteException {
                Log.i("Motor No", "No motor Enabled Failed");
            }
        });
    }


    void log(String text) {
        Log.i(TAG, "ROBOT_LOG : "+ text);
    }
}
