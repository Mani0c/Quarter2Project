import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

public class Plane {
    
    // Constants and variables related to planes
    public int PLANE_COUNT = 6; // Number of planes
    public BufferedImage[] plane = new BufferedImage[PLANE_COUNT]; // Array to hold plane images
    public BufferedImage[] bossPlane = new BufferedImage[PLANE_COUNT]; // Array to hold boss plane images
    public BufferedImage[] originalPlane = new BufferedImage[PLANE_COUNT]; // Original plane images
    public int[] planeX = new int[PLANE_COUNT]; // X coordinates of the planes
    public int[] planeY = new int[PLANE_COUNT]; // Y coordinates of the planes
    public int[] bossPlaneX = new int[PLANE_COUNT]; // X coordinates of the boss planes
    public int[] bossPlaneY = new int[PLANE_COUNT]; // Y coordinates of the boss planes
    private int[] planeSpeed = new int[PLANE_COUNT]; // Speeds of the planes
    public int planeWidth = 120; // Width of the plane
    public int planeHeight = 50; // Height of the plane
    public int bossPlaneWidth = 200; // Width of the boss plane
    public int bossPlaneHeight = 100; // Height of the boss plane
    public boolean planeFlip = false; // Determines the direction of the boss plane

    // Constructor: Initializes plane images, positions, and speeds
    public Plane() {
        try {
            // Load plane images, scale them, and initialize positions and speeds
            for (int i = 0; i < PLANE_COUNT; i++) {
                originalPlane[i] = ImageIO.read(new File("Media/Images/plane.png")); // Load the original plane image
                if (originalPlane[i] == null) {
                    System.out.println("The image could not be loaded.");
                    continue; // Skip if image cannot be loaded
                }

                // Scale the plane image and store it in the array
                plane[i] = new BufferedImage(planeWidth, planeHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = plane[i].createGraphics();
                g2d.drawImage(originalPlane[i], 0, 0, planeWidth, planeHeight, null);
                g2d.dispose();

                // Initialize random positions and speeds for the planes
                planeX[i] = 1100 + i * PLANE_COUNT; // Stagger planes horizontally
                planeY[i] = (int) (Math.random() * 300); // Random vertical position within the screen
                planeSpeed[i] = (int) (Math.random() * 3 + 2); // Random speed
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to spawn the boss plane
    public void spawnPlaneBoss() {
        try {
            for (int i = 0; i < 1; i++) {
                originalPlane[i] = ImageIO.read(new File("Media/Images/plane.png")); // Load boss plane image
                if (originalPlane[i] == null) {
                    System.out.println("The image could not be loaded.");
                    continue; // Skip if image cannot be loaded
                }

                // Scale the boss plane image and store it
                bossPlane[i] = new BufferedImage(bossPlaneWidth, bossPlaneHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bossPlane[i].createGraphics();
                g2d.drawImage(originalPlane[i], 0, 0, bossPlaneWidth, bossPlaneHeight, null);
                g2d.dispose();

                // Initialize position and speed for the boss plane
                bossPlaneX[i] = 1100; // Stagger boss plane horizontally
                bossPlaneY[i] = (int) (Math.random() * 300); // Random vertical position
                planeSpeed[i] = 10; // Set a fixed speed for the boss plane
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the boss plane's movement
    public void updateBossPlane() {
        for (int i = 0; i < 1; i++) {
            if (bossPlane[i] != null) {
                // Move the boss plane left or right based on the planeFlip variable
                if (!planeFlip) {
                    bossPlaneX[i] -= planeSpeed[i]; // Move left
                }
                if (planeFlip) {
                    bossPlaneX[i] += planeSpeed[i]; // Move right
                }

                // Reverse direction if boss plane hits the screen bounds
                if (bossPlaneX[i] <= -120) {
                    planeFlip = true; // Reverse speed to move right
                }
                if (bossPlaneX[i] >= 965) {
                    planeFlip = false; // Reverse speed to move left
                }
            }
        }
    }

    // Method to update the positions of all planes
    public void updatePlanePositions() {
        for (int i = 0; i < PLANE_COUNT; i++) {
            if (plane[i] != null) {
                planeX[i] -= planeSpeed[i]; // Move the plane left
                if (planeX[i] < -120) { // Reset plane when off-screen
                    resetPlane(i);
                }
            }
        }
    }

    // Method to reset a plane's position and speed when it moves off-screen
    public void resetPlane(int index) {
        planeX[index] = 1100; // Reset plane to start off-screen to the right
        planeY[index] = (int) (Math.random() * 300); // Random vertical position within screen height
        planeSpeed[index] = (int) (Math.random() * 3 + 2); // Random speed
    }

    // Method to draw all planes and their associated numbers
    public void drawPlanes(Graphics g) {
        for (int i = 0; i < PLANE_COUNT; i++) {
            if (plane[i] != null) {
                // Draw the plane image at its current position
                g.drawImage(plane[i], planeX[i], planeY[i], null);

                // Draw the plane's index above it as a label
                g.setColor(Color.BLACK); // Set text color
                g.setFont(new Font("Arial", Font.BOLD, 12)); // Set text font and size
                g.drawString(String.valueOf(i), planeX[i] + planeWidth / 2, planeY[i] - 10); // Draw index above the plane
            }
        }
    }

    // Method to draw the boss plane and its number
    public void drawBossPlane(Graphics g) {
        for (int i = 0; i < 1; i++) {
            if (bossPlane[i] != null) {
                Graphics2D g2d = (Graphics2D) g;

                // Draw the boss plane, flipping horizontally if needed
                if (planeFlip) {
                    g2d.drawImage(bossPlane[i], bossPlaneX[i] + bossPlaneWidth, bossPlaneY[i], -bossPlaneWidth, bossPlaneHeight, null);
                } else {
                    g2d.drawImage(bossPlane[i], bossPlaneX[i], bossPlaneY[i], bossPlaneWidth, bossPlaneHeight, null);
                }

                // Draw the boss plane's index above it
                g.setColor(Color.BLACK); // Set text color
                g.setFont(new Font("Arial", Font.BOLD, 12)); // Set text font and size
                g.drawString(String.valueOf(i), bossPlaneX[i] + planeWidth / 2, bossPlaneY[i] - 10); // Draw index above the plane
            }
        }
    }

    // Getters for plane positions
    public int getPlaneX(int index) {
        return planeX[index];
    }

    public int getPlaneY(int index) {
        return planeY[index];
    }

    public int getBossPlaneX(int index) {
        return bossPlaneX[index];
    }

    public int getBossPlaneY(int index) {
        return bossPlaneY[index];
    }
}
