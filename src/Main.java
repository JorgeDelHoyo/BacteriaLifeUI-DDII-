import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Variables
        int dimension = 30;

        // Create logic
        BacteriaLifeLogic logic = new BacteriaLifeLogic(dimension);

        // Create UI
        SwingUtilities.invokeLater(() -> {
            new BacteriaLifeUI(logic);
        });
    }
}