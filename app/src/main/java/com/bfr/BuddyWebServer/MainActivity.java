package com.bfr.BuddyWebServer;import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;


import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.BuddyWebServer.R;



public class MainActivity extends BuddyActivity implements WebhookCallback {

    final String  TAG = "BuddyWebServer";

    //initialize the different textView variable
    TextView mPositivityValue;
    EditText speed_text;  //initialization of Edit text to enter the values of speed, text and pitch
    EditText volume_text;
    EditText pitch_text;
    EditText to_say;
    Button setFR, setENG;

    private WebhookServer server;
    private Handler mainHandler;
    private Robot robot;
    private RobotBehaviour robotAi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the different button to launch the different speech parameters of Buddy
        findViewById(R.id.startServer).setOnClickListener(v -> onButtonStart());
        findViewById(R.id.stopServer).setOnClickListener(v -> onButtonStop());
        volume_text= findViewById(R.id.number);
        to_say = findViewById(R.id.editTextToSay);

        robot = new Robot();

        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    @Override
    public void onWebhookReceived(String route, String data) {
        mainHandler.post(() -> {
            log("webhook " + route + " " +  data);
            switch (route) {
                case "/speak" :
                    robot.speak(data);
                    break;
                case "/face" :
                    onFaceReceived(data);
                    break;
                case "/head_pitch" :
                    robot.setHeadPitch(parseInt(data, 0));
                    break;
                case "/head_yaw" :
                    robot.setHeadYaw(parseInt(data, 0));
                    break;
                case "/head_pitchyaw" :
                    onHeadPitchYawReceived(data);
                    break;
                case "/forward" :
                    robot.move(parseInt(data, 0), -1);
                    break;
                case "/backward" :
                    robot.move(parseInt(data, 0), 1);
                    break;
                case "/rotate" :
                    robot.rotate(parseInt(data, 0));
                    break;
                case "/stop" :
                    robot.stopMove();
                    break;
                default:
                    log("unknown command : " + route + " " + data);
            }
        });
    }

    private int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void onFaceReceived(String data) {
        int integer = parseInt(data, 0);
        robot.setFace(integer);
    }

    private void onHeadPitchYawReceived(String data) {
        String[] parts = data.split(",");
        if (parts.length != 2) {
            log("need 2 args separate by ,");
            return;
        }
        int pitch = parseInt(parts[0], 0);
        int yaw = parseInt(parts[1], 0);
        robot.setHeadPitchYaw(pitch, yaw);
    }

    private void onButtonStop() {
        stopServer();
    }

    private void onButtonStart() {
        createServer();
    }

    private void log(String str) {

        to_say.setText(str);
        Log.i(TAG, "LOG : "+ str);
    }

    private void createServer() {
        // Create and start the server on a specific port (e.g., 8080)
        server = new WebhookServer(3457, this::onWebhookReceived);
        try {
            server.start();
            to_say.setText("server started");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            to_say.setText(e.getMessage());
        }

        robot.enable();
    }

    private void stopServer() {
        robot.disable();

        // Stop the server when your app is destroyed
        if (server != null) {
            server.stop();
        }
        to_say.setText("server stopped");
    }

    @Override
    //This function is called when the SDK is ready
    public void onSDKReady() {
        Log.i(TAG, "Buddy SDK ready");
        createServer();
        robot.onSdkReady();
        robotAi = new RobotBehaviour(robot);
        robotAi.init();
    }

    // Catches SPEAKING event.
    // Writes what has been spoken.
    @Override
    public void onEvent(EventItem iEvent) {

    }
}


