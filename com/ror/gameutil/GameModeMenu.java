package com.ror.gameutil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import com.ror.engine.SoundManager;

public class GameModeMenu extends JFrame implements ActionListener {

    private JPanel mainPanel;
    private JButton pvp, ai, arcade, backButton;
    private String playerName;

    private Image[] backgrounds;
    private int currentBg = 0;

    private SoundManager sound;

    public GameModeMenu(String name) {
        this.playerName = name;

        // --- FIX: Create a new SoundManager if we didn't bring one! ---
        this.sound = new SoundManager();

        // NOTE: Uncomment the line below and change "playMusic" to whatever method your SoundManager uses!
        // this.sound.playMusic("/images/BGM/bgm_main.wav");

        initUI();
    }

    public GameModeMenu(String name, SoundManager sound) {
        this.playerName = name;

        // --- FIX: If sound is null, create a new one. Otherwise, keep using the current one. ---
        this.sound = (sound != null) ? sound : new SoundManager();

        // NOTE: Uncomment the line below and change "playMusic" to whatever method your SoundManager uses!
        // this.sound.playMusic("/images/BGM/bgm_main.wav");

        initUI();
    }

    private void initUI() {

        setTitle("Happy Meal Tournament - Select Game Mode");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new GridBagLayout()) {

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
                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        setContentPane(mainPanel);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel title = new JLabel("CHOOSE GAME MODE", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent() + 5;

                // Drop shadow
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), x + 4, y + 4);

                // Gradient Fill
                g2.setPaint(new GradientPaint(0, 0, Color.YELLOW, 0, getHeight(), new Color(255, 140, 0)));
                g2.drawString(getText(), x, y);
            }
        };
        title.setFont(new Font("Impact", Font.BOLD, 48));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(800, 80));

        pvp = new JButton("PLAYER VS PLAYER");
        ai = new JButton("PLAYER VS AI");
        arcade = new JButton("ARCADE MODE");
        backButton = new JButton("BACK");

        Color actionColor = new Color(220, 20, 60);  // Crimson Red
        Color backColor = new Color(30, 144, 255);   // Dodger Blue

        styleButton(pvp, actionColor);
        styleButton(ai, actionColor);
        styleButton(arcade, actionColor);
        styleButton(backButton, backColor);

        pvp.addActionListener(this);
        ai.addActionListener(this);
        arcade.addActionListener(this);

        backButton.addActionListener(e -> {
            new HappyMealGame(sound).setVisible(true);
            dispose();
        });

        menuPanel.add(title);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        menuPanel.add(pvp);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        menuPanel.add(ai);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        menuPanel.add(arcade);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        menuPanel.add(backButton);

        mainPanel.add(menuPanel);
    }

    private void styleButton(JButton btn, Color base) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 22));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension size = new Dimension(400, 65);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size);
        btn.setMinimumSize(size);

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                int w = c.getWidth();
                int h = c.getHeight();

                Color bg = new Color(0, 0, 0, 160); // Glass black
                Color glow = base;

                if (model.isRollover()) {
                    bg = base.darker();
                    glow = base.brighter();
                }

                if (model.isPressed()) {
                    bg = base.darker().darker();
                }

                // Inner Drop Shadow
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(6, 6, w - 12, h - 12, 30, 30);

                // Button Body
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w - 12, h - 12, 30, 30);

                // Glowing Border
                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, w - 12, h - 12, 30, 30);

                // Text Formatting
                FontMetrics fm = g2.getFontMetrics();
                String text = b.getText();

                int x = (w - fm.stringWidth(text)) / 2 - 6;
                int y = (h + fm.getAscent()) / 2 - 6;

                // Text Drop Shadow
                g2.setColor(Color.BLACK);
                g2.drawString(text, x + 2, y + 2);

                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String selectedMode = "";
        String difficulty = "Medium";

        if (e.getSource() == pvp) {
            selectedMode = "PvP";

        } else if (e.getSource() == ai) {
            selectedMode = "PvAI";

            String[] options = {"Easy", "Medium", "Hard"};
            difficulty = (String) JOptionPane.showInputDialog(
                    this,
                    "Select AI Difficulty:",
                    "Difficulty",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    "Medium"
            );

            if (difficulty == null) return;

        } else if (e.getSource() == arcade) {

            if (sound != null) {
                sound.stop();
            }

            com.ror.gamemodel.Entity defaultHero = new com.ror.gamemodel.Playable.Mark();
            com.ror.gamemodel.Entity firstEnemy = new com.ror.gamemodel.Playable.Ted();

            new com.ror.gameutil.StoryCutscene(defaultHero, firstEnemy).setVisible(true);
            dispose();

            return;
        }

        if (sound != null) {
            sound.stop();
        }

        new HeroSelection(playerName, selectedMode, difficulty, sound).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new GameModeMenu("TestPlayer").setVisible(true)
        );
    }
}