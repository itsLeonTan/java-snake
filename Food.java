
public class Food {
    private int[] X;
    private int[] Y;
    public Food() {
        X = new int[10];
        Y = new int[10];
    }
    
    public int getFoodX(int n) { return X[n]; }
    public int getFoodY(int n) { return Y[n]; }
        
    public void newFood() {
        do{
            if (GamePanel.getRunning()) {
                X[0] = (int) (Math.random() * (GamePanel.getScreenWidth() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
                Y[0] = (int) (Math.random() * (GamePanel.getScreenHeight() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
            }
            else if (GamePanel.getMenu()) {
                X[0] = (int) (Math.random() * (375 / GamePanel.getUnitSize())) * GamePanel.getUnitSize() + 300;
                Y[0] = (int) (Math.random() * (375 / GamePanel.getUnitSize())) * GamePanel.getUnitSize() + 100;
            }
            
        } while (checkFoodSpawn());
        
        if (GamePanel.getMods() == 'F') {
            for (int i = 1; i < X.length; i++) {
                do {
                    X[i] = (int) (Math.random() * (GamePanel.getScreenWidth() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
                    Y[i] = (int) (Math.random() * (GamePanel.getScreenHeight() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
                } while (checkFoodSpawn());
            }
        }
    }
    
    public void newFood(int index) {
        do{
            X[index] = (int) (Math.random() * (GamePanel.getScreenWidth() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();
            Y[index] = (int) (Math.random() * (GamePanel.getScreenHeight() / GamePanel.getUnitSize())) * GamePanel.getUnitSize();            
        } while (checkFoodSpawn());
    }
    
    public void resetFood() {
        for (int i = 0; i < X.length; i++) {
            X[i] = - GamePanel.getUnitSize();
            Y[i] = - GamePanel.getUnitSize();
        }
    }
    
    public boolean checkFoodSpawn() {
        for (int i = 0; i < Snake.getBodyParts(); i++) {
            if (Snake.getSnakeX(i) == X[0] && Snake.getSnakeY(i) == Y[0]) return true;
        }
        for (int j = 1; j < X.length; j++) {
            if (X[j] == X[0] && Y[j] == Y[0]) return true;
        }
        return false;
    }
}