
public class Food {
    private final int[] x;
    private final int[] y;
    public Food() {
        x = new int[10];
        y = new int[10];
    }
    
    public int getFoodX(int n) { return x[n]; }
    public int getFoodY(int n) { return y[n]; }
        
    public void newFood() {
        do{
            if (GamePanel.getRunning()) {
                x[0] = (int) (Math.random() * (GamePanel.getScreenWidth() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
                y[0] = (int) (Math.random() * (GamePanel.getScreenHeight() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
            }
            else if (GamePanel.getMenu()) {
                x[0] = (int) (Math.random() * (375 / GamePanel.getUnitSize())) * GamePanel.getUnitSize() + 300;
                y[0] = (int) (Math.random() * (375 / GamePanel.getUnitSize())) * GamePanel.getUnitSize() + 100;
            }
            
        } while (checkFoodSpawn());
        
        if (GamePanel.getMods() == 'F') {
            for (int i = 1; i < x.length; i++) {
                do {
                    x[i] = (int) (Math.random() * (GamePanel.getScreenWidth() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
                    y[i] = (int) (Math.random() * (GamePanel.getScreenHeight() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
                } while (checkFoodSpawn());
            }
        }
    }
    
    public void newFood(int index) {
        do{
            x[index] = (int) (Math.random() * (GamePanel.getScreenWidth() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
            y[index] = (int) (Math.random() * (GamePanel.getScreenHeight() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();            
        } while (checkFoodSpawn());
    }
    
    public void resetFood() {
        for (int i = 0; i < x.length; i++) {
            x[i] = - GamePanel.getUnitSize();
            y[i] = - GamePanel.getUnitSize();
        }
    }
    
    public boolean checkFoodSpawn() {
        for (int i = 0; i < Snake.getBodyParts(); i++) {
            if (Snake.getSnakeX(i) == x[0] && Snake.getSnakeY(i) == y[0]) return true;
        }
        for (int j = 1; j < x.length; j++) {
            if (x[j] == x[0] && y[j] == y[0]) return true;
        }
        return false;
    }
}