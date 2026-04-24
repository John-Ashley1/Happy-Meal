package com.ror.gameutil;

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
    private boolean running = false;

    // --- TILEMAP VARIABLES ---
    private BufferedImage floorTile;
    private BufferedImage tlTop, tmTop, trTop;
    private BufferedImage tlFace, tmFace, trFace;
    private BufferedImage leftWall, rightWall;
    private final int TILE_SIZE = 16;

    // --- EXPANDED ROOM BLUEPRINT (17x13) ---
    // 0=Floor, 1=TopLeft(Top), 2=TopMid(Top), 3=TopRight(Top)
    // 4=TopLeft(Face), 5=TopMid(Face), 6=TopRight(Face)
    // 7=LeftWall, 8=RightWall
    private int[][] roomMap = {
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3}, // Row 0
            {4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6}, // Row 1
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 2
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 3
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 4
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 5
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 6
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 7
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 8
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 9
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 10
            {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3}, // Row 11: Bottom Wall Top
            {4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6}  // Row 12: Bottom Wall Face
    };

    // --- SPRITE ARRAYS ---
    private BufferedImage[] idleFrames;
    private BufferedImage[] walkFrames;
    private BufferedImage[] attackFrames;

    private final int STATE_IDLE = 0;
    private final int STATE_WALK = 1;
    private final int STATE_ATTACK = 2;
    private int currentState = STATE_IDLE;

    private int currentFrame = 0;
    private int animationTimer = 0;
    private int animationSpeed = 6;

    private int playerX = 50;
    private int playerY = 50;
    private int playerSpeed = 2;
    private int facingDirection = 1;

    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    public ArcadeGamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(this);
        setFocusable(true);

        loadSprites();
    }
    private void loadSprites() {
        try {
            // 1. Load Floor Tile
            BufferedImage floorAtlas = ImageIO.read(new File("images/map/atlas_floor-16x16.png"));
            floorTile = floorAtlas.getSubimage(0, 0, 16, 16);

            // 2. Load the High Atlas (MATH FULLY FIXED HERE)
            BufferedImage wallHighAtlas = ImageIO.read(new File("images/map/atlas_walls_high-16x32.png"));
            tlTop = wallHighAtlas.getSubimage(16, 0, 16, 16);  // Top Left Corner Wall -- NEEDS TO BE FIXED --
            tmTop = wallHighAtlas.getSubimage(16, 64, 16, 32);  // Supposed to be Top Middle Wall -- NEEDS TO BE FIXED --
            trTop = wallHighAtlas.getSubimage(48, 0, 16, 16);  // Top Right Corner Wall -- NEEDS TO BE FIXED --

            tlFace = wallHighAtlas.getSubimage(16, 16, 16, 16);  // Face A.K.A. BOUNDERY/BORDER for Top Left Wall -- NEEDS TO BE FIXED --
            tmFace = wallHighAtlas.getSubimage(16, 64, 16, 32); // Face A.K.A. BOUNDERY/BORDER for Top Middle Wall -- NEEDS TO BE FIXED --
            trFace = wallHighAtlas.getSubimage(48, 16, 16, 16); // Face A.K.A. BOUNDERY/BORDER for Top Right Wall -- NEEDS TO BE FIXED --

            // 3. Load the Low Atlas for Sides (Y AXIS FIXED TO 0)
            BufferedImage wallLowAtlas = ImageIO.read(new File("images/map/atlas_walls_low-16x16.png"));
            leftWall = wallLowAtlas.getSubimage(16, 0, 16, 16); // -- NEEDS TO BE FIXED --
            rightWall = wallLowAtlas.getSubimage(48, 0, 16, 16); // -- NEEDS TO BE FIXED --

            // 4. Load Player Animations
            BufferedImage idleSheet = ImageIO.read(new File("images/Soldier-Idle.png"));
            int idleW = idleSheet.getWidth() / 6;
            idleFrames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) idleFrames[i] = idleSheet.getSubimage(i * idleW, 0, idleW, idleSheet.getHeight());

            BufferedImage walkSheet = ImageIO.read(new File("images/Soldier-Walk.png"));
            int walkW = walkSheet.getWidth() / 8;
            walkFrames = new BufferedImage[8];
            for (int i = 0; i < 8; i++) walkFrames[i] = walkSheet.getSubimage(i * walkW, 0, walkW, walkSheet.getHeight());

            BufferedImage attackSheet = ImageIO.read(new File("images/Soldier-Attack01.png"));
            int attackW = attackSheet.getWidth() / 6;
            attackFrames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) attackFrames[i] = attackSheet.getSubimage(i * attackW, 0, attackW, attackSheet.getHeight());

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
        int previousState = currentState;

        if (spacePressed && currentState != STATE_ATTACK) {
            currentState = STATE_ATTACK;
        }

        if (currentState != STATE_ATTACK) {
            boolean isMoving = false;

            int nextX = playerX;
            int nextY = playerY;

            if (upPressed) { nextY -= playerSpeed; isMoving = true; }
            if (downPressed) { nextY += playerSpeed; isMoving = true; }
            if (leftPressed) { nextX -= playerSpeed; isMoving = true; facingDirection = -1; }
            if (rightPressed) { nextX += playerSpeed; isMoving = true; facingDirection = 1; }

            int leftWallBounds = TILE_SIZE;
            int rightWallBounds = (16 * TILE_SIZE) - 12;
            int topWallBounds = (2 * TILE_SIZE);
            int bottomWallBounds = (11 * TILE_SIZE) - 12;

            if (nextX >= leftWallBounds && nextX <= rightWallBounds) {
                playerX = nextX;
            }
            if (nextY >= topWallBounds && nextY <= bottomWallBounds) {
                playerY = nextY;
            }

            if (isMoving) currentState = STATE_WALK;
            else currentState = STATE_IDLE;
        }

        if (currentState != previousState) {
            currentFrame = 0;
            animationTimer = 0;
        }

        BufferedImage[] activeArray = getActiveFrameArray();

        if (activeArray == null) return; // <-- NEW: Stops the crash if images are missing!

        animationTimer++;
        if (animationTimer >= animationSpeed) {
            currentFrame++;
            animationTimer = 0;

            if (currentFrame >= activeArray.length) {
                if (currentState == STATE_ATTACK) {
                    currentState = STATE_IDLE;
                    spacePressed = false;
                }
                currentFrame = 0;
            }
        }
    }

    private BufferedImage[] getActiveFrameArray() {
        if (currentState == STATE_WALK) return walkFrames;
        if (currentState == STATE_ATTACK) return attackFrames;
        return idleFrames;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.scale(3.0, 3.0);

        // --- RENDER THE TILEMAP ---
        for (int row = 0; row < roomMap.length; row++) {
            for (int col = 0; col < roomMap[row].length; col++) {
                int tileID = roomMap[row][col];
                int xPos = col * TILE_SIZE;
                int yPos = row * TILE_SIZE;

                // Draw Floor behind everything first to prevent transparent gaps!
                if (row >= 2 && floorTile != null) {
                    g2d.drawImage(floorTile, xPos, yPos, null);
                }

                if (tileID == 1 && tlTop != null) g2d.drawImage(tlTop, xPos, yPos, null);
                else if (tileID == 2 && tmTop != null) g2d.drawImage(tmTop, xPos, yPos, null);
                else if (tileID == 3 && trTop != null) g2d.drawImage(trTop, xPos, yPos, null);
                else if (tileID == 4 && tlFace != null) g2d.drawImage(tlFace, xPos, yPos, null);
                else if (tileID == 5 && tmFace != null) g2d.drawImage(tmFace, xPos, yPos, null);
                else if (tileID == 6 && trFace != null) g2d.drawImage(trFace, xPos, yPos, null);
                else if (tileID == 7 && leftWall != null) g2d.drawImage(leftWall, xPos, yPos, null);
                else if (tileID == 8 && rightWall != null) g2d.drawImage(rightWall, xPos, yPos, null);
            }
        }

        // Render the Soldier
        BufferedImage[] activeArray = getActiveFrameArray();
        if (activeArray != null && currentFrame < activeArray.length) {
            BufferedImage currentImg = activeArray[currentFrame];

            if (facingDirection == -1) {
                g2d.drawImage(currentImg, playerX + currentImg.getWidth(), playerY, -currentImg.getWidth(), currentImg.getHeight(), null);
            } else {
                g2d.drawImage(currentImg, playerX, playerY, null);
            }
        }

        g2d.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_SPACE) spacePressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}