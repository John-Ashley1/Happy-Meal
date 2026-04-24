package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Playable.Mark;
import com.ror.gamemodel.Playable.Ted;

import javax.swing.*;

public class ArcadeFrame extends JFrame {

    public ArcadeFrame(Entity player, Entity enemy) {
        setTitle("Happy Meal Tournament - ARCADE SURVIVAL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Add our custom game engine panel to the window
        ArcadeGamePanel gamePanel = new ArcadeGamePanel();
        add(gamePanel);

        pack(); // Automatically sizes the window to fit the 800x600 panel perfectly
        setLocationRelativeTo(null); // Center on screen

        // Start the 60 FPS loop!
        gamePanel.startGameThread();
    }

    // --- ADDED THIS SO YOU CAN CLICK RUN/DEBUG! ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("DEBUG: Starting 2D Arcade Engine in isolation...");

            // Create two quick dummy characters so the frame doesn't crash
            Entity p1 = new Mark();
            Entity p2 = new Ted();

            new ArcadeFrame(p1, p2).setVisible(true);
        });
    }
}