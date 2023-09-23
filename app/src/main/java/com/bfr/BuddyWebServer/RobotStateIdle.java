package com.bfr.BuddyWebServer;

import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddysdk.BuddySDK;

import java.util.Random;

class RobotStateIdle extends RobotState {
    public RobotStateIdle(Robot robot) {
        super(robot);
    }

    @Override
    public void enter() {
        System.out.println("Entering RobotStateIdle");
    }

    @Override
    public void update() {
        System.out.println("Updating RobotStateIdle");
    }

    @Override
    public void exit() {
        System.out.println("Exiting RobotStateIdle");
    }

    @Override
    public void onTouchDetected(String id) {
        BuddySDK.UI.setFacialExpression(FacialExpression.ANGRY);
        Random random = new Random();
        robot.setHeadPitchYaw(random.nextInt(40)-10, random.nextInt(40)-20);
        robot.speak(getRandomString(
                "Touches moi encore la tête et je t'explose la gueule ! pignouf!",
                "Pas la tête ! T'as pas pris ta claque du matin ou quoi ?",
                "Non mais ho ! Est ce que je te tripote ta tête de pignouf moi ?"
        ));
    }
}