package com.bfr.BuddyWebServer;



import android.util.Log;

import java.util.Random;

class RobotState implements State, RobotEventCatcher {
    protected Robot robot;

    public RobotState(Robot robot) {
        this.robot = robot;
    }
    @Override
    public void enter() {
        Log.i("State", "Entering RobotState");
    }

    @Override
    public void update() {
        Log.i("State", "Updating RobotState");
    }

    @Override
    public void exit() {
        Log.i("State", "Exiting RobotState");
    }

    @Override
    public void onTouchDetected(String id) {

    }

    public static String getRandomString(String... strings) {
        if (strings.length == 0) {
            return null; // Aucune chaîne à choisir
        }

        Random random = new Random();
        int randomIndex = random.nextInt(strings.length);

        return strings[randomIndex];
    }
}