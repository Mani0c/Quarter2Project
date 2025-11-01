import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

 //Represents a bomb that can be dropped by a plane or boss.
public class Bomb {
    // Constants
    private static final int SCREEN_HEIGHT = 600; 
    // instance variables
    private int speed = 5; // Speed of the bomb when falling
    private int x, y; // Position of the bomb
    public boolean isDropped = false; // Flag to track if the bomb is falling
    public boolean isBossDropped = false; // Flag to track if the boss's bomb is falling
    private int planeIndex; // Index of the plane dropping the bomb
    private BufferedImage bombImage; // Image of the bomb

  
     //Constructor to initialize the bomb's position relative to a plane.
    public Bomb(int planeX, int planeY, int planeIndex) {
        this.x = planeX + 60; 
        this.y = planeY + 50; 
        this.planeIndex = planeIndex;
        try {
            bombImage = ImageIO.read(new File("Media/Images/bomb.png")); // Load bomb image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    //Drops the bomb from the plane.
    public void drop() {
        isDropped = true; 
    }

    //return if isDropped is true or false 
    public boolean isDropped() {
        return isDropped;
    }

    //Drops the bomb from the boss plane.
    public void bossDrop() {
        isBossDropped = true;
    }

       //return if isBossDropped is true or false 
    public boolean isBossDropped() {
        return isBossDropped;
    }

    //Updates the position of the bomb based on the location of the plane.
    public void update(int planeX, int planeY) {
        if (!isDropped) {
            // If not dropped, stay attached to the plane
            this.x = planeX + 60;
            this.y = planeY + 50;
        } else {
            y += speed;
            if (y > SCREEN_HEIGHT) {
                // Reset bomb position when it goes off-screen
                this.x = planeX + 60;
                this.y = planeY + 50;
                isDropped = false;
            }
        }
    }

    
    //Updates the position of the bomb dropped by the boss.
    public void bossUpdate(int bossPlaneX, int bossPlaneY) {
        if (!isBossDropped) {
            // If not dropped, stay attached to the boss plane
            this.x = bossPlaneX + 50;
            this.y = bossPlaneY + 60;
        } else {
            y += speed;
            if (y > SCREEN_HEIGHT) {
                // Reset bomb position when it goes off-screen
                this.x = bossPlaneX + 60;
                this.y = bossPlaneY + 50;
                isBossDropped = false;
            }
        }
    }


    //Increases the speed of the bomb.
    public void increaseSpeed(int speed) {
        this.speed = speed;
    }

    
    //Draws the bomb on the screen.
    public void draw(Graphics g) {
        g.drawImage(bombImage, x, y, null); // Draw the bomb at its current position
    }

    

    /**
     * Checks if the bomb collides with the hummer vehicle.
     * return True if there is a collision, otherwise false
     */
    public boolean checkCollision(int hummerX, int hummerY) {
        if (isDropped) {
            // Create bounding rectangles for collision detection
            Rectangle bombRect = new Rectangle(x, y, 10, 20);
            Rectangle hummerRect = new Rectangle(hummerX, hummerY, 120, 50);
            return bombRect.intersects(hummerRect);//Uses the rectangle class in order to check for intersections
        }
        return false;
    }

    /**
     * Checks if the boss's bomb collides with the hummer vehicle.
    return True if there is a collision, otherwise false
     */
    public boolean checkBossCollision(int hummerX, int hummerY) {
        if (isBossDropped) {
            // Create bounding rectangles for collision detection
            Rectangle bombRect = new Rectangle(x, y, 10, 20);
            Rectangle hummerRect = new Rectangle(hummerX, hummerY, 120, 50);
            return bombRect.intersects(hummerRect); //Uses the rectangle class in order to check for intersections

        }
        return false;
    }
}
