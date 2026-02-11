import com.bacterialife.BacteriaLifeLogic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BacteriaLifeLogicTest {

    // =========================================================================
    // 1. TEST DE INICIALIZACIÓN
    // =========================================================================
    @Test
    public void testInicializacion() {
        int dim = 5;
        BacteriaLifeLogic logic = new BacteriaLifeLogic(dim);
        int[][] gen = logic.generateInitialGen();

        assertEquals(dim, gen.length);
        assertEquals(dim, gen[0].length);
        assertEquals(0, logic.getRound());

        // Verificar que solo hay 0s y 1s
        for (int[] fila : gen) {
            for (int celda : fila) {
                assertTrue(celda == 0 || celda == 1, "La celda debe ser 0 o 1");
            }
        }
    }

    // =========================================================================
    // 2. TEST DE VECINOS (checkNeighbours)
    // =========================================================================
    @Test
    public void testContarVecinos() {
        /*
           Tablero 3x3:
           1 0 0
           0 1 0
           0 0 1
         */
        int[][] tablero = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        // El centro (1,1) tiene 2 vecinos diagonales: (0,0) y (2,2)
        assertEquals(2, BacteriaLifeLogic.checkNeighbours(tablero, 1, 1), "El centro debería tener 2 vecinos");

        // La esquina (0,0) tiene 1 vecino diagonal: (1,1)
        assertEquals(1, BacteriaLifeLogic.checkNeighbours(tablero, 0, 0), "La esquina (0,0) debería tener 1 vecino");

        // La esquina vacía (0,2) tiene 1 vecino diagonal: (1,1) -> ¡CORREGIDO AQUÍ!
        assertEquals(1, BacteriaLifeLogic.checkNeighbours(tablero, 0, 2), "La esquina (0,2) tiene al centro como vecino diagonal");
    }

    // =========================================================================
    // 3. REGLAS DE EVOLUCIÓN (generateNewGen)
    // =========================================================================

    @Test
    public void testMuertePorSoledad() {
        // Una bacteria sola (1) muere si tiene 0 o 1 vecino
        BacteriaLifeLogic logic = new BacteriaLifeLogic(3);
        int[][] gen = {
                {0, 0, 0},
                {0, 1, 0}, // Sola
                {0, 0, 0}
        };

        int[][] nuevaGen = logic.generateNewGen(gen);
        assertEquals(0, nuevaGen[1][1], "Debe morir por soledad (0 vecinos)");
    }

    @Test
    public void testMuertePorAsfixia() {
        // Una bacteria rodeada de 4 o más muere
        /*
           0 1 0
           1 1 1  <- La del centro tiene 4 vecinos (arriba, izq, der, abajo)
           0 1 0
         */
        BacteriaLifeLogic logic = new BacteriaLifeLogic(3);
        int[][] gen = {
                {0, 1, 0},
                {1, 1, 1},
                {0, 1, 0}
        };
        // Verificamos vecinos antes
        assertEquals(4, BacteriaLifeLogic.checkNeighbours(gen, 1, 1));

        int[][] nuevaGen = logic.generateNewGen(gen);
        assertEquals(0, nuevaGen[1][1], "Debe morir por asfixia (4 vecinos)");
    }

    @Test
    public void testSupervivencia() {
        // Sobrevive con 2 o 3 vecinos
        /*
           1 0 0
           0 1 0  <- Tiene 2 vecinos
           0 0 1
         */
        BacteriaLifeLogic logic = new BacteriaLifeLogic(3);
        int[][] gen = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        int[][] nuevaGen = logic.generateNewGen(gen);
        assertEquals(1, nuevaGen[1][1], "Debe sobrevivir (2 vecinos)");
    }

    @Test
    public void testNacimiento() {
        // Nace si hay exactamente 3 vecinos
        /*
           0 1 0
           1 0 1  <- El centro está vacío (0) pero tiene 3 vecinos
           0 0 0
         */
        BacteriaLifeLogic logic = new BacteriaLifeLogic(3);
        int[][] gen = {
                {0, 1, 0},
                {1, 0, 1},
                {0, 0, 0}
        };
        // Verificamos vecinos
        assertEquals(3, BacteriaLifeLogic.checkNeighbours(gen, 1, 1));

        int[][] nuevaGen = logic.generateNewGen(gen);
        assertEquals(1, nuevaGen[1][1], "Debe nacer (3 vecinos)");
    }

    // =========================================================================
    // 4. TEST DE ESTABILIDAD
    // =========================================================================
    @Test
    public void testGeneracionEstable() {
        int[][] gen1 = {{0,0}, {0,0}};
        int[][] gen2 = {{0,0}, {0,0}};
        assertTrue(BacteriaLifeLogic.checkStableGen(gen1, gen2));

        int[][] gen3 = {{1,0}, {0,0}};
        assertFalse(BacteriaLifeLogic.checkStableGen(gen1, gen3));
    }

    // =========================================================================
    // 5. TEST DE LÍMITE DE RONDAS
    // =========================================================================
    @Test
    public void testMaxRondas() {
        BacteriaLifeLogic logic = new BacteriaLifeLogic(3);
        int[][] gen = new int[3][3];

        // Simulamos 301 rondas
        for(int i=0; i<=301; i++) {
            gen = logic.generateNewGen(gen);
        }

        // La ronda debería haber incrementado
        assertTrue(logic.getRound() > 300);

        // Al pasar el límite, devuelve la misma gen (sin cambios)
        int[][] nextGen = logic.generateNewGen(gen);
        assertTrue(BacteriaLifeLogic.checkStableGen(gen, nextGen), "Si supera MAX_ROUNDS no debe evolucionar");
    }
}