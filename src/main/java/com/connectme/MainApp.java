package com.connectme;

import javax.swing.SwingUtilities;

import com.connectme.controller.UserController;
import com.connectme.view.DashboardScreen;
import com.formdev.flatlaf.FlatLightLaf;
import com.connectme.view.LoginScreen;

public class MainApp {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UserController userController =  new UserController();

        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
            //new DashboardScreen(userController).setVisible(true);
        });
    }
}

