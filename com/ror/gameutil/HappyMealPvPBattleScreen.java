package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class HappyMealPvPBattleScreen extends JFrame {

    private Entity player1, player2;
    private boolean isPlayer1Turn = true;
    private boolean isPVE = false;

    // UI Components
    private JLabel lblPlayer1, lblPlayer2;
    private JProgressBar pbHealthP1, pbManaP1, pbHealthP2, pbManaP2;
    private JTextArea txtBattleLog;
    private JButton[] skillButtonsP1 = new JButton[3];
    private JButton[] skillButtonsP2 = new JButton[3];

    private ImageIcon p1DefaultIcon, p2DefaultIcon;

    public HappyMealPvPBattleScreen(Entity p1, Entity p2, boolean isPVE) {
        this.player1 = p1;
        this.player2 = p2;
        this.isPVE = isPVE;

        initComponents();
        setCharacterImages();
        updateUI();
        logMessage("Battle Start! " + p1.getName() + " vs " + p2.getName());
    }

    private void initComponents() {
        setTitle("Happy Meal Tournament - Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel battlePanel = new JPanel(new GridBagLayout());
        battlePanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        // === PLAYER 1 SIDE ===
        lblPlayer1 = new JLabel();
        lblPlayer1.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
        lblPlayer1.setPreferredSize(new Dimension(450, 450));

        pbHealthP1 = createProgressBar("HP", Color.RED);
        pbManaP1 = createProgressBar("MANA", Color.BLUE);

        JPanel p1Panel = new JPanel(new BorderLayout(0, 10));
        p1Panel.setOpaque(false);
        p1Panel.add(new JLabel(player1.getName(), SwingConstants.CENTER), BorderLayout.NORTH);
        p1Panel.add(lblPlayer1, BorderLayout.CENTER);
        p1Panel.add(createBarPanel(pbHealthP1, pbManaP1), BorderLayout.SOUTH);

        // === PLAYER 2 SIDE ===
        lblPlayer2 = new JLabel();
        lblPlayer2.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
        lblPlayer2.setPreferredSize(new Dimension(450, 450));

        pbHealthP2 = createProgressBar("HP", Color.RED);
        pbManaP2 = createProgressBar("MANA", Color.BLUE);

        JPanel p2Panel = new JPanel(new BorderLayout(0, 10));
        p2Panel.setOpaque(false);
        p2Panel.add(new JLabel(player2.getName(), SwingConstants.CENTER), BorderLayout.NORTH);
        p2Panel.add(lblPlayer2, BorderLayout.CENTER);
        p2Panel.add(createBarPanel(pbHealthP2, pbManaP2), BorderLayout.SOUTH);

        // === BATTLE LOG ===
        txtBattleLog = new JTextArea();
        txtBattleLog.setEditable(false);
        txtBattleLog.setBackground(Color.BLACK);
        txtBattleLog.setForeground(Color.YELLOW);
        txtBattleLog.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane logScroll = new JScrollPane(txtBattleLog);
        logScroll.setPreferredSize(new Dimension(1200, 180));

        // === SKILL BUTTONS ===
        JPanel skillsP1 = createSkillPanel(skillButtonsP1, true);
        JPanel skillsP2 = createSkillPanel(skillButtonsP2, false);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        battlePanel.add(p1Panel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        battlePanel.add(p2Panel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        battlePanel.add(logScroll, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1;
        battlePanel.add(skillsP1, gbc);

        gbc.gridx = 1;
        battlePanel.add(skillsP2, gbc);

        add(battlePanel, BorderLayout.CENTER);
        pack();
    }

    private JProgressBar createProgressBar(String title, Color color) {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        bar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return bar;
    }

    private JPanel createBarPanel(JProgressBar health, JProgressBar mana) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 5));
        panel.setOpaque(false);
        panel.add(health);
        panel.add(mana);
        return panel;
    }

    private JPanel createSkillPanel(JButton[] buttons, boolean isP1) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);

        for (int i = 0; i < 3; i++) {
            buttons[i] = new JButton("Skill " + (i+1));
            buttons[i].setFont(new Font("Monospaced", Font.BOLD, 16));
            buttons[i].setPreferredSize(new Dimension(200, 80));
            buttons[i].addActionListener(e -> useSkill((JButton)e.getSource(), isP1));
            panel.add(buttons[i]);
        }
        return panel;
    }

    private void setCharacterImages() {
        try {
            p1DefaultIcon = new ImageIcon(getClass().getResource("/images/characters/" +
                    player1.getName().toLowerCase().replace("happy ", "") + ".jpg"));
            p2DefaultIcon = new ImageIcon(getClass().getResource("/images/characters/" +
                    player2.getName().toLowerCase().replace("happy ", "") + ".jpg"));

            lblPlayer1.setIcon(p1DefaultIcon);
            lblPlayer2.setIcon(p2DefaultIcon);
        } catch (Exception e) {
            System.out.println("Image not found for character");
        }
    }

    private void updateUI() {
        pbHealthP1.setMaximum(player1.getMaxHealth());
        pbHealthP1.setValue(player1.getCurrentHealth());
        pbHealthP1.setString("HP: " + player1.getCurrentHealth() + "/" + player1.getMaxHealth());

        pbManaP1.setMaximum(player1.getMaxMana());
        pbManaP1.setValue(player1.getCurrentMana());
        pbManaP1.setString("MANA: " + player1.getCurrentMana() + "/" + player1.getMaxMana());

        pbHealthP2.setMaximum(player2.getMaxHealth());
        pbHealthP2.setValue(player2.getCurrentHealth());
        pbHealthP2.setString("HP: " + player2.getCurrentHealth() + "/" + player2.getMaxHealth());

        pbManaP2.setMaximum(player2.getMaxMana());
        pbManaP2.setValue(player2.getCurrentMana());
        pbManaP2.setString("MANA: " + player2.getCurrentMana() + "/" + player2.getMaxMana());

        updateSkillButtons();
        checkGameOver();
    }

    private void updateSkillButtons() {
        Entity active = isPlayer1Turn ? player1 : player2;
        JButton[] buttons = isPlayer1Turn ? skillButtonsP1 : skillButtonsP2;

        for (int i = 0; i < 3; i++) {
            Skill skill = active.getSkills().get(i);
            buttons[i].setText(skill.getName() + (skill.isReady() ? "" : " (CD)"));
            buttons[i].setEnabled(skill.isReady() && isPlayer1Turn == (active == player1));
        }
    }

    private void useSkill(JButton btn, boolean isP1) {
        Entity attacker = isP1 ? player1 : player2;
        Entity target = isP1 ? player2 : player1;

        int index = -1;
        JButton[] btns = isP1 ? skillButtonsP1 : skillButtonsP2;
        for (int i = 0; i < 3; i++) {
            if (btns[i] == btn) index = i;
        }

        Skill skill = attacker.getSkills().get(index);
        if (!skill.isReady()) return;

        logMessage("\n=== " + attacker.getName() + "'s TURN ===");
        skill.apply(attacker, target, this::logMessage);
        skill.resetCooldown();

        playSkillAnimation(isP1 ? lblPlayer1 : lblPlayer2);

        updateUI();

        if (!target.isDead()) {
            isPlayer1Turn = !isPlayer1Turn;
            updateSkillButtons();
        }
    }

    private void playSkillAnimation(JLabel label) {
        // Simple flash effect for now (can be upgraded to GIFs like NBA)
        Color original = label.getBorder().getClass().equals(javax.swing.border.LineBorder.class) ?
                ((javax.swing.border.LineBorder)label.getBorder()).getLineColor() : Color.WHITE;

        label.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
        Timer t = new Timer(400, e -> label.setBorder(BorderFactory.createLineBorder(original, 3)));
        t.setRepeats(false);
        t.start();
    }

    public void logMessage(String message) {
        txtBattleLog.append(message + "\n");
        txtBattleLog.setCaretPosition(txtBattleLog.getDocument().getLength());
    }

    private void checkGameOver() {
        if (player1.isDead() || player2.isDead()) {
            String winner = player1.isDead() ? player2.getName() : player1.getName();
            JOptionPane.showMessageDialog(this, "🎉 " + winner + " WINS THE BATTLE!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            // Return to menu or next match
        }
    }

    // For testing
    public static void main(String[] args) {
        Entity p1 = new com.ror.gamemodel.Playable.Mark();
        Entity p2 = new com.ror.gamemodel.Playable.Ted();
        new HappyMealPvPBattleScreen(p1, p2, false).setVisible(true);
    }
}
