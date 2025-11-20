package com.connectme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.connectme.config.DbConnection;
import com.connectme.controller.UserController;
import com.formdev.flatlaf.FlatLightLaf;
import com.connectme.view.LoginScreen;

import java.util.logging.Logger;

public class MainApp {

    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        // Configurar look and feel
        setupLookAndFeel();
        
        // Configurar shutdown hook
        setupShutdownHook();
        
        // Inicializar aplicação na EDT
        SwingUtilities.invokeLater(MainApp::initializeApplication);
    }

    private static void setupLookAndFeel() {
        try {
            // Usar FlatLaf
            FlatLightLaf.setup();
            logger.info("FlatLaf tema claro configurado");
        } catch (Exception e) {
            logger.warning("FlatLaf não disponível: " + e.getMessage());
            try {
                // Método CORRETO para pegar o look and feel do sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Ignorar se não conseguir configurar
                logger.warning("Não foi possível configurar look and feel: " + ex.getMessage());
            }
        }
    }

    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Encerrando aplicação ConnectMe...");
            try {
                DbConnection.closeDataSource();
                logger.info("Recursos liberados com sucesso");
            } catch (Exception e) {
                logger.warning("Erro ao fechar recursos: " + e.getMessage());
            }
        }));
    }

    private static void initializeApplication() {
        try {
            logger.info("=== Iniciando ConnectMe ===");
            
            // Testar conexão com banco
            if (!testDatabaseConnection()) {
                return; // Sai se não conseguir conectar
            }
            
            // Criar controller principal
            UserController userController = new UserController();
            
            // Mostrar tela de login
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
            
            logger.info("ConnectMe inicializado com sucesso");
            
        } catch (Exception e) {
            String errorMsg = "Erro crítico ao iniciar aplicação: " + e.getMessage();
            logger.severe(errorMsg);
            showErrorDialog(errorMsg);
        }
    }

    private static boolean testDatabaseConnection() {
        try {
            // Teste rápido de conexão
            DbConnection.getConnection().close();
            logger.info("✓ Conexão com banco de dados estabelecida");
            return true;
            
        } catch (Exception e) {
            String errorMsg = 
                "Não foi possível conectar ao banco de dados.\n\n" +
                "Por favor, verifique:\n" +
                "• Servidor MySQL está rodando\n" + 
                "• Credenciais em application.properties\n" +
                "• Database 'connectme_db' existe\n\n" +
                "Erro técnico: " + e.getMessage();
                
            logger.severe("✗ Falha na conexão com banco: " + e.getMessage());
            showErrorDialog(errorMsg);
            return false;
        }
    }

    private static void showErrorDialog(String message) {
        // Usar invokeLater para garantir que está na EDT
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null, 
                message, 
                "ConnectMe - Erro de Inicialização", 
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
}