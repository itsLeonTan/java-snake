import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;
    private final int UNIT_SIZE = 25; // Size of each grid square
    private final int DELAY = 100; // Game speed (ms)
    
    // Snake variables
    private final int x[] = new int[SCREEN_WIDTH * SCREEN_HEIGHT / (UNIT_SIZE * UNIT_SIZE)];
    private final int y[] = new int[SCREEN_WIDTH * SCREEN_HEIGHT / (UNIT_SIZE * UNIT_SIZE)];
    private int bodyParts = 6; // Initial snake length
    private char direction = 'R'; // 'U' = up, 'D' = down, 'L' = left, 'R' = right
    private char colorList[] = {'R', 'G', 'B', 'Y', 'C', 'P', 'W'};
    private int color = 1;
    
    // Food variables
    private int foodX;
    private int foodY;
    
    
    private int score = 0;
    private boolean running = false; // Game state
    private boolean pause = false;
    private boolean customize = false;
    
    private Timer timer;
    
    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.black);
        setFocusable(true); // Needed for keyboard input
        addKeyListener(new MyKeyAdapter());
        
        startGame();
    }
    
    public void startGame() {
        // Reset snake
        bodyParts = 6;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        
        // Reset food and score
        foodX = -UNIT_SIZE;
        foodY = -UNIT_SIZE;
        
        if (!customize) newFood();
        
        running = true;
        
        timer = new Timer(DELAY, this);
        timer.start();
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
    
    public boolean checkFoodSpawn() {
        for (int i = 0; i < bodyParts; i++) {
            if (x[i] == foodX && y[i] == foodY) return true;
        }
        return false;
    }
    
    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) running = false;
        }
        
        // Check if head touches boarders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH) running = false;
        if (y[0] < 0 || y[0] >= SCREEN_HEIGHT) running = false;
        if (!running) timer.stop();
    }
    
    public void draw(Graphics g) {
        if (running) {
            g.setColor(new Color(25, 25, 25));
            // Draw horizontal lines
            for (int y = 0; y <= SCREEN_HEIGHT; y += UNIT_SIZE) {
                g.drawLine(0, y, SCREEN_WIDTH, y);
            }
            // Draw verticle lines
            for (int x = 0; x <= SCREEN_WIDTH; x += UNIT_SIZE) {
                g.drawLine(x, 0, x, SCREEN_HEIGHT);
            }
            
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
            if (!customize) g.drawString("Score: " + score, 10, 20); // Draw score
            
            FontMetrics metrics = getFontMetrics(g.getFont());
            if (customize) g.drawString("< 'space' to select and start >", (SCREEN_WIDTH - metrics.stringWidth("< 'space' to select and start >")) / 2, SCREEN_HEIGHT * 2 / 3);
            if (pause) g.drawString("Paused", (SCREEN_WIDTH - metrics.stringWidth("Paused")) / 2, SCREEN_HEIGHT / 2);
        }
            
        if (!running) {
            // Display score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + score)) / 2, SCREEN_HEIGHT / 3);
            g.drawString("'space' to retry", (SCREEN_WIDTH - metrics1.stringWidth("'space' to retry")) / 2, SCREEN_HEIGHT * 2 / 3);
            g.drawString("'enter' to customize", (SCREEN_WIDTH - metrics1.stringWidth("'enter' to customize")) / 2, SCREEN_HEIGHT * 2 / 3 + 25);
            
            // Display Game Over text
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 75));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        }
    }
    
    public void move(){
        // Move body segments
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        // Custom movement for customize menu
        if (customize) {
            if (x[0] == 450) direction = 'D';
            if (y[0] == 325) direction = 'L';
            if (x[0] == 325 && direction != 'R') direction = 'U';
            if (y[0] == 225 && direction != 'D') direction = 'R';
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
        if (running && !pause) {
            move();
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
            if (running && !customize) {
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
            } else if (running && customize) {
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
                        timer.stop();
                        startGame();
                    }
                }
            } else{
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE -> startGame();
                    case KeyEvent.VK_ENTER -> {
                        customize = true;
                        startGame();
                    }
                }
            }
        }
    }
}