import com.bacterialife.Main;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MainTest {
    @Test
    public void testMain() {
        assertDoesNotThrow(() -> {
            // 1. Cubrir el constructor implícito (evita que la clase cuente como no cubierta)
            new Main();

            // 2. Ejecutar el método main
            Main.main(new String[]{});
        });
    }
}