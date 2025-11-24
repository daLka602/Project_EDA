package com.connectme.view.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconUtils {
    public static ImageIcon colorizeIcon(ImageIcon icon, Color color) {
        Image image = icon.getImage();
        BufferedImage buffered = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(image, 0, 0, null);

        // Aplicar cor
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.setColor(color);
        g2d.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());

        g2d.dispose();
        return new ImageIcon(buffered);
    }
}
