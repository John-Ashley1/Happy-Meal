package com.ror.gameutil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

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
        panel.add(quitBtn);

        setContentPane(panel);
    }

    private JButton styleButton(String text, Color outlineColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 20));
        btn.setBackground(new Color(20, 20, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 50));

        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(outlineColor, 2),
                new EmptyBorder(10, 20, 10, 20)
        ));

        return btn;
    }
}