package com.ror.gameutil;

import javax.swing.*;
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
        this.sound = null;
        initUI();
    }

    public GameModeMenu(String name, SoundManager sound) {
        this.playerName =  name;
        this.sound = sound;
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

                g.drawImage(backgrounds[currentBg], 0, 0,
                        getWidth(), getHeight(), this);

                g.setColor(new Color(0, 0, 0, 140));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        setContentPane(mainPanel);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel title = new JLabel("CHOOSE GAME MODE");
        title.setFont(new Font("Monospaced", Font.BOLD, 36));
        title.setForeground(new Color(255, 215, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        pvp = new JButton("PLAYER VS PLAYER");
        ai = new JButton("PLAYER VS AI");
        arcade = new JButton("ARCADE MODE");
        backButton = new JButton("BACK");

        styleButton(pvp, new Color(220, 20, 60));
        styleButton(ai, new Color(255, 140, 0));
        styleButton(arcade, new Color(50, 205, 50));
        styleButton(backButton, new Color(80, 80, 80));

        pvp.addActionListener(this);
        ai.addActionListener(this);
        arcade.addActionListener(this);

        backButton.addActionListener(e -> {
            new HappyMealGame(sound).setVisible(true);
            dispose();
        });

        menuPanel.add(title);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        menuPanel.add(pvp);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(ai);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(arcade);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        menuPanel.add(backButton);

        mainPanel.add(menuPanel);
    }

    private void styleButton(JButton btn, Color baseColor) {

        btn.setFont(new Font("Monospaced", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension size = new Dimension(360, 58);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size);

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {

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
                g2.fillRoundRect(3, 4, w - 6, h - 6, 25, 25);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);

                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, w - 6, h - 6, 25, 25);

                g2.setFont(btn.getFont());
                FontMetrics fm = g2.getFontMetrics();

                String text = b.getText();

                int x = (w - fm.stringWidth(text)) / 2 - 3;
                int y = (h + fm.getAscent()) / 2 - 5;

                g2.setColor(new Color(0, 0, 0, 160));
                g2.drawString(text, x + 1, y + 1);

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

            if (difficulty == null) difficulty = "Medium";

        } else if (e.getSource() == arcade) {
            selectedMode = "Arcade";
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