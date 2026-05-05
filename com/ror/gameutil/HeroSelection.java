package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Playable.*;
import com.ror.gamemodel.Skill;
import com.ror.engine.SoundManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HeroSelection extends JFrame implements ActionListener {

    private final JButton mark, ted, den, ashley, vince, zack, clent, trone;
    private final JLabel instructionLabel;
    private final JTextArea statTextArea;

    private final String mode;
    private final String difficulty;

    private final ArrayList<String> availableHeroes;
    private String player1Hero = null;
    private String player2Hero = null;

    private SoundManager sound;

    public HeroSelection(String playerName, String mode, String difficulty) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.sound = null;

        setTitle("Happy Meal Tournament - Select Your Hero");
        setSize(1050, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] heroes = {
                "Happy Mark", "Happy Ted", "Happy Den", "Happy Ashley",
                "Happy Vince", "Happy Zack", "Happy Clent", "Happy Trone"
        };

        availableHeroes = new ArrayList<>();
        Collections.addAll(availableHeroes, heroes);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 15, 25),
                        0, getHeight(), new Color(45, 10, 50)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("CHOOSE YOUR FIGHTER", JLabel.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 32));
        title.setForeground(new Color(255, 215, 0));

        instructionLabel = new JLabel("PLAYER 1: SELECT YOUR HERO!", JLabel.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        instructionLabel.setForeground(Color.WHITE);

        topPanel.add(title);
        topPanel.add(instructionLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- HERO GRID ---
        JPanel heroPanel = new JPanel(new GridLayout(2, 4, 10, 15));
        heroPanel.setOpaque(false);

        mark = createHeroButton("Happy Mark", "/images/characters/mark/Mark.png");
        ted = createHeroButton("Happy Ted", "/images/characters/ted/Ted.png");
        den = createHeroButton("Happy Den", "/images/characters/den/den.png");
        ashley = createHeroButton("Happy Ashley", "/images/characters/ashley/Ashley.png");
        vince = createHeroButton("Happy Vince", "/images/characters/vince/Vince.png");
        zack = createHeroButton("Happy Zack", "/images/characters/zack/Zack.png");
        clent = createHeroButton("Happy Clent", "/images/characters/clent/Clent.png");
        trone = createHeroButton("Happy Trone", "/images/characters/trone/Trone.png");

        JButton[] heroButtons = {mark, ted, den, ashley, vince, zack, clent, trone};
        for (JButton b : heroButtons) heroPanel.add(b);
        mainPanel.add(heroPanel, BorderLayout.CENTER);

        // --- SIDE STAT PANEL ---
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(250, 0));
        infoPanel.setBorder(new CompoundBorder(
                new EmptyBorder(0, 10, 0, 0),
                new LineBorder(new Color(255, 255, 0), 2)
        ));

        statTextArea = new JTextArea();
        statTextArea.setEditable(false);
        statTextArea.setOpaque(false);
        statTextArea.setForeground(Color.WHITE);
        statTextArea.setFont(new Font("Monospaced", Font.BOLD, 15));
        statTextArea.setMargin(new Insets(15, 15, 15, 15));
        statTextArea.setText("HOVER OVER A HERO\nTO VIEW STATS");

        infoPanel.add(statTextArea, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.EAST);

        // --- NEW: BOTTOM BUTTON PANEL ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton backButton = new JButton("BACK");
        styleFancyButton(backButton, new Color(30, 144, 255)); // Dodger Blue
        backButton.addActionListener(ignored -> {
            new GameModeMenu(playerName, sound).setVisible(true);
            dispose();
        });

        JButton startButton = new JButton("START BATTLE");
        styleFancyButton(startButton, new Color(50, 200, 50)); // Terminal Green
        startButton.addActionListener(ignored -> attemptStartBattle());

        bottomPanel.add(backButton);
        bottomPanel.add(Box.createHorizontalGlue()); // Pushes buttons to opposite sides
        bottomPanel.add(startButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    public HeroSelection(String playerName, String mode, String difficulty, SoundManager sound) {
        this(playerName, mode, difficulty);
        this.sound = sound;
    }

    private JButton createHeroButton(String heroName, String imagePath) {
        JButton btn = new JButton(heroName);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.BLACK);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(new Color(220, 20, 60), 1));

        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaled = icon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) {
            System.out.println("Image not found: " + imagePath);
        }

        btn.addActionListener(this);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (availableHeroes.contains(heroName)) {
                    updateStatsPanel(heroName);
                }
            }
        });

        return btn;
    }

    // --- NEW: Fancy Button Styler (Imported from your GameModeMenu) ---
    private void styleFancyButton(JButton btn, Color base) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 22));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Dimension size = new Dimension(280, 55);
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

                Color bg = new Color(0, 0, 0, 160);
                Color glow = base;

                if (model.isRollover()) {
                    bg = base.darker();
                    glow = base.brighter();
                }
                if (model.isPressed()) {
                    bg = base.darker().darker();
                }

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(4, 4, w - 8, h - 8, 20, 20);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w - 8, h - 8, 20, 20);

                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, w - 8, h - 8, 20, 20);

                FontMetrics fm = g2.getFontMetrics();
                String text = b.getText();
                int x = (w - fm.stringWidth(text)) / 2 - 4;
                int y = (h + fm.getAscent()) / 2 - 4;

                g2.setColor(Color.BLACK);
                g2.drawString(text, x + 2, y + 2);
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        });
    }

    private void updateStatsPanel(String heroName) {
        Entity hero = createHeroEntity(heroName);
        StringBuilder sb = new StringBuilder();

        sb.append(hero.getName().toUpperCase()).append("\n\n");
        sb.append("HP: ").append(hero.getMaxHealth())
                .append(" | MANA: ").append(hero.getMaxMana()).append("\n\n");

        sb.append("SKILLS:\n");
        for (Skill skill : hero.getSkills()) {
            sb.append(" • ").append(skill.getName()).append("\n");
        }

        statTextArea.setText(sb.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton selected = (JButton) e.getSource();
        String chosenHero = selected.getText();

        if (!availableHeroes.contains(chosenHero)) {
            return;
        }

        switch (mode) {
            case "PvP": handlePvP(chosenHero); break;
            case "PvAI": handlePvAI(chosenHero); break;
            case "Arcade": handleArcade(chosenHero); break;
        }
    }

    // --- UPDATED: These methods now only lock the character in, they do NOT start the game! ---
    private void handlePvP(String chosenHero) {
        if (player1Hero == null) {
            player1Hero = chosenHero;
            availableHeroes.remove(chosenHero);
            instructionLabel.setText("PLAYER 2: SELECT YOUR HERO!");
            statTextArea.setText("PLAYER 2:\nHOVER OVER A HERO\nTO VIEW STATS");
        } else if (player2Hero == null) {
            player2Hero = chosenHero;
            availableHeroes.remove(chosenHero);
            instructionLabel.setForeground(Color.GREEN);
            instructionLabel.setText("READY! PRESS START BATTLE ->");
        }
    }

    private void handlePvAI(String chosenHero) {
        if (player1Hero == null) {
            player1Hero = chosenHero;
            availableHeroes.remove(chosenHero);

            Random r = new Random();
            player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

            instructionLabel.setForeground(Color.GREEN);
            instructionLabel.setText("READY! PRESS START BATTLE ->");

            JOptionPane.showMessageDialog(this,
                    "AI automatically selected: " + player2Hero + "\nDifficulty: " + difficulty);
        }
    }

    private void handleArcade(String chosenHero) {
        if (player1Hero == null) {
            player1Hero = chosenHero;
            availableHeroes.remove(chosenHero);

            Random r = new Random();
            player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

            instructionLabel.setForeground(Color.GREEN);
            instructionLabel.setText("READY! PRESS START BATTLE ->");
        }
    }

    // --- NEW: The method triggered by the "START BATTLE" button ---
    private void attemptStartBattle() {
        if (mode.equals("PvP")) {
            if (player1Hero == null || player2Hero == null) {
                JOptionPane.showMessageDialog(this, "Please select 2 Heroes before starting!", "Not Ready", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            if (player1Hero == null) {
                JOptionPane.showMessageDialog(this, "Please select a Hero before starting!", "Not Ready", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (sound != null) sound.stop();

        if (mode.equals("Arcade")) {
            new StoryCutscene(
                    createHeroEntity(player1Hero),
                    createHeroEntity(player2Hero)
            ).setVisible(true);
        } else {
            new GuiBattleArena(
                    createHeroEntity(player1Hero),
                    createHeroEntity(player2Hero),
                    mode
            ).setVisible(true);
        }

        dispose();
    }

    private Entity createHeroEntity(String heroName) {
        switch (heroName) {
            case "Happy Mark": return new Mark();
            case "Happy Ted": return new Ted();
            case "Happy Den": return new Den();
            case "Happy Ashley": return new Ashley();
            case "Happy Vince": return new Vince();
            case "Happy Zack": return new Zack();
            case "Happy Clent": return new Clent();
            case "Happy Trone": return new Trone();
            default: return new Mark();
        }
    }
}