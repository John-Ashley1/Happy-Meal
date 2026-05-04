package com.ror.gameutil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AboutWindow extends JFrame {

    public AboutWindow() {
        setTitle("About Happy Meal");
        setSize(750, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.BLACK);

        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10,10,10,10));

        buildUI();

        setVisible(true);
    }

    private void buildUI() {

        JLabel title = new JLabel("HAPPY MEAL TOURNAMENT", SwingConstants.CENTER);
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setBorder(new LineBorder(Color.WHITE, 2));

        add(title, BorderLayout.NORTH);

        JTextArea description = new JTextArea(
                "GAME DESCRIPTION\n\n" +
                        "Welcome to Happy Meal Tournament!\n\n" +
                        "A Turn-Based 1v1 Battle Game where strategy, timing,\n" +
                        "and skill management decide the winner.\n\n" +
                        "GAME MODES:\n" +
                        "• PvP (Best of 3)\n" +
                        "• PvE (vs AI)\n" +
                        "• Arcade Survival Mode\n\n" +
                        "OBJECTIVE:\n" +
                        "Defeat your opponent and become the Champion of Joy Arena!"
        );

        description.setEditable(false);
        description.setBackground(Color.BLACK);
        description.setForeground(Color.WHITE);
        description.setFont(new Font("Monospaced", Font.PLAIN, 14));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(new EmptyBorder(10,10,10,10));

        JScrollPane scroll = new JScrollPane(description);
        scroll.setBorder(new LineBorder(Color.WHITE, 2));

        add(scroll, BorderLayout.CENTER);

        JPanel devPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        devPanel.setBackground(Color.BLACK);
        devPanel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.WHITE, 2),
                "DEVELOPERS TEAM",
                0, 0,
                new Font("Monospaced", Font.BOLD, 14),
                Color.YELLOW
        ));

        String[] names = {
                "Mark Clent Zozobrado", "Ted Luis Managbanag",
                "Danielle Ben Ibale", "John Ashley Loquillano",
                "Vince Miguel Llanos"
        };

        String[] images = {
                "/images/characters/mark.jpg",
                "/images/characters/ted.jpg",
                "/images/characters/den.jpg",
                "/images/characters/ashley.jpg",
                "/images/characters/vince.jpg",
        };

        for (int i = 0; i < images.length; i++) {

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(20, 20, 20));
            card.setBorder(new LineBorder(Color.WHITE, 1));

            ImageIcon icon;
            try {
                icon = new ImageIcon(getClass().getResource(images[i]));
            } catch (Exception e) {
                icon = new ImageIcon();
                System.out.println("Image not found: " + images[i]);
            }

            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);

            JLabel pic = new JLabel(new ImageIcon(img));
            pic.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel name = new JLabel(names[i], SwingConstants.CENTER);
            name.setForeground(Color.WHITE);
            name.setFont(new Font("Monospaced", Font.BOLD, 12));

            card.add(pic, BorderLayout.CENTER);
            card.add(name, BorderLayout.SOUTH);

            devPanel.add(card);
        }

        JButton closeBtn = new JButton("CLOSE");

        closeBtn.setFocusPainted(false);
        closeBtn.setBackground(Color.BLACK);
        closeBtn.setForeground(Color.YELLOW);
        closeBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        closeBtn.setBorder(new LineBorder(Color.YELLOW, 2));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setOpaque(true);

        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(Color.YELLOW);
                closeBtn.setForeground(Color.BLACK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(Color.BLACK);
                closeBtn.setForeground(Color.YELLOW);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(Color.GRAY);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                closeBtn.setBackground(Color.YELLOW);
                closeBtn.setForeground(Color.BLACK);
            }
        });

        closeBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(devPanel, BorderLayout.CENTER);
        bottomPanel.add(closeBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AboutWindow::new);
    }
}