import javax.swing.*;

public class SnakeGame extends JFrame {
    public SnakeGame(){
        setTitle("Snake");
        setResizable(false);
        
        add(new GamePanel());
        
        pack(); // Adjusts window size to fit panel 
        setLocationRelativeTo(null); // Centers the window
        setVisible(true);
    }
    
    public static void main() {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}