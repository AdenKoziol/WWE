package org.example;
import java.util.Scanner;

public class MainContext {
    private State currentState;
    private static final Scanner scanner = new Scanner(System.in);

    public MainContext() {
        // The application boots into the Main Menu state
        this.currentState = new MainMenuState(); 
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public void run() {
        // The core game loop runs as long as there is an active state
        while (currentState != null) {
            currentState.handleInput(scanner, this);
        }
    }

    public static void main(String[] args) {
        MainContext app = new MainContext();
        app.run();
    }
}
