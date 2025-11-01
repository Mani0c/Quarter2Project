import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

public class Hummer{
    // Declare the variables for the hummer object, its position, and remaining lives
    public BufferedImage hummer;  // The image of the hummer
    public int HummerX = 960;     // The X-coordinate of the hummer's position
    public int HummerY = 500;     // The Y-coordinate of the hummer's position
    public int lives = 3;         // Number of lives the hummer has

    // Constructor that loads the hummer image and scales it to a smaller size
    public Hummer(){
        try {
            // Read the original image from the file
            BufferedImage originalHummer = ImageIO.read(new File("Media/Images/hummer.png"));
            
            // Create a new BufferedImage with the desired size for the hummer
            hummer = new BufferedImage(90, 50, BufferedImage.TYPE_INT_ARGB); 
            
            // Create a Graphics2D object to draw the scaled image
            Graphics2D g2d = hummer.createGraphics();
            
            // Draw the original image onto the new image, scaling it to 90x50 pixels
            g2d.drawImage(originalHummer, 0, 0, 90, 50, null);
            g2d.dispose();  // Dispose of the graphics context after drawing
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
    
    // This method decreases the X-coordinate of the hummer to move it left
    public int HummerX(){
        HummerX -= 1;
        return HummerX;
    }

    // This method resets the X-coordinate of the hummer to its starting position
    public int reset(){
        HummerX = 960;
        return HummerX;
    }

    // This method returns the current X-coordinate of the hummer
    public int getX(){
        return HummerX;
    }
    
    // This method returns the current Y-coordinate of the hummer
    public int getY(){
        return HummerY;
    }

    // This method updates the number of lives when the hummer is hit
    public void updateLives(boolean isHit) {
        if (isHit) {
            lives--;
            System.out.println("Hummer hit! Lives remaining: " + lives);  // Print the number of remaining lives
        }
    }

    // This method returns the current number of lives
    public int getLives(){
        return lives;
    }
}
