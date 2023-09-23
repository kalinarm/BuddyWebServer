package com.bfr.BuddyWebServer;

interface State {
    void enter(); // Méthode appelée lors de l'entrée dans cet état
    void update(); // Méthode appelée pour mettre à jour cet état
    void exit(); // Méthode appelée lors de la sortie de cet état
}

public class StateManager {
    protected State currentState;

    public void setState(State newState) {
        // Quitter l'ancien état
        if (currentState != null) {
            currentState.exit();
        }

        // Entrer dans le nouvel état
        newState.enter();
        currentState = newState;
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }
}

