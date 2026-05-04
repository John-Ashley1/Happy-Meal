package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class GuiBattleArena extends JFrame implements BattleView {

    private Entity player1, player2;
    private boolean isPlayer1Turn = true;

    private int p1Wins = 0, p2Wins = 0, round = 1;
    private final int MAX_ROUNDS = 3;

    private JLabel lblPlayer1, lblPlayer2;
    private JProgressBar pbHealthP1, pbManaP1, pbHealthP2, pbManaP2;
    private JTextArea txtBattleLog;
    private JButton[] skillButtonsP1 = new JButton[3];
    private JButton[] skillButtonsP2 = new JButton[3];

    private JLabel lblRound;
    private JLabel lblTurnIndicator;

    // Idle penalty timer
    private javax.swing.Timer idleTimer;
    private static final int  IDLE_SECONDS    = 10;
    private static final int  IDLE_HP_PENALTY = 100;  // HP deducted if player idles
    private static final int  SKILL_MANA_COST = 2;    // mana cost per skill use

    private java.util.Map<Entity, Integer> manaHalfPoints = new java.util.HashMap<>();

    // Colors
    private static final Color BG_DARK      = new Color(10, 10, 18);
    private static final Color PANEL_BG     = new Color(18, 18, 30);
    private static final Color P1_COLOR     = new Color(0, 200, 255);
    private static final Color P2_COLOR     = new Color(255, 80, 80);
    private static final Color HP_COLOR     = new Color(50, 220, 100);
    private static final Color MANA_COLOR   = new Color(80, 120, 255);
    private static final Color GOLD         = new Color(255, 200, 50);
    private static final Color BTN_READY    = new Color(30, 30, 50);
    private static final Color BTN_DISABLED = new Color(20, 20, 30);
    private static final Color LOG_BG       = new Color(8, 8, 15);
    private static final Color LOG_FG       = new Color(180, 255, 160);

    public GuiBattleArena(Entity p1, Entity p2) {
        this.player1 = p1;
        this.player2 = p2;
        initComponents();
        setCharacterImages();
        updateUI();
        logMessage("⚔  BATTLE START!  " + p1.getName() + "  vs  " + p2.getName());
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

    private JPanel buildTopBar() {
        JPanel top = new JPanel(null); // absolute layout for true centering
        top.setPreferredSize(new Dimension(0, 70));
        top.setBackground(new Color(14, 14, 24));
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 40, 70)));

        // Back button pinned left
        JButton backBtn = styledButton("← MENU", new Color(40, 40, 60));
        backBtn.setBounds(12, 17, 90, 36);
        backBtn.addActionListener(e -> returnToMenu());
        top.add(backBtn);

        // Matchup + Round — both centered across full width
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

        // Fighter panels side by side
        JPanel fightersRow = new JPanel(new GridLayout(1, 2, 12, 0));
        fightersRow.setOpaque(false);
        fightersRow.add(buildFighterPanel(true));
        fightersRow.add(buildFighterPanel(false));
        main.add(fightersRow, BorderLayout.CENTER);

        // Battle log
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

        // Outer panel: vertical BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(accent.darker().darker(), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // ── Name ──
        JLabel nameLabel = new JLabel(entity.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLabel.setForeground(accent);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(6));

        // ── Character image (fills remaining space) ──
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

        // ── HP Bar (fixed tall height) ──
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        hpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        hpBar.setMinimumSize(new Dimension(0, 36));
        hpBar.setPreferredSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(hpBar);
        panel.add(Box.createVerticalStrut(5));

        // ── Mana Bar (fixed tall height) ──
        manaBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        manaBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        manaBar.setMinimumSize(new Dimension(0, 36));
        manaBar.setPreferredSize(new Dimension(Short.MAX_VALUE, 36));
        panel.add(manaBar);
        panel.add(Box.createVerticalStrut(8));

        // ── Skill buttons (3 fixed height buttons) ──
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
        JLabel hint = new JLabel("Select a skill to attack — Best of 3 Rounds");
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

    private void startIdleTimer() {
        if (idleTimer != null) idleTimer.stop();

        idleTimer = new javax.swing.Timer(IDLE_SECONDS * 1000, e -> {

            Entity idle = isPlayer1Turn ? player1 : player2;

            logMessage("⏰ " + idle.getName() + " was too slow!");

            // OPTIONAL: small penalty (not required)
            applyDamage(idle, 50);

            // 🔥 SWITCH TURN (THIS IS THE REAL FIX)
            isPlayer1Turn = !isPlayer1Turn;

            logMessage("⚡ Turn skipped!");

            updateUI();
            startIdleTimer();
        });

        idleTimer.setRepeats(false);
        idleTimer.start();
    }

    private void stopIdleTimer() {
        if (idleTimer != null) { idleTimer.stop(); idleTimer = null; }
    }

    /** Reduce entity HP — tries takeDamage(), falls back to reflection on currentHealth field */
    private void applyDamage(Entity e, int amount) {
        try {
            // Try takeDamage(int) method first
            java.lang.reflect.Method m = e.getClass().getMethod("takeDamage", int.class);
            m.invoke(e, amount);
        } catch (Exception ex1) {
            try {
                // Fallback: directly set field via reflection
                java.lang.reflect.Field f = findField(e.getClass(), "currentHealth");
                f.setAccessible(true);
                int cur = (int) f.get(e);
                f.set(e, Math.max(0, cur - amount));
            } catch (Exception ex2) { /* silent */ }
        }
    }

    /** Reduce entity Mana — tries setCurrentMana(), falls back to reflection */
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
            } catch (Exception ex2) { /* silent */ }
        }
    }

    private java.lang.reflect.Field findField(Class<?> cls, String name) throws NoSuchFieldException {
        while (cls != null) {
            try { return cls.getDeclaredField(name); }
            catch (NoSuchFieldException ex) { cls = cls.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }

    /** Restore mana — tries setCurrentMana(), falls back to reflection */
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
            } catch (Exception ex2) { /* silent */ }
        }
    }

    private void handleManaOnAttack(Entity atk) {
        if (atk.getCurrentMana() > 0) {
            // Normal: deduct cost
            applyManaCost(atk, SKILL_MANA_COST);
        } else {
            // Mana is 0 — regen mode: accumulate half-points (+1.5 per attack = 3 half-units)
            int half = manaHalfPoints.getOrDefault(atk, 0) + 3; // 3 half-units = 1.5 mana
            int fullMana = half / 2;      // how many full mana points to restore
            int remainder = half % 2;     // leftover half-point
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

        // Handle mana: cost if > 0, regen if at 0
        handleManaOnAttack(atk);

        skill.apply(atk, tgt, this::logMessage);
        skill.resetCooldown();

        // Reset idle timer since player acted
        startIdleTimer();

        if (!tgt.isDead()) {
            isPlayer1Turn = !isPlayer1Turn;
        }

        updateUI();
    }

    private void updateUI() {
        // Window title shows whose turn it is
        String turnName = isPlayer1Turn ? player1.getName() : player2.getName();
        setTitle("⚔ " + turnName + "'s Turn");

        // Center: Matchup name (colored per turn) + Round
        lblTurnIndicator.setText(player1.getName() + "  VS  " + player2.getName());
        lblTurnIndicator.setForeground(isPlayer1Turn ? P1_COLOR : P2_COLOR);
        lblRound.setText("ROUND  " + round + "  /  " + MAX_ROUNDS);

        // HP / Mana
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
            boolean p2Ready = s2.isReady() && !isPlayer1Turn;
            skillButtonsP2[i].setText(s2.getName() + (s2.isReady() ? "" : " [CD]"));
            skillButtonsP2[i].setEnabled(p2Ready);
            skillButtonsP2[i].setBackground(p2Ready ? new Color(80, 20, 20) : BTN_DISABLED);
        }
    }

    private void checkGameOver() {
        if (!player1.isDead() && !player2.isDead()) return;

        if (player1.isDead()) p2Wins++; else p1Wins++;
        logMessage("⚡ Round " + round + " over! Score → " + player1.getName() + " " + p1Wins + " : " + p2Wins + " " + player2.getName());

        if (p1Wins == 2 || p2Wins == 2 || round == MAX_ROUNDS) {
            String winner = p1Wins > p2Wins ? player1.getName() : player2.getName();
            JOptionPane.showMessageDialog(this,
                    "🏆 " + winner + " WINS the match!  (" + p1Wins + " - " + p2Wins + ")",
                    "Battle Over", JOptionPane.INFORMATION_MESSAGE);
            returnToMenu();
        } else {
            round++;
            startNextRound();
        }
    }

    private void startNextRound() {
        stopIdleTimer();
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
            case "Happy Trone": return new com.ror.gamemodel.Playable.Trone();
            case "Happy Vince":  return new com.ror.gamemodel.Playable.Vince();
            case "Happy Zack":   return new com.ror.gamemodel.Playable.Zack();
            default:             return new com.ror.gamemodel.Playable.Mark();
        }
    }

    private void setCharacterImages() {
        // Defer until after pack/show so label dimensions are known
        SwingUtilities.invokeLater(() -> {
            loadImage(lblPlayer1, player1.getName());
            loadImage(lblPlayer2, player2.getName());
        });
    }

    private void loadImage(JLabel label, String name) {
        try {
            String path = getGifPath(name);
            java.net.URL url = getClass().getResource(path);

            if (url == null) {
                label.setText(name);
                label.setIcon(null);
                return;
            }

            ImageIcon icon = new ImageIcon(url);

            // 🔥 TARGET SIZE (YOU CONTROL THIS)
            int targetW = label.getWidth() > 0 ? label.getWidth() : 300;
            int targetH = label.getHeight() > 0 ? label.getHeight() : 200;

            Image img = icon.getImage();

            // 🔥 KEEP RATIO (NO STRETCH)
            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();

            double scale = Math.min((double) targetW / imgW, (double) targetH / imgH);

            int newW = (int)(imgW * scale);
            int newH = (int)(imgH * scale);

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_FAST);

            label.setIcon(new ImageIcon(scaled));
            label.setText("");

            // 🔥 CENTER IT
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);

            label.setOpaque(false);

        } catch (Exception e) {
            label.setText("?");
            label.setIcon(null);
        }
    }

    private String getGifPath(String name) {
        switch (name) {
            case "Happy Ashley": return "/images/characters/ashley_Gif.gif";
            case "Happy Clent":  return "/images/characters/clent_Gif.gif";
            case "Happy Den":    return "/images/characters/den_gif.gif";
            case "Happy Mark":   return "/images/characters/mark_gif.gif";
            case "Happy Ted":    return "/images/characters/ted_gif.gif";
            case "Happy Trone": return "/images/characters/trone_gif.gif";
            case "Happy Vince":  return "/images/characters/vince_gif.gif";
            case "Happy Zack":   return "/images/characters/zack_gif.gif";

            default: return "/images/characters/mark_gif.gif";
        }
    }

    private void returnToMenu() {
        stopIdleTimer();
        dispose();

        new HeroSelection("Player", "PvP", "Normal").setVisible(true); // 🔥 GO HERE INSTEAD
    }

    @Override
    public void logMessage(String msg) {
        txtBattleLog.append(msg + "\n");
        txtBattleLog.setCaretPosition(txtBattleLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new GuiBattleArena(
                        new com.ror.gamemodel.Playable.Mark(),
                        new com.ror.gamemodel.Playable.Ted()
                ).setVisible(true)
        );
    }
}
