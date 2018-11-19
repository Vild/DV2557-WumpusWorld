package wumpusworld.neuralnetwork;

/**
 *
 * @author Dan Printzell
 */
public class Teacher {

    private Generation currentGeneration;
    private final int GENERATION_LIMIT = 10;

    public static void main(String[] args) {
        Teacher t = new Teacher();
        t.run();
    }
    
    public Teacher() {
        currentGeneration = new Generation();
    }

    public void run() {
        for (int generation = 0; generation < GENERATION_LIMIT; generation++) {
            currentGeneration.run();

            final String prefix = String.format("/tmp/wumpus/networks/%02d", generation);
            currentGeneration.save(prefix);

            // Skip the creation of a new generation when the last generation have been tested
            if (generation < GENERATION_LIMIT - 1)
                currentGeneration = currentGeneration.mutateNewGeneration();
        }
    }
}
