package com.bfr.BuddyWebServer;

import android.util.Log;

public class RobotBehaviour extends StateManager implements RobotEventCatcher {
    protected Robot robot;

    public RobotBehaviour(Robot robot) {

        this.robot = robot;
        robot.callback = this;
    }

    public void init() {
        setState(new RobotStateIdle(robot));
    }


    @Override
    public void onTouchDetected(String id) {
        Log.i("State", "State onTouchDetected");

        if (currentState == null) return;
        ((RobotState)currentState).onTouchDetected(id);
    }
}
