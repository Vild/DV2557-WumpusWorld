package wumpusworld.neuralnetwork;

/**
 *
 * @author Dan Printzell
 */
public class ScoreHandler {

    private float score = 0;
    private boolean done = false;

    private int tickCounter = 1;
    
    public boolean isDone() { return done;}

    public void reset() {
        score = 0;
        done = false;
        tickCounter = 1;
    }

    public void newTick() {
        if (done)
            return;

        tickCounter++;
        score += 1f;
    }

    public void inHole() {
        if (done)
            return;

        score -= 5f;
    }

    public void finished() {
        if (done)
            return;

        done = true;
        score += 5f;
    }

    public void died() {
        if (done)
            return;

        done = true;
        score -= 100f / tickCounter;
    }

    public void invalidMove() {
        if (done)
            return;

        score -= 40f / tickCounter;
        done = true;
    }

    public void exploredNewTile() {
        if (done)
            return;

        score += 4f;
    }

    public void exploredOldTile() {
        if (done)
            return;

        score += 2f;
    }

    public float getScore() {
        return score;
    }

    public void noMoreMoves() {
        score -= 5f;
    }
    
    public void changedDirection() {
        score += 10f;
    }

    public void submitWorldScore(int score) {
        this.score += score;
    }

}
