package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Playable.*;
import com.ror.engine.SoundManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HeroSelection extends JFrame implements ActionListener {

    private final JButton mark;
    private final JButton ted;
    private final JButton den;
    private final JButton ashley;
    private final JButton vince;
    private final JButton zack;
    private final JButton clent;
    private final JButton trone;
    private final JButton backButton;
    private final JLabel instructionLabel;

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

        // --- 1. Window size stays strictly consistent ---
        setSize(800, 750);

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

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("CHOOSE YOUR FIGHTER", JLabel.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 32));
        title.setForeground(new Color(255, 215, 0));

        instructionLabel = new JLabel("SELECT YOUR HERO!", JLabel.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        instructionLabel.setForeground(Color.WHITE);

        topPanel.add(title);
        topPanel.add(instructionLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- 2. Reduced horizontal gaps slightly to maximize portrait width ---
        JPanel heroPanel = new JPanel(new GridLayout(2, 4, 10, 15));
        heroPanel.setOpaque(false);

        mark = createHeroButton("Happy Mark", "images/characters/mark/Mark.png");
        ted = createHeroButton("Happy Ted", "images/characters/ted/Ted.png");
        den = createHeroButton("Happy Den", "images/characters/den/den.png");
        ashley = createHeroButton("Happy Ashley", "images/characters/ashley/Ashley.png");
        vince = createHeroButton("Happy Vince", "images/characters/vince/Vince.png");
        zack = createHeroButton("Happy Zack", "images/characters/zack/Zack.png");
        clent = createHeroButton("Happy Clent", "images/characters/clent/Clent.png");
        trone = createHeroButton("Happy Trone", "images/characters/trone/Trone.png");

        JButton[] heroButtons = {mark, ted, den, ashley, vince, zack, clent, trone};
        for (JButton b : heroButtons) heroPanel.add(b);

        mainPanel.add(heroPanel, BorderLayout.CENTER);

        backButton = new JButton("BACK TO MENU");
        backButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        backButton.addActionListener(e -> {
            new GameModeMenu("Player").setVisible(true);
            dispose();
        });

        mainPanel.add(backButton, BorderLayout.SOUTH);
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

        // Keep the thinner border so it doesn't crowd the image
        btn.setBorder(new LineBorder(new Color(220, 20, 60), 1));

        try {
            ImageIcon icon = new ImageIcon(imagePath);
            // --- 3. Maximized Image Scale (170x170 perfectly fills the column width) ---
            Image scaled = icon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            System.out.println("Image not found: " + imagePath);
        }

        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton selected = (JButton) e.getSource();
        String chosenHero = selected.getText();

        switch (mode) {
            case "PvP": handlePvP(chosenHero); break;
            case "PvAI": handlePvAI(chosenHero); break;
            case "Arcade": handleArcade(chosenHero); break;
        }
    }

    private void handlePvP(String chosenHero) {
        if (player1Hero == null) {
            player1Hero = chosenHero;
            availableHeroes.remove(chosenHero);
            instructionLabel.setText("PLAYER 2 SELECT HERO!");
        } else {
            player2Hero = chosenHero;
            launchBattleArena();
        }
    }

    private void handlePvAI(String chosenHero) {
        player1Hero = chosenHero;
        availableHeroes.remove(chosenHero);

        Random r = new Random();
        player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

        JOptionPane.showMessageDialog(this,
                "AI selected: " + player2Hero + "\nDifficulty: " + difficulty);

        launchBattleArena();
    }

    private void handleArcade(String chosenHero) {
        player1Hero = chosenHero;
        availableHeroes.remove(chosenHero);

        Random r = new Random();
        player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

        launchArcadeMode();
    }

    private void launchBattleArena() {

        if (sound != null) sound.stop();

        new GuiBattleArena(
                createHeroEntity(player1Hero),
                createHeroEntity(player2Hero),
                mode
        ).setVisible(true);

        dispose();
    }

    private void launchArcadeMode() {

        if (sound != null) sound.stop();

        // Launch the Cinematic Intro instead of the ArcadeFrame!
        new StoryCutscene(
                createHeroEntity(player1Hero),
                createHeroEntity(player2Hero)
        ).setVisible(true);

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