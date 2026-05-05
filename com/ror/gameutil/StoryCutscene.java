package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class StoryCutscene extends JFrame {

    private Entity selectedHero;
    private Entity enemy;

    // The "Slides" of your cutscene
    private String[] storyText = {
            "Long ago, the Joy Arena was a place of honor...",
            "But the darkness crept in, and the tournaments grew twisted.",
            "Only one hero is brave enough to face the Gauntlet.",
            "That hero is you."
    };

    // Optional: Add file paths to images if you want a visual slide for each text line!
    // private String[] storyImages = { "bg1.png", "bg2.png", "bg3.png", "hero.png" };

    private int currentSlide = 0;
    private String displayedText = "";
    private int charIndex = 0;
    private Timer typewriterTimer;
    private boolean isTyping = false;

    // A custom panel to draw our cinematic screen
    private JPanel cinematicPanel;

    public StoryCutscene(Entity selectedHero, Entity enemy) {
        this.selectedHero = selectedHero;
        this.enemy = enemy;

        setTitle("The Story Begins...");
        setSize(920, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cinematicPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // 1. Draw Background (Pitch Black)
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 2. (Optional) Draw a cinematic image here based on currentSlide

                // 3. Draw the Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 24));

                // Centering the text on the screen
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(displayedText);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() - 100; // Near the bottom

                g2d.drawString(displayedText, x, y);

                // 4. Draw the blinking "Press Space" prompt if typing is done
                if (!isTyping) {
                    g2d.setColor(Color.YELLOW);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 16));
                    g2d.drawString("Press [SPACE] to continue", getWidth() - 250, getHeight() - 40);
                }
            }
        };

        add(cinematicPanel);

        // Listen for the spacebar to advance the cutscene
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    advanceCutscene();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    skipCutscene(); // Allow players to skip!
                }
            }
        });

        startTyping();
    }

    private void startTyping() {
        if (currentSlide >= storyText.length) {
            endCutscene();
            return;
        }

        displayedText = "";
        charIndex = 0;
        isTyping = true;
        String fullText = storyText[currentSlide];

        typewriterTimer = new Timer(50, e -> {
            if (charIndex < fullText.length()) {
                displayedText += fullText.charAt(charIndex);
                charIndex++;
                cinematicPanel.repaint(); // Redraw the screen with the new letter
            } else {
                isTyping = false;
                typewriterTimer.stop();
                cinematicPanel.repaint(); // Show the yellow prompt
            }
        });
        typewriterTimer.start();
    }

    private void advanceCutscene() {
        if (isTyping) {
            // Instantly finish the sentence if they press space while typing
            typewriterTimer.stop();
            displayedText = storyText[currentSlide];
            isTyping = false;
            cinematicPanel.repaint();
        } else {
            // Move to the next slide
            currentSlide++;
            startTyping();
        }
    }

    private void skipCutscene() {
        if (typewriterTimer != null) typewriterTimer.stop();
        endCutscene();
    }

    private void endCutscene() {
        // Destroy the cinematic window and launch the actual game!
        dispose();
        new ArcadeFrame(selectedHero, enemy).setVisible(true);
    }
}