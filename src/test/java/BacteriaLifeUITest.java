import com.bacterialife.BacteriaLifeLogic;
import com.bacterialife.BacteriaLifeUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BacteriaLifeUITest {

    @Mock
    private BacteriaLifeLogic logicMock;

    private BacteriaLifeUI ui;
    private JFrame frame;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // CONFIGURACIÓN: Usamos dimensión 30 para coincidir con la constante de la clase
        int dim = 30;

        int[][] initialGen = new int[dim][dim];
        when(logicMock.generateInitialGen()).thenReturn(initialGen);
        when(logicMock.getRound()).thenReturn(0);

        // Inicializar UI
        ui = new BacteriaLifeUI(logicMock);

        // Ocultar Frame
        Frame[] frames = Frame.getFrames();
        for (Frame f : frames) {
            if (f.getTitle().equals("BacteriaLife") && f.isVisible()) {
                frame = (JFrame) f;
                frame.setVisible(false);
            }
        }
    }

    @AfterEach
    public void tearDown() {
        if (frame != null) frame.dispose();
    }

    // =========================================================================
    // 1. TEST DE INICIALIZACIÓN (Constructor y Grid)
    // =========================================================================
    @Test
    public void testGridInitialization() throws Exception {
        JPanel genPanel = obtenerGenPanel();
        assertNotNull(genPanel);
        assertTrue(genPanel.getLayout() instanceof GridLayout);
        assertEquals(900, genPanel.getComponentCount()); // 30x30
        verify(logicMock, times(1)).generateInitialGen();
    }

    // =========================================================================
    // 2. TEST COMPLETO DE CIRCLE
    // =========================================================================
    @Test
    public void testCircleMethods() throws Exception {
        JPanel genPanel = obtenerGenPanel();
        Object circle = genPanel.getComponent(0); // Es una instancia de Circle
        Class<?> circleClass = circle.getClass();

        // 1. Test getColor()
        Method getColor = circleClass.getMethod("getColor");
        Color c = (Color) getColor.invoke(circle);
        assertEquals(Color.WHITE, c);

        // 2. Test setCircleColor()
        Method setCircleColor = circleClass.getMethod("setCircleColor", Color.class);
        setCircleColor.invoke(circle, Color.RED);
        assertEquals(Color.RED, getColor.invoke(circle), "El color debería cambiar a ROJO");

        // 3. Test getPreferredSize()
        Method getPreferredSize = circleClass.getMethod("getPreferredSize");
        Dimension dim = (Dimension) getPreferredSize.invoke(circle);
        assertEquals(new Dimension(10, 10), dim, "El tamaño preferido debe ser 10x10");

        // 4. Test paintComponent() - Forzamos el pintado para cubrir esa línea
        Method paintComponent = circleClass.getDeclaredMethod("paintComponent", Graphics.class);
        paintComponent.setAccessible(true);

        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        paintComponent.invoke(circle, image.getGraphics());
    }

    // =========================================================================
    // 3. TEST DE MÉTODOS PRIVADOS DE LÓGICA UI
    // =========================================================================
    @Test
    public void testDeepCopy() throws Exception {
        Method deepCopyMethod = BacteriaLifeUI.class.getDeclaredMethod("deepCopy", int[][].class);
        deepCopyMethod.setAccessible(true);

        int[][] original = {{1, 0}, {0, 1}};
        int[][] copia = (int[][]) deepCopyMethod.invoke(ui, (Object) original);

        assertNotSame(original, copia, "Debe ser una instancia nueva");
        assertTrue(Arrays.deepEquals(original, copia), "El contenido debe ser idéntico");

        int[][] copiaNull = (int[][]) deepCopyMethod.invoke(ui, (Object) null);
        assertNull(copiaNull);
    }

    @Test
    public void testRefreshGenPanel() throws Exception {
        Field bacteriaGenField = BacteriaLifeUI.class.getDeclaredField("bacteriaGen");
        bacteriaGenField.setAccessible(true);
        int[][] nuevaGen = new int[30][30];
        nuevaGen[0][0] = 1; // Bacteria viva en la esquina
        bacteriaGenField.set(ui, nuevaGen);

        Method refreshMethod = BacteriaLifeUI.class.getDeclaredMethod("refreshGenPanel");
        refreshMethod.setAccessible(true);
        refreshMethod.invoke(ui);

        // Verificar que el panel se actualizó
        JPanel genPanel = obtenerGenPanel();
        Object primerCircle = genPanel.getComponent(0);
        Method getColor = primerCircle.getClass().getMethod("getColor");
        Color color = (Color) getColor.invoke(primerCircle);

        assertEquals(Color.BLACK, color, "La bacteria viva (1) debería pintarse de NEGRO tras el refresh");
    }

    // =========================================================================
    // 4. TEST DE INTERACCIÓN
    // =========================================================================
    @Test
    public void testStartButtonLogic() throws Exception {
        JButton btnStart = encontrarBotonPorTexto(frame, "Start");

        assertDoesNotThrow(() -> {
            for(java.awt.event.ActionListener al : btnStart.getActionListeners()) {
                al.actionPerformed(null);
            }
        });

        // Verificar que al menos tiene listeners
        assertTrue(btnStart.getActionListeners().length > 0);
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private JPanel obtenerGenPanel() throws Exception {
        Field f = BacteriaLifeUI.class.getDeclaredField("genPanel");
        f.setAccessible(true);
        return (JPanel) f.get(ui);
    }

    private JButton encontrarBotonPorTexto(Container container, String texto) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                if (texto.equals(((JButton) comp).getText())) return (JButton) comp;
            } else if (comp instanceof Container) {
                JButton res = encontrarBotonPorTexto((Container) comp, texto);
                if (res != null) return res;
            }
        }
        return null;
    }
}