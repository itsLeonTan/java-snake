import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    private final static int SCREEN_WIDTH = 800;
    private final static int SCREEN_HEIGHT = 600;
    private final static int UNIT_SIZE = 25; // Size of each grid square
    private final static int TOTAL_UNITS = SCREEN_WIDTH * SCREEN_HEIGHT / (UNIT_SIZE * UNIT_SIZE);
    private static int delay = 100; // Game speed (ms)
    
    public static int getScreenWidth() { return SCREEN_WIDTH; }
    public static int getScreenHeight() { return SCREEN_HEIGHT; }
    public static int getUnitSize() { return UNIT_SIZE; }
    public static int getTotalUnits() { return TOTAL_UNITS; }
    public static int getDelay() { return delay; }
    
    private String congratsOut;
    private final String[] congrats = {
                "Ssspectacular! You've slithered into the top ranks.",
                "Hiss-tory has been made! You're on the leaderboard!",
                "Your snake leaves only defeated players in its trail!",
                "The leaderboard just got a little more venomous!",
                "You are scaling the food chain!",
                "You came. You saw. You sss-scored!",
                "Another snake bites the dust."
            };
    
    // Game scores
    private int score = 0;
    private int[] scoreBoard = new int[10];
    private String name = ""; // For player name input
    private String[] nameBoard = new String[10];
    
    // Snake's Movements & Color
    private static char direction; // 'U' = up, 'D' = down, 'L' = left, 'R' = right
    private boolean directionChanged = false;
    private boolean[] whichCoor = {true, false, false, false};
    private int setX, setY; // For custom direction
    private char colorList[] = {'R', 'G', 'B', 'Y', 'C', 'P', 'W'};
    private int color = 1;
    
    public static char getDirection() { return direction; }
    
    Snake snake = new Snake();
    Food food = new Food();
    
    // Game state
    private static char mods = 'C';
    private int menuOption = 0;
    private static boolean menu = false;
    private static boolean running = false; 
    private boolean pause = false;
    private boolean customize = false;
    private boolean leaderboard = false;
    private boolean loss = false;
    private boolean beaten = false;
    
    public static char getMods() { return mods; }
    public static boolean getMenu() { return menu; }
    public static boolean getRunning() { return running; }
    
    private Timer timer;
    
    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true); // Needed for keyboard input
        addKeyListener(new MyKeyAdapter());
        
        timer = new Timer(delay, this);
        timer.start();
        reset();
        menu = true;
        food.newFood();
        
        updateLeaderboard();
    }
    
    public void menuManager() {
        menu = false;
        food.resetFood();
        
        switch (menuOption) {
            case 0 -> {
                mods = 'C';
                startGame();
            }
            case 1 -> {
                mods = 'F';
                startGame();
            }
            case 2 -> {
                mods = 'S';
                startGame();
            }
            case 3 -> customize = true;
            case 4 -> leaderboard = true;
        }
    }
    
    public void startGame() {
        reset();
        score = 0;
        running = true;
        if (mods == 'S') {
            timerChange(150);
        }
        if (!customize && !leaderboard) food.newFood();
    }
    
    public void timerChange(int change) {
        delay = change;
        timer.stop();
        timer = new Timer(delay, this);
        timer.start();
    }
    
    public final void reset() {
        direction = 'R';
        snake.resetSnake();
        food.resetFood();
        timerChange(100);
    }
    
    public final void updateLeaderboard() {
        try {
            File file = new File("Leaderboard.txt");
            try (Scanner scanner = new Scanner(file)) {
                // Read from leaderboard.txt
                for (int i = 0; i < 10; i++) {
                    nameBoard[i] = scanner.next();
                    scoreBoard[i] = scanner.nextInt();
                }

                if (beaten) {
                    beaten = false;
                    nameBoard[9] = name;
                    scoreBoard[9] = score;
                    for (int i = 8; i >= 0; i--) {
                        if (scoreBoard[i + 1] > scoreBoard[i]) {
                            scoreBoard[i + 1] = scoreBoard[i];
                            scoreBoard[i] = score;
                            nameBoard[i + 1] = nameBoard[i];
                            nameBoard[i] = name;
                        }
                    }
                    
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        for (int i = 0; i < scoreBoard.length; i++) writer.println(nameBoard[i] + " " + scoreBoard[i]);
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void checkScoring() {
        if (Snake.getSnakeX(0) == food.getFoodX(0) && Snake.getSnakeY(0) == food.getFoodY(0)) {
            if (running) {
                Snake.setBodyParts(Snake.getBodyParts() + 1);
                score++;
                food.newFood(0);
                if (mods == 'S') timerChange(delay - 5);
            }
            else if (menu) {
                color = (int)(Math.random() * 6);
                food.newFood();
            }
        }
        if (mods == 'F') {
            // Food array length set at 10 here. PROBLEMATIC.
            for (int i = 1; i < 10; i++) {
                if (Snake.getSnakeX(0) == food.getFoodX(i) && Snake.getSnakeY(0) == food.getFoodY(i)) {
                    Snake.setBodyParts(Snake.getBodyParts() + 1);
                    score++;
                    food.newFood(i);
                }
            }
        }
    }
    
    public void draw(Graphics g) {
        g.setColor(new Color(25, 25, 25));
        // Draw horizontal lines
        for (int y = 0; y <= SCREEN_HEIGHT; y += UNIT_SIZE) g.drawLine(0, y, SCREEN_WIDTH, y);
        // Draw verticle lines
        for (int x = 0; x <= SCREEN_WIDTH; x += UNIT_SIZE) g.drawLine(x, 0, x, SCREEN_HEIGHT);
        
        // Draw food
        g.setColor(Color.red);
        g.fillOval(food.getFoodX(0), food.getFoodY(0), UNIT_SIZE, UNIT_SIZE);
        if (mods == 'F') {
            // Food array length set at 10 here. PROBLEMATIC.
            for (int i = 1; i < 10; i++) g.fillOval(food.getFoodX(i), food.getFoodY(i), UNIT_SIZE, UNIT_SIZE);
        }
        
        // Draw snake
        for (int i = 0; i < Snake.getBodyParts(); i++) {
            int gradient = 255 - (i * 200 / Snake.getBodyParts());
            switch (colorList[color]) {
                case 'R' -> g.setColor(new Color(gradient, 0, 0));
                case 'G' -> g.setColor(new Color(0, gradient, 0));
                case 'B' -> g.setColor(new Color(0, 0, gradient));
                case 'Y' -> g.setColor(new Color(gradient, gradient, 0));
                case 'C' -> g.setColor(new Color(0, gradient, gradient));
                case 'P' -> g.setColor(new Color(gradient, 0, gradient));
                case 'W' -> g.setColor(new Color(gradient, gradient, gradient));
            }
            g.fillRect(Snake.getSnakeX(i), Snake.getSnakeY(i), UNIT_SIZE, UNIT_SIZE);
        }
        
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        if (running) g.drawString("Score: " + score, 10, 20); // Draw score
        
        if (menu) {
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("Classic", 85, 196);
            g.drawString("Frenzy", 85, 246);
            g.drawString("Speed Run", 85, 296);
            g.drawString("Customize", 85, 346);
            g.drawString("Leaderboard", 85, 396);
            switch (menuOption) {
                case 0 -> g.drawString(">", 60, 196);
                case 1 -> g.drawString(">", 60, 246);
                case 2 -> g.drawString(">", 60, 296);
                case 3 -> g.drawString(">", 60, 346);
                case 4 -> g.drawString(">", 60, 396);
            }
        }
        
        FontMetrics metrics = getFontMetrics(g.getFont());
        if (customize) {
            g.drawString("<                                    >", (SCREEN_WIDTH - metrics.stringWidth("<                                    >")) / 2, SCREEN_HEIGHT / 2);
            g.drawString("SPACE to start", (SCREEN_WIDTH - metrics.stringWidth("SPACE to start")) / 2, 400);
            g.drawString("L to leaderboard", (SCREEN_WIDTH - metrics.stringWidth("L to leaderboard")) / 2, 425);
            g.drawString("ESCAPE to main menu", (SCREEN_WIDTH - metrics.stringWidth("ESCAPE to main menu")) / 2, 450);
        }
        
        if (leaderboard) {
            g.drawString("RANK", 250, 155);
            g.drawString("NAME", 350, 155);
            g.drawString("SCORE", 475, 155);
            for (int i = 0; i < 10; i++) {
                int yPos = (180 + i * 25);
                String rank;
                String zero = "00";
                
                switch (i) {
                    case 0 -> rank = "ST";
                    case 1 -> rank = "ND";
                    case 2 -> rank = "RD";
                    default -> rank = "TH";
                }
                
                if (scoreBoard[i] >= 10) zero = "0";
                if (scoreBoard[i] >= 100) zero = "";
                
                g.drawString((i + 1) + rank, 250, yPos);
                g.drawString(nameBoard[i], 350, yPos);
                g.drawString(zero + scoreBoard[i], 495, yPos);
            }
            
            g.drawString("SPACE to start", (SCREEN_WIDTH - metrics.stringWidth("SPACE to start")) / 2, 495);
            g.drawString("C to customize", (SCREEN_WIDTH - metrics.stringWidth("C to customize")) / 2, 520);
            g.drawString("ESCAPE to main menu", (SCREEN_WIDTH - metrics.stringWidth("ESCAPE to main menu")) / 2, 545);
        }
        
        if (pause) g.drawString("Paused", (SCREEN_WIDTH - metrics.stringWidth("Paused")) / 2, SCREEN_HEIGHT / 2);
            
        // GameOver screen
        if (loss) {
            // Display score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, SCREEN_HEIGHT / 3);
            g.drawString("SPACE to retry", (SCREEN_WIDTH - metrics1.stringWidth("SPACE to retry")) / 2, 400);
            g.drawString("C to customize", (SCREEN_WIDTH - metrics1.stringWidth("C to customize")) / 2, 425);
            g.drawString("L to leaderboard", (SCREEN_WIDTH - metrics1.stringWidth("L to leaderboard")) / 2, 450);
            
            // Display Game Over text
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 75));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        }
        
        if (beaten) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            
            g.drawString("Score : " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, 125);
            g.drawString(congratsOut, (SCREEN_WIDTH - metrics1.stringWidth(congratsOut)) / 2, 150);
            g.drawString("ENTER to submit score", (SCREEN_WIDTH - metrics1.stringWidth("ENTER to submit score")) / 2, 450);
            g.drawString("ESCAPE to discard score", (SCREEN_WIDTH - metrics1.stringWidth("ESCAPE to discard score")) / 2, 475);
            
            String nameOut;
            if (name.length() != 6) nameOut = name + "_";
            else nameOut = name;
            
            g.setColor(Color.green);
            g.setFont(new Font("Arial", Font.BOLD, 65));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString(nameOut,  (SCREEN_WIDTH - metrics2.stringWidth(nameOut)) / 2, 310);
        }
    }
    
    public void setDirection() {
        if (Math.abs(setX - Snake.getSnakeX(0)) > Math.abs(setY - Snake.getSnakeY(0))) {
            if ((setX - Snake.getSnakeX(0)) > 0) {
                if (!snake.checkCollisions(Snake.getSnakeX(0) + UNIT_SIZE, Snake.getSnakeY(0))) direction = 'R';
                else {
                    if (Math.random() >= 0.2) direction = 'U';
                    else direction = 'L';
                }
            }
            else {
                if (!snake.checkCollisions(Snake.getSnakeX(0) - UNIT_SIZE, Snake.getSnakeY(0))) direction = 'L';
                else {
                    if (Math.random() >= 0.2) direction = 'U';
                    else direction = 'R';
                }
            }
        } else {
            if ((setY - Snake.getSnakeY(0)) > 0) {
                if (!snake.checkCollisions(Snake.getSnakeX(0), Snake.getSnakeY(0) + UNIT_SIZE)) direction = 'D';
                else {
                    if (Math.random() >= 0.5) direction = 'R';
                    else direction = 'L';
                }
            }
            else {
                if (!snake.checkCollisions(Snake.getSnakeX(0), Snake.getSnakeY(0) - UNIT_SIZE)) direction = 'U';
                else {
                    if (Math.random() >= 0.5) direction = 'R';
                    else direction = 'L';
                }
            }
        }
    }
    
    public void calculateDirection(int[] X, int[] Y, boolean[] Coor) {
        for (int i = 0; i < 4; i++) {
            if (Coor[i]) {
                setX = X[i];
                setY = Y[i];
            }
            if (Snake.getSnakeX(0) == X[i] && Snake.getSnakeY(0) == Y[i]) {
                if (i == 3 && Coor[3]) {
                    setX = Snake.getSnakeX(0);
                    setY = Snake.getSnakeY(0);
                    whichCoor[3] = false;
                    whichCoor[0] = true;
                } else if (Coor[i]) {
                    setX = X[i+1];
                    setY = Y[i+1];
                    whichCoor[i] = false;
                    whichCoor[i+1] = true;
                }
            }
        }
        setDirection();
    }
    
    public void move(){
        snake.move();
        
        // Custom movements
        if (menu) {
            setX = food.getFoodX(0);
            setY = food.getFoodY(0);
            setDirection();
        }else if (customize){
            int[] X = {325, 450, 450, 325};
            int[] Y = {225, 225, 325, 325};
            calculateDirection(X, Y, whichCoor);
        }else if (leaderboard) {
            int[] X = {200, 575, 575, 200};
            int[] Y = {100, 100, 425, 425};
            calculateDirection(X, Y, whichCoor);
        }else if (loss) {
            int[] X = {150, 625, 625, 150};
            int[] Y = {100, 100, 475, 475};
            calculateDirection(X, Y, whichCoor);
        }else if (beaten) {
            int[] X = {175, 600, 600, 175};
            int[] Y = {225, 225, 325, 325};
            calculateDirection(X, Y, whichCoor);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // This will run every DELAY milliseconds
        if (!pause) {
            move();
            directionChanged = false;
        }
        if (running) {
            if (snake.checkCollisions()) {
                running = false;
                loss = true;
                reset();
                
                if (mods == 'C') {
                    for (int i = 9; i >= 0; i--) {
                        if (score > scoreBoard[i]) {
                            congratsOut = congrats[(int) (Math.random() * congrats.length)];
                            beaten = true;
                            loss = false;
                            name = ""; // Clear name;
                            break;
                        }
                    }
                }
                mods = 'C';
            }
        }
        checkScoring();
        repaint(); // Triggers the painComponent method
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clears display
        draw(g); 
    }
    
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (menu) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> menuManager();
                    case KeyEvent.VK_UP -> {
                        if (menuOption != 0) menuOption--;
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (menuOption != 4) menuOption++;
                    }
                    
                }
            }
            if (running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> { 
                        if (direction != 'R' && !directionChanged) {
                            direction = 'L';
                            directionChanged = true;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> { 
                        if (direction != 'L' && !directionChanged) {
                            direction = 'R'; 
                            directionChanged = true;
                        }
                    }
                    case KeyEvent.VK_UP -> { 
                        if (direction != 'D' && !directionChanged) {
                            direction = 'U'; 
                            directionChanged = true;
                        }
                    }
                    case KeyEvent.VK_DOWN -> { 
                        if (direction != 'U' && !directionChanged) {
                            direction = 'D';
                            directionChanged = true;
                        } 
                    }
                    
                    case KeyEvent.VK_SPACE -> pause = !pause;
                }
            } else if (customize) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> { 
                        if (color > 0) color--;
                        else color += colorList.length - 1;
                    }
                    case KeyEvent.VK_RIGHT -> { 
                        if (color < colorList.length - 1) color++;
                        else color -= colorList.length - 1;
                    }
                    case KeyEvent.VK_SPACE -> {
                        customize = false;
                        startGame();
                    }
                    case KeyEvent.VK_L -> {
                        customize = false;
                        leaderboard = true;
                    }
                    case KeyEvent.VK_ESCAPE -> {
                        customize = false;
                        menu = true;
                        food.newFood();
                    }
                }
            } else if (leaderboard) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> {
                        leaderboard = false;
                        startGame();
                    }
                    case KeyEvent.VK_C -> {
                        leaderboard = false;
                        customize = true;
                    }
                    case KeyEvent.VK_ESCAPE -> {
                        leaderboard = false;
                        menu = true;
                        food.newFood();
                    }
                }
            } else if (loss) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> {
                        loss = false;
                        startGame();
                    }
                    case KeyEvent.VK_C -> {
                        loss = false;
                        customize = true;
                    }
                    case KeyEvent.VK_L -> {
                        loss = false;
                        leaderboard = true;
                    }
                    case KeyEvent.VK_ESCAPE -> {
                        loss = false;
                        menu = true;
                        food.newFood();
                    }
                }
            } else if (beaten) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A, KeyEvent.VK_B, KeyEvent.VK_C, KeyEvent.VK_D,
                         KeyEvent.VK_E, KeyEvent.VK_F, KeyEvent.VK_G, KeyEvent.VK_H,
                         KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L,
                         KeyEvent.VK_M, KeyEvent.VK_N, KeyEvent.VK_O, KeyEvent.VK_P,
                         KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_S, KeyEvent.VK_T,
                         KeyEvent.VK_U, KeyEvent.VK_V, KeyEvent.VK_W, KeyEvent.VK_X,
                         KeyEvent.VK_Y, KeyEvent.VK_Z -> {
                        if (name.length() < 6) {
                            name += KeyEvent.getKeyText(e.getKeyCode());
                        }
                    }
                    
                    case KeyEvent.VK_BACK_SPACE -> {
                        if (name.length() > 0) {
                            name = name.substring(0, name.length() - 1);
                        }
                    }
                    case KeyEvent.VK_ESCAPE -> {
                        beaten = false;
                        menu = true;
                        food.newFood();
                    }
                    case KeyEvent.VK_ENTER -> {
                        if (name.length() != 0) {
                            updateLeaderboard(); 
                            leaderboard = true;
                        }
                    }
                }
            }
        }
    }
}