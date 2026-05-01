package org.example;
import java.util.Scanner;

public interface State {
    void handleInput(Scanner scanner, MainContext context);
}
