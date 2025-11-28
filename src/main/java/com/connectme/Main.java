package com.connectme;

import com.connectme.config.DbConnection;
import com.connectme.controller.AuthController;
import com.connectme.view.LoginScreen;
import com.connectme.view.MainFrame;
import com.connectme.model.entities.User;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("Inicializando ConnectMe...");

                DbConnection.initialize();
                logger.info("Banco de dados inicializado");

                // Configurar Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                AuthController authController = new AuthController();

                // Criar e mostrar LoginScreen
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setOnLoginSuccess(() -> {
                    User authenticatedUser = loginScreen.getAuthenticatedUser();
                    if (authenticatedUser != null) {
                        logger.info("Usuário autenticado: " + authenticatedUser.getUsername());
                        MainFrame mainFrame = new MainFrame(authenticatedUser, authController);
                        mainFrame.setVisible(true);
                    }
                });
                loginScreen.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                logger.severe("Erro ao inicializar aplicação: " + e.getMessage());
                JOptionPane.showMessageDialog(
                        null,
                        "Erro ao inicializar aplicação: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}