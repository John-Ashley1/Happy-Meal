package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Player;
import com.ror.gamemodel.Playable.Mark;
import com.ror.gamemodel.Playable.Ted;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ArcadeGamePanel extends JPanel implements Runnable, KeyListener {

    private Thread gameThread;
    private final int FPS = 60;
    private volatile boolean running = false;

    // --- NEW: Dialogue System Variables ---
    private DialogueOverlay dialogue;
    private boolean isDialogueActive = true;

    // --- TILEMAP VARIABLES ---
    private BufferedImage floorTile;
    private BufferedImage tlTop, tmTop, trTop, trbTop, tlbTop, tmbTop;
    private BufferedImage leftWall, rightWall;
    private BufferedImage blWall, bmWall, brWall;
    private BufferedImage tdlWall, tdmWall, tdrWall, bdrWall, bdlWall, bdmWall;
    private BufferedImage itdlWall, itdmWall, itdrWall, ibdrWall, ibdlWall, ibdmWall;

    private boolean isNearDoor = false;
    private final int TILE_SIZE = 16;

    private int[][] roomMap = {
            {1, 2, 2, 2, 2, 2, 2, 12, 13, 14, 2, 2, 2, 2, 2, 2, 3},
            {4, 6, 6, 6, 6, 6, 6, 15, 16, 17, 6, 6, 6, 6, 6, 6, 5},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8},
            {9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11}
    };

    private Player player;
    private Entity currentHero;
    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    public ArcadeGamePanel(Entity selectedHero) {
        setPreferredSize(new Dimension(816, 624));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(this);
        setFocusable(true);

        loadSprites();

        // --- NEW: STRICTLY ENFORCE MARK AS THE ARCADE HERO ---
        // We completely ignore the 'selectedHero' passed into the constructor.
        this.currentHero = new Mark();

        // Hardcode Mark's animation assets since he is the sole Arcade protagonist
        String animationFolder = "soldier";
        String spritePrefix = "Soldier";

        player = new Player(animationFolder, spritePrefix, TILE_SIZE);

        // --- Initialize the Story Dialogue! ---
        String[] story = {
                "You have entered the arena, " + currentHero.getName() + "...",
                "The air is cold, and the shadows hide your enemies.",
                "Beyond that door lies the Joy Arena.",
                "Defeat the enemy ahead to prove your worth."
        };
        dialogue = new DialogueOverlay(story);
    }

    private void loadSprites() {
        try {
            BufferedImage floorAtlas = ImageIO.read(new File("images/map/atlas_floor-16x16.png"));
            floorTile = floorAtlas.getSubimage(0, 0, 16, 16);

            BufferedImage wallAtlas = ImageIO.read(new File("images/map/atlas_walls_low-16x16-Sheet.png"));

            tlTop = wallAtlas.getSubimage(16, 0, 16, 16);
            tmTop = wallAtlas.getSubimage(32, 48, 16, 16);
            trTop = wallAtlas.getSubimage(48, 0, 16, 16);

            tlbTop = wallAtlas.getSubimage(144, 16, 16, 16);
            trbTop = wallAtlas.getSubimage(160, 16, 16, 16);
            tmbTop = wallAtlas.getSubimage(160, 32, 16, 16);

            leftWall = wallAtlas.getSubimage(0, 16, 16, 16);
            rightWall = wallAtlas.getSubimage(0, 0, 16, 16);

            blWall = wallAtlas.getSubimage(16, 32, 16, 16);
            bmWall = wallAtlas.getSubimage(32, 48, 16, 16);
            brWall = wallAtlas.getSubimage(48, 32, 16, 16);

            BufferedImage doorWall = ImageIO.read(new File("images/map/atlas_walls_high-16x32-Sheet.png"));

            tdlWall = doorWall.getSubimage(272, 96, 16, 16);
            tdmWall = doorWall.getSubimage(288, 96, 16, 16);
            tdrWall = doorWall.getSubimage(304, 96, 16, 16);

            bdrWall = doorWall.getSubimage(304, 112, 16, 16);
            bdlWall = doorWall.getSubimage(272, 112, 16, 16);
            bdmWall = doorWall.getSubimage(288, 112, 16, 16);

            itdlWall = doorWall.getSubimage(336, 96, 16, 16);
            itdmWall = doorWall.getSubimage(352, 96, 16, 16);
            itdrWall = doorWall.getSubimage(368, 96, 16, 16);

            ibdlWall = doorWall.getSubimage(336, 112, 16, 16);
            ibdmWall = doorWall.getSubimage(352, 112, 16, 16);
            ibdrWall = doorWall.getSubimage(368, 112, 16, 16);

        } catch (IOException e) {
            System.out.println("DEBUG: Could not load images! Check your file paths.");
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        running = true;
    }

    public void stopGameThread() {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }
            repaint();
        }
    }

    private void update() {
        // Freeze the game logic if the dialogue is active!
        if (isDialogueActive) return;

        int cols = roomMap[0].length;
        int rows = roomMap.length;

        int worldWidth = cols * TILE_SIZE;
        int worldHeight = rows * TILE_SIZE;

        int leftWallBounds = TILE_SIZE;
        int topWallBounds = 2 * TILE_SIZE;

        int rightWallBounds = worldWidth - TILE_SIZE - player.getPlayerWidth();
        int bottomWallBounds = worldHeight - TILE_SIZE - player.getPlayerHeight();

        player.update(upPressed, downPressed, leftPressed, rightPressed, spacePressed);
        player.clampPosition(leftWallBounds, rightWallBounds, topWallBounds, bottomWallBounds);

        if (player.isAttackFinished()) spacePressed = false;

        int doorLeftEdge = 6 * TILE_SIZE;
        int doorRightEdge = 10 * TILE_SIZE;
        int doorInteractionDepth = 4 * TILE_SIZE;

        if (player.getPlayerX() >= doorLeftEdge && player.getPlayerX() <= doorRightEdge &&
                player.getPlayerY() <= doorInteractionDepth) {
            isNearDoor = true;
        } else {
            isNearDoor = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.scale(3.0, 3.0);

        if (floorTile != null) {
            for (int row = 0; row < roomMap.length; row++) {
                for (int col = 0; col < roomMap[row].length; col++) {
                    g2d.drawImage(floorTile, col * TILE_SIZE, row * TILE_SIZE, null);
                }
            }
        }

        for (int row = 0; row < roomMap.length; row++) {
            for (int col = 0; col < roomMap[row].length; col++) {
                int tileID = roomMap[row][col];
                int xPos = col * TILE_SIZE;
                int yPos = row * TILE_SIZE;

                if (tileID == 1 && tlTop != null) g2d.drawImage(tlTop, xPos, yPos, null);
                else if (tileID == 2 && tmTop != null) g2d.drawImage(tmTop, xPos, yPos, null);
                else if (tileID == 3 && trTop != null) g2d.drawImage(trTop, xPos, yPos, null);

                else if (tileID == 4 && tlbTop != null) g2d.drawImage(tlbTop, xPos, yPos, null);
                else if (tileID == 5 && trbTop != null) g2d.drawImage(trbTop, xPos, yPos, null);
                else if (tileID == 6 && tmbTop != null) g2d.drawImage(tmbTop, xPos, yPos, null);

                else if (tileID == 7 && leftWall != null) g2d.drawImage(leftWall, xPos, yPos, null);
                else if (tileID == 8 && rightWall != null) g2d.drawImage(rightWall, xPos, yPos, null);

                else if (tileID == 9 && blWall != null) g2d.drawImage(blWall, xPos, yPos, null);
                else if (tileID == 10 && bmWall != null) g2d.drawImage(bmWall, xPos, yPos, null);
                else if (tileID == 11 && brWall != null) g2d.drawImage(brWall, xPos, yPos, null);

                else if (tileID >= 12 && tileID <= 17) {
                    if (isNearDoor) {
                        if (tileID == 12 && itdlWall != null) g2d.drawImage(itdlWall, xPos, yPos, null);
                        else if (tileID == 13 && itdmWall != null) g2d.drawImage(itdmWall, xPos, yPos, null);
                        else if (tileID == 14 && itdrWall != null) g2d.drawImage(itdrWall, xPos, yPos, null);
                        else if (tileID == 15 && ibdlWall != null) g2d.drawImage(ibdlWall, xPos, yPos, null);
                        else if (tileID == 16 && ibdmWall != null) g2d.drawImage(ibdmWall, xPos, yPos, null);
                        else if (tileID == 17 && ibdrWall != null) g2d.drawImage(ibdrWall, xPos, yPos, null);
                    } else {
                        if (tileID == 12 && tdlWall != null) g2d.drawImage(tdlWall, xPos, yPos, null);
                        else if (tileID == 13 && tdmWall != null) g2d.drawImage(tdmWall, xPos, yPos, null);
                        else if (tileID == 14 && tdrWall != null) g2d.drawImage(tdrWall, xPos, yPos, null);
                        else if (tileID == 15 && bdlWall != null) g2d.drawImage(bdlWall, xPos, yPos, null);
                        else if (tileID == 16 && bdmWall != null) g2d.drawImage(bdmWall, xPos, yPos, null);
                        else if (tileID == 17 && bdrWall != null) g2d.drawImage(bdrWall, xPos, yPos, null);
                    }
                }
            }
        }

        player.render(g2d, TILE_SIZE);

        if (isNearDoor && !isDialogueActive) {
            g2d.setFont(new Font("Monospaced", Font.BOLD, 8));
            g2d.setColor(Color.BLACK);
            g2d.drawString("[E] ENTER ARENA", (7 * TILE_SIZE) - 4, TILE_SIZE + 1);
            g2d.setColor(Color.YELLOW);
            g2d.drawString("[E] ENTER ARENA", (7 * TILE_SIZE) - 5, TILE_SIZE);
        }

        // Draw the dialogue box on top of everything!
        if (isDialogueActive) {
            dialogue.render(g2d, 272, 208);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Intercept all inputs if dialogue is running
        if (isDialogueActive) {
            if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                dialogue.advance();
                if (dialogue.isFinished()) {
                    isDialogueActive = false; // Unfreeze the game!
                }
            }
            return;
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_SPACE) spacePressed = true;

        if (code == KeyEvent.VK_E) {
            if (isNearDoor) {
                running = false;
                JFrame parentWindow = (JFrame) SwingUtilities.getWindowAncestor(this);
                parentWindow.dispose();

                Entity enemy = new Ted();
                new GuiBattleArena(currentHero, enemy, "Arcade").setVisible(true);
            }
        }

        if (code == KeyEvent.VK_ESCAPE) {
            running = false;
            JFrame parentWindow = (JFrame) SwingUtilities.getWindowAncestor(this);

            MainMenu pauseMenu = new MainMenu(
                    parentWindow,
                    () -> {
                        running = true;
                        startGameThread();
                    },
                    () -> {
                        new IntroScreen().setVisible(true);
                        parentWindow.dispose();
                    }
            );

            pauseMenu.setVisible(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isDialogueActive) return;

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}