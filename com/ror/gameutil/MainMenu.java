package com.ror.gameutil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
<<<<<<< HEAD

public class MainMenu extends JDialog {

    public MainMenu(JFrame parentFrame, Runnable onResume, Runnable onQuit) {
        this(parentFrame, onResume, () -> {
            new GameModeMenu("Player").setVisible(true);
            parentFrame.dispose();
        }, onQuit);
    }

    public MainMenu(JFrame parentFrame, Runnable onResume, Runnable onBackToMenu, Runnable onQuit) {
        super(parentFrame, true);

        setUndecorated(true);
        setSize(350, 450);
        setLocationRelativeTo(parentFrame);

        setBackground(new Color(0, 0, 0, 180));

        initUI(onResume, onBackToMenu, onQuit);
    }

    private void initUI(Runnable onResume, Runnable onBackToMenu, Runnable onQuit) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel title = new JLabel("PAUSED");
        title.setFont(new Font("Monospaced", Font.BOLD, 40));
        title.setForeground(new Color(255, 215, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton resumeBtn = styleButton("RESUME", new Color(50, 200, 50));
        // FIX: Renamed 'e' to 'ignored'
        resumeBtn.addActionListener(ignored -> {
            dispose();
            if (onResume != null) onResume.run();
        });

        JButton backToMenuBtn = styleButton("BACK TO MENU", new Color(30, 144, 255));
        // FIX: Renamed 'e' to 'ignored'
        backToMenuBtn.addActionListener(ignored -> {
            dispose();
            if (onBackToMenu != null) onBackToMenu.run();
        });

        JButton quitBtn = styleButton("QUIT", new Color(220, 20, 60));
        // FIX: Renamed 'e' to 'ignored'
        quitBtn.addActionListener(ignored -> {
            dispose();
            if (onQuit != null) onQuit.run();
        });

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
        panel.add(resumeBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backToMenuBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
=======
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JDialog {

    // We pass "Runnables" so this menu can trigger different code
    // depending on which game mode it is currently floating over!
    public MainMenu(JFrame parentFrame, Runnable onResume, Runnable onQuit) {
        // 'true' makes it modal (blocks the user from clicking the game behind it)
        super(parentFrame, true);

        // Removes the default Windows/Mac title bar so it looks like a clean game overlay
        setUndecorated(true);
        setSize(300, 400);
        setLocationRelativeTo(parentFrame); // Centers it perfectly over the game

        // Creates a semi-transparent black background
        setBackground(new Color(0, 0, 0, 180));

        initUI(onResume, onQuit);
    }

    private void initUI(Runnable onResume, Runnable onQuit) {
        // Main panel for the menu
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false); // Let the transparent dialog background show through
        panel.setBorder(new EmptyBorder(50, 20, 50, 20));

        // PAUSE TITLE
        JLabel title = new JLabel("PAUSED");
        title.setFont(new Font("Monospaced", Font.BOLD, 40));
        title.setForeground(new Color(255, 215, 0)); // Arcade Gold
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // RESUME BUTTON
        JButton resumeBtn = styleButton("RESUME", new Color(50, 200, 50));
        resumeBtn.addActionListener(e -> {
            dispose(); // Close this floating menu
            if (onResume != null) onResume.run(); // Tell the game to unpause!
        });

        // QUIT BUTTON
        JButton quitBtn = styleButton("QUIT", new Color(220, 20, 60));
        quitBtn.addActionListener(e -> {
            dispose(); // Close this floating menu
            if (onQuit != null) onQuit.run(); // Tell the engine to load the Intro Screen!
        });

        // Add everything to the panel with some spacing
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 60)));
        panel.add(resumeBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
>>>>>>> ba45c2d (my local project changes)
        panel.add(quitBtn);

        setContentPane(panel);
    }

<<<<<<< HEAD
=======
    // A helper method to keep your arcade button styles consistent
>>>>>>> ba45c2d (my local project changes)
    private JButton styleButton(String text, Color outlineColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 20));
        btn.setBackground(new Color(20, 20, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
<<<<<<< HEAD
        btn.setMaximumSize(new Dimension(250, 50));
=======
        btn.setMaximumSize(new Dimension(200, 50));
>>>>>>> ba45c2d (my local project changes)

        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(outlineColor, 2),
                new EmptyBorder(10, 20, 10, 20)
        ));

        return btn;
    }
}