package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Playable.*;
import com.ror.gameutil.GuiBattleArena;
import com.ror.engine.SoundManager;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HeroSelection extends JFrame implements ActionListener {

    // ================= BUTTONS =================
    private final JButton mark, ted, den, ashley, vince, zack, clent, trone;

    private final JLabel instructionLabel;
    private final JTextArea heroInfoArea;
    private JButton startButton;

    private final String mode;
    private final String difficulty;

    private final ArrayList<String> availableHeroes;
    private String player1Hero = null;
    private String player2Hero = null;

    private SoundManager sound;

    // ✅ GLOBAL LOCK FIX
    private boolean selectionLocked = false;

    // ================= CONSTRUCTOR =================
    public HeroSelection(String playerName, String mode, String difficulty) {

        this.mode = mode;
        this.difficulty = difficulty;

        setTitle("Hero Selection");
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        String[] heroes = {
                "Happy Mark", "Happy Ted", "Happy Den", "Happy Ashley",
                "Happy Vince", "Happy Zack", "Happy Clent", "Happy Trone"
        };

        availableHeroes = new ArrayList<>(Arrays.asList(heroes));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 10, 20),
                        0, getHeight(), new Color(40, 10, 50)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // ================= TITLE =================
        JLabel title = new JLabel("HERO SELECTION", JLabel.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 32));
        title.setForeground(Color.YELLOW);

        instructionLabel = new JLabel("SELECT YOUR HERO", JLabel.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        instructionLabel.setForeground(Color.WHITE);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(title);
        top.add(instructionLabel);

        mainPanel.add(top, BorderLayout.NORTH);

        // ================= HERO GRID =================
        JPanel grid = new JPanel(new GridLayout(2, 4, 10, 10));
        grid.setOpaque(false);

        mark = createHeroButton("images/characters/Happy_Mark.png");
        ted = createHeroButton("images/characters/Happy_Ted.png");
        den = createHeroButton("images/characters/Happy_Den.png");
        ashley = createHeroButton("images/characters/Happy_Ashley.png");
        vince = createHeroButton("images/characters/Happy_Vince.png");
        zack = createHeroButton("images/characters/Happy_Zack.png");
        clent = createHeroButton("images/characters/Happy_Clent.png");
        trone = createHeroButton("images/characters/Happy_Throne.png");

        grid.add(mark);
        grid.add(ted);
        grid.add(den);
        grid.add(ashley);
        grid.add(vince);
        grid.add(zack);
        grid.add(clent);
        grid.add(trone);

        mainPanel.add(grid, BorderLayout.CENTER);

        // ================= INFO PANEL =================
        heroInfoArea = new JTextArea();
        heroInfoArea.setEditable(false);
        heroInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        heroInfoArea.setForeground(Color.WHITE);
        heroInfoArea.setBackground(new Color(20, 20, 20));
        heroInfoArea.setBorder(new LineBorder(Color.YELLOW, 2));

        mainPanel.add(new JScrollPane(heroInfoArea), BorderLayout.EAST);

        // ================= START BUTTON =================
        startButton = new JButton("START BATTLE");
        styleButton(startButton, new Color(220, 20, 60));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> launchBattleArena());

        JButton back = new JButton("BACK");
        styleButton(back, new Color(30, 144, 255));
        back.addActionListener(e -> {
            new GameModeMenu("Player", sound).setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.setOpaque(false);
        bottom.add(back);
        bottom.add(startButton);

        mainPanel.add(bottom, BorderLayout.SOUTH);
    }

    // ================= SOUND CONSTRUCTOR =================
    public HeroSelection(String playerName, String mode, String difficulty, SoundManager sound) {
        this(playerName, mode, difficulty);
        this.sound = sound;
    }

    // ================= ACTION =================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (selectionLocked) return;

        JButton btn = (JButton) e.getSource();
        String hero = getHero(btn);

        showHeroInfo(hero);

        switch (mode) {
            case "PvP": handlePvP(hero, btn); break;
            case "PvAI": handlePvAI(hero, btn); break;
            case "Arcade": handleArcade(hero, btn); break;
        }
    }

    // ================= HERO BUTTON CREATION (FIXED ERROR HERE) =================
    private JButton createHeroButton(String imgPath) {

        JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.RED, 2));
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon icon = new ImageIcon(imgPath);
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // fallback if image missing
            btn.setText("NO IMG");
            btn.setForeground(Color.WHITE);
        }

        btn.addActionListener(this);
        return btn;
    }

    // ================= HERO MAP =================
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

    // ================= FULL HERO INFO =================
    private void showHeroInfo(String hero) {

        switch (hero) {

            case "Happy Mark":
                heroInfoArea.setText(
                        "HAPPY MARK\nSWORDSMAN\nHP: 1800 | MANA: 450\n\n" +
                                "SKILLS:\n• Joyful Slam\n• Guardian Break\n• Festival Fury"
                );
                break;

            case "Happy Ted":
                heroInfoArea.setText(
                        "HAPPY TED\nTEMPLAR\nHP: 2000 | MANA: 400\n\n" +
                                "SKILLS:\n• Shield Bash\n• Guardian Stand\n• Unbreakable Wall"
                );
                break;

            case "Happy Den":
                heroInfoArea.setText(
                        "HAPPY DEN\nBOXER\nHP: 1650 | MANA: 320\n\n" +
                                "SKILLS:\n• Hard Punch\n• Meteor Uppercut\n• Almighty Fist"
                );
                break;

            case "Happy Ashley":
                heroInfoArea.setText(
                        "HAPPY ASHLEY\nASSASSIN\nHP: 1500 | MANA: 400\n\n" +
                                "SKILLS:\n• Shadow Dash\n• Phoenix Drive"
                );
                break;

            case "Happy Vince":
                heroInfoArea.setText(
                        "HAPPY VINCE\nBRAWLER\nHP: 2000 | MANA: 350\n\n" +
                                "SKILLS:\n• Lightning Jab\n• Crushing Knee"
                );
                break;

            case "Happy Zack":
                heroInfoArea.setText(
                        "HAPPY ZACK\nSPORTY\nHP: 2000 | MANA: 350\n\n" +
                                "SKILLS:\n• Javelin Throw\n• Egoist"
                );
                break;

            case "Happy Clent":
                heroInfoArea.setText(
                        "HAPPY CLENT\nMAGE\nHP: 2000 | MANA: 500\n\n" +
                                "SKILLS:\n• Book of IT\n• Think of IT\n• Come to me"
                );
                break;

            case "Happy Trone":
                heroInfoArea.setText(
                        "HAPPY TRONE\nVAMPIRE\nHP: 3000\n\n" +
                                "SKILLS:\n• Blood Spear\n• Blood Shield\n• Blood Explosion"
                );
                break;
        }
    }

    // ================= PvP =================
    private void handlePvP(String hero, JButton btn) {

        if (player1Hero == null) {
            player1Hero = hero;
            btn.setEnabled(false);
            instructionLabel.setText("PLAYER 2 SELECT HERO");

        } else {
            player2Hero = hero;
            btn.setEnabled(false);

            instructionLabel.setText("READY TO START");

            selectionLocked = true;
            disableAllButtons();
        }

        checkStart();
    }

    // ================= PvAI =================
    private void handlePvAI(String hero, JButton btn) {

        player1Hero = hero;
        btn.setEnabled(false);

        Random r = new Random();
        player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

        instructionLabel.setText("READY TO START");

        selectionLocked = true;
        disableAllButtons();

        checkStart();
    }

    // ================= Arcade =================
    private void handleArcade(String hero, JButton btn) {

        player1Hero = hero;
        btn.setEnabled(false);

        Random r = new Random();
        player2Hero = availableHeroes.get(r.nextInt(availableHeroes.size()));

        instructionLabel.setText("READY TO START");

        selectionLocked = true;
        disableAllButtons();

        checkStart();
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

        if (sound != null) sound.stop();

        new GuiBattleArena(
                createHero(player1Hero),
                createHero(player2Hero)
        ).setVisible(true);

        dispose();
    }

    // ================= HERO FACTORY =================
    private Entity createHero(String name) {
        switch (name) {
            case "Happy Mark": return new Mark();
            case "Happy Ted": return new Ted();
            case "Happy Den": return new Den();
            case "Happy Ashley": return new Ashley();
            case "Happy Vince": return new Vince();
            case "Happy Zack": return new Zack();
            case "Happy Clent": return new Clent();
            case "Happy Trone": return new Trone();
        }
        return new Mark();
    }

    // ================= STYLE =================
    private void styleButton(JButton btn, Color base) {

        btn.setFont(new Font("Monospaced", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                int w = c.getWidth();
                int h = c.getHeight();

                Color bg = base.darker().darker();
                Color glow = base;

                if (model.isRollover()) {
                    bg = base;
                    glow = base.brighter();
                }

                if (model.isPressed()) {
                    bg = base.darker();
                }

                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(4,5,w-8,h-8,30,30);

                g2.setColor(bg);
                g2.fillRoundRect(0,0,w-8,h-8,30,30);

                g2.setColor(glow);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0,0,w-8,h-8,30,30);

                FontMetrics fm = g2.getFontMetrics();
                String text = b.getText();

                int x = (w - fm.stringWidth(text)) / 2;
                int y = (h + fm.getAscent()) / 2 - 5;

                g2.setColor(Color.BLACK);
                g2.drawString(text, x+2, y+2);

                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        });
    }
}