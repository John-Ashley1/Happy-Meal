package com.ror.gameutil;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.ror.engine.SoundManager;

public class IntroScreen extends JFrame {

    private JPanel mainPanel;
    private JButton startButton;
    private JButton aboutButton;
    private JButton exitButton;
    private SoundManager sound;

    public IntroScreen() {
        sound = new SoundManager();
        sound.setFile(SoundManager.BGM_MAIN);
        sound.loop();
        initUI();
    }

    public IntroScreen(SoundManager sound) {
        this.sound = sound;
        initUI();
    }

    private void initUI() {

        setTitle("Happy Meal");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel() {
            private Image[] backgrounds;
            private int currentBg = 0;

            {
                backgrounds = new Image[]{
                        new ImageIcon(getClass().getResource("/images/BG/bg_4.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_f_15.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_m_1.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_r_15.png")).getImage(),
                        new ImageIcon(getClass().getResource("/images/BG/bg_w_4.png")).getImage()
                };

                new Timer(2000, e -> {
                    currentBg = (currentBg + 1) % backgrounds.length;
                    repaint();
                }).start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgrounds[currentBg], 0, 0, getWidth(), getHeight(), this);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        setContentPane(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // --- Tightened borders to maximize screen space ---
        mainPanel.setBorder(new EmptyBorder(0, 50, 10, 50));

        // --- Logo Implementation ---
        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            java.net.URL logoUrl = getClass().getResource("/images/map/HappyMeal.png");
            if (logoUrl != null) {
                ImageIcon rawIcon = new ImageIcon(logoUrl);
                // Scaled down from 600 to 500 to save vertical height
                Image scaledImg = rawIcon.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
                titleLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                titleLabel.setText("HAPPY MEAL");
                titleLabel.setFont(new Font("Impact", Font.BOLD, 80));
                titleLabel.setForeground(Color.ORANGE);
            }
        } catch (Exception ex) {
            System.out.println("DEBUG: Could not load HappyMeal.png from images/map/");
        }

        // --- Create Our Buttons (Scaled to 220px to fit all three) ---
        startButton = createImageButton("/images/map/START.png", 220, "START");
        aboutButton = createImageButton("/images/map/ABOUTUS.png", 220, "ABOUT US");
        exitButton  = createImageButton("/images/map/EXIT.png", 220, "EXIT");

        // --- Assign Button Actions ---
        startButton.addActionListener(e -> {
            new HappyMealGame(sound).setVisible(true);
            dispose();
        });

        aboutButton.addActionListener(e -> {
            new AboutWindow().setVisible(true);
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        // --- NEW STACKING ORDER (Fixed for 800x600 resolution) ---

        // Puts the logo near the top sky instead of the middle of the screen
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(titleLabel);

        // Gap between Logo and Start Button
        mainPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        mainPanel.add(startButton);

        // Tight 5px gap between buttons
        mainPanel.add(Box.createRigidArea(new Dimension(0, -50)));
        mainPanel.add(aboutButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, -50)));
        mainPanel.add(exitButton);

        // Pushes everything up from the bottom
        mainPanel.add(Box.createVerticalGlue());
    }

    /**
     * HELPER METHOD: Automatically builds a button from an image, strips default Swing UI,
     * and generates a glowing hover effect!
     */
    private JButton createImageButton(String imagePath, int targetWidth, String fallbackText) {
        JButton btn = new JButton();
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);

        try {
            java.net.URL imgUrl = getClass().getResource(imagePath);
            if (imgUrl != null) {
                ImageIcon rawIcon = new ImageIcon(imgUrl);
                Image scaledImg = rawIcon.getImage().getScaledInstance(targetWidth, -1, Image.SCALE_SMOOTH);
                ImageIcon defaultIcon = new ImageIcon(scaledImg);

                btn.setIcon(defaultIcon);

                BufferedImage hoverImg = new BufferedImage(defaultIcon.getIconWidth(), defaultIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = hoverImg.createGraphics();
                g2d.drawImage(defaultIcon.getImage(), 0, 0, null);

                g2d.setComposite(AlphaComposite.SrcAtop);
                g2d.setColor(new Color(255, 255, 255, 70));
                g2d.fillRect(0, 0, hoverImg.getWidth(), hoverImg.getHeight());
                g2d.dispose();

                btn.setRolloverIcon(new ImageIcon(hoverImg));
                btn.setPressedIcon(defaultIcon);

                // Forces the layout manager to respect the image size, preventing stretching bugs
                btn.setMaximumSize(new Dimension(defaultIcon.getIconWidth(), defaultIcon.getIconHeight()));

            } else {
                btn.setText(fallbackText);
                btn.setFont(new Font("Arial", Font.BOLD, 24));
                btn.setBackground(new Color(220, 20, 60));
                btn.setForeground(Color.WHITE);
                btn.setContentAreaFilled(true);
            }
        } catch (Exception ex) {
            System.out.println("DEBUG: Could not load " + imagePath);
        }

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IntroScreen().setVisible(true));
    }
}