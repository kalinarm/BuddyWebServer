package com.bfr.BuddyWebServer;


import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

import java.util.Random;


interface  RobotEventCatcher
{
    void onTouchDetected(String id);
}

public class Robot {

    final String  TAG = "BuddyWebServer";

    private int default_head_speed = 30;
    private int default_move_speed = 30;
    private int default_rotate_speed = 90;

    boolean touchHeadTop = false;

    RobotEventCatcher callback;

    IUsbCommadRsp defaultCallback = new IUsbCommadRsp() {
        @Override
        public void onSuccess(String s) throws RemoteException {
            Log.i(TAG, "Command success : "+ s);
        }

        @Override
        public void onFailed(String s) throws RemoteException {
            Log.i(TAG, "Command failed : "+ s);
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    public void enable() {
        EnableYesMotor(1);
        EnableNoMotor(1);
        enableWheels();

        EnableSensor();
    }
    public void disable() {
        disableWheels();
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
                        BuddySDK.UI.setFacialExpression(FacialExpression.NEUTRAL);
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

    public void move(float distance, float sens) {
        BuddySDK.USB.moveBuddy(default_move_speed * sens, distance * 0.01f, defaultCallback);
    }
    public void rotate(float angle) {
        BuddySDK.USB.rotateBuddy(default_rotate_speed, angle, defaultCallback);

    }
    public void stopMove() {
        BuddySDK.USB.emergencyStopMotors(defaultCallback);
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

    private void EnableSensor() {
        Log.i("Sensor", "try to enable sensors");
        BuddySDK.USB.enableSensorModule(true, new IUsbCommadRsp.Stub() {//called to enable sensors


            @Override
            public void onSuccess(String s) throws RemoteException {//in case of success
                Log.i(TAG, "Enabled Sensors");//show if it achieved

                //BuddyHeadSensors Launch
                BuddySDK.Sensors.HeadTouchSensors().Top().isTouched();//Top head sensor
                BuddySDK.Sensors.HeadTouchSensors().Left().isTouched();//Left sensor touched
                BuddySDK.Sensors.HeadTouchSensors().Right().isTouched();//Right sensor touched

                Thread SensorsTh = new Thread() {
                    public void run() {//function of the thread
                        while (true) {//do indefinitly

                            //Head Sensors touch working
                            boolean top = BuddySDK.Sensors.HeadTouchSensors().Top().isTouched();//boolean registered
                            boolean left = BuddySDK.Sensors.HeadTouchSensors().Left().isTouched();//boolean registered
                            boolean right = BuddySDK.Sensors.HeadTouchSensors().Right().isTouched();//boolean registered

                            boolean headTouch = top | left | right;

                            if (headTouch && !touchHeadTop) {
                                if (callback != null) callback.onTouchDetected("head");
                                Log.i("Sensor", "Head touched");

                            }
                            touchHeadTop = headTouch;
                        }
                    }
                };
                SensorsTh.start();//start the thread
            }

            @Override
            public void onFailed(String s) throws RemoteException {//in case of failure
                Log.i(TAG, "Fail to Enable sensors :"+s);//if fail show why
            }
        });
    }

    void enableWheels() {
        BuddySDK.USB.enableWheels(1, 1, new IUsbCommadRsp.Stub() {   //function which enable the wheels

            @Override
            public void onSuccess(String s) throws RemoteException {
                //in Case of sucess of enabeling the wheels we decide to show some text at screen
                Log.i(TAG, "wheels are enabled");
            }

            @Override
            public void onFailed(String s) throws RemoteException {
                //In case of failure we want to be inform of the reason of the failure
                Log.i(TAG, "Wheels enable failed :" + s);
            }
        });
    }

    void disableWheels() {
        BuddySDK.USB.enableWheels(0, 0, new IUsbCommadRsp.Stub() {   //function which enable the wheels

            @Override
            public void onSuccess(String s) throws RemoteException {
                //in Case of sucess of enabeling the wheels we decide to show some text at screen
                Log.i(TAG, "wheels are disabled");
            }

            @Override
            public void onFailed(String s) throws RemoteException {
                //In case of failure we want to be inform of the reason of the failure
                Log.i(TAG, "Wheels disable failed :" + s);
            }
        });
    }


    void log(String text) {
        Log.i(TAG, "ROBOT_LOG : "+ text);
    }
}
