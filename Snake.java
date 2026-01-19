
public class Snake {
    private final static int[] x = new int[GamePanel.getTotalUnits()];
    private final static int[] y = new int[GamePanel.getTotalUnits()];
    private static int bodyParts; // Snake length
    private char colorList[] = {'R', 'G', 'B', 'Y', 'C', 'P', 'W'};
    private int color = 1;
    
    public static int getBodyParts() { return bodyParts; }
    public static void setBodyParts(int n) { bodyParts = n; }
    public static int getSnakeX(int n) { return x[n]; }
    public static int getSnakeY(int n) { return y[n]; }
    
    public Snake() {
        bodyParts = 6;
    }
    
    public void resetSnake() {
        bodyParts = 6;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
    }
    
    public boolean checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) return true;
        }
        // Check if head touches boarders
        if (x[0] < 0 || x[0] >= GamePanel.getScreenWidth()) return true;
        if (y[0] < 0 || y[0] >= GamePanel.getScreenHeight()) return true;
        return false;
    }
    
    public boolean checkCollisions(int menuX, int menuY) {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if((menuX == x[i]) && (menuY == y[i])) return true;
        }
        return false;
    }
    
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        // Move head
        switch (GamePanel.getDirection()) {
            case 'U' -> y[0] = y[0] - GamePanel.getUnitSize();
            case 'D' -> y[0] = y[0] + GamePanel.getUnitSize();
            case 'L' -> x[0] = x[0] - GamePanel.getUnitSize();
            case 'R' -> x[0] = x[0] + GamePanel.getUnitSize();
        }
    }
}