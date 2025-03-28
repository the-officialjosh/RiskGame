package org.soen6441.risk_game.game_engine.controller.user_input;

import java.util.Scanner;

public class UserInputScanner {
    private Scanner d_scanner;
    private UserInputScanner() {
        d_scanner = new Scanner(System.in);
    }

    public Scanner getScanner() {
        return d_scanner;
    }

    private static UserInputScanner d_userInputScanner;
    public static UserInputScanner getInstance() {
        if (d_userInputScanner == null)
            d_userInputScanner = new UserInputScanner();
        return d_userInputScanner;
    }
}
