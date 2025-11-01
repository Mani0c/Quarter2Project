import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


class GameLogic { 
    //set up variables, arrays, instantiations, and pool/hash
    private Screen screen; //Instatiates screen
    private int bossBombFreq = 6; //double of how many bombs the boss drops each run
    private Bomb[] bomb = new Bomb[6]; //bombs of the normal planes
    private Bomb[] bossBomb = new Bomb[bossBombFreq]; //bombs of the boss plane
    private Hummer hummer; //instantiates Hummer
    private Plane planeManager; //instantiates Plane
    private Chopper chopper, chopper2;//instantiates Chopper aka the 2 players
    private int defeatedPlanes; //for checking the number of planes defeated in total
    private int chopperLives = 3, chopper2Lives = 3; //chopper total lives
    private boolean upPressed, downPressed, leftPressed, rightPressed; // booleans for movement
    private boolean wPressed, aPressed, sPressed, dPressed;// booleans for movement
    private long startTime;//to set when start time, to use timer
    private int score = 0;//score...keeps track of score
    private Map<String, Clip> soundPool = new HashMap<>(); //where the sounds are stored
    private int endGameSound = 0; //just makes sure the game sound is only played once
    private int planeBossSpawn = 0; //makes sure only one boss plane is spawned
    private int currentLevel = 0;//keeps track of current level Key:
    /*0 = Start screen
      1 = Level 1
      2 = Level 2
      3 = Boss level
      4 = Win
      5 = Death  
      6 = Keybind screen */ 
    private long lastBossBombTime = 0; // Tracks the last bomb drop time
    private int bossBombCounter = 0; // Counts bombs in the current cycle
    private boolean bossBombPause = false; // Indicates if the system is in pause phase
    private int bossHealth;//Tracks the Boss health
    
    //Constructor: passes through the class screen and calls loadSounds method
    public GameLogic(Screen screen) {
        this.screen = screen;
        loadSounds();
    }
    
    //loads the sounds from the media folder
    private void loadSounds() {
        loadSound("Media/Audio/BombExplosion.wav");
        loadSound("Media/Audio/ChopperHit.wav");
        loadSound("Media/Audio/Lost.wav");
        //Add more sounds here <----
    }
    //Loads the clip and saves it and then allows it to be called by playsound
    private void loadSound(String soundFileName) {
        try {
            // Check if sound is already in the pool
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
    
    //allows the clip to be played in different methods
    public void playSound(String soundFileName) {
        try {
            if (soundPool.containsKey(soundFileName)) {
                Clip clip = soundPool.get(soundFileName);  
                clip.setFramePosition(0); // Reset to the beginning
                clip.start(); // Play the sound
                if(currentLevel == 5){
                    if (clip.isRunning()) {
                        clip.stop(); // Stop the current playback to allow restart
                    }
                }
            } else {
                System.out.println("Sound not found in pool: " + soundFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to play sound: " + soundFileName);
        }
    }

    //sets all the instance variables and instatiates the necesary objects to start level one
    public void startLevelOne() {
        hummer = new Hummer();
        planeManager = new Plane();
        chopper = new Chopper(200, 200);
        chopper2 = new Chopper(600, 200);
        currentLevel = 1;
        assignBombs();
    }
    //just sets it to screen 6 when you go to the keybind screen
    public void keybindScreen() {
        currentLevel = 6;
    }

    //method that resets all the variables that get updated during the game 
    //and need to be reset to play the game again
    public void resetGame() {
        defeatedPlanes = 0;
        chopperLives = 3;
        chopper2Lives = 3;
        currentLevel = 0;
        score = 0;
        endGameSound = 0;
        resetAllKeyPresses();
    }

    //methos that uses a for loop to assign each bomb to its own plane
    public void assignBombs() {
        for (int i = 0; i < bomb.length; i++) {
            bomb[i] = new Bomb(planeManager.getPlaneX(i), planeManager.getPlaneY(i), i);
        }
    }

    //method to reset all the keys so it doesnt get stuck in the true state
    private void resetAllKeyPresses(){
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        wPressed = false;
        aPressed = false;
        sPressed = false;
        dPressed = false;
    }

    //method to get the elapsed time in seconds
    public int getElapsedTimeInSeconds() {
        return (int) ((System.nanoTime() - startTime) / 1000000000); // Convert nanoseconds to seconds
    }

    //returns the current level
   public int currentLevel(){
        return currentLevel;
   }

    //returns the current score
    public int getScore() {
        return score;
    }

    //method to add points to the score
    public void addScore(int points) {
        score += points;
    }

    //method that takes away health from the boss
    public void bossDamage(int damage){
        bossHealth -= damage;
    }

    //returns the boss health
    public int bossHealth(){
        return bossHealth;
    }

    //this handels all the key preses that get sent to the screen then to the keyboard listener
    public void handleKeyPress(int keyCode) {
        if (currentLevel == 5 && keyCode == KeyEvent.VK_R) {
            resetGame();
        }
        switch (keyCode) {
            case KeyEvent.VK_UP -> upPressed = true;
            case KeyEvent.VK_DOWN -> downPressed = true;
            case KeyEvent.VK_LEFT -> leftPressed = true;
            case KeyEvent.VK_RIGHT -> rightPressed = true;
            case KeyEvent.VK_W -> wPressed = true;
            case KeyEvent.VK_A -> aPressed = true;
            case KeyEvent.VK_S -> sPressed = true;
            case KeyEvent.VK_D -> dPressed = true;
            case KeyEvent.VK_SPACE -> chopper2.fire(System.nanoTime() / 1000000);
            case KeyEvent.VK_ALT -> chopper.fire(System.nanoTime() / 1000000);
        }
    }

    //handles all the key releases that get sent to screen then keyboard listener
    public void handleKeyRelease(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP -> upPressed = false;
            case KeyEvent.VK_DOWN -> downPressed = false;
            case KeyEvent.VK_LEFT -> leftPressed = false;
            case KeyEvent.VK_RIGHT -> rightPressed = false;
            case KeyEvent.VK_W -> wPressed = false;
            case KeyEvent.VK_A -> aPressed = false;
            case KeyEvent.VK_S -> sPressed = false;
            case KeyEvent.VK_D -> dPressed = false;
        }
        if(keyCode == KeyEvent.VK_O){
            if(currentLevel == 0){
                startLevelOne();
            }
            else if(currentLevel == 6){
                currentLevel = 0;
                resetGame();
            }
            else if(currentLevel == 2){
                currentLevel = 3;
                startTime = System.nanoTime();
                hummer.reset(); 
                resetAllKeyPresses();
                planeManager.spawnPlaneBoss();
                for (int i = 0; i < bossBomb.length; i++) {
                    bossBomb[i] = new Bomb(planeManager.getBossPlaneX(0), planeManager.getBossPlaneY(0), i);
                }
                bossHealth =100;
            } else if(currentLevel == 1){
                for (int i = 0; i < planeManager.PLANE_COUNT; i++) {
                    planeManager.resetPlane(i);
                }
                currentLevel++;
            }else{
                currentLevel++;
            }
        }
    }

    //method that is constantly checked for and keyboard movements and then moves the players accordingly
    public void updateChopper(){
        if (upPressed) chopper.moveUp();
        if (downPressed) chopper.moveDown();
        if (leftPressed) chopper.moveLeft();
        if (rightPressed) chopper.moveRight();
        if (wPressed) chopper2.moveUp();
        if (sPressed) chopper2.moveDown();
        if (aPressed) chopper2.moveLeft();
        if (dPressed) chopper2.moveRight();
    }

    //this goes through the entire array and updates its postion to where it should be
    //it also passes through the chance for how likely it is for the bomb to drop
    public void bombDroppingUpdate(double bombDropChance) {
        for (int i = 0; i < bomb.length; i++) {
            Bomb b = bomb[i];
            if (b != null) {
                b.update(planeManager.getPlaneX(i), planeManager.getPlaneY(i));
                if (!b.isDropped() && Math.random() < bombDropChance) {
                    b.drop();
                }
            }
        }
    }
    
    //This method is constantly updating the game states 
    //everything that needs to be updated gets funneled through here
    public void updateGameState() {
        if (currentLevel == 0 || chopper == null || chopper2 == null) {
            return;
        }
        //this if statements updates stuff that should be true for all levels 1, 2, and 3
        if(currentLevel == 1 || currentLevel == 2 || currentLevel == 3){
            //Game over check check
            if (hummer.getLives() <= 0 || (chopper.getLives() <= 0 && chopper2.getLives() <= 0)){
                if(endGameSound < 1){
                    playSound("Media/Audio/Lost.wav");
                    endGameSound++;
                }
                //goes to death screen
                currentLevel = 5;
            }
            //update chopper
            updateChopper();
            chopper.moveProjectile();
            chopper2.moveProjectile();
            //checks collisions
            checkCollisions();
            hummer.HummerX();
        }
        //if its level one all of these are active
        if (currentLevel == 1) {
            //if the hummer gets to the end it updates all the necessary instance variables
            if(hummer.getX() < 0){
                currentLevel = 2;
                hummer.reset(); 
                resetAllKeyPresses();
                for (int i = 0; i < planeManager.PLANE_COUNT; i++) {
                    planeManager.resetPlane(i);
                }    
            }
            //Update Planes
            planeManager.updatePlanePositions();
            // Update bombs
            bombDroppingUpdate(0.01);

        }
        if (currentLevel == 2){
            //waits 3 seconds before strting to drow the bombs
            if(getElapsedTimeInSeconds() > 3){
                bombDroppingUpdate(0.05);
            }
            //increases the speed of the bombs by 9
            for (Bomb b : bomb) {
                b.increaseSpeed(9);
            }
            //updates the hummer

            if(hummer.getX() < 0){
                bossHealth = 100;
                currentLevel = 3;
                startTime = System.nanoTime();
                hummer.reset(); 
                resetAllKeyPresses();
                planeManager.spawnPlaneBoss();
                for (int i = 0; i < bossBomb.length; i++) {
                    bossBomb[i] = new Bomb(planeManager.getBossPlaneX(0), planeManager.getBossPlaneY(0), i);
                }
                
            }
            planeManager.updatePlanePositions();
        }
        
    if (currentLevel == 3) {
            chopper.moveProjectile();
            chopper2.moveProjectile();
            planeManager.updateBossPlane();
            if(bossHealth <= 0){
                currentLevel = 4;
            }
            if (planeBossSpawn < 1) {
                planeManager.spawnPlaneBoss();
                planeBossSpawn++;
            }
            // Handle boss bomb logic
            long currentTime = System.nanoTime() / 1_000_000; // Current time in milliseconds
            if (!bossBombPause) {
                if (currentTime - lastBossBombTime >= 500) { // 0.5 seconds interval
                    if (bossBombCounter < bossBombFreq/2) { // Drop up to 3 bombs
                        for (Bomb bossBomb : bossBomb) {
                            if(bossBomb == null){
                                System.out.println("bomb is null");
                            }
                            if (bossBomb != null && !bossBomb.isBossDropped()) {
                                bossBomb.bossDrop();
                                break; // Drop one bomb at a time
                            }
                        }
                        bossBombCounter++;
                        lastBossBombTime = currentTime;
                    } else { // After dropping 3 bombs, enter pause phase
                        bossBombPause = true;
                        lastBossBombTime = currentTime; // Start pause timing
                    }
                }
            } else {
                if (currentTime - lastBossBombTime >= 2000) { // 2 seconds pause
                    bossBombPause = false; // Resume dropping bombs
                    bossBombCounter = 0; // Reset bomb counter
                }
            }
            // Update bomb positions for Boss Plane
            for (Bomb bossBomb : bossBomb) {
                if (bossBomb != null){
                bossBomb.bossUpdate(planeManager.getBossPlaneX(0), planeManager.getBossPlaneY(0));
                }
            }
        }
    }
    
    //method that draws all the components
    public void renderGame(Graphics g, BufferedImage heartImage) {
        //draws all the stuff that is in level 1 and 2
        if (currentLevel == 1 || currentLevel == 2){
            //draws planes and bombs
            planeManager.drawPlanes(g);
            for (Bomb b : bomb) {
                if (b != null) {
                    b.draw(g);
                }
            }
        }
        //draws all the components in level 3
        if (currentLevel == 3){
            //System.out.println(getElapsedTimeInSeconds());
            planeManager.drawBossPlane(g);
            if (hummer.getX() < 0){
                currentLevel = 4;
            }
            for (Bomb b : bossBomb) {
                if (b.isBossDropped()) {
                    b.draw(g); // Draw the bomb if it is dropped
                }
            }
        }
        //this is the stuff that always gets drawn (This method is not called outside of level 1, 2, and 3)
        g.drawImage(hummer.hummer, hummer.HummerX, hummer.getY(), null);//Hummer
        drawHearts(g, heartImage, hummer.getLives(), hummer.getX()+40, hummer.getY()+5);//Hearts and Choppers
        if (chopper.isAlive()) {
            chopper.render(g);
            drawHearts(g, heartImage, chopper.getLives(), chopper.getX()+40, chopper.getY()+5);
        }
        if (chopper2.isAlive()) {
            chopper2.render(g);
            drawHearts(g, heartImage, chopper2.getLives(), chopper2.getX()+40, chopper2.getY()+5);
        }
        //Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
    }
    //method that draws the hearts centred above the object is being drawn above
    private void drawHearts(Graphics g, BufferedImage heartImage, int lives, int x, int y) {
        int heartWidth = 30, heartSpacing = 4;
        int totalWidth = lives * heartWidth + (lives - 1) * heartSpacing;
        int startX = x - totalWidth / 2, startY = y - 40;
        for (int i = 0; i < lives; i++) {
            g.drawImage(heartImage, startX + i * (heartWidth + heartSpacing), startY, heartWidth, heartWidth, null);
        }
    }
    //method that checks collisions
    public void checkCollisions() {
        boolean[] planeProcessed = new boolean[planeManager.PLANE_COUNT]; // Track processed planes
        //for loop that goes through all the planes
        for (int i = 0; i < planeManager.PLANE_COUNT; i++) {
            //this checks collisions with the normal planes and choppers in levels 1 and 2
            if(currentLevel == 1 || currentLevel == 2){
                if (planeManager.plane[i] != null && !planeProcessed[i]) { // Check only unprocessed planes
                    int planeX = planeManager.getPlaneX(i);
                    int planeY = planeManager.getPlaneY(i);
                    int planeWidth = planeManager.planeWidth;
                    int planeHeight = planeManager.planeHeight;
                    //boss plane
    
                    if (currentLevel == 1 || currentLevel == 2 ){
                        // Check collision with Chopper 1
                        if (chopper.isAlive() && chopper.checkCollision(chopper.getX(), chopper.getY(), chopper.getWidth(), chopper.getHeight(), planeX, planeY, planeWidth, planeHeight)) {
                            planeManager.resetPlane(i);
                            int previousLives = chopper.getLives();
                            chopper.updateLives(true);
                            addScore(10);
                            planeProcessed[i] = true;
                            if (previousLives > chopper.getLives()) {
                                playSound("Media/Audio/ChopperHit.wav");
                            }
                        } 
                        // Check collision with Chopper 2
                        else if (chopper2.isAlive() && chopper2.checkCollision(chopper2.getX(), chopper2.getY(), chopper2.getWidth(), chopper2.getHeight(), planeX, planeY, planeWidth, planeHeight)) {
                            planeManager.resetPlane(i);
                            int previousLives = chopper.getLives();
                            chopper2.updateLives(true);
                            addScore(10);
                            planeProcessed[i] = true;
                            if (previousLives > chopper2.getLives()) {
                                playSound("Media/Audio/ChopperHit.wav");
                            }
                        } 
                    }
    
                }
            }
        }
        //checks collision with boss plane and choppers 
        if(currentLevel == 3){
            int bossPlaneX = planeManager.getBossPlaneX(0);
            int bossPlaneY = planeManager.getBossPlaneY(0);
            int bossPlaneWidth = planeManager.bossPlaneWidth;
            int bossPlaneHeight = planeManager.bossPlaneHeight;
            if (chopper.isAlive() && chopper.checkCollision(chopper.getX(), chopper.getY(), chopper.getWidth(), chopper.getHeight(), bossPlaneX, bossPlaneY, bossPlaneWidth, bossPlaneHeight)) {
                int previousLives = chopper.getLives();
                chopper.updateLives(true); // Reduce lives
                if (previousLives > chopper.getLives()) {
                    playSound("Media/Audio/ChopperHit.wav");
                }
            }

            if (chopper2.isAlive() && chopper2.checkCollision(chopper2.getX(), chopper2.getY(), chopper2.getWidth(), chopper2.getHeight(), bossPlaneX, bossPlaneY, bossPlaneWidth, bossPlaneHeight)) {
                int previousLives = chopper2.getLives();
                chopper2.updateLives(true); // Reduce lives
                if (previousLives > chopper2.getLives()) {
                    playSound("Media/Audio/ChopperHit.wav");
                }
            }
        }

        // Check projectile collisions with planes using Chopper's checkCollisionWithPlanes method
        if (chopper.isAlive() && chopper.checkCollisionWithPlanes(planeManager)) {
            addScore(20); // Bonus score for projectile hit
        }
        if (chopper2.isAlive() && chopper2.checkCollisionWithPlanes(planeManager)) {
            addScore(20); // Bonus score for projectile hit
        }
        if (chopper.isAlive() && chopper.checkCollisionWithBossPlane(planeManager, currentLevel)) {
            addScore(20); // Bonus score for projectile hit
            bossDamage(5);
        }
        if (chopper2.isAlive() && chopper2.checkCollisionWithBossPlane(planeManager, currentLevel)) {
            addScore(20); // Bonus score for projectile hit
            bossDamage(5);
        }
        // Check bomb collisions with the hummer
        for (Bomb b : bomb) {
            if (b != null && b.isDropped() && b.checkCollision(hummer.getX(), hummer.getY())) {
                hummer.updateLives(true);
                b.isDropped = false;
                playSound("Media/Audio/BombExplosion.wav");

            }
        }
        for (Bomb b : bossBomb){
            if (b != null && b.isBossDropped() && b.checkBossCollision(hummer.getX(), hummer.getY())) {
                hummer.updateLives(true);
                b.isBossDropped = false;
                playSound("Media/Audio/BombExplosion.wav");
            }
        }
        
    }
} 