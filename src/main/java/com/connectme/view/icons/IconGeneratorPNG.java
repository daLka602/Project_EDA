package com.connectme.view.icons;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class IconGeneratorPNG {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color DARK_COLOR = new Color(52, 73, 94);

    public static void main(String[] args) {
        // Criar diretório para ícones
        File iconsDir = new File("icons");
        if (!iconsDir.exists()) {
            iconsDir.mkdirs();
        }

        // Gerar todos os ícones
        generateAllIcons(32); // Tamanho 32x32 pixels

        System.out.println("Ícones gerados com sucesso na pasta 'icons'!");
    }

    public static void generateAllIcons(int size) {
        generateIcon(size, "connectme", PRIMARY_COLOR);
        generateIcon(size, "agenda", DARK_COLOR);
        generateIcon(size, "contacts", PRIMARY_COLOR);
        generateIcon(size, "admin", WARNING_COLOR);
        generateIcon(size, "logout", DANGER_COLOR);
        generateIcon(size, "search", DARK_COLOR);
        generateIcon(size, "add", SUCCESS_COLOR);
        generateIcon(size, "import", PRIMARY_COLOR);
        generateIcon(size, "all", DARK_COLOR);
        generateIcon(size, "clients", SUCCESS_COLOR);
        generateIcon(size, "partners", WARNING_COLOR);
        generateIcon(size, "suppliers", PRIMARY_COLOR);
        generateIcon(size, "user", DARK_COLOR);
    }

    public static void generateIcon(int size, String type, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Configurações de qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fundo transparente
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, size, size);

        switch (type.toLowerCase()) {
            case "connectme":
                drawConnectMeIcon(g2d, size, color);
                break;
            case "agenda":
                drawAgendaIcon(g2d, size, color);
                break;
            case "contacts":
                drawContactsIcon(g2d, size, color);
                break;
            case "admin":
                drawAdminIcon(g2d, size, color);
                break;
            case "logout":
                drawLogoutIcon(g2d, size, color);
                break;
            case "search":
                drawSearchIcon(g2d, size, color);
                break;
            case "add":
                drawAddIcon(g2d, size, color);
                break;
            case "import":
                drawImportIcon(g2d, size, color);
                break;
            case "all":
                drawAllContactsIcon(g2d, size, color);
                break;
            case "clients":
                drawClientsIcon(g2d, size, color);
                break;
            case "partners":
                drawPartnersIcon(g2d, size, color);
                break;
            case "suppliers":
                drawSuppliersIcon(g2d, size, color);
                break;
            case "user":
                drawUserIcon(g2d, size, color);
                break;
        }

        g2d.dispose();

        // Salvar como PNG
        try {
            File output = new File("icons/" + type + ".png");
            ImageIO.write(image, "PNG", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawConnectMeIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);
        g2d.fillOval(2, 2, size-4, size-4);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size-12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "C";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);
    }

    private static void drawAgendaIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);
        g2d.fillRoundRect(4, 4, size-8, size-8, 6, 6);

        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            int y = 8 + i * 6;
            g2d.drawLine(6, y, size-6, y);
        }
    }

    private static void drawContactsIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Cabeça
        g2d.fillOval(size/4, 4, size/2, size/2);

        // Corpo
        int[] xPoints = {4, size-4, size-8, 8};
        int[] yPoints = {size/2+4, size/2+4, size-4, size-4};
        g2d.fillPolygon(xPoints, yPoints, 4);
    }

    private static void drawAdminIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Engrenagem
        int center = size / 2;
        int radius = size / 3;

        // Desenhar engrenagem
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI / 4 * i;
            int x1 = (int) (center + radius * Math.cos(angle));
            int y1 = (int) (center + radius * Math.sin(angle));
            int x2 = (int) (center + (radius + 4) * Math.cos(angle));
            int y2 = (int) (center + (radius + 4) * Math.sin(angle));

            g2d.fillRect(x1 - 1, y1 - 1, 3, 3);
            g2d.drawLine(center, center, x2, y2);
        }

        // Círculo central
        g2d.setColor(Color.WHITE);
        g2d.fillOval(center-4, center-4, 8, 8);
    }

    private static void drawLogoutIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Porta
        g2d.fillRect(6, 4, size-12, size-8);

        // Seta de saída
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(
                new int[]{size-8, size-8, size-4},
                new int[]{size/2-4, size/2+4, size/2},
                3
        );

        // Maçaneta
        g2d.fillOval(10, size/2-2, 4, 4);
    }

    private static void drawSearchIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Círculo da lupa
        g2d.drawOval(4, 4, size-12, size-12);

        // Cabo da lupa
        g2d.drawLine(size-8, size-8, size-4, size-4);
    }

    private static void drawAddIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Círculo de fundo
        g2d.fillOval(2, 2, size-4, size-4);

        // Sinal de +
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(size/2, 6, size/2, size-6);
        g2d.drawLine(6, size/2, size-6, size/2);
    }

    private static void drawImportIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Seta para baixo
        g2d.fillPolygon(
                new int[]{size/2, size/2-6, size/2+6},
                new int[]{8, 20, 20},
                3
        );

        // Linha
        g2d.fillRect(size/2-1, 20, 2, size-16);

        // Caixa
        g2d.fillRect(6, size-12, size-12, 6);
    }

    private static void drawAllContactsIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Múltiplas pessoas
        for (int i = 0; i < 3; i++) {
            int x = 4 + i * 8;
            int headSize = 6;

            // Cabeça
            g2d.fillOval(x, 6, headSize, headSize);

            // Corpo
            g2d.fillRect(x-1, 12, headSize+2, 8);
        }
    }

    private static void drawClientsIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Pessoa com símbolo de cliente ($)
        g2d.fillOval(10, 6, 12, 12);
        g2d.fillRect(8, 18, 16, 10);

        // Símbolo de dinheiro
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("$", 14, 16);
    }

    private static void drawPartnersIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Duas pessoas se cumprimentando
        // Pessoa 1
        g2d.fillOval(6, 6, 8, 8);
        g2d.fillRect(4, 14, 12, 8);

        // Pessoa 2
        g2d.fillOval(18, 6, 8, 8);
        g2d.fillRect(16, 14, 12, 8);

        // Aperto de mãos
        g2d.drawLine(16, 18, 20, 18);
    }

    private static void drawSuppliersIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Caminhão de entrega
        g2d.fillRect(6, 10, size-12, 8);
        g2d.fillRect(16, 6, 10, 4);

        // Rodas
        g2d.fillOval(8, 18, 6, 6);
        g2d.fillOval(20, 18, 6, 6);
    }

    private static void drawUserIcon(Graphics2D g2d, int size, Color color) {
        g2d.setColor(color);

        // Cabeça
        g2d.fillOval(8, 4, size-16, size-16);

        // Corpo
        g2d.fillRect(10, size/2+4, size-20, size/2-8);
    }
}