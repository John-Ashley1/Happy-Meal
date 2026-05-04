package com.ror.gameutil;

import java.awt.*;
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
        mainPanel.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topBar.setOpaque(false);

        JButton aboutTopButton = new JButton("ABOUT");
        JButton exitButton = new JButton("EXIT");

        styleTopButton(aboutTopButton, new Color(255, 200, 0));
        styleTopButton(exitButton, new Color(180, 0, 0));

        aboutTopButton.addActionListener(e -> new AboutWindow());
        exitButton.addActionListener(e -> System.exit(0));

        topBar.add(aboutTopButton);
        topBar.add(exitButton);

        mainPanel.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        TitleText title = new TitleText();
        title.setPreferredSize(new Dimension(800, 200));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton = new JButton("START");

        startButton.setFont(new Font("Monospaced", Font.BOLD, 26));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.setPreferredSize(new Dimension(280, 65));
        startButton.setMaximumSize(new Dimension(280, 65));

        startButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                int w = c.getWidth();
                int h = c.getHeight();

                Color base = new Color(220, 20, 60);

                Color bg = base.darker().darker();
                Color glow = base;

                if (model.isRollover()) {
                    bg = base;
                    glow = base.brighter();
                }

                if (model.isPressed()) {
                    bg = base.darker();
                }

                g2.setColor(new Color(0, 0, 0, 130));
                g2.fillRoundRect(4, 5, w - 8, h - 8, 30, 30);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w - 8, h - 8, 30, 30);

                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, w - 8, h - 8, 30, 30);

                g2.setFont(b.getFont());
                FontMetrics fm = g2.getFontMetrics();

                String text = b.getText();

                int x = (w - fm.stringWidth(text)) / 2 - 2;
                int y = (h + fm.getAscent()) / 2 - 5;

                g2.setColor(new Color(0, 0, 0, 160));
                g2.drawString(text, x + 2, y + 2);

                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        });

        startButton.addActionListener(e -> {
            new HappyMealGame(sound).setVisible(true);
            dispose();
        });

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void styleTopButton(JButton button, Color baseColor) {

        button.setFont(new Font("Monospaced", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 40));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {

            @Override
            public void paint(Graphics g, JComponent c) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                int w = c.getWidth();
                int h = c.getHeight();

                Color bg = baseColor.darker().darker();
                Color glow = baseColor;

                if (model.isRollover()) {
                    bg = baseColor;
                    glow = baseColor.brighter();
                }

                if (model.isPressed()) {
                    bg = baseColor.darker();
                }

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(3, 4, w - 6, h - 6, 20, 20);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w - 6, h - 6, 20, 20);

                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, w - 6, h - 6, 20, 20);

                g2.setFont(b.getFont());
                FontMetrics fm = g2.getFontMetrics();

                String text = b.getText();
                int x = (w - fm.stringWidth(text)) / 2 - 2;
                int y = (h + fm.getAscent()) / 2 - 4;

                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        });
    }

    class TitleText extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            String text = "HAPPY MEAL";
            Font font = new Font("Impact", Font.BOLD, 100);
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2 + fm.getAscent() / 2;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(text, x + 6, y + 6);

            g2d.setPaint(new GradientPaint(
                    0, 0, Color.YELLOW,
                    0, getHeight(), new Color(255, 140, 0)
            ));
            g2d.drawString(text, x, y);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IntroScreen().setVisible(true));
    }
}