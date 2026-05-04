package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;
import com.ror.gamemodel.Playable.Mark;
import com.ror.gamemodel.Playable.Ted;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GuiBattleArena extends JFrame implements BattleView {

    // Data Models
    private Entity player1;
    private Entity player2;
    private boolean isPlayer1Turn = true;
    private String mode;

    // UI Components
    private JProgressBar topHealthBar, bottomHealthBar;
    private JLabel topNameLabel, bottomNameLabel, bottomStatsLabel;
    private JTextArea combatLog;
    private JPanel skillsPanel;

    public GuiBattleArena(Entity player1, Entity player2, String mode) {
        this.player1 = player1;
        this.player2 = player2;
        this.mode = mode;

        // 1. Window Setup
        setTitle("Happy Meal Tournament - Battle");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout(10, 10));

        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // 2. Build the UI Sections
        buildTopPanel();
        buildCenterLog();
        buildBottomPanel();

        // --- NEW: Attach the global Escape key listener! ---
        setupPauseMenu();

        // 3. Start the Game!
        logMessage("A battle begins! " + player1.getName() + " vs. " + player2.getName() + ".");
        updateTurnUI();
    }

    // --- NEW: THE PAUSE MENU LOGIC ---
    private void setupPauseMenu() {
        // Create the action we want to happen when ESC is pressed
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Summon the reusable Main Menu!
                MainMenu pauseMenu = new MainMenu(
                        GuiBattleArena.this,

                        // ACTION A: Resume
                        () -> {
                            // Because it's turn-based, we don't need to restart a loop.
                            // The dialog closing is enough to unfreeze the screen!
                        },

                        // ACTION B: Quit
                        () -> {
                            // Go back to the Intro Screen and destroy the arena
                            new IntroScreen().setVisible(true);
                            dispose();
                        }
                );

                pauseMenu.setVisible(true); // Show the menu and freeze the arena
            }
        };

        // Bind the ESCAPE key to our action across the ENTIRE window
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private void buildTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(Color.BLACK);
        topPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        topNameLabel = new JLabel(player2.getName());
        topNameLabel.setForeground(Color.WHITE);
        topNameLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        topHealthBar = new JProgressBar(0, player2.getMaxHealth());
        topHealthBar.setValue(player2.getCurrentHealth());
        topHealthBar.setPreferredSize(new Dimension(300, 25));
        topHealthBar.setForeground(Color.GREEN);
        topHealthBar.setBackground(Color.DARK_GRAY);
        topHealthBar.setStringPainted(false);

        topPanel.add(topNameLabel);
        topPanel.add(topHealthBar);

        add(topPanel, BorderLayout.NORTH);
    }

    private void buildCenterLog() {
        combatLog = new JTextArea();
        combatLog.setBackground(Color.BLACK);
        combatLog.setForeground(Color.WHITE);
        combatLog.setFont(new Font("Monospaced", Font.PLAIN, 16));
        combatLog.setEditable(false);
        combatLog.setLineWrap(true);
        combatLog.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(combatLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        scrollPane.getVerticalScrollBar().setBackground(Color.BLACK);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void buildBottomPanel() {
        JPanel bottomWrapper = new JPanel(new BorderLayout(0, 10));
        bottomWrapper.setBackground(Color.BLACK);

        JPanel statusBox = new JPanel(new BorderLayout(15, 0));
        statusBox.setBackground(Color.BLACK);
        statusBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        bottomNameLabel = new JLabel(player1.getName());
        bottomNameLabel.setForeground(Color.WHITE);
        bottomNameLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        bottomHealthBar = new JProgressBar(0, player1.getMaxHealth());
        bottomHealthBar.setValue(player1.getCurrentHealth());
        bottomHealthBar.setPreferredSize(new Dimension(400, 30));
        bottomHealthBar.setForeground(Color.GREEN);
        bottomHealthBar.setBackground(Color.DARK_GRAY);
        bottomHealthBar.setStringPainted(true);

        bottomStatsLabel = new JLabel("Mana: " + player1.getCurrentMana());
        bottomStatsLabel.setForeground(Color.WHITE);
        bottomStatsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        statusBox.add(bottomNameLabel, BorderLayout.WEST);
        statusBox.add(bottomHealthBar, BorderLayout.CENTER);
        statusBox.add(bottomStatsLabel, BorderLayout.EAST);

        skillsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        skillsPanel.setBackground(Color.BLACK);
        skillsPanel.setPreferredSize(new Dimension(800, 60));

        bottomWrapper.add(statusBox, BorderLayout.NORTH);
        bottomWrapper.add(skillsPanel, BorderLayout.SOUTH);

        add(bottomWrapper, BorderLayout.SOUTH);
    }

    private void updateTurnUI() {
        Entity activePlayer = isPlayer1Turn ? player1 : player2;

        logMessage("\n--- " + activePlayer.getName().toUpperCase() + "'S TURN ---");

        for (Skill skill : activePlayer.getSkills()) {
            skill.reduceCooldown();
        }

        topHealthBar.setValue(player2.getCurrentHealth());
        bottomHealthBar.setValue(player1.getCurrentHealth());
        bottomHealthBar.setString("HP: " + player1.getCurrentHealth() + "/" + player1.getMaxHealth());
        bottomStatsLabel.setText("Mana: " + player1.getCurrentMana());

        skillsPanel.removeAll();

        for (Skill skill : activePlayer.getSkills()) {
            JButton skillBtn = new JButton();
            skillBtn.setBackground(Color.BLACK);
            skillBtn.setForeground(Color.WHITE);
            skillBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
            skillBtn.setFocusPainted(false);
            skillBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

            if (skill.isReady()) {
                skillBtn.setText(skill.getName());
                skillBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                skillBtn.addActionListener(e -> executeSkill(skill, activePlayer));
            } else {
                skillBtn.setText(skill.getName() + " (CD: " + skill.getCooldown() + ")");
                skillBtn.setForeground(Color.GRAY);
                skillBtn.setEnabled(false);
            }
            skillsPanel.add(skillBtn);
        }

        skillsPanel.revalidate();
        skillsPanel.repaint();
    }

    private void executeSkill(Skill skill, Entity activePlayer) {
        Entity targetPlayer = isPlayer1Turn ? player2 : player1;

        skill.apply(activePlayer, targetPlayer, this);
        skill.resetCooldown();

        if (targetPlayer.isDead()) {
            updateHealthBarsFinal();
            logMessage("\n*** K.O.! " + targetPlayer.getName() + " has been defeated! ***");
            logMessage("*** WINNER: " + activePlayer.getName().toUpperCase() + " ***");

            skillsPanel.removeAll();

            JButton returnBtn = new JButton("CONTINUE");
            returnBtn.setBackground(new Color(50, 200, 50));
            returnBtn.setForeground(Color.WHITE);
            returnBtn.setFont(new Font("Monospaced", Font.BOLD, 18));
            returnBtn.setFocusPainted(false);
            returnBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            returnBtn.addActionListener(e -> routeToNextScreen());

            skillsPanel.add(returnBtn);

            skillsPanel.revalidate();
            skillsPanel.repaint();
            return;
        }

        isPlayer1Turn = !isPlayer1Turn;
        updateTurnUI();
    }

    private void updateHealthBarsFinal() {
        topHealthBar.setValue(player2.getCurrentHealth());
        bottomHealthBar.setValue(player1.getCurrentHealth());
        bottomHealthBar.setString("HP: " + player1.getCurrentHealth() + "/" + player1.getMaxHealth());
    }

    private void routeToNextScreen() {
        dispose();

        if ("Arcade".equalsIgnoreCase(mode)) {
            player1.heal(player1.getMaxHealth());
            new ArcadeFrame(player1, player2).setVisible(true);
        } else if ("PvP".equalsIgnoreCase(mode) || "PvAI".equalsIgnoreCase(mode)) {
            new HeroSelection("Player", mode, "Normal").setVisible(true);
        } else {
            new GameModeMenu("Player").setVisible(true);
        }
    }

    @Override
    public void logMessage(String message) {
        combatLog.append(message + "\n");
        combatLog.setCaretPosition(combatLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Entity p1 = new Mark();
            Entity p2 = new Ted();
            new GuiBattleArena(p1, p2, "Test").setVisible(true);
        });
    }
}