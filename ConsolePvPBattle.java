package com.ror.gameutil;


import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;
import com.ror.gamemodel.Playable.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * GuiPvPBattle — Best-of-3 PvP Battle Arena
 * Place in: com/ror/gameutil/
 * Hook in HeroSelection.launchBattleArena() for PvP mode.
 */
public class ConsolePvPBattle extends JFrame implements BattleView {

    // Data Models
    private final String p1Name, p2Name;
    private Entity player1;
    private Entity player2;
    private boolean isPlayer1Turn = true;


    // Round Tracking
    private int currentRound  = 1;
    private int p1Wins        = 0;
    private int p2Wins        = 0;
    private static final int MAX_ROUNDS = 3;
    private Timer turnTimer;
    private int timeLeft = 10;
    private JLabel timerLabel;

    // UI Components
    private JProgressBar p1HealthBar, p2HealthBar;
    private JLabel p1NameLabel, p2NameLabel;
    private JLabel p1StatsLabel, p2StatsLabel;
    private JLabel turnLabel;
    private JLabel roundLabel;
    private JLabel scoreLabel;
    private JTextArea combatLog;
    private JPanel skillsPanel;

    // Colors
    private static final Color BG       = new Color(15, 15, 25);
    private static final Color PANEL_BG = new Color(25, 25, 40);
    private static final Color GOLD     = new Color(255, 215, 0);
    private static final Color P1_COLOR = new Color(50, 180, 255);
    private static final Color P2_COLOR = new Color(220, 20,  60);
    private static final Color HP_GREEN = new Color(50, 220, 80);
    private static final Color HP_LOW   = new Color(220, 60, 60);
    private static final Color MANA_COL = new Color(80, 130, 255);
    private static final Color LOG_FG   = new Color(200, 200, 200);
    private static final Color SKILL_BG = new Color(30, 30, 50);
    private static final Color SKILL_CD = new Color(20, 20, 30);

    // Fonts
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 18);
    private static final Font STAT_FONT  = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font LOG_FONT   = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font BTN_FONT   = new Font("Monospaced", Font.BOLD, 13);
    private static final Font TURN_FONT  = new Font("Monospaced", Font.BOLD, 15);
    private static final Font ROUND_FONT = new Font("Monospaced", Font.BOLD, 13);


    public ConsolePvPBattle(Entity player1, Entity player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.p1Name  = player1.getName();
        this.p2Name  = player2.getName();

        setTitle("Happy Meal Tournament — PvP Battle");
        setSize(870, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        setContentPane(root);

        buildTopPanel(root);
        buildCenterLog(root);
        buildBottomPanel(root);

        logMessage("★  ROUND 1  —  " + p1Name + "  VS  " + p2Name + "  ★");
        updateTurnUI();
    }

    //TOP: Round bar + Player 2 stats
    private void buildTopPanel(JPanel root) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(BG);

        turnLabel = new JLabel("", JLabel.CENTER);
        timerLabel = new JLabel("⏳ Time Left: 10", JLabel.CENTER);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        timerLabel.setForeground(Color.ORANGE);
        // Round / Score bar

        wrapper.add(turnLabel, BorderLayout.NORTH);
        JPanel turnPanel = new JPanel(new GridLayout(2, 1));
        turnPanel.setBackground(BG);
        turnPanel.add(turnLabel);
        turnPanel.add(timerLabel);

        wrapper.add(turnPanel, BorderLayout.NORTH);

        JPanel roundBar = new JPanel(new GridLayout(1, 3));
        roundBar.setBackground(new Color(20, 20, 35));
        roundBar.setBorder(new EmptyBorder(4, 10, 4, 10));

        roundLabel = new JLabel("ROUND  1 / 3", JLabel.LEFT);
        roundLabel.setFont(ROUND_FONT);
        roundLabel.setForeground(GOLD);

        scoreLabel = new JLabel(p1Name + "  0  —  0  " + p2Name, JLabel.CENTER);
        scoreLabel.setFont(ROUND_FONT);
        scoreLabel.setForeground(Color.WHITE);

        // Back to Menu button
        JButton backBtn = new JButton("◀ MENU");
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        backBtn.setBackground(new Color(50, 50, 65));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(2, 8, 2, 8)));
        backBtn.addActionListener(e -> returnToMenu());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrap.setBackground(new Color(20, 20, 35));
        btnWrap.add(backBtn);

        roundBar.add(roundLabel);
        roundBar.add(scoreLabel);
        roundBar.add(btnWrap);

        // Player 2 panel (opponent — shown at top)
        JPanel p2Panel = new JPanel(new BorderLayout(12, 0));
        p2Panel.setBackground(PANEL_BG);
        p2Panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(P2_COLOR, 2),
                new EmptyBorder(6, 10, 6, 10)));

        p2NameLabel = new JLabel(player2.getName());
        p2NameLabel.setForeground(P2_COLOR);
        p2NameLabel.setFont(TITLE_FONT);

        p2HealthBar = makeHealthBar(player2);

        p2StatsLabel = new JLabel("Mana: " + player2.getCurrentMana());
        p2StatsLabel.setForeground(MANA_COL);
        p2StatsLabel.setFont(STAT_FONT);

        p2Panel.add(p2NameLabel,  BorderLayout.WEST);
        p2Panel.add(p2HealthBar,  BorderLayout.CENTER);
        p2Panel.add(p2StatsLabel, BorderLayout.EAST);

        wrapper.add(roundBar, BorderLayout.NORTH);
        wrapper.add(p2Panel,  BorderLayout.CENTER);

        root.add(wrapper, BorderLayout.NORTH);
    }

    // CENTER: Combat log
    private void buildCenterLog(JPanel root) {
        combatLog = new JTextArea();
        combatLog.setBackground(new Color(10, 10, 18));
        combatLog.setForeground(LOG_FG);
        combatLog.setFont(LOG_FONT);
        combatLog.setEditable(false);
        combatLog.setLineWrap(true);
        combatLog.setWrapStyleWord(true);
        combatLog.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(combatLog);
        scroll.setBorder(BorderFactory.createLineBorder(GOLD, 1));
        scroll.getVerticalScrollBar().setBackground(BG);

        root.add(scroll, BorderLayout.CENTER);
    }

    // BOTTOM: Player 1 stats + turn label + skill buttons
    private void buildBottomPanel(JPanel root) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(BG);

        turnLabel = new JLabel("", JLabel.CENTER);
        turnLabel.setFont(TURN_FONT);
        turnLabel.setBorder(new EmptyBorder(3, 0, 3, 0));

        // Player 1 panel (current player — shown at bottom)
        JPanel p1Panel = new JPanel(new BorderLayout(12, 0));
        p1Panel.setBackground(PANEL_BG);
        p1Panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(P1_COLOR, 2),
                new EmptyBorder(6, 10, 6, 10)));

        p1NameLabel = new JLabel(player1.getName());
        p1NameLabel.setForeground(P1_COLOR);
        p1NameLabel.setFont(TITLE_FONT);

        p1HealthBar = makeHealthBar(player1);
        p1HealthBar.setStringPainted(true);
        p1HealthBar.setString("HP: " + player1.getCurrentHealth() + "/" + player1.getMaxHealth());

        p1StatsLabel = new JLabel("Mana: " + player1.getCurrentMana());
        p1StatsLabel.setForeground(MANA_COL);
        p1StatsLabel.setFont(STAT_FONT);

        p1Panel.add(p1NameLabel,  BorderLayout.WEST);
        p1Panel.add(p1HealthBar,  BorderLayout.CENTER);
        p1Panel.add(p1StatsLabel, BorderLayout.EAST);

        skillsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        skillsPanel.setBackground(BG);
        skillsPanel.setPreferredSize(new Dimension(850, 58));

        JPanel statusAndSkills = new JPanel(new BorderLayout(0, 6));
        statusAndSkills.setBackground(BG);
        statusAndSkills.add(p1Panel,     BorderLayout.NORTH);
        statusAndSkills.add(skillsPanel, BorderLayout.SOUTH);

        wrapper.add(turnLabel,       BorderLayout.NORTH);
        wrapper.add(statusAndSkills, BorderLayout.CENTER);

        root.add(wrapper, BorderLayout.SOUTH);
    }

    // ── GAME LOGIC
    private void updateTurnUI() {
        Entity active = isPlayer1Turn ? player1 : player2;
        Color  tColor = isPlayer1Turn ? P1_COLOR : P2_COLOR;

        turnLabel.setText("▶  " + active.getName().toUpperCase() + "'S TURN");
        turnLabel.setForeground(tColor);

        for (Skill skill : active.getSkills()) {
            skill.reduceCooldown();
        }

        refreshBars();
        rebuildSkillButtons(active, tColor);
        startTurnTimer();
    }

    private void rebuildSkillButtons(Entity active, Color tColor) {
        skillsPanel.removeAll();

        for (Skill skill : active.getSkills()) {
            JButton btn = new JButton();
            btn.setFont(BTN_FONT);
            btn.setFocusPainted(false);

            if (skill.isReady()) {
                btn.setText("<html><center>" + skill.getName() + "</center></html>");
                btn.setBackground(SKILL_BG);
                btn.setForeground(Color.WHITE);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(tColor, 2),
                        new EmptyBorder(4, 6, 4, 6)));
                btn.addActionListener(e -> executeSkill(skill));
            } else {
                btn.setText("<html><center>" + skill.getName()
                        + "<br><font color='gray'>(CD: " + skill.getCooldown() + ")</font></center></html>");
                btn.setBackground(SKILL_CD);
                btn.setForeground(Color.GRAY);
                btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                btn.setEnabled(false);
            }
            skillsPanel.add(btn);
        }

        skillsPanel.revalidate();
        skillsPanel.repaint();
    }

    private void executeSkill(Skill skill) {
        Entity active = isPlayer1Turn ? player1 : player2;
        Entity target = isPlayer1Turn ? player2 : player1;

        skill.apply(active, target, this);
        skill.resetCooldown();
        refreshBars();

        if (turnTimer != null) {
            turnTimer.stop();
        }

        if (target.isDead()) {
            if (isPlayer1Turn) p1Wins++; else p2Wins++;

            logMessage("\n★ K.O.!  " + target.getName() + " is down!");
            logMessage("★ Round " + currentRound + " winner:  " + active.getName().toUpperCase() + "!\n");

            disableSkills();
            updateScoreLabel();

            // Short pause then evaluate round result
            Timer t = new Timer(1800, e -> checkRoundOver());
            t.setRepeats(false);
            t.start();
            return;
        }

        isPlayer1Turn = !isPlayer1Turn;
        updateTurnUI();
    }

    private void checkRoundOver() {
        if (p1Wins >= 2) {
            showMatchWinner(p1Name);
        } else if (p2Wins >= 2) {
            showMatchWinner(p2Name);
        } else if (currentRound >= MAX_ROUNDS) {
            if      (p1Wins > p2Wins) showMatchWinner(p1Name);
            else if (p2Wins > p1Wins) showMatchWinner(p2Name);
            else                       showDraw();
        } else {
            currentRound++;
            startNextRound();
        }
    }

    private void startNextRound() {
        player1 = createFreshEntity(p1Name);
        player2 = createFreshEntity(p2Name);
        isPlayer1Turn = true;

        p1NameLabel.setText(player1.getName());
        p2NameLabel.setText(player2.getName());
        p1HealthBar.setMaximum(player1.getMaxHealth());
        p2HealthBar.setMaximum(player2.getMaxHealth());
        roundLabel.setText("ROUND  " + currentRound + " / 3");

        logMessage("══════════════════════════════════════");
        logMessage("        ★  ROUND " + currentRound + "  START!  ★");
        logMessage("══════════════════════════════════════\n");

        updateTurnUI();
    }

    private void showMatchWinner(String name) {
        String msg = "🏆  " + name.toUpperCase() + " WINS THE MATCH!\n"
                + "Score:  " + p1Name + "  " + p1Wins + "  —  " + p2Wins + "  " + p2Name;

        logMessage("\n╔══════════════════════════════════════╗");
        logMessage("   🏆  MATCH WINNER:  " + name.toUpperCase());
        logMessage("   Score: " + p1Name + " " + p1Wins + " — " + p2Wins + " " + p2Name);
        logMessage("╚══════════════════════════════════════╝\n");

        int choice = JOptionPane.showOptionDialog(this,
                msg, "Match Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Back to Menu"},
                "Play Again");

        if (choice == 0) restartMatch();
        else             returnToMenu();
    }

    private void showDraw() {
        logMessage("\n★  IT'S A DRAW!  Both fighters are evenly matched!\n");

        int choice = JOptionPane.showOptionDialog(this,
                "It's a DRAW!\nScore: " + p1Name + " " + p1Wins + " — " + p2Wins + " " + p2Name,
                "Match Draw",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Back to Menu"},
                "Play Again");

        if (choice == 0) restartMatch();
        else             returnToMenu();
    }

    private void restartMatch() {
        dispose();
        new ConsolePvPBattle(createFreshEntity(p1Name), createFreshEntity(p2Name)).setVisible(true);
    }



    private void returnToMenu() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Return to the main menu?\nCurrent match progress will be lost.",
                "Back to Menu",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            try {
                Class<?> menuClass = Class.forName("GameModeMenu");
                java.lang.reflect.Constructor<?> ctor = menuClass.getConstructor(String.class);
                JFrame menu = (JFrame) ctor.newInstance(p1Name);
                menu.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // HELPERS
    private void refreshBars() {
        p2HealthBar.setValue(player2.getCurrentHealth());
        updateBarColor(p2HealthBar, player2);
        p2StatsLabel.setText("Mana: " + player2.getCurrentMana());

        p1HealthBar.setValue(player1.getCurrentHealth());
        p1HealthBar.setString("HP: " + player1.getCurrentHealth() + "/" + player1.getMaxHealth());
        updateBarColor(p1HealthBar, player1);
        p1StatsLabel.setText("Mana: " + player1.getCurrentMana());
    }

    private void updateBarColor(JProgressBar bar, Entity e) {
        double ratio = (double) e.getCurrentHealth() / e.getMaxHealth();
        bar.setForeground(ratio > 0.4 ? HP_GREEN : HP_LOW);
    }

    private void updateScoreLabel() {
        scoreLabel.setText(p1Name + "  " + p1Wins + "  —  " + p2Wins + "  " + p2Name);
    }

    private void disableSkills() {
        skillsPanel.removeAll();
        skillsPanel.revalidate();
        skillsPanel.repaint();
    }

    private JProgressBar makeHealthBar(Entity e) {
        JProgressBar bar = new JProgressBar(0, e.getMaxHealth());
        bar.setValue(e.getCurrentHealth());
        bar.setPreferredSize(new Dimension(380, 26));
        bar.setForeground(HP_GREEN);
        bar.setBackground(new Color(40, 40, 40));
        bar.setStringPainted(false);
        bar.setBorderPainted(false);
        return bar;
    }

    private Entity createFreshEntity(String name) {
        switch (name) {
            case "Mark":   return new Mark();
            case "Ted":    return new Ted();
            case "Den":    return new Den();
            case "Ashley": return new Ashley();
            case "Vince":  return new Vince();
            case "Zack":   return new Zack();
            case "Clent":  return new Clent();
            case "Trone":  return new Trone();
            default:       return new Mark();
        }
    }

    private void startTurnTimer() {
        if (turnTimer != null && turnTimer.isRunning()) {
            turnTimer.stop();
        }

        timeLeft = 10;
        timerLabel.setText("⏳ Time Left: " + timeLeft);

        turnTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("⏳ Time Left: " + timeLeft);

            if (timeLeft <= 0) {
                turnTimer.stop();
                applyTimeoutPunishment();
            }
        });

        turnTimer.start();
    }

    private void applyTimeoutPunishment() {
        Entity active = isPlayer1Turn ? player1 : player2;
        Entity opponent = isPlayer1Turn ? player2 : player1;

        logMessage("⏰ TIME OUT! " + active.getName() + " failed to act!");
        logMessage("⚠ " + opponent.getName() + " gets a FREE ATTACK!");

        Skill punishSkill = opponent.getSkills().get(0);
        punishSkill.apply(opponent, active, this);

        refreshBars();

        if (active.isDead()) {
            if (isPlayer1Turn) p2Wins++;
            else p1Wins++;

            disableSkills();

            Timer t = new Timer(1800, e -> checkRoundOver());
            t.setRepeats(false);
            t.start();
            return;
        }

        isPlayer1Turn = !isPlayer1Turn;
        updateTurnUI();
    }


    @Override
    public void logMessage(String message) {
        combatLog.append(message + "\n");
        combatLog.setCaretPosition(combatLog.getDocument().getLength());
    }

    // ── QUICK TEST
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ConsolePvPBattle(new Mark(), new Ted()).setVisible(true));
    }
}
