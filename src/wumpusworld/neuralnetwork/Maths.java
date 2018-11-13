package wumpusworld.neuralnetwork;

/**
 *
 * @author wild
 */
public class Maths {
    static float sigmoid(float x) {
        if (x < -10)
            return 0;
        else if (x > 10)
            return 1;
        else
            return (float) (1 / (1 + Math.exp(-x)));
    }
}
