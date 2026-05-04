package com.ror.gamemodel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player {
    private volatile int playerX = 50;
    private volatile int playerY = 50;
    private int playerSpeed = 2;
    private int playerWidth;
    private int playerHeight;
    private volatile int facingDirection = 1;

    public final int STATE_IDLE = 0;
    public final int STATE_WALK = 1;
    public final int STATE_ATTACK = 2;
    private volatile int currentState = STATE_IDLE;

    private volatile int currentFrame = 0;
    private volatile int animationTimer = 0;
    private int animationSpeed = 6;

    private BufferedImage[] idleFrames;
    private BufferedImage[] walkFrames;
    private BufferedImage[] attackFrames;

    private boolean attackFinished = false;
    private String animationFolder;

    // --- NEW: Stores "Soldier", "Orc", etc. ---
    private String spritePrefix;

    // --- NEW: Constructor now accepts the spritePrefix ---
    public Player(String animationFolder, String spritePrefix, int tileSize) {
        this.animationFolder = animationFolder;
        this.spritePrefix = spritePrefix;
        playerWidth = tileSize;
        playerHeight = tileSize;
        loadSprites();
    }

    private void loadSprites() {
        try {
            // Dynamically injects the prefix so it isn't hardcoded to "Soldier"
            BufferedImage idleSheet = ImageIO.read(new File("images/animation/twodeeanimation/" + animationFolder + "/" + spritePrefix + "-Idle.png"));
            int idleW = idleSheet.getWidth() / 6;
            idleFrames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) idleFrames[i] = idleSheet.getSubimage(i * idleW, 0, idleW, idleSheet.getHeight());

            BufferedImage walkSheet = ImageIO.read(new File("images/animation/twodeeanimation/" + animationFolder + "/" + spritePrefix + "-Walk.png"));
            int walkW = walkSheet.getWidth() / 8;
            walkFrames = new BufferedImage[8];
            for (int i = 0; i < 8; i++) walkFrames[i] = walkSheet.getSubimage(i * walkW, 0, walkW, walkSheet.getHeight());

            BufferedImage attackSheet = ImageIO.read(new File("images/animation/twodeeanimation/" + animationFolder + "/" + spritePrefix + "-Attack01.png"));
            int attackW = attackSheet.getWidth() / 6;
            attackFrames = new BufferedImage[6];
            for (int i = 0; i < 6; i++) attackFrames[i] = attackSheet.getSubimage(i * attackW, 0, attackW, attackSheet.getHeight());

        } catch (IOException e) {
            System.out.println("DEBUG: Could not load player sprites! Check your file paths.");
            e.printStackTrace();
        }
    }

    public void update(boolean up, boolean down, boolean left, boolean right, boolean space) {
        int previousState = currentState;
        attackFinished = false;

        if (space && currentState != STATE_ATTACK) {
            currentState = STATE_ATTACK;
        }

        if (currentState != STATE_ATTACK) {
            boolean isMoving = false;
            int nextX = playerX;
            int nextY = playerY;

            if (up) { nextY -= playerSpeed; isMoving = true; }
            if (down) { nextY += playerSpeed; isMoving = true; }
            if (left) { nextX -= playerSpeed; isMoving = true; facingDirection = -1; }
            if (right) { nextX += playerSpeed; isMoving = true; facingDirection = 1; }

            playerX = nextX;
            playerY = nextY;

            if (isMoving) currentState = STATE_WALK;
            else currentState = STATE_IDLE;
        }

        if (currentState != previousState) {
            currentFrame = 0;
            animationTimer = 0;
        }

        BufferedImage[] activeArray = getActiveFrameArray();

        if (activeArray != null) {
            animationTimer++;
            if (animationTimer >= animationSpeed) {
                currentFrame++;
                animationTimer = 0;

                if (currentFrame >= activeArray.length) {
                    if (currentState == STATE_ATTACK) {
                        currentState = STATE_IDLE;
                        attackFinished = true;
                    }
                    currentFrame = 0;
                }
            }
        }
    }

    public void clampPosition(int leftBound, int rightBound, int topBound, int bottomBound) {
        playerX = Math.max(leftBound, Math.min(playerX, rightBound));
        playerY = Math.max(topBound, Math.min(playerY, bottomBound));
    }

    private BufferedImage[] getActiveFrameArray() {
        if (currentState == STATE_WALK) return walkFrames;
        if (currentState == STATE_ATTACK) return attackFrames;
        return idleFrames;
    }

    public void render(java.awt.Graphics2D g2d, int tileSize) {
        BufferedImage[] activeArray = getActiveFrameArray();
        if (activeArray != null && currentFrame < activeArray.length) {
            BufferedImage currentImg = activeArray[currentFrame];

            int drawX = playerX - (currentImg.getWidth() - tileSize) / 2;
            int drawY = playerY - (currentImg.getHeight() - tileSize) / 2;

            if (facingDirection == -1) {
                g2d.drawImage(currentImg, drawX + currentImg.getWidth(), drawY, -currentImg.getWidth(), currentImg.getHeight(), null);
            } else {
                g2d.drawImage(currentImg, drawX, drawY, null);
            }
        }
    }

    public int getCurrentState() { return currentState; }
    public boolean isAttackFinished() { return attackFinished; }
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public int getPlayerWidth() { return playerWidth; }
    public int getPlayerHeight() { return playerHeight; }
}