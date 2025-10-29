import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.Scanner;

public class GamePanel extends JPanel implements ActionListener {
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;
    private final int UNIT_SIZE = 25; // Size of each grid square
    private final int TOTAL_UNITS = SCREEN_WIDTH * SCREEN_HEIGHT / (UNIT_SIZE * UNIT_SIZE);
    private final int DELAY = 100; // Game speed (ms)
    
    // Game scores
    private int score = 0;
    private int[] scoreBoard = new int[10];
    private String[] nameBoard = new String[10];
    private boolean beaten = false;
    
    // Snake variables
    private final int x[] = new int[TOTAL_UNITS];
    private final int y[] = new int[TOTAL_UNITS];
    private boolean[] whichCoor = {true, false, false, false};
    private int setX, setY; // For custom direction
    private int bodyParts; // Snake length
    private char direction; // 'U' = up, 'D' = down, 'L' = left, 'R' = right
    private char colorList[] = {'R', 'G', 'B', 'Y', 'C', 'P', 'W'};
    private int color = 1;
    
    // Food variables
    private int foodX;
    private int foodY;
    
    // Game state
    private boolean running = false; 
    private boolean pause = false;
    private boolean customize = false;
    private boolean leaderboard = false;
    
    private Timer timer;
    
    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true); // Needed for keyboard input
        addKeyListener(new MyKeyAdapter());
        
        timer = new Timer(DELAY, this);
        timer.start();
        startGame();
        
        try { updateLeaderboard(); }
        catch (IOException ioe) { ioe.printStackTrace(); }
    }
    
    public void startGame() {
        reset();
        score = 0;
        running = true;
        if (!customize && !leaderboard) newFood();
    }
    
    public void reset() {
        // Initialize snake length and direction
        bodyParts = 6;
        direction = 'R';
        
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        
        // Reset food and score
        foodX = -UNIT_SIZE;
        foodY = -UNIT_SIZE;
    }
    
    public void updateLeaderboard() throws IOException {
        File fileS = new File("ScoreBoard.txt");
        File fileN = new File("NameBoard.txt");
        Scanner inputS = new Scanner(fileS);
        Scanner inputN = new Scanner(fileN);
        
        if (beaten) {
            PrintWriter writer = new PrintWriter(new FileWriter(fileS));
            for (int score: scoreBoard) writer.print(score + " ");
            beaten = false;
            writer.close();
        }
        
        // Read from leaderboard.txt
        for (int i = 0; i < 10; i++) {
            scoreBoard[i] = inputS.nextInt();
            nameBoard[i] = inputN.nextLine();
        }
    }
    
    public boolean checkFoodSpawn() {
        for (int i = 0; i < bodyParts; i++) {
            if (x[i] == foodX && y[i] == foodY) return true;
        }
        return false;
    }
    
    public void newFood() {
        do{
            foodX = (int) (Math.random() * (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            foodY = (int) (Math.random() * (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } while (checkFoodSpawn());
    }
    
    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyParts++;
            score++;
            newFood();
        }
    }
    
    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) running = false;
        }
        
        // Check if head touches boarders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH) running = false;
        if (y[0] < 0 || y[0] >= SCREEN_HEIGHT) running = false;
        
        if (!running) {
            reset();
            
            // Compare score with leaderboard
            int temp = -1; // To hold score while score shifts
            for (int i = 0; i < 10; i++) {
                if (beaten == false && scoreBoard[i] < score){
                    beaten = true;
                    temp = scoreBoard[i];
                    scoreBoard[i] = score;
                } else if (beaten) {
                    int replace = temp;
                    temp = scoreBoard[i];
                    scoreBoard[i] = replace;
                }
            }
            
            try { updateLeaderboard(); }
            catch (IOException ioe) { ioe.printStackTrace(); }
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
        g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);
        
        // Draw snake
        for (int i = 0; i < bodyParts; i++) {
            int gradient = 255 - (i * 200 / bodyParts);
            if (colorList[color] == 'R') g.setColor(new Color(gradient, 0, 0));
            else if (colorList[color] == 'G') g.setColor(new Color(0, gradient, 0));
            else if (colorList[color] == 'B') g.setColor(new Color(0, 0, gradient));
            else if (colorList[color] == 'Y') g.setColor(new Color(gradient, gradient, 0));
            else if (colorList[color] == 'C') g.setColor(new Color(0, gradient, gradient));
            else if (colorList[color] == 'P') g.setColor(new Color(gradient, 0, gradient));
            else if (colorList[color] == 'W') g.setColor(new Color(gradient, gradient, gradient));
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
        
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        if (running) g.drawString("Score: " + score, 10, 20); // Draw score
        
        FontMetrics metrics = getFontMetrics(g.getFont());
        if (customize) {
            g.drawString("<                                    >", (SCREEN_WIDTH - metrics.stringWidth("<                                    >")) / 2, SCREEN_HEIGHT / 2);
            g.drawString("SPACE to start", (SCREEN_WIDTH - metrics.stringWidth("SPACE to start")) / 2, 400);
            g.drawString("'L' to leaderboard", (SCREEN_WIDTH - metrics.stringWidth("'L' to leaderboard")) / 2, 425);
        }
        
        if (leaderboard) {
            g.drawString("RANK", 250, 180);
            g.drawString("NAME", 350, 180);
            g.drawString("SCORE", 475, 180);
            for (int i = 0; i < 10; i++) {
                int yPos = (205 + i * 25);
                String rank;
                String zero = "00";
                
                if (i == 0) rank = "ST";
                else if (i == 1) rank = "ND";
                else if (i == 2) rank = "RD";
                else rank = "TH";
                
                if (scoreBoard[i] >= 10) zero = "0";
                if (scoreBoard[i] >= 100) zero = "";
                
                g.drawString((i + 1) + rank, 250, yPos);
                g.drawString(nameBoard[i], 350, yPos);
                g.drawString(zero + scoreBoard[i], 495, yPos);
            }
            
            g.drawString("SPACE to start", (SCREEN_WIDTH - metrics.stringWidth("SPACE to start")) / 2, 520);
            g.drawString("ENTER to customize", (SCREEN_WIDTH - metrics.stringWidth("ENTER to customize")) / 2, 545);
        }
        
        if (pause) g.drawString("Paused", (SCREEN_WIDTH - metrics.stringWidth("Paused")) / 2, SCREEN_HEIGHT / 2);
            
        // GameOver screen
        if (!running && !customize && !leaderboard) {
            // Display score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, SCREEN_HEIGHT / 3);
            g.drawString("SPACE to retry", (SCREEN_WIDTH - metrics1.stringWidth("SPACE to retry")) / 2, 400);
            g.drawString("ENTER to customize", (SCREEN_WIDTH - metrics1.stringWidth("ENTER to customize")) / 2, 425);
            g.drawString("'L' to leaderboard", (SCREEN_WIDTH - metrics1.stringWidth("'L' to leaderboard")) / 2, 450);
            
            // Display Game Over text
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 75));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        }
    }
    
    public void setDirection(int[] X, int[] Y, boolean[] Coor) {
        for (int i = 0; i < 4; i++) {
            if (Coor[i]) {
                setX = X[i];
                setY = Y[i];
            }
            if (x[0] == X[i] && y[0] == Y[i]) {
                if (i == 3 && Coor[3]) {
                    setX = X[0];
                    setY = Y[0];
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
        
        if (Math.abs(setX - x[0]) > Math.abs(setY - y[0])) {
            if ((setX - x[0]) > 0) direction = 'R';
            else direction = 'L';
        } else {
            if ((setY - y[0]) > 0) direction = 'D';
            else direction = 'U';
        }
    }
    
    public void move(){
        // Move body segments
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        // Custom movements
        if (customize){
            int[] X = {325, 450, 450, 325};
            int[] Y = {225, 225, 325, 325};
            setDirection(X, Y, whichCoor);
        }else if (leaderboard) {
            int[] X = {200, 575, 575, 200};
            int[] Y = {125, 125, 450, 450};
            setDirection(X, Y, whichCoor);
        }else if (!running) {
            int[] X = {150, 625, 625, 150};
            int[] Y = {100, 100, 475, 475};
            setDirection(X, Y, whichCoor);
        }
        
        // Move head
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // This will run every DELAY milliseconds
        if (!pause) move();
        if (running){
            checkFood();
            checkCollisions(); 
        }
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
            if (running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                    case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                    case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                    case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                    
                    case KeyEvent.VK_SPACE -> {
                        if (pause) pause = false;
                        else pause = true;
                    }
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
                }
            } else if (leaderboard) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> {
                        leaderboard = false;
                        startGame();
                    }
                    case KeyEvent.VK_ENTER -> {
                        leaderboard = false;
                        customize = true;
                    }
                }
            }else{
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> startGame();
                    case KeyEvent.VK_ENTER -> {
                        customize = true;
                    }
                    case KeyEvent.VK_L -> {
                        leaderboard = true;
                    }
                }
            }
        }
    }
}