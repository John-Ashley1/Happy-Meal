package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiBattleArena extends JFrame implements BattleView {

    private Entity player1, player2;
    private String mode;
    private boolean isPlayer1Turn = true;
    private boolean isAiMatch = false;

    private int p1Wins = 0, p2Wins = 0, round = 1;
    private final int MAX_ROUNDS = 3;

    private JLabel lblPlayer1, lblPlayer2;
    private JProgressBar pbHealthP1, pbManaP1, pbHealthP2, pbManaP2;
    private JTextArea txtBattleLog;
    private JButton[] skillButtonsP1 = new JButton[3];
    private JButton[] skillButtonsP2 = new JButton[3];

    private JLabel lblRound;
    private JLabel lblTurnIndicator;

    private javax.swing.Timer idleTimer;
    private static final int  IDLE_SECONDS    = 10;
    private static final int  IDLE_HP_PENALTY = 100;
    private static final int  SKILL_MANA_COST = 2;

    private java.util.Map<Entity, Integer> manaHalfPoints = new java.util.HashMap<>();

    private static final Color BG_DARK      = new Color(10, 10, 18);
    private static final Color P1_COLOR     = new Color(0, 200, 255);
    private static final Color P2_COLOR     = new Color(255, 80, 80);
    private static final Color HP_COLOR     = new Color(50, 220, 100);
    private static final Color MANA_COLOR   = new Color(80, 120, 255);
    private static final Color GOLD         = new Color(255, 200, 50);
    private static final Color BTN_READY    = new Color(30, 30, 50);
    private static final Color BTN_DISABLED = new Color(20, 20, 30);
    private static final Color LOG_BG       = new Color(8, 8, 15);
    private static final Color LOG_FG       = new Color(180, 255, 160);

    public GuiBattleArena(Entity p1, Entity p2, String mode) {
        this.player1 = p1;
        this.player2 = p2;
        this.mode = mode;

        this.isAiMatch = "Arcade".equalsIgnoreCase(mode) || "PvAI".equalsIgnoreCase(mode);

        for (Skill s : p1.getSkills()) {
            while (!s.isReady()) s.reduceCooldown();
        }
        for (Skill s : p2.getSkills()) {
            while (!s.isReady()) s.reduceCooldown();
        }

        initComponents();
        setupPauseMenu();
        setCharacterImages();
        updateUI();

        logMessage("⚔  BATTLE START!  " + p1.getName() + "  vs  " + p2.getName() + (isAiMatch ? " [AI]" : ""));
        startIdleTimer();
    }

    private void initComponents() {
        setTitle("⚔ Battle Arena");
        setSize(920, 720);
        setMinimumSize(new Dimension(860, 680));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private void setupPauseMenu() {
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenu pauseMenu = new MainMenu(
                        GuiBattleArena.this,
                        () -> {},
                        () -> {
                            stopIdleTimer();
                            new IntroScreen().setVisible(true);
                            dispose();
                        }
                );
                pauseMenu.setVisible(true);
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(null);
        top.setPreferredSize(new Dimension(0, 70));
        top.setBackground(new Color(14, 14, 24));
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 40, 70)));

        lblTurnIndicator = new JLabel("", SwingConstants.CENTER);
        lblTurnIndicator.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTurnIndicator.setForeground(Color.WHITE);
        lblTurnIndicator.setBounds(0, 8, 900, 28);
        top.add(lblTurnIndicator);

        lblRound = new JLabel("ROUND  1  /  3", SwingConstants.CENTER);
        lblRound.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblRound.setForeground(GOLD);
        lblRound.setBounds(0, 38, 900, 22);
        top.add(lblRound);

        return top;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 8)) {
            private Image bgImage;
            {
                try {
                    java.net.URL url = getClass().getResource("/images/BG/bg_4.png");
                    if (url != null) bgImage = new ImageIcon(url).getImage();
                } catch (Exception ignored) {}
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null)
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        main.setOpaque(false);

        JPanel fightersRow = new JPanel(new GridLayout(1, 2, 12, 0));
        fightersRow.setOpaque(false);
        fightersRow.add(buildFighterPanel(true));
        fightersRow.add(buildFighterPanel(false));
        main.add(fightersRow, BorderLayout.CENTER);

        txtBattleLog = new JTextArea(4, 60);
        txtBattleLog.setEditable(false);
        txtBattleLog.setBackground(LOG_BG);
        txtBattleLog.setForeground(LOG_FG);
        txtBattleLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtBattleLog.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        txtBattleLog.setLineWrap(true);
        txtBattleLog.setWrapStyleWord(true);

        JScrollPane logScroll = new JScrollPane(txtBattleLog);
        logScroll.setPreferredSize(new Dimension(0, 90));
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(35, 35, 60), 1));
        logScroll.setBackground(LOG_BG);
        main.add(logScroll, BorderLayout.SOUTH);

        return main;
    }

    private JPanel buildFighterPanel(boolean isP1) {
        if (isP1) {
            pbHealthP1 = createBar(HP_COLOR);
            pbManaP1   = createBar(MANA_COLOR);
            lblPlayer1 = new JLabel();
        } else {
            pbHealthP2 = createBar(HP_COLOR);
            pbManaP2   = createBar(MANA_COLOR);
            lblPlayer2 = new JLabel();
        }

        JProgressBar hpBar   = isP1 ? pbHealthP1 : pbHealthP2;
        JProgressBar manaBar = isP1 ? pbManaP1   : pbManaP2;
        JLabel imgLabel      = isP1 ? lblPlayer1  : lblPlayer2;
        JButton[] skillBtns  = isP1 ? skillButtonsP1 : skillButtonsP2;
        Entity entity        = isP1 ? player1 : player2;
        Color accent         = isP1 ? P1_COLOR : P2_COLOR;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(accent.darker().darker(), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        String tag = (!isP1 && isAiMatch) ? " [AI]" : "";
        JLabel nameLabel = new JLabel(entity.getName() + tag, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLabel.setForeground(accent);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(6));

        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        imgLabel.setBackground(new Color(14, 14, 22));
        imgLabel.setOpaque(true);
        imgLabel.setBorder(BorderFactory.createLineBorder(accent.darker(), 2));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(300, 220));
        imgLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        panel.add(imgLabel);
        panel.add(Box.createVerticalStrut(8));

        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        hpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        hpBar.setMinimumSize(new Dimension(0, 36));
        hpBar.setPreferredSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(hpBar);
        panel.add(Box.createVerticalStrut(5));

        manaBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        manaBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        manaBar.setMinimumSize(new Dimension(0, 36));
        manaBar.setPreferredSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(manaBar);
        panel.add(Box.createVerticalStrut(8));

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            skillBtns[i] = styledButton("Skill " + (i + 1), BTN_READY);
            skillBtns[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            skillBtns[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            skillBtns[i].setMinimumSize(new Dimension(0, 42));
            skillBtns[i].setPreferredSize(new Dimension(Short.MAX_VALUE, 42));
            skillBtns[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accent.darker().darker(), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            skillBtns[i].addActionListener(e -> useSkill(idx, isP1));
            panel.add(skillBtns[i]);
            if (i < 2) panel.add(Box.createVerticalStrut(4));
        }

        return panel;
    }

    private JPanel buildBottomBar() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(new Color(14, 14, 24));
        bottom.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(40, 40, 70)));
        JLabel hint = new JLabel("Select a skill to attack — Best of 3 Rounds | [ESC] Pause Menu");
        hint.setFont(new Font("Monospaced", Font.PLAIN, 11));
        hint.setForeground(new Color(90, 90, 120));
        bottom.add(hint);
        return bottom;
    }

    private JProgressBar createBar(Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(new Color(25, 25, 40));
        bar.setFont(new Font("Monospaced", Font.BOLD, 14));
        bar.setPreferredSize(new Dimension(0, 34));
        return bar;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100), 1));
        return btn;
    }

    private void advanceTurn() {
        isPlayer1Turn = !isPlayer1Turn;

        Entity activePlayer = isPlayer1Turn ? player1 : player2;
        for (Skill skill : activePlayer.getSkills()) {
            skill.reduceCooldown();
        }
    }

    private void startIdleTimer() {
        if (idleTimer != null) idleTimer.stop();

        idleTimer = new javax.swing.Timer(IDLE_SECONDS * 1000, e -> {
            Entity idle = isPlayer1Turn ? player1 : player2;
            logMessage("⏰ " + idle.getName() + " was too slow!");
            applyDamage(idle, IDLE_HP_PENALTY);

            advanceTurn();

            logMessage("⚡ Turn skipped!");

            updateUI();
            checkGameOver();

            if(!player1.isDead() && !player2.isDead()) {
                if (!isPlayer1Turn && isAiMatch) {
                    Timer aiTimer = new Timer(1000, ev -> executeAiTurn());
                    aiTimer.setRepeats(false);
                    aiTimer.start();
                } else {
                    startIdleTimer();
                }
            }
        });

        idleTimer.setRepeats(false);
        idleTimer.start();
    }

    private void stopIdleTimer() {
        if (idleTimer != null) { idleTimer.stop(); idleTimer = null; }
    }

    private void applyDamage(Entity e, int amount) {
        try {
            java.lang.reflect.Method m = e.getClass().getMethod("takeDamage", int.class);
            m.invoke(e, amount);
        } catch (Exception ex1) {
            try {
                java.lang.reflect.Field f = findField(e.getClass(), "currentHealth");
                f.setAccessible(true);
                int cur = (int) f.get(e);
                f.set(e, Math.max(0, cur - amount));
            } catch (Exception ex2) { }
        }
    }

    private void applyManaCost(Entity e, int cost) {
        try {
            java.lang.reflect.Method m = e.getClass().getMethod("setCurrentMana", int.class);
            m.invoke(e, Math.max(0, e.getCurrentMana() - cost));
        } catch (Exception ex1) {
            try {
                java.lang.reflect.Field f = findField(e.getClass(), "currentMana");
                f.setAccessible(true);
                int cur = (int) f.get(e);
                f.set(e, Math.max(0, cur - cost));
            } catch (Exception ex2) { }
        }
    }

    private void applyManaRegen(Entity e, int amount) {
        try {
            java.lang.reflect.Method m = e.getClass().getMethod("setCurrentMana", int.class);
            m.invoke(e, Math.min(e.getMaxMana(), e.getCurrentMana() + amount));
        } catch (Exception ex1) {
            try {
                java.lang.reflect.Field f = findField(e.getClass(), "currentMana");
                f.setAccessible(true);
                int cur = (int) f.get(e);
                int max = e.getMaxMana();
                f.set(e, Math.min(max, cur + amount));
            } catch (Exception ex2) { }
        }
    }

    private java.lang.reflect.Field findField(Class<?> cls, String name) throws NoSuchFieldException {
        while (cls != null) {
            try { return cls.getDeclaredField(name); }
            catch (NoSuchFieldException ex) { cls = cls.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }

    private void handleManaOnAttack(Entity atk) {
        if (atk.getCurrentMana() > 0) {
            applyManaCost(atk, SKILL_MANA_COST);
        } else {
            int half = manaHalfPoints.getOrDefault(atk, 0) + 3;
            int fullMana = half / 2;
            int remainder = half % 2;
            manaHalfPoints.put(atk, remainder);
            if (fullMana > 0) {
                applyManaRegen(atk, fullMana);
                logMessage("✨ " + atk.getName() + " recovered " + fullMana + " mana!");
            }
        }
    }

    private void useSkill(int index, boolean isP1) {
        if (isPlayer1Turn != isP1) return;

        Entity atk = isP1 ? player1 : player2;
        Entity tgt = isP1 ? player2 : player1;

        Skill skill = atk.getSkills().get(index);
        if (!skill.isReady()) return;

        handleManaOnAttack(atk);

        skill.apply(atk, tgt, this::logMessage);
        skill.resetCooldown();

        startIdleTimer();

        if (!tgt.isDead()) {
            advanceTurn();
            updateUI();

            if (!isPlayer1Turn && isAiMatch) {
                Timer aiTimer = new Timer(1000, ev -> executeAiTurn());
                aiTimer.setRepeats(false);
                aiTimer.start();
            }
        } else {
            updateUI();
        }
    }

    private void executeAiTurn() {
        if (player2.isDead() || player1.isDead()) return;

        stopIdleTimer();

        List<Integer> readySkills = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (player2.getSkills().get(i).isReady()) {
                readySkills.add(i);
            }
        }

        if (readySkills.isEmpty()) {
            logMessage("🤖 " + player2.getName() + " has no ready skills — skipping turn.");
        } else {
            int randomSkillIndex = readySkills.get(new Random().nextInt(readySkills.size()));
            Skill skill = player2.getSkills().get(randomSkillIndex);

            handleManaOnAttack(player2);
            skill.apply(player2, player1, this::logMessage);
            skill.resetCooldown();
            logMessage("▶  " + player2.getName() + " used " + skill.getName() + "!");
        }

        if (!player1.isDead() && !player2.isDead()) {
            advanceTurn();
            startIdleTimer();
        }

        updateUI();
    }

    private void updateUI() {
        String turnName = isPlayer1Turn ? player1.getName() : player2.getName();
        setTitle("⚔ " + turnName + "'s Turn");

        lblTurnIndicator.setText(player1.getName() + "  VS  " + player2.getName() + (isAiMatch ? "  [AI]" : ""));
        lblTurnIndicator.setForeground(isPlayer1Turn ? P1_COLOR : P2_COLOR);
        lblRound.setText("ROUND  " + round + "  /  " + MAX_ROUNDS);

        updateBar(pbHealthP1, player1.getCurrentHealth(), player1.getMaxHealth(), "HP");
        updateBar(pbManaP1,   player1.getCurrentMana(),   player1.getMaxMana(),   "MP");
        updateBar(pbHealthP2, player2.getCurrentHealth(), player2.getMaxHealth(), "HP");
        updateBar(pbManaP2,   player2.getCurrentMana(),   player2.getMaxMana(),   "MP");

        updateSkills();
        checkGameOver();
    }

    private void updateBar(JProgressBar bar, int current, int max, String label) {
        bar.setMaximum(max);
        bar.setValue(current);
        bar.setString(label + ": " + current + " / " + max);
    }

    private void updateSkills() {
        for (int i = 0; i < 3; i++) {
            Skill s1 = player1.getSkills().get(i);
            boolean p1Ready = s1.isReady() && isPlayer1Turn;
            skillButtonsP1[i].setText(s1.getName() + (s1.isReady() ? "" : " [CD]"));
            skillButtonsP1[i].setEnabled(p1Ready);
            skillButtonsP1[i].setBackground(p1Ready ? new Color(20, 60, 90) : BTN_DISABLED);

            Skill s2 = player2.getSkills().get(i);
            boolean p2Ready = s2.isReady() && !isPlayer1Turn && !isAiMatch;
            skillButtonsP2[i].setText(s2.getName() + (s2.isReady() ? "" : " [CD]"));
            skillButtonsP2[i].setEnabled(p2Ready);
            skillButtonsP2[i].setBackground((s2.isReady() && !isPlayer1Turn) ? new Color(80, 20, 20) : BTN_DISABLED);
        }
    }

    private void checkGameOver() {
        if (!player1.isDead() && !player2.isDead()) return;

        stopIdleTimer();

        if (player1.isDead()) p2Wins++; else p1Wins++;
        logMessage("⚡ Round " + round + " over! Score → " + player1.getName() + " " + p1Wins + " : " + p2Wins + " " + player2.getName());

        if (p1Wins == 2 || p2Wins == 2 || round == MAX_ROUNDS) {
            boolean player1Won = p1Wins > p2Wins;

            // --- NEW: Intercept defeat ONLY in Arcade Mode! ---
            if ("Arcade".equalsIgnoreCase(mode) && !player1Won) {
                handleArcadeDefeat();
            } else {
                // Normal win/loss popup for all other modes (or if Player 1 wins Arcade)
                String winner = player1Won ? player1.getName() : player2.getName();
                JOptionPane.showMessageDialog(this,
                        "🏆 " + winner + " WINS the match!  (" + p1Wins + " - " + p2Wins + ")",
                        "Battle Over", JOptionPane.INFORMATION_MESSAGE);
                routeToNextScreen();
            }
        } else {
            round++;
            startNextRound();
        }
    }

    // --- NEW: Dedicated Defeat Popup Logic ---
    private void handleArcadeDefeat() {
        stopIdleTimer();

        // Define the buttons for our popup
        Object[] options = {"Retry", "Quit"};

        // Show the Option Dialog
        int choice = JOptionPane.showOptionDialog(this,
                "You were defeated by " + player2.getName(),
                "DEFEATED",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, // Gives it a dramatic 'Error' icon
                null,
                options,
                options[0] // Makes 'Retry' the default selected button
        );

        dispose(); // Close the Battle Arena window

        if (choice == JOptionPane.YES_OPTION) {
            // RETRY: Heal the player up and throw them back into the Arcade room!
            player1.heal(player1.getMaxHealth());
            applyManaRegen(player1, player1.getMaxMana());
            new ArcadeFrame(player1, player2).setVisible(true);
        } else {
            // QUIT: Return directly to the Intro Screen
            new IntroScreen().setVisible(true);
        }
    }

    private void startNextRound() {
        player1 = createFresh(player1.getName());
        player2 = createFresh(player2.getName());
        manaHalfPoints.clear();
        isPlayer1Turn = true;
        setCharacterImages();
        logMessage("🔥 ROUND " + round + " — FIGHT!");
        updateUI();
        startIdleTimer();
    }

    private Entity createFresh(String name) {
        switch (name) {
            case "Happy Mark":   return new com.ror.gamemodel.Playable.Mark();
            case "Happy Ted":    return new com.ror.gamemodel.Playable.Ted();
            case "Happy Ashley": return new com.ror.gamemodel.Playable.Ashley();
            case "Happy Clent":  return new com.ror.gamemodel.Playable.Clent();
            case "Happy Den":    return new com.ror.gamemodel.Playable.Den();
            case "Happy Trone":  return new com.ror.gamemodel.Playable.Trone();
            case "Happy Vince":  return new com.ror.gamemodel.Playable.Vince();
            case "Happy Zack":   return new com.ror.gamemodel.Playable.Zack();
            default:             return new com.ror.gamemodel.Playable.Mark();
        }
    }

    // --- THE FIX: EXACT FRAME COUNTS BASED ON YOUR SPRITESHEETS! ---
    private int getFrameCount(String characterKey) {
        switch (characterKey) {
            case "ashley": return 7;
            case "mark":   return 10;
            case "ted":    return 4;  // FIXED: Ted is the stone statue with 4 frames!
            case "clent":  return 10;
            case "den":    return 4;
            case "trone":  return 4;
            case "vince":  return 10;
            case "zack":   return 10;
            default:       return 1;
        }
    }

    private void setCharacterImages() {
        SwingUtilities.invokeLater(() -> {
            loadImage(lblPlayer1, player1.getName());
            loadImage(lblPlayer2, player2.getName());
        });
    }

    private void loadImage(JLabel label, String name) {
        try {
            Timer oldTimer = (Timer) label.getClientProperty("animTimer");
            if (oldTimer != null) oldTimer.stop();

            String key = name.toLowerCase().replace("happy ", "");
            String capKey = Character.toUpperCase(key.charAt(0)) + key.substring(1);

            java.net.URL assetUrl = getClass().getResource("/images/characters/" + key + "/" + capKey + "_Asset.png");

            if (assetUrl != null) {
                BufferedImage spriteSheet = ImageIO.read(assetUrl);

                // Use the exact calculated frames!
                int numFrames = getFrameCount(key);

                int frameW = spriteSheet.getWidth() / numFrames;
                int frameH = spriteSheet.getHeight();

                if (spriteSheet.getWidth() % numFrames != 0) {
                    System.out.println("WARNING: " + capKey + "_Asset.png width " + spriteSheet.getWidth() +
                            " is not perfectly divisible by " + numFrames + ".");
                }

                int labelW = label.getWidth() > 10 ? label.getWidth() : 300;
                int labelH = label.getHeight() > 10 ? label.getHeight() : 220;

                double scale = Math.min((double) labelW / frameW, (double) labelH / frameH);
                int dW = (int) (frameW * scale);
                int dH = (int) (frameH * scale);

                ImageIcon[] frames = new ImageIcon[numFrames];
                for (int i = 0; i < numFrames; i++) {
                    BufferedImage sub = spriteSheet.getSubimage(i * frameW, 0, frameW, frameH);
                    frames[i] = new ImageIcon(sub.getScaledInstance(dW, dH, Image.SCALE_REPLICATE));
                }

                label.setIcon(frames[0]);
                label.setText("");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setOpaque(false);

                if (numFrames > 1) {
                    Timer animTimer = new Timer(150, e -> {
                        Integer currentFrame = (Integer) label.getClientProperty("currentFrame");
                        if (currentFrame == null) currentFrame = 0;
                        currentFrame = (currentFrame + 1) % numFrames;

                        label.setIcon(frames[currentFrame]);
                        label.putClientProperty("currentFrame", currentFrame);
                    });
                    animTimer.start();
                    label.putClientProperty("animTimer", animTimer);
                }

                return;
            }

            java.net.URL url = getClass().getResource("/images/characters/" + key + "/" + key + ".png");
            if (url == null) url = getClass().getResource("/images/characters/" + key + "/" + capKey + ".png");

            if (url == null) {
                label.setText(name);
                label.setIcon(null);
                return;
            }

            ImageIcon icon = new ImageIcon(url);

            int labelW = label.getWidth()  > 10 ? label.getWidth()  : 300;
            int labelH = label.getHeight() > 10 ? label.getHeight() : 220;

            double scale = Math.min(
                    (double) labelW / icon.getIconWidth(),
                    (double) labelH / icon.getIconHeight()
            );

            int dW = (int) (icon.getIconWidth() * scale);
            int dH = (int) (icon.getIconHeight() * scale);

            Image scaled = icon.getImage().getScaledInstance(dW, dH, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setText("");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);

        } catch (Exception e) {
            label.setText("?");
            label.setIcon(null);
        }
    }

    private void routeToNextScreen() {
        stopIdleTimer();
        dispose();

        if ("Arcade".equalsIgnoreCase(mode)) {
            player1.heal(player1.getMaxHealth());
            applyManaRegen(player1, player1.getMaxMana());

            new ArcadeFrame(player1, player2).setVisible(true);

        } else if ("PvP".equalsIgnoreCase(mode) || "PvAI".equalsIgnoreCase(mode)) {
            new HeroSelection("Player", mode, "Normal").setVisible(true);
        } else {
            new GameModeMenu("Player").setVisible(true);
        }
    }

    @Override
    public void logMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtBattleLog.append(msg + "\n");
            txtBattleLog.setCaretPosition(txtBattleLog.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new GuiBattleArena(
                        new com.ror.gamemodel.Playable.Mark(),
                        new com.ror.gamemodel.Playable.Ted(),
                        "Arcade"
                ).setVisible(true)
        );
    }
}