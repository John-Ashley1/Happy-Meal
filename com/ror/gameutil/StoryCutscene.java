package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;

public class StoryCutscene extends JFrame {

    private Entity selectedHero;
    private Entity enemy;

    // --- THE STORY SCRIPT ---
    private String[] storyText = {
            // Slide 1 (t1.png)
            "Long ago, in a world illuminated by neon signs\nand the smell of slightly overpriced stadium snacks,\nthere stood a city.",

            // Slide 2.1 (t2.png)
            "Once a year, the city hosts the legendary\nHappy Meal Tournament.\n\nLegends say the winner doesn't just receive a trophy.\nThey receive The Happy Meal...",

            // Slide 2.2 (t3.png)
            "...an ancient, golden artifact said to grant the victor\nabsolute joy, supreme cosmic power, and...\n\na really, really cool limited-edition plastic toy\nthat you can’t get anywhere else.",

            // Slide 4 (t4.png)
            "To win the toy—er, the power—\nwarriors from across the digital plains gather.\n\nBut nobody is here just to fight.\nEveryone has a vibe.",

            // Slide 5 (t5.png)
            "Some fight to protect their honor,\nvowing to avenge their clan...\n\n(who mostly just got disqualified last year\nfor bringing outside snacks into the arena).",

            // Slide 6.1 (t6.png)
            "They are weirdos. They are heroes.\nThey are everything in between.\n\nAnd then... there is You.",

            // Slide 6.2 (t6.png)
            "You aren't exactly sure how you ended up\nin the sign-up line.\n\nMaybe you got lost on the way to the bathroom.\nMaybe destiny called your name.",

            // Slide 7 (t7.png) - THE FINALE!
            "  "
    };

    // --- THE IMAGE MAP ---
    private String[] imageFiles = {
            "t1.png",
            "t2.png",
            "t3.png",
            "t4.png",
            "t5.png",
            "t6.png",
            "t6.png",
            "t7.png"
    };

    private Image[] storyImages;

    private int currentSlide = 0;
    private String displayedText = "";
    private int charIndex = 0;

    // Animation Engine Variables
    private Timer gameLoop;
    private float alpha = 0.0f;
    private float panY = 0.0f;
    private int tickCounter = 0;
    private boolean isTyping = false;

    private JPanel cinematicPanel;

    public StoryCutscene(Entity selectedHero, Entity enemy) {
        this.selectedHero = selectedHero;
        this.enemy = enemy;
        this.storyImages = new Image[storyText.length];

        loadImages();

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

                // 1. Draw Pitch Black Background
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 2. Draw Fading & Panning Image
                if (storyImages[currentSlide] != null) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                    Image img = storyImages[currentSlide];

                    int targetW = 750;
                    int targetH = 420;
                    int imgX = (getWidth() - targetW) / 2;
                    int imgY = 60 + (int) panY;

                    g2d.drawImage(img, imgX, imgY, targetW, targetH, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }

                // 3. Draw the Typewriter Text
                g2d.setColor(Color.WHITE);

                // Make the final "Are you ready?" text larger for dramatic effect
                if (currentSlide == storyText.length - 1) {
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 36));
                } else {
                    g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
                }

                FontMetrics fm = g2d.getFontMetrics();
                String[] lines = displayedText.split("\n");
                int lineHeight = fm.getHeight();
                int startY = getHeight() - 150;

                // Shift text down slightly if it's the final slide so it's perfectly centered
                if (currentSlide == storyText.length - 1) {
                    startY = getHeight() - 100;
                }

                for (int i = 0; i < lines.length; i++) {
                    int textWidth = fm.stringWidth(lines[i]);
                    int x = (getWidth() - textWidth) / 2;
                    g2d.drawString(lines[i], x, startY + (i * lineHeight));
                }

                // 4. Draw the blinking prompt when typing is done
                if (!isTyping && alpha >= 1.0f) {
                    g2d.setColor(Color.YELLOW);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 16));
                    g2d.drawString("Press [SPACE]", getWidth() - 200, getHeight() - 40);
                }
            }
        };

        add(cinematicPanel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    advanceCutscene();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    skipCutscene();
                }
            }
        });

        startAnimationLoop();
        startSlide();
    }

    private void loadImages() {
        for (int i = 0; i < imageFiles.length; i++) {
            try {
                java.net.URL url = getClass().getResource("/images/storyImages/" + imageFiles[i]);
                if (url != null) {
                    storyImages[i] = ImageIO.read(url);
                }
            } catch (Exception e) {
                System.out.println("Could not load story image: " + imageFiles[i]);
            }
        }
    }

    private void startAnimationLoop() {
        gameLoop = new Timer(16, e -> {
            tickCounter++;

            if (alpha < 1.0f) {
                alpha += 0.015f;
                if (alpha > 1.0f) alpha = 1.0f;
            }

            panY -= 0.15f;

            if (isTyping && tickCounter % 3 == 0) {
                String fullText = storyText[currentSlide];
                if (charIndex < fullText.length()) {
                    displayedText += fullText.charAt(charIndex);
                    charIndex++;
                } else {
                    isTyping = false;
                }
            }

            cinematicPanel.repaint();
        });
        gameLoop.start();
    }

    private void startSlide() {
        if (currentSlide >= storyText.length) {
            endCutscene();
            return;
        }

        displayedText = "";
        charIndex = 0;
        isTyping = true;
        tickCounter = 0;

        // Seamless Image Transitions
        boolean isNewImage = (currentSlide == 0) || !imageFiles[currentSlide].equals(imageFiles[currentSlide - 1]);

        if (isNewImage) {
            alpha = 0.0f;
            panY = 20.0f;
        }
    }

    private void advanceCutscene() {
        if (isTyping || alpha < 1.0f) {
            displayedText = storyText[currentSlide];
            isTyping = false;
            alpha = 1.0f;
            cinematicPanel.repaint();
        } else {
            currentSlide++;
            startSlide();
        }
    }

    private void skipCutscene() {
        if (gameLoop != null) gameLoop.stop();
        endCutscene();
    }

    private void endCutscene() {
        if (gameLoop != null) gameLoop.stop();
        dispose();
        new ArcadeFrame(selectedHero, enemy).setVisible(true);
    }
}