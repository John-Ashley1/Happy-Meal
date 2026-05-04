package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Player;

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

    // --- TILEMAP VARIABLES ---
    private BufferedImage floorTile;
    private BufferedImage tlTop, tmTop, trTop, trbTop, tlbTop, tmbTop;
    private BufferedImage leftWall, rightWall;
    private BufferedImage blWall, bmWall, brWall;
    private BufferedImage tdlWall,tdmWall, tdrWall, bdrWall, bdlWall, bdmWall;

    private final int TILE_SIZE = 16;

    // --- PERFECT 17x13 DUNGEON BOX ---
    // 0=Floor, 1=TopLeft, 2=TopMid, 3=TopRightBottom, 4=TopLeftBottom //trb = 4 tlb = 5
    // 7=LeftWall, 8=RightWall
    // 9=BotLeft, 10=BotMid, 11=BotRight
    //
    private int[][] roomMap = {
            {1, 2, 2, 2, 2, 2, 2, 12, 13, 14, 2, 2, 2, 2, 2, 2, 3}, // Row 0: Top Wall
            {4, 6, 6, 6, 6, 6, 6, 15, 16, 17, 6, 6, 6, 6, 6, 6, 5}, // Row 1
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 2
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 3
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 4
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 5
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 6
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 7
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 8
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 9
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 10
            {7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8}, // Row 11
            {9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11}  // Row 12: Bottom Wall
    };



    private Player player;

    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    // Notice it now accepts 'Entity selectedHero' inside the parentheses!
    public ArcadeGamePanel(Entity selectedHero) {
        setPreferredSize(new Dimension(816, 624));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(this);
        setFocusable(true);

        loadSprites();

        // Check which hero they selected and assign the correct animation folder
        String animationFolder = "soldier"; // Default fallback

        if (selectedHero != null && selectedHero.getName().contains("Mark")) {
            animationFolder = "soldier";
        }

        player = new Player(animationFolder, TILE_SIZE);
    }

    private void loadSprites() {
        try {
            // 1. Load Floor Tile
            BufferedImage floorAtlas = ImageIO.read(new File("images/map/atlas_floor-16x16.png"));
            floorTile = floorAtlas.getSubimage(0, 0, 16, 16);

            // 2. Load the Atlas (SHIFTED TO X=64 FOR THE INTERIOR ROOM TILES)
            BufferedImage wallAtlas = ImageIO.read(new File("images/map/atlas_walls_low-16x16-Sheet.png"));
            System.out.println("Wall atlas size: " + wallAtlas.getWidth() + "x" + wallAtlas.getHeight());

            // Top Row (Y = 0)
            tlTop = wallAtlas.getSubimage(16, 0, 16, 16);
            tmTop = wallAtlas.getSubimage(32, 48, 16, 16);
            trTop = wallAtlas.getSubimage(48, 0, 16, 16);

            // Top Bottom Row (Y = 16, 32)
            tlbTop = wallAtlas.getSubimage(144, 16, 16, 16); // left
            trbTop = wallAtlas.getSubimage(160, 16, 16, 16); // right
            tmbTop = wallAtlas.getSubimage(160, 32, 16, 16); // mid

            // Side Walls (Y = 16)
            leftWall = wallAtlas.getSubimage(0, 16, 16, 16);
            rightWall = wallAtlas.getSubimage(0, 0, 16, 16);

            // Bottom Row (Y = 32)
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
        // =========================
        // ✅ DYNAMIC WORLD BOUNDS FIX
        // =========================
        int cols = roomMap[0].length;
        int rows = roomMap.length;

        int worldWidth = cols * TILE_SIZE;
        int worldHeight = rows * TILE_SIZE;

        // 2-tile wall thickness
        int leftWallBounds = TILE_SIZE;
        int topWallBounds = 2 * TILE_SIZE;

        // Collision box matches sprite size

        int rightWallBounds = worldWidth - TILE_SIZE - player.getPlayerWidth();
        int bottomWallBounds = worldHeight - TILE_SIZE - player.getPlayerHeight();

        player.update(upPressed, downPressed, leftPressed, rightPressed, spacePressed);
        player.clampPosition(leftWallBounds, rightWallBounds, topWallBounds, bottomWallBounds);

        if (player.isAttackFinished()) spacePressed = false;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.scale(3.0, 3.0);

        // Fill the entire background with the floor tile first,
        // to guarantee NO black gaps anywhere behind the walls!
        if (floorTile != null) {
            for (int row = 0; row < roomMap.length; row++) {
                for (int col = 0; col < roomMap[row].length; col++) {
                    g2d.drawImage(floorTile, col * TILE_SIZE, row * TILE_SIZE, null);
                }
            }
        }

        // --- RENDER THE WALLS ---
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

                else if (tileID == 12 && tdlWall != null) g2d.drawImage(tdlWall, xPos, yPos, null);
                else if (tileID == 13 && tdmWall != null) g2d.drawImage(tdmWall, xPos, yPos, null);
                else if (tileID == 14 && tdrWall != null) g2d.drawImage(tdrWall, xPos, yPos, null);

                else if (tileID == 15 && bdlWall != null) g2d.drawImage(bdlWall, xPos, yPos, null);
                else if (tileID == 16 && bdmWall != null) g2d.drawImage(bdmWall, xPos, yPos, null);
                else if (tileID == 17 && bdrWall != null) g2d.drawImage(bdrWall, xPos, yPos, null);
            }
        }

        // Render the Soldier
        player.render(g2d, TILE_SIZE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_SPACE) spacePressed = true;

        if (code == KeyEvent.VK_ESCAPE) {

            // 1. Pause the game engine
            running = false;

            // 2. Find the window this panel is living inside
            JFrame parentWindow = (JFrame) SwingUtilities.getWindowAncestor(this);

            // 3. Summon the reusable Main Menu!
            MainMenu pauseMenu = new MainMenu(
                    parentWindow,

                    // ACTION A: What happens when they click Resume?
                    () -> {
                        running = true; // Unpause logic
                        startGameThread(); // Restart the loop
                    },

                    // ACTION B: What happens when they click Quit?
                    () -> {
                        // Open the Intro Screen
                        new IntroScreen().setVisible(true);
                        // Destroy the current game window
                        parentWindow.dispose();
                    }
            );

            // 4. Show the menu (This freezes the screen until they click a button)
            pauseMenu.setVisible(true);
        }


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