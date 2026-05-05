package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Playable.*;
import com.ror.engine.SoundManager;

import javax.swing.*;
<<<<<<< HEAD
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
=======
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HeroSelection extends JFrame implements ActionListener {

    private final JButton mark, ted, den, ashley, vince, zack, clent, trone;

    private final JLabel instructionLabel;
    private final JTextArea heroInfoArea;
    private JButton startButton;
>>>>>>> ba45c2d (my local project changes)

    private final String mode;
    private final String difficulty;

    private final ArrayList<String> availableHeroes;
<<<<<<< HEAD
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
=======

    private String player1Hero = null;
    private String player2Hero = null;

    private JButton player1Btn = null;
    private JButton player2Btn = null;

    private SoundManager sound;

    private boolean selectionLocked = false;

    private Image background;

    public HeroSelection(String playerName, String mode, String difficulty) {

        this.mode = mode;
        this.difficulty = difficulty;
        this.sound = null;

        background = new ImageIcon(getClass().getResource("/images/BG/aboutbg.png")).getImage();

        setTitle("Hero Selection");
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        String[] heroNames = {
>>>>>>> ba45c2d (my local project changes)
                "Happy Mark", "Happy Ted", "Happy Den", "Happy Ashley",
                "Happy Vince", "Happy Zack", "Happy Clent", "Happy Trone"
        };

<<<<<<< HEAD
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
=======
        availableHeroes = new ArrayList<>(Arrays.asList(heroNames));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                if (background != null) {
                    g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }

                g2.setColor(new Color(0,0,0,150));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        JLabel title = new JLabel("HERO SELECTION", JLabel.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 32));
        title.setForeground(Color.YELLOW);

        instructionLabel = new JLabel("SELECT YOUR HERO", JLabel.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        instructionLabel.setForeground(Color.WHITE);

        JPanel top = new JPanel(new GridLayout(2,1));
        top.setOpaque(false);
        top.add(title);
        top.add(instructionLabel);

        mainPanel.add(top, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2,4,10,10));
        grid.setOpaque(false);

        mark = createHeroButton("images/characters/mark/Mark.png");
        ted = createHeroButton("images/characters/ted/Ted.png");
        den = createHeroButton("images/characters/den/Den.png");
        ashley = createHeroButton("images/characters/ashley/Ashley.png");
        vince = createHeroButton("images/characters/vince/Vince.png");
        zack = createHeroButton("images/characters/zack/Zack.png");
        clent = createHeroButton("images/characters/clent/Clent.png");
        trone = createHeroButton("images/characters/trone/Trone.png");

        JButton[] heroButtons = {mark, ted, den, ashley, vince, zack, clent, trone};
        for (JButton b : heroButtons) grid.add(b);

        mainPanel.add(grid, BorderLayout.CENTER);

        heroInfoArea = new JTextArea();
        heroInfoArea.setEditable(false);
        heroInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        heroInfoArea.setForeground(Color.WHITE);
        heroInfoArea.setBackground(new Color(20,20,20));
        heroInfoArea.setBorder(new LineBorder(Color.YELLOW,2));

        mainPanel.add(new JScrollPane(heroInfoArea), BorderLayout.EAST);

        startButton = new JButton("START BATTLE");
        styleButton(startButton);
        startButton.setEnabled(false);
        startButton.addActionListener(e -> launchBattleArena());

        JButton back = new JButton("BACK");
        styleButton(back);
        back.addActionListener(e -> {
            new GameModeMenu("Player", sound).setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel(new GridLayout(1,2,10,10));
        bottom.setOpaque(false);
        bottom.add(back);
        bottom.add(startButton);

        mainPanel.add(bottom, BorderLayout.SOUTH);
>>>>>>> ba45c2d (my local project changes)
    }

    public HeroSelection(String playerName, String mode, String difficulty, SoundManager sound) {
        this(playerName, mode, difficulty);
        this.sound = sound;
    }

<<<<<<< HEAD
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

=======
    @Override
    public void actionPerformed(ActionEvent e) {

        if (selectionLocked) return;

        JButton btn = (JButton) e.getSource();
        String hero = getHero(btn);

        if (mode.equals("PvP")) {
            highlightPvP(btn);
        } else {
            highlightSingle(btn);
        }

        showHeroInfo(hero);

        switch (mode) {
            case "PvP": handlePvP(hero, btn); break;
            case "PvAI": handlePvAI(hero, btn); break;
            case "Arcade": handleArcade(hero, btn); break;
        }

        checkStart();
    }

    private JButton createHeroButton(String imgPath) {

        JButton btn = new JButton() {
            Image img;

            {
                try {
                    img = new ImageIcon(getClass().getResource("/" + imgPath)).getImage();
                } catch (Exception e) {
                    img = null;
                }
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.RED, 2));
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

>>>>>>> ba45c2d (my local project changes)
        btn.addActionListener(this);
        return btn;
    }

<<<<<<< HEAD
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
=======
    private void highlightSingle(JButton btn) {

        if (player1Btn != null) {
            player1Btn.setBorder(new LineBorder(Color.RED, 2));
        }

        player1Btn = btn;
        btn.setBorder(new LineBorder(Color.YELLOW, 4));
    }

    private void highlightPvP(JButton btn) {

        if (player1Btn == null) {
            player1Btn = btn;
            btn.setBorder(new LineBorder(Color.GREEN, 4));
        } else if (player2Btn == null) {
            player2Btn = btn;
            btn.setBorder(new LineBorder(Color.BLUE, 4));
        }
    }

    private String getHero(JButton btn) {
        if (btn == mark) return "Happy Mark";
        if (btn == ted) return "Happy Ted";
        if (btn == den) return "Happy Den";
        if (btn == ashley) return "Happy Ashley";
        if (btn == vince) return "Happy Vince";
        if (btn == zack) return "Happy Zack";
        if (btn == clent) return "Happy Clent";
        if (btn == trone) return "Happy Trone";
        return "";
    }

    private void showHeroInfo(String hero) {

        switch (hero) {
            case "Happy Mark":
                heroInfoArea.setText("HAPPY MARK\nSWORDSMAN\nHP: 1800 | MANA: 450\n\nSKILLS:\n• Joyful Slam\n• Guardian Break\n• Festival Fury");
                break;
            case "Happy Ted":
                heroInfoArea.setText("HAPPY TED\nTEMPLAR\nHP: 2000 | MANA: 400\n\nSKILLS:\n• Shield Bash\n• Guardian Stand\n• Unbreakable Wall");
                break;
            case "Happy Den":
                heroInfoArea.setText("HAPPY DEN\nBOXER\nHP: 1650 | MANA: 320\n\nSKILLS:\n• Hard Punch\n• Meteor Uppercut\n• Almighty Fist");
                break;
            case "Happy Ashley":
                heroInfoArea.setText("HAPPY ASHLEY\nASSASSIN\nHP: 1500 | MANA: 400\n\nSKILLS:\n• Shadow Dash\n• Phoenix Drive");
                break;
            case "Happy Vince":
                heroInfoArea.setText("HAPPY VINCE\nBRAWLER\nHP: 2000 | MANA: 350\n\nSKILLS:\n• Lightning Jab\n• Crushing Knee");
                break;
            case "Happy Zack":
                heroInfoArea.setText("HAPPY ZACK\nSPORTY\nHP: 2000 | MANA: 350\n\nSKILLS:\n• Javelin Throw\n• Egoist");
                break;
            case "Happy Clent":
                heroInfoArea.setText("HAPPY CLENT\nMAGE\nHP: 2000 | MANA: 500\n\nSKILLS:\n• Book of IT\n• Think of IT\n• Come to me");
                break;
            case "Happy Trone":
                heroInfoArea.setText("HAPPY TRONE\nVAMPIRE\nHP: 3000 | MANA: 500\n\nSKILLS:\n• Blood Spear\n• Blood Shield\n• Blood Explosion");
                break;
            default:
                heroInfoArea.setText("");
        }
    }

    private void handlePvP(String hero, JButton btn) {
        if (player1Hero == null) {
            player1Hero = hero;
            instructionLabel.setText("PLAYER 2 SELECT HERO");
        } else {
            player2Hero = hero;
            selectionLocked = true;
            disableAllButtons();
        }
    }

    private void handlePvAI(String hero, JButton btn) {
        player1Hero = hero;
>>>>>>> ba45c2d (my local project changes)

        Random r = new Random();
        player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

<<<<<<< HEAD
        JOptionPane.showMessageDialog(this,
                "AI selected: " + player2Hero + "\nDifficulty: " + difficulty);

        launchBattleArena();
    }

    private void handleArcade(String chosenHero) {
        player1Hero = chosenHero;
        availableHeroes.remove(chosenHero);
=======
        selectionLocked = true;
        disableAllButtons();
    }

    private void handleArcade(String hero, JButton btn) {
        player1Hero = hero;
>>>>>>> ba45c2d (my local project changes)

        Random r = new Random();
        player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

<<<<<<< HEAD
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
=======
        selectionLocked = true;
        disableAllButtons();
    }

    private void disableAllButtons() {
        mark.setEnabled(false);
        ted.setEnabled(false);
        den.setEnabled(false);
        ashley.setEnabled(false);
        vince.setEnabled(false);
        zack.setEnabled(false);
        clent.setEnabled(false);
        trone.setEnabled(false);
    }

    private void checkStart() {
        if (player1Hero != null && player2Hero != null) {
            startButton.setEnabled(true);
        }
    }

    private void launchBattleArena() {
        new GuiBattleArena(createHero(player1Hero), createHero(player2Hero), mode).setVisible(true);
        dispose();
    }

    private Entity createHero(String name) {
        switch (name) {
>>>>>>> ba45c2d (my local project changes)
            case "Happy Mark": return new Mark();
            case "Happy Ted": return new Ted();
            case "Happy Den": return new Den();
            case "Happy Ashley": return new Ashley();
            case "Happy Vince": return new Vince();
            case "Happy Zack": return new Zack();
            case "Happy Clent": return new Clent();
            case "Happy Trone": return new Trone();
<<<<<<< HEAD
            default: return new Mark();
        }
=======
        }
        return new Mark();
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Monospaced", Font.BOLD, 18));
        btn.setBackground(new Color(220,20,60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(Color.WHITE,2));
>>>>>>> ba45c2d (my local project changes)
    }
}