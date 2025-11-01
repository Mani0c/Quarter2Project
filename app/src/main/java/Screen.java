import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Screen extends JPanel implements KeyListener, ActionListener {
    // Instance variables for UI components and game logic
    private BufferedImage backg, heartImage, backg1, backg2; //all the backgrounds and heart Images
    private JButton startGame, keybinds, returnB;// all the buttons
    private JLabel titleLabel; //the gif
    private GameLogic gameLogic; //instanstiates GameLogic
    private Plane plane;//instanstiates plane

    // Constructor
    public Screen() {
        setupPanel();     // Set up the panel's properties
        loadAssets();     // Load images and assets (backgrounds, heart)
        setupUIElements();  // Set up UI components (title, button)
        gameLogic = new GameLogic(this);  // Initialize the game logic
    } 

    // Set up panel properties
    private void setupPanel() {
        setFocusable(true); // Allow the panel to receive keyboard input
        setLayout(null);     // Use absolute positioning for UI components
        addKeyListener(this);  // Add KeyListener to capture key events
    }

    // Load images and assets (backgrounds, heart icon)
    private void loadAssets() {
        try {
            backg = ImageIO.read(getClass().getResourceAsStream("/Media/Images/backgroundImage.png"));
            heartImage = ImageIO.read(getClass().getResourceAsStream("/Media/Images/heart.png"));
            backg1 = ImageIO.read(getClass().getResourceAsStream("/Media/Images/backgroundImage1.png"));
            backg2 = ImageIO.read(getClass().getResourceAsStream("/Media/Images/backgroundImage2.png"));
        } catch (IOException e) {
            e.printStackTrace();  // Handle IO exceptions
        }
    }

    // Set up UI elements (title, start button)
    private void setupUIElements() {
        // Title Label (shows game title)
        ImageIcon titleGif = new ImageIcon("Media/Images/gameTitle.gif");
        titleLabel = new JLabel(titleGif);
        titleLabel.setBounds((960 - titleGif.getIconWidth()) / 2, 100, titleGif.getIconWidth(), titleGif.getIconHeight());
        add(titleLabel);

        // Start Button
        int buttonWidth = 200, buttonHeight = 60;
        startGame = new JButton("Start the Game!");
        startGame.setBounds((960 - buttonWidth) / 2, (600 - buttonHeight) / 2, buttonWidth, buttonHeight);
        startGame.setFont(new Font("Arial", Font.PLAIN, 20));
        startGame.addActionListener(this);  // Add action listener to the button
        add(startGame);

        //Keybind Button
        int kButtonWidth = 200, kButtonHeight = 60;
        keybinds = new JButton("Keybinds");
        keybinds.setBounds(20, 510, kButtonWidth, kButtonHeight);
        keybinds.setFont(new Font("Arial", Font.PLAIN, 20));
        keybinds.addActionListener(this);
        add(keybinds);
        //return button
        int rButtonWidth = 200, rButtonHeight = 60;
        returnB = new JButton("Return");
        returnB.setBounds(20, 510, rButtonWidth, rButtonHeight);
        returnB.setFont(new Font("Arial", Font.PLAIN, 20));
        returnB.addActionListener(this);
        add(returnB);

    }

    // Override getPreferredSize() to set the panel's dimensions
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(960, 600);  // Return panel size
    }

    // Override paintComponent to render different screens based on game level
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateButtonVisibility();
        
        // Draw appropriate content based on the current level
        switch(gameLogic.currentLevel()) {
            case 0:
                g.drawImage(backg, 0, 0, null);
                drawStartScreen(g);
                break;
            case 1:
                g.drawImage(backg, 0, 0, null);
                drawLevelOne(g);
                break;
            case 2:
                g.drawImage(backg1, 0, 0, null);
                gameLogic.renderGame(g, heartImage);
                drawLevelTwo(g);
                break;
            case 3:
                g.drawImage(backg2, 0, 0, null);
                drawBossLevel(g);
                break;
            case 4:
                drawWinScreen(g);
                break;
            case 5:
                drawDeathScreen(g);
                break;
            case 6:
                drawKeybindScreen(g);
                break;
            default:
                break;
        }
    }

    // Draw the Start Screen UI
    private void drawStartScreen(Graphics g) {
        
        // Draw game instructions
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fm = g.getFontMetrics();
        
        String text1 = "In this game you take out the incoming planes";
        String text2 = "and try to get the vehicle to the other side without it dying.";
        String text3 = "This is the same for all levels until you win";
        
        // Center the text horizontally
        g.drawString(text1, (960 - fm.stringWidth(text1)) / 2, 360);
        g.drawString(text2, (960 - fm.stringWidth(text2)) / 2, 380);
        g.drawString(text3, (960 - fm.stringWidth(text3)) / 2, 400);

        // Draw "Good Luck!!" message
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm1 = g.getFontMetrics();
        String text4 = "Good Luck!!";
        g.drawString(text4, (960 - fm1.stringWidth(text4)) / 2, 430);
    }
    // Draw Level 1 UI
    private void drawLevelOne(Graphics g) {
        gameLogic.renderGame(g, heartImage);  // Render game state for level 1
    }

    // Draw Level 2 UI
    private void drawLevelTwo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String text = "You are now in Level 2!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (960 - fm.stringWidth(text)) / 2, 300);
        
        // Render game state
        gameLogic.renderGame(g, heartImage);
    }

    // Draw the Boss Level UI
    private void drawBossLevel(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String text = "You are now in the Boss Level!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (960 - fm.stringWidth(text)) / 2, 300);
        
        // Render game state and boss health
        gameLogic.renderGame(g, heartImage);
        String hpText = "HP is " + gameLogic.bossHealth();
        g.drawString(hpText, (960 - fm.stringWidth(hpText)) / 2, 30);
    }

    // Draw the Win Screen when the player wins the game
    private void drawWinScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String text = "CONGRATULATIONS!";
        String text2 = "You Have Won The Game!";
        
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (960 - fm.stringWidth(text)) / 2, 200);
        g.drawString(text2, (960 - fm.stringWidth(text2)) / 2, 250);

        // Display final score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fm1 = g.getFontMetrics();
        String scoreText = "Your score was " + gameLogic.getScore();
        g.drawString(scoreText, (960 - fm1.stringWidth(scoreText)) / 2, 300);
    }

    // Draw the Death Screen when the game is over
    private void drawDeathScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String text = "Game Over";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (960 - fm.stringWidth(text)) / 2, 200);

        // Prompt to restart the game
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fm1 = g.getFontMetrics();
        String restartText = "Press R to Restart";
        g.drawString(restartText, (960 - fm1.stringWidth(restartText)) / 2, 300);
    }

    private void drawKeybindScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 960, 600);
        g.setColor(Color.WHITE); // Set text color
        g.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font for the keybinds
        FontMetrics fm = g.getFontMetrics();
        
        // Keybind text
        String[] keybindsText = {
            "Player 1:",
            "Up Arrow Key = Up",
            "Down Arrow Key = Down",
            "Left Arrow Key = Left",
            "Right Arrow Key = Right",
            "Option or Alt = Fire Projectile",
            "",
            "Player 2:",
            "W = Up",
            "S = Down",
            "A = Left",
            "D = Right",
            "Space Bar = Fire Projectile",
            "",
            "",
            "O = Skip"
        };

        // Calculate vertical starting point for centering
        int startY = (600 - keybindsText.length * fm.getHeight()) / 2; // Screen height = 600
        
        // Draw each line of keybind text centered horizontally
        for (int i = 0; i < keybindsText.length; i++) {
            String line = keybindsText[i];
            int x = (960 - fm.stringWidth(line)) / 2; // Center horizontally, screen width = 960
            int y = startY + i * fm.getHeight(); // Calculate Y position for each line
            g.drawString(line, x, y);
        }
    }

    // KeyListener methods to handle key events
    @Override
    public void keyPressed(KeyEvent e) {
        gameLogic.handleKeyPress(e.getKeyCode());  // Handle key press
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gameLogic.handleKeyRelease(e.getKeyCode());  // Handle key release
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    //method that handels when each of the buttons should be visible and not
    private void updateButtonVisibility() {
        if (gameLogic.currentLevel() == 0) {
            titleLabel.setVisible(true);
            startGame.setVisible(true);
            keybinds.setVisible(true);
            returnB.setVisible(false);
        } else if (gameLogic.currentLevel() == 6) {
            titleLabel.setVisible(false);
            startGame.setVisible(false);
            keybinds.setVisible(false);
            returnB.setVisible(true);
        } else {
            titleLabel.setVisible(false);
            startGame.setVisible(false);
            keybinds.setVisible(false);
            returnB.setVisible(false);
        }
    }

    // ActionListener method to handle the start button click
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == startGame) {
            gameLogic.startLevelOne();  // Start the first level
        }
        if(event.getSource() == keybinds) {
            gameLogic.keybindScreen(); //Go to the Keybind Screen
        }
        if(event.getSource() == returnB){
            gameLogic.resetGame(); //Go back to the Start Menu
        }
    }

    // Animation loop to continuously update and repaint the game
    public void animate() {
        while (true) {
            try {
                Thread.sleep(70);  // Control frame rate
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            gameLogic.updateGameState();  // Update game state
            repaint();  // Repaint the screen
        }
    }
}