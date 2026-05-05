package com.ror.gameutil;

import java.awt.*;
import javax.swing.Timer;

public class DialogueOverlay {
    private String[] storyLines;
    private int currentLineIndex = 0;
    private String displayedText = "";
    private int charIndex = 0;

    private boolean isFinished = false;
    private boolean isTyping = false;
    private Timer typewriterTimer;

    public DialogueOverlay(String[] lines) {
        this.storyLines = lines;
        startTyping();
    }

    private void startTyping() {
        if (currentLineIndex >= storyLines.length) {
            isFinished = true;
            return;
        }

        displayedText = "";
        charIndex = 0;
        isTyping = true;
        String fullText = storyLines[currentLineIndex];

        // Types one character every 30 milliseconds
        typewriterTimer = new Timer(30, e -> {
            if (charIndex < fullText.length()) {
                displayedText += fullText.charAt(charIndex);
                charIndex++;
            } else {
                isTyping = false;
                typewriterTimer.stop();
            }
        });
        typewriterTimer.start();
    }

    public void advance() {
        if (isFinished) return;

        if (isTyping) {
            // If the player presses space while typing, skip the animation and show the full line
            typewriterTimer.stop();
            displayedText = storyLines[currentLineIndex];
            isTyping = false;
        } else {
            // If the line is fully typed, go to the next line
            currentLineIndex++;
            startTyping();
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void render(Graphics2D g2d, int screenWidth, int screenHeight) {
        if (isFinished) return;

        // Dimensions for the dialogue box (using the scaled 3.0 pixel coordinate system)
        int boxWidth = screenWidth - 20;
        int boxHeight = 45;
        int x = 10;
        int y = screenHeight - boxHeight - 10;

        // Draw semi-transparent background
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(x, y, boxWidth, boxHeight);

        // Draw white border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, boxWidth, boxHeight);

        // Draw the typing text
        g2d.setFont(new Font("Monospaced", Font.BOLD, 8));
        g2d.setColor(Color.WHITE);

        // Simple manual wrap for long strings (Optional: can be expanded for dynamic wrapping later)
        g2d.drawString(displayedText, x + 8, y + 16);

        // Draw the continue prompt when typing is done
        if (!isTyping) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("Press [SPACE] >", x + boxWidth - 85, y + boxHeight - 6);
        }
    }
}