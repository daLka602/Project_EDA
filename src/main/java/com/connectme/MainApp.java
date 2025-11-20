package com.connectme;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import com.connectme.view.LoginScreen;

public class MainApp {

    public static void main(String[] args) {

        // Define tema moderno
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}

