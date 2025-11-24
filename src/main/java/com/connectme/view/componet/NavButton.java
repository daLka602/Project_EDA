package com.connectme.view.componet;

import org.jdesktop.animation.timing.Animator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NavButton extends JButton {

    private Animator animator;
    private int targetSize;
    private float animatSize;
    private Point pressedPoint;
    private float alpha;
    private Color effectColor = new Color(255, 255, 255);

    public NavButton(String text, boolean isActive) {
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setText(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setPreferredSize(new Dimension(180, 46));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (isActive) {
            setBackground(new Color(73, 80, 249));
            setForeground(Color.WHITE);
        } else {
            setBackground(Color.WHITE);
            setForeground(new Color(9, 9, 30));
        }
    }

   @Override
    protected void paintComponent(Graphics grphcs) {
        int width = getWidth();
        int height = getHeight();
        int cornerRadius = 20;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        if (pressedPoint != null) {
            g2.setColor(effectColor);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            g2.fillOval((int) (pressedPoint.x - animatSize / 2), (int) (pressedPoint.y - animatSize / 2), (int) animatSize, (int) animatSize);
        }
        g2.dispose();
        grphcs.drawImage(img, 0, 0, null);
        super.paintComponent(grphcs);
    }
}
