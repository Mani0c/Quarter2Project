import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.Graphics2D;

public class Chopper {
    private int x, y; // Position of the chopper
    private boolean facingLeft = false; // Whether the chopper is facing left
    private BufferedImage chopper; // Image representing the chopper
    private BufferedImage originalChopper; // Original image for scaling
    private int shotsFired = 0; // Number of shots fired
    private long lastShotTime = 0; // Time the last shot was fired
    private boolean isOnCooldown = false; // Whether the chopper is on cooldown after firing
    private static final int MAX_SHOTS = 15; // Maximum number of shots
    private static final long COOLDOWN_TIME = 5000; // Cooldown time between shots
    private int lives = 3; // Lives of the chopper
    private boolean isAlive = true; // Status of the chopper (alive or dead)
    private Map<String, Clip> soundPool = new HashMap<>(); // Sound pool to store loaded sounds
    private Clip currentPlayingClip = null; // The currently playing sound clip

    // Projectile properties
    private static final int MAX_PROJECTILES = 100; // Maximum number of projectiles
    private int[] projectileX = new int[MAX_PROJECTILES]; // X positions of the projectiles
    private int[] projectileY = new int[MAX_PROJECTILES]; // Y positions of the projectiles
    private boolean[] projectileFired = new boolean[MAX_PROJECTILES]; // Whether each projectile is fired
    private int projectileSpeed = 50; // Speed of the projectiles
    private int projectileWidth = 30; // Width of the projectiles
    private int projectileHeight = 3; // Height of the projectiles

    // Constructor to initialize the chopper's position and properties
    public Chopper(int x, int y) {
        this.x = x;
        this.y = y;

        // Initialize projectile arrays
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            projectileFired[i] = false;
            projectileX[i] = -1; // Start off-screen
            projectileY[i] = -1;
        }

        // Load the chopper image
        try {
            originalChopper = ImageIO.read(new File("Media/Images/chopper.png"));
            chopper = new BufferedImage(75, 60, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = chopper.createGraphics();
            g2d.drawImage(originalChopper, 0, 0, 75, 60, null);
            g2d.dispose();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load chopper image");
        }

        loadSounds(); // Load sounds used in the game
    }

    // Load sound files into the sound pool
    private void loadSounds() {
        loadSound("Media/Audio/ChopperShot.wav"); // Load the chopper shot sound
    }

    // Render the chopper and its projectiles on the screen
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = chopper.getWidth();
        int height = chopper.getHeight();

        // Draw the chopper (flip horizontally if facing left)
        if (facingLeft) {
            g2d.drawImage(chopper, x + width, y, -width, height, null);
        } else {
            g2d.drawImage(chopper, x, y, width, height, null);
        }

        // Draw all active projectiles
        g.setColor(Color.RED);
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (projectileFired[i]) {
                g.fillOval(projectileX[i], projectileY[i], projectileWidth, projectileHeight);
            }
        }
    }

    // Move the projectiles based on their direction (left or right)
    public void moveProjectile() {
        for (int i = 0; i < MAX_PROJECTILES; i++) {
            if (projectileFired[i]) {
                if (facingLeft) {
                    projectileX[i] -= projectileSpeed; // Move projectiles left
                } else {
                    projectileX[i] += projectileSpeed; // Move projectiles right
                }
                // Reset projectile if it goes off-screen
                if (projectileX[i] < 0 || projectileX[i] > 960) {
                    resetProjectile(i);
                }
            }
        }
    }

    // Reset a projectile when it goes off-screen
    private void resetProjectile(int index) {
        projectileFired[index] = false;
        projectileX[index] = -1;
        projectileY[index] = -1;
    }

    // Load a sound file into the sound pool
    private void loadSound(String soundFileName) {
        try {
            // Check if the sound is already in the pool
            if (!soundPool.containsKey(soundFileName)) {
                File soundFile = new File(soundFileName); // Path to the sound file
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream); // Load the clip
                soundPool.put(soundFileName, clip); // Add to the pool
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load sound: " + soundFileName);
        }
    }

    // Play a sound from the sound pool
    public void playSound(String soundFileName) {
        Clip clip = soundPool.get(soundFileName);
        if (clip != null) {
            // Stop the currently playing clip
            if (currentPlayingClip != null && currentPlayingClip.isRunning()) {
                currentPlayingClip.stop();
            }

            // Start the new clip
            clip.setFramePosition(0);
            currentPlayingClip = clip; // Update the currently playing clip
            new Thread(() -> currentPlayingClip.start()).start(); // Play the clip in a separate thread
        } else {
            System.out.println("Sound not found in pool: " + soundFileName);
        }
    }

    // Fire a projectile if it's not on cooldown and within the shot limit
    public void fire(long currentTime) {
        if(isAlive){
            if (isOnCooldown) {
                if (currentTime - lastShotTime >= COOLDOWN_TIME) {
                    isOnCooldown = false;
                    shotsFired = 0;
                } else {
                    System.out.println("On cooldown! Please wait...");
                    return;
                }
            }
            // Check to see if the amount of shots are over the max limit of shots
            if (shotsFired < MAX_SHOTS) {
                //checks for projectile if it is fired or not and then pulls from array to update
                for (int i = 0; i < MAX_PROJECTILES; i++) {
                    if (!projectileFired[i]) {
                        projectileFired[i] = true;
                        projectileX[i] = x + (facingLeft ? -projectileWidth : 75);
                        projectileY[i] = y + 30;
                        shotsFired++;
                        lastShotTime = currentTime;
                        playSound("Media/Audio/ChopperShot.wav"); // Play shot sound if alive
                        return;
                    }
                }
            } else {
                isOnCooldown = true;
                lastShotTime = currentTime;
                System.out.println("Cooldown started! Wait 5 seconds.");
            }
        }
    }

    // Check if two rectangles (representing objects) are colliding
    public boolean checkCollision(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2) {
        return x1 < x2 + width2 && x1 + width1 > x2 && y1 < y2 + height2 && y1 + height1 > y2;
    }

    // Check for collision between chopper's projectiles and enemy planes
    public boolean checkCollisionWithPlanes(Plane planeM) {
        for (int i = 0; i < planeM.PLANE_COUNT; i++) {
            if (planeM.plane[i] != null) {
                int planeX = planeM.getPlaneX(i);
                int planeY = planeM.getPlaneY(i);
                int planeWidth = planeM.planeWidth;
                int planeHeight = planeM.planeHeight;

                // Check each projectile for collision with the plane
                for (int j = 0; j < MAX_PROJECTILES; j++) {
                    if (projectileFired[j] && checkCollision(projectileX[j], projectileY[j], projectileWidth, projectileHeight, planeX, planeY, planeWidth, planeHeight)) {
                        resetProjectile(j); 
                        planeM.resetPlane(i); 
                        return true; 
                    }
                }
            }
        }
        return false; 
    }

    // Check for collision with the boss plane (only on specific levels)
    public boolean checkCollisionWithBossPlane(Plane planeM, int currentLevel) {
        if (currentLevel != 3) {
            return false; 
        }
        for (int i = 0; i < 1; i++) {
            if (planeM.plane[i] != null) {
                int bossPlaneX = planeM.getBossPlaneX(i);
                int bossPlaneY = planeM.getBossPlaneY(i);
                int bossPlaneWidth = planeM.bossPlaneWidth;
                int bossPlaneHeight = planeM.bossPlaneHeight;

                // Check each projectile for collision with the boss plane
                for (int j = 0; j < MAX_PROJECTILES; j++) {
                    if (projectileFired[j] && checkCollision(projectileX[j], projectileY[j], projectileWidth, projectileHeight, bossPlaneX, bossPlaneY, bossPlaneWidth, bossPlaneHeight)) {
                        resetProjectile(j); 
                        return true; 
                    }
                }
            }
        }
        return false; 
    }

    // Gets choppers lives
    public int getLives() {
        return lives;
    }

    //returns if the chopper is alive
    public boolean isAlive() {
        return isAlive;
    }

    // Update lives when the chopper gets hit
    public void updateLives(boolean isHit) {
        if (isHit) {
            lives--;
            System.out.println("Chopper hit! Lives remaining: " + lives);
        }
        if (lives == 0) {
            isAlive = false;
        }
    }

    //Getters for Possition and Size
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return chopper.getWidth();
    }

    public int getHeight() {
        return chopper.getHeight();
    }

    // Movement methods for the chopper
    public void moveUp() {
        if (y > 0) {
            y -= 10;
        }
    }
    public void moveDown() {
        if (y + getHeight() < 600) {
            y += 10;
        }
    }
    public void moveLeft() {
        if (x >= 0) {
            x -= 5;
            facingLeft = true;
        }
    }
    public void moveRight() {
        if (x + getWidth() <= 960) {
            x += 5;
            facingLeft = false;
        }
    }
}
