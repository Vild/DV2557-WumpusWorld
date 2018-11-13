package wumpusworld.neuralnetwork;

/**
 *
 * @author Dan Printzell
 */
public class ScoreHandler {

    private float score;
    private boolean done;

    private int tickCounter;

    public void newTick() {
        if (done)
            return;

        tickCounter++;
    }

    public void inHole() {
        if (done)
            return;

        score -= 2 * tickCounter;
    }

    public void finished() {
        if (done)
            return;

        done = true;
        score += 1000 * tickCounter;
    }

    public void died() {
        if (done)
            return;

        done = true;
        score -= 1 * tickCounter * tickCounter;
    }

    public void invalidMove() {
        if (done)
            return;

        score -= 1 / tickCounter;
    }

    public void exploredNewTile() {
        if (done)
            return;

        score += 10 / tickCounter;
    }

    public void exploredOldTile() {
        if (done)
            return;

        score += 1 / tickCounter;
    }

    public float getScore() {
        return score;
    }

}
