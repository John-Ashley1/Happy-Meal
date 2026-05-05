package com.ror.gameutil;

import com.ror.engine.SoundManager;
import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;



public class GuiBattleArenaAI extends JFrame implements BattleView {

    // ── battle entities ────────────────────────────────────────────────────
    private Entity player;          // human
    private Entity ai;
    private SoundManager sound;// computer

    // ── round tracking ─────────────────────────────────────────────────────
    private int playerWins = 0;
    private int aiWins     = 0;
    private int round      = 1;
    private static final int MAX_ROUNDS = 3;

    // names used to re-create entities at round start
    private final String playerHeroName;
    private final String aiHeroName;

    // ── AI ─────────────────────────────────────────────────────────────────
        private final AiBrain   aiBrain;
    private final AiDifficulty difficulty;

    /** Delay (ms) before the AI executes its chosen skill. */
    private final int AI_THINK_DELAY;

    // ── UI components ──────────────────────────────────────────────────────
    private JLabel        lblPlayer, lblAi;
    private JProgressBar  pbHpPlayer, pbMpPlayer, pbHpAi, pbMpAi;
    private JTextArea     txtLog;
    private JButton[]     skillBtns = new JButton[3];
    private JLabel        lblTurnIndicator, lblRound;

    // ── idle-penalty timer ─────────────────────────────────────────────────
    private javax.swing.Timer idleTimer;
    private static final int  IDLE_SECONDS    = 10;
    private static final int  IDLE_HP_PENALTY = 100;

    // ── mana regen bookkeeping ─────────────────────────────────────────────
    private static final int  SKILL_MANA_COST = 2;
    private java.util.Map<Entity, Integer> manaHalf = new java.util.HashMap<>();

    // ── palette (matches GuiBattleArena) ───────────────────────────────────
    private static final Color BG_DARK    = new Color(10, 10, 18);
    private static final Color PANEL_BG   = new Color(18, 18, 30);
    private static final Color PLAYER_CLR = new Color(0, 200, 255);
    private static final Color AI_CLR     = new Color(255, 80, 80);
    private static final Color HP_CLR     = new Color(50, 220, 100);
    private static final Color MP_CLR     = new Color(80, 120, 255);
    private static final Color GOLD       = new Color(255, 200, 50);
    private static final Color BTN_ON     = new Color(30, 30, 50);
    private static final Color BTN_OFF    = new Color(20, 20, 30);
    private static final Color LOG_BG     = new Color(8, 8, 15);
    private static final Color LOG_FG     = new Color(180, 255, 160);

    // ══════════════════════════════════════════════════════════════════════
    // Constructor
    // ══════════════════════════════════════════════════════════════════════


    public GuiBattleArenaAI(Entity player, Entity ai, String difficulty, SoundManager sound) {
        this.player         = player;
        this.ai             = ai;
        this.playerHeroName = player.getName();
        this.aiHeroName     = ai.getName();
        this.difficulty     = AiDifficulty.from(difficulty);
        this.aiBrain        = new AiBrain(this.difficulty);
        this.AI_THINK_DELAY = (this.difficulty == AiDifficulty.EASY) ? 1200 : 800;
        this.sound = sound;

        buildUI();
        setCharacterImages();
        updateUI();
        logMessage("⚔  BATTLE START!  " + player.getName() + "  vs  " + ai.getName()
                + "  [AI – " + difficulty + "]");
        startIdleTimer();
    }

    // ══════════════════════════════════════════════════════════════════════
    // UI construction
    // ══════════════════════════════════════════════════════════════════════

    private void buildUI() {
        setTitle("⚔ PvAI – " + player.getName() + " vs " + ai.getName());
        setSize(920, 720);
        setMinimumSize(new Dimension(860, 680));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── top bar ────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel top = new JPanel(null);
        top.setPreferredSize(new Dimension(0, 70));
        top.setBackground(new Color(14, 14, 24));
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 40, 70)));

        JButton backBtn = styledBtn("← MENU", new Color(40, 40, 60));
        backBtn.setBounds(12, 17, 90, 36);
        backBtn.addActionListener(e -> returnToMenu());
        top.add(backBtn);

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

    // ── center area ────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel main = new JPanel(new BorderLayout(10, 8)) {
            private Image bgImg;
            {
                try {
                    java.net.URL u = getClass().getResource("/images/BG/bg_4.png");
                    if (u != null) bgImg = new ImageIcon(u).getImage();
                } catch (Exception ignored) {}
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImg != null) g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        main.setOpaque(false);

        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.add(buildFighterPanel(true));   // player (left)
        row.add(buildFighterPanel(false));  // AI     (right)
        main.add(row, BorderLayout.CENTER);

        txtLog = new JTextArea(4, 60);
        txtLog.setEditable(false);
        txtLog.setBackground(LOG_BG);
        txtLog.setForeground(LOG_FG);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtLog);
        scroll.setPreferredSize(new Dimension(0, 90));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(35, 35, 60), 1));
        scroll.setBackground(LOG_BG);
        main.add(scroll, BorderLayout.SOUTH);

        return main;
    }

    // ── single fighter panel (left = player, right = AI) ──────────────────
    private JPanel buildFighterPanel(boolean isPlayer) {
        Entity  entity  = isPlayer ? player : ai;
        Color   accent  = isPlayer ? PLAYER_CLR : AI_CLR;
        JLabel  imgLbl  = new JLabel();

        JProgressBar hpBar = bar(HP_CLR);
        JProgressBar mpBar = bar(MP_CLR);

        if (isPlayer) {
            lblPlayer  = imgLbl;
            pbHpPlayer = hpBar;
            pbMpPlayer = mpBar;
        } else {
            lblAi  = imgLbl;
            pbHpAi = hpBar;
            pbMpAi = mpBar;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(accent.darker().darker(), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        // name
        String tag   = isPlayer ? "" : "  [AI]";
        JLabel name  = new JLabel(entity.getName() + tag, SwingConstants.CENTER);
        name.setFont(new Font("Monospaced", Font.BOLD, 16));
        name.setForeground(accent);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        panel.add(name);
        panel.add(Box.createVerticalStrut(6));

        // image
        imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imgLbl.setVerticalAlignment(SwingConstants.CENTER);
        imgLbl.setBackground(new Color(14, 14, 22));
        imgLbl.setOpaque(true);
        imgLbl.setBorder(BorderFactory.createLineBorder(accent.darker(), 2));
        imgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLbl.setPreferredSize(new Dimension(Short.MAX_VALUE, 200));
        imgLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        panel.add(imgLbl);
        panel.add(Box.createVerticalStrut(8));

        // bars
        for (JProgressBar b : new JProgressBar[]{ hpBar, mpBar }) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            b.setMinimumSize(new Dimension(0, 36));
            b.setPreferredSize(new Dimension(Short.MAX_VALUE, 36));
            panel.add(b);
            panel.add(Box.createVerticalStrut(5));
        }
        panel.add(Box.createVerticalStrut(3));

        // skill buttons — only the player side gets interactive buttons
        if (isPlayer) {
            for (int i = 0; i < 3; i++) {
                final int idx = i;
                skillBtns[i] = styledBtn("Skill " + (i + 1), BTN_ON);
                skillBtns[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                skillBtns[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
                skillBtns[i].setMinimumSize(new Dimension(0, 42));
                skillBtns[i].setPreferredSize(new Dimension(Short.MAX_VALUE, 42));
                skillBtns[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent.darker().darker(), 1),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                skillBtns[i].addActionListener(e -> onPlayerSkill(idx));
                panel.add(skillBtns[i]);
                if (i < 2) panel.add(Box.createVerticalStrut(4));
            }
        } else {
            // AI side: static "thinking" label
            JLabel aiLabel = new JLabel("AI is thinking…", SwingConstants.CENTER);
            aiLabel.setFont(new Font("Monospaced", Font.ITALIC, 13));
            aiLabel.setForeground(new Color(150, 80, 80));
            aiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(aiLabel);
        }

        return panel;
    }

    // ── bottom hint bar ────────────────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(new Color(14, 14, 24));
        bottom.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(40, 40, 70)));
        JLabel hint = new JLabel("Select a skill to attack — AI difficulty: "
                + difficulty.name() + " — Best of 3");
        hint.setFont(new Font("Monospaced", Font.PLAIN, 11));
        hint.setForeground(new Color(90, 90, 120));
        bottom.add(hint);
        return bottom;
    }

    // ══════════════════════════════════════════════════════════════════════
    // Turn logic
    // ══════════════════════════════════════════════════════════════════════

    /** Called when the human player clicks one of their skill buttons. */
    private void onPlayerSkill(int index) {
        Skill s = player.getSkills().get(index);
        if (!s.isReady()) return;

        stopIdleTimer();
        lockPlayerButtons(true);   // prevent double-click during AI turn

        // execute player skill
        handleManaOnAttack(player);
        s.apply(player, ai, this::logMessage);
        s.resetCooldown();
        logMessage("▶  " + player.getName() + " used " + s.getName() + "!");

        updateUI();

        if (ai.isDead()) { checkGameOver(); return; }

        // schedule AI turn after a visual delay
        javax.swing.Timer aiDelay = new javax.swing.Timer(AI_THINK_DELAY, e -> doAiTurn());
        aiDelay.setRepeats(false);
        aiDelay.start();
    }

    /** Executes one AI turn. */
    private void doAiTurn() {
        int idx = aiBrain.chooseSkill(ai, player);

        if (idx == -1) {
            // all AI skills on cooldown — tick cooldowns and give turn back
            logMessage("🤖 AI has no ready skills — skipping turn.");
        } else {
            Skill s = ai.getSkills().get(idx);
            handleManaOnAttack(ai);
            s.apply(ai, player, this::logMessage);
            s.resetCooldown();
            logMessage("🤖  " + ai.getName() + " used " + s.getName() + "!");
        }

        updateUI();

        if (player.isDead()) { checkGameOver(); return; }

        // give control back to the player
        lockPlayerButtons(false);
        startIdleTimer();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Mana helpers  (mirrored from GuiBattleArena)
    // ══════════════════════════════════════════════════════════════════════

    private void handleManaOnAttack(Entity atk) {
        if (atk.getCurrentMana() > 0) {
            applyManaCost(atk, SKILL_MANA_COST);
        } else {
            int half    = manaHalf.getOrDefault(atk, 0) + 3;
            int full    = half / 2;
            int rem     = half % 2;
            manaHalf.put(atk, rem);
            if (full > 0) {
                applyManaRegen(atk, full);
                logMessage("✨ " + atk.getName() + " recovered " + full + " mana!");
            }
        }
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
            } catch (Exception ex2) { /* silent */ }
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
            } catch (Exception ex2) { /* silent */ }
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
                f.set(e, Math.min(e.getMaxMana(), cur + amount));
            } catch (Exception ex2) { /* silent */ }
        }
    }

    private java.lang.reflect.Field findField(Class<?> cls, String name)
            throws NoSuchFieldException {
        while (cls != null) {
            try { return cls.getDeclaredField(name); }
            catch (NoSuchFieldException ex) { cls = cls.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Idle timer
    // ══════════════════════════════════════════════════════════════════════

    private void startIdleTimer() {
        if (idleTimer != null) idleTimer.stop();
        idleTimer = new javax.swing.Timer(IDLE_SECONDS * 1000, e -> {
            applyDamage(player, IDLE_HP_PENALTY);
            logMessage("⏰ " + player.getName() + " was idle! Lost " + IDLE_HP_PENALTY + " HP.");
            updateUI();
            if (!player.isDead()) startIdleTimer();
            else checkGameOver();
        });
        idleTimer.setRepeats(false);
        idleTimer.start();
    }

    private void stopIdleTimer() {
        if (idleTimer != null) { idleTimer.stop(); idleTimer = null; }
    }

    // ══════════════════════════════════════════════════════════════════════
    // UI refresh
    // ══════════════════════════════════════════════════════════════════════

    private void updateUI() {
        setTitle("⚔ PvAI – " + player.getName() + " vs " + ai.getName() + " [AI]");

        lblTurnIndicator.setText(player.getName() + "  VS  " + ai.getName() + "  [AI]");
        lblTurnIndicator.setForeground(PLAYER_CLR);
        lblRound.setText("ROUND  " + round + "  /  " + MAX_ROUNDS);

        updateBar(pbHpPlayer, player.getCurrentHealth(), player.getMaxHealth(), "HP");
        updateBar(pbMpPlayer, player.getCurrentMana(),   player.getMaxMana(),   "MP");
        updateBar(pbHpAi,     ai.getCurrentHealth(),     ai.getMaxHealth(),     "HP");
        updateBar(pbMpAi,     ai.getCurrentMana(),       ai.getMaxMana(),       "MP");

        refreshSkillButtons();
    }

    private void updateBar(JProgressBar bar, int cur, int max, String label) {
        bar.setMaximum(max);
        bar.setValue(cur);
        bar.setString(label + ": " + cur + " / " + max);
    }

    private void refreshSkillButtons() {
        for (int i = 0; i < 3; i++) {
            Skill s   = player.getSkills().get(i);
            boolean ok = s.isReady();
            skillBtns[i].setText(s.getName() + (ok ? "" : " [CD]"));
            skillBtns[i].setEnabled(ok);
            skillBtns[i].setBackground(ok ? new Color(20, 60, 90) : BTN_OFF);
        }
    }

    /** Enable / disable all player skill buttons. */
    private void lockPlayerButtons(boolean locked) {
        for (JButton b : skillBtns) b.setEnabled(!locked);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Game-over / round logic
    // ══════════════════════════════════════════════════════════════════════

    private void checkGameOver() {
        if (!player.isDead() && !ai.isDead()) return;
        stopIdleTimer();

        String roundWinner;
        if (player.isDead()) { aiWins++;     roundWinner = ai.getName() + " (AI)"; }
        else                  { playerWins++; roundWinner = player.getName();       }

        logMessage("⚡ Round " + round + " → " + roundWinner + " wins!  Score: "
                + player.getName() + " " + playerWins + " : " + aiWins
                + " " + ai.getName() + " [AI]");

        boolean matchOver = (playerWins == 2 || aiWins == 2 || round == MAX_ROUNDS);

        if (matchOver) {
            String matchWinner = playerWins > aiWins
                    ? player.getName() + " 🎉 (YOU WIN!)"
                    : ai.getName()     + " 🤖 (AI WINS)";
            JOptionPane.showMessageDialog(this,
                    "🏆  " + matchWinner + "\n\nFinal: " + playerWins + " – " + aiWins,
                    "Match Over", JOptionPane.INFORMATION_MESSAGE);
            returnToMenu();
        } else {
            round++;
            startNextRound();
        }
    }

    private void startNextRound() {
        player = createFresh(playerHeroName);
        ai     = createFresh(aiHeroName);
        manaHalf.clear();
        setCharacterImages();
        logMessage("🔥 ROUND " + round + " — FIGHT!");
        updateUI();
        lockPlayerButtons(false);
        startIdleTimer();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Hero / image helpers
    // ══════════════════════════════════════════════════════════════════════

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
        SwingUtilities.invokeLater(() -> {
            loadImage(lblPlayer, player.getName());
            loadImage(lblAi,     ai.getName());
        });
    }

    private void loadImage(JLabel label, String name) {
        try {
            String key = name.toLowerCase().replace("happy ", "");

            java.net.URL url = null;

            // 🔥 1. Try animated GIF (your main goal)
            url = getClass().getResource("/images/characters/" + key + ".gif");

            // 🔁 2. Fallbacks if GIF not found
            if (url == null)
                url = getClass().getResource("/images/characters/" + key + ".gif");

            if (url == null)
                url = getClass().getResource("/images/characters/" + key + ".png");

            if (url == null)
                url = getClass().getResource("/images/characters/" + key + ".jpg");

            // 🧩 3. Handle "Happy_Name.png" fallback
            if (url == null) {
                String capKey = "Happy_" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
                url = getClass().getResource("/images/characters/" + capKey + ".png");
            }


            if (url == null) {
                label.setText(name);
                label.setIcon(null);
                return;
            }

            ImageIcon icon = new ImageIcon(url);


            if (url.toString().endsWith(".gif")) {
                label.setIcon(icon); // keep original size → animation works
            } else {
                // scale only for static images
                int labelW = label.getWidth()  > 10 ? label.getWidth()  : 380;
                int labelH = label.getHeight() > 10 ? label.getHeight() : 200;

                double scale = Math.min(
                        (double) labelW / icon.getIconWidth(),
                        (double) labelH / icon.getIconHeight()
                );

                int dW = (int) (icon.getIconWidth() * scale);
                int dH = (int) (icon.getIconHeight() * scale);

                Image scaled = icon.getImage().getScaledInstance(dW, dH, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
            }

            label.setText("");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);

        } catch (Exception e) {
            label.setText("?");
            label.setIcon(null);
        }
    }

    private void returnToMenu() {
        stopIdleTimer();
        dispose();
        SwingUtilities.invokeLater(() -> {
                    new GameModeMenu(player.getName(), null).setVisible(true);
        });
    }

    // ══════════════════════════════════════════════════════════════════════
    // Widget helpers
    // ══════════════════════════════════════════════════════════════════════

    private JProgressBar bar(Color color) {
        JProgressBar b = new JProgressBar(0, 100);
        b.setStringPainted(true);
        b.setForeground(color);
        b.setBackground(new Color(25, 25, 40));
        b.setFont(new Font("Monospaced", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(0, 34));
        return b;
    }

    private JButton styledBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100), 1));
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════
    // BattleView
    // ══════════════════════════════════════════════════════════════════════

    @Override
    public void logMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    // ══════════════════════════════════════════════════════════════════════
    // Quick-launch entry point
    // ══════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            SoundManager sound = new SoundManager(); // or null if not ready

            new GuiBattleArenaAI(
                    new com.ror.gamemodel.Playable.Mark(),
                    new com.ror.gamemodel.Playable.Ted(),
                    "Medium",
                    sound
            ).setVisible(true);
        });
    }
}