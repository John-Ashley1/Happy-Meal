package com.ror.gameutil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AboutWindow extends JFrame {

    private Image background;

    public AboutWindow() {

        background = new ImageIcon(getClass().getResource("/images/BG/aboutbg.png")).getImage();

        setTitle("About Happy Meal");
        setSize(750, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }

                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        setContentPane(mainPanel);

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
        description.setOpaque(false);
        description.setForeground(Color.WHITE);
        description.setFont(new Font("Monospaced", Font.PLAIN, 14));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(description);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(new LineBorder(Color.WHITE, 2));

        add(scroll, BorderLayout.CENTER);

        JPanel devPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        devPanel.setOpaque(false);
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
                "/images/characters/vince.jpg"
        };

        for (int i = 0; i < images.length; i++) {

            JPanel card = new JPanel(new BorderLayout());
            card.setOpaque(false);
            card.setBorder(new LineBorder(Color.WHITE, 1));

            ImageIcon icon;
            try {
                icon = new ImageIcon(getClass().getResource(images[i]));
            } catch (Exception e) {
                icon = new ImageIcon();
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
        closeBtn.setForeground(Color.YELLOW);
        closeBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        closeBtn.setBorder(new LineBorder(Color.YELLOW, 2));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setOpaque(false);

        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(Color.BLACK);
                closeBtn.setBackground(Color.YELLOW);
                closeBtn.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(Color.YELLOW);
                closeBtn.setOpaque(false);
            }
        });

        closeBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(devPanel, BorderLayout.CENTER);
        bottomPanel.add(closeBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AboutWindow::new);
    }
}