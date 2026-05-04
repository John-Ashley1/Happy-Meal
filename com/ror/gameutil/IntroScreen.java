package com.ror.gameutil;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.ror.engine.SoundManager;

public class IntroScreen extends JFrame {

    private JPanel mainPanel;
    private JButton startButton;
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
        mainPanel.setBorder(new EmptyBorder(100, 50, 100, 50));

        // --- Logo Implementation ---
        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            java.net.URL logoUrl = getClass().getResource("/images/map/HappyMeal.png");
            if (logoUrl != null) {
                ImageIcon rawIcon = new ImageIcon(logoUrl);
                Image scaledImg = rawIcon.getImage().getScaledInstance(600, -1, Image.SCALE_SMOOTH);
                titleLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                titleLabel.setText("HAPPY MEAL");
                titleLabel.setFont(new Font("Impact", Font.BOLD, 80));
                titleLabel.setForeground(Color.ORANGE);
            }
        } catch (Exception ex) {
            System.out.println("DEBUG: Could not load HappyMeal.png from images/map/");
        }

        // --- NEW: Image-based Start Button with Hover Effect ---
        startButton = new JButton();
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Strip away default button UI (borders, background fills, etc.)
        startButton.setFocusPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);

        try {
            java.net.URL startUrl = getClass().getResource("/images/map/START.png");
            if (startUrl != null) {
                ImageIcon rawStart = new ImageIcon(startUrl);
                // Scale the start button to a good size (250px wide)
                Image scaledStart = rawStart.getImage().getScaledInstance(250, -1, Image.SCALE_SMOOTH);
                ImageIcon defaultIcon = new ImageIcon(scaledStart);

                startButton.setIcon(defaultIcon);

                // --- MAGIC: Generate the "Light Up" Hover Icon ---
                BufferedImage hoverImg = new BufferedImage(defaultIcon.getIconWidth(), defaultIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = hoverImg.createGraphics();
                g2d.drawImage(defaultIcon.getImage(), 0, 0, null);

                // SrcAtop means we only draw the white glow over pixels that actually exist (ignoring transparency)
                g2d.setComposite(AlphaComposite.SrcAtop);
                g2d.setColor(new Color(255, 255, 255, 70)); // 70 alpha white glow
                g2d.fillRect(0, 0, hoverImg.getWidth(), hoverImg.getHeight());
                g2d.dispose();

                // Tell Swing to swap to this glowing image when the mouse hovers!
                startButton.setRolloverIcon(new ImageIcon(hoverImg));
                startButton.setPressedIcon(defaultIcon); // Returns to normal size/color when clicked

            } else {
                // Failsafe if image is missing
                startButton.setText("START");
                startButton.setFont(new Font("Arial", Font.BOLD, 24));
                startButton.setBackground(new Color(220, 20, 60));
                startButton.setForeground(Color.WHITE);
                startButton.setContentAreaFilled(true);
            }
        } catch (Exception ex) {
            System.out.println("DEBUG: Could not load START.png from images/map/");
        }

        startButton.addActionListener(e -> {
            new HappyMealGame(sound).setVisible(true);
            dispose();
        });

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalGlue());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IntroScreen().setVisible(true));
    }
}