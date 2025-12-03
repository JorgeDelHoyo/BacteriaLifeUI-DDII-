import java.util.Arrays;
import java.util.Random;

public class BacteriaLifeLogic {
    private final int DIMENSION;
    private int round = 0;
    private static final int MAX_ROUNDS = 300;
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    // Main function, launching the algorithm
    public BacteriaLifeLogic(int dimension) {
        this.DIMENSION = dimension;
    }

    // Generate initial generation, random positions
    public int[][] generateInitialGen() {
        Random rand = new Random();
        int[][] genTable = new int[DIMENSION][DIMENSION];

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                genTable[i][j] = rand.nextInt(2);
            }
        }
        return genTable;
    }

    // Generating a new gen until it's stable
    public int[][] generateNewGen(int[][] gen) {
        round++;
        // To prevent stack overflow, it wasn't in the assignment but just in case
        if (round > MAX_ROUNDS) {
            System.out.println("Can't get a stable gen.");
            return gen;
        }
        int[][] newGen = new int[DIMENSION][DIMENSION];
        // Check each position
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                int bacteria = gen[i][j];
                int neighbours = checkNeighbours(gen, i, j);

                // Apply rules
                if (bacteria == 0 && neighbours == 3) {
                    newGen[i][j] = 1; // Nacimiento
                } else if (bacteria == 1 && (neighbours == 0 || neighbours == 1)) {
                    newGen[i][j] = 0; // Muerte por soledad
                } else if (bacteria == 1 && neighbours > 3) {
                    newGen[i][j] = 0; // Muerte por asfixia
                } else {
                    newGen[i][j] = bacteria; // Nothing changes, supervivencia
                }
            }
        }

        // In BacteriaLifeLogic
        if (round > MAX_ROUNDS) {
            System.out.println("Can't get a stable gen.");
            return gen;
        }


        return newGen;

    }


    // Count the neighbours
    public static int checkNeighbours(int[][] gen, int row, int col) {
        int neighbours = 0;
        // 8 possible positions of neighbours
        for (int[] d : DIRECTIONS) {
            int r = row + d[0];
            int c = col + d[1];
            // Make sure it's within bounds and if it's not empty increment the value
            if (inBounds(gen, r, c) && gen[r][c] == 1) {
                neighbours++;
            }
        }

        return neighbours;
    }

    // Makes sure coordinates are within bounds
    public static boolean inBounds(int[][] gen, int row, int col) {
        return row >= 0 && row < gen.length &&
                col >= 0 && col < gen[0].length;
    }

    public int getRound() {
        return round;
    }

    // Check if the gen hasn't changed (if it's stable)
    public static boolean checkStableGen(int[][] oldGen, int[][] newGen) {
        return Arrays.deepEquals(oldGen, newGen);
    }
}
