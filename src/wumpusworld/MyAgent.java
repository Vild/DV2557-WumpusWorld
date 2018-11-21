package wumpusworld;

import java.util.logging.Level;
import java.util.logging.Logger;
import wumpusworld.neuralnetwork.Network;
import wumpusworld.neuralnetwork.Teacher;

/**
 * Contains starting code for creating your own Wumpus World agent. Currently
 * the agent only make a random decision each turn.
 *
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent {

    private World w;
    private Network network;

    /**
     * Creates a new instance of your solver agent.
     *
     * @param world Current world state
     */
    public MyAgent(World world) {
        // TODO: Load the best network here
        this(world, new Network(world, String.format("/tmp/wumpus/networks/%02d/network_00.dat", Teacher.GENERATION_LIMIT - 1)));
    }

    public MyAgent(World world, Network network) {
        w = world;
        this.network = network;
    }

    public void setWorld(World world) {
        w = world;
        network.setWorld(world);
    }

    public Network getNetwork() {
        return network;
    }

    /**
     * Asks your solver agent to execute an action.
     */
    @Override
    public void doAction() {
        network.scoreHandler.newTick();
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();

        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY)) {
            w.doAction(World.A_GRAB);
            network.scoreHandler.finished();
            return;
        }

        //Basic action:
        //We are in a pit. Climb up.
        if (w.isInPit()) {
            w.doAction(World.A_CLIMB);
            network.scoreHandler.inHole();
            return;
        }

        if (w.gameOver()) {
            network.scoreHandler.died();
            return;
        }

        //Test the environment
        /*if (w.hasBreeze(cX, cY))
            System.out.println("I am in a Breeze");
        if (w.hasStench(cX, cY))
            System.out.println("I am in a Stench");
        if (w.hasPit(cX, cY))
            System.out.println("I am in a Pit");
        if (w.getDirection() == World.DIR_RIGHT)
            System.out.println("I am facing Right");
        if (w.getDirection() == World.DIR_LEFT)
            System.out.println("I am facing Left");
        if (w.getDirection() == World.DIR_UP)
            System.out.println("I am facing Up");
        if (w.getDirection() == World.DIR_DOWN)
            System.out.println("I am facing Down");*/
        Network.Action out;
        try {
            out = network.update();
        } catch (Exception ex) {
            Logger.getLogger(MyAgent.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        boolean success;
        boolean isUnknown = false;

        turnTo(out.direction);
        if (out.move) {
            switch (out.direction) {
                case World.DIR_UP:
                    cY++;
                    break;
                case World.DIR_DOWN:
                    cY--;
                    break;
                case World.DIR_LEFT:
                    cX--;
                    break;
                case World.DIR_RIGHT:
                    cX++;
                    break;
                default:
                    break;
            }

            isUnknown = w.isUnknown(cX, cY);
            success = w.doAction(World.A_MOVE);
        } else
            success = w.doAction(World.A_SHOOT);

        if (!success)
            network.scoreHandler.invalidMove();
        else if (out.move)
            if (isUnknown)
                network.scoreHandler.exploredNewTile();
            else
                network.scoreHandler.exploredOldTile();

        //System.out.printf("New score: %f\n", network.scoreHandler.getScore());
    }

    int lastDirection = 0;

    private void turnTo(int i) {
        String direction = i < w.getDirection() ? World.A_TURN_LEFT : World.A_TURN_RIGHT;

        do {
            if (lastDirection != i)
                network.scoreHandler.changedDirection();
            w.doAction(direction);
        } while (i != w.getDirection());
        lastDirection = i;
    }
}
