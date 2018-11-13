package wumpusworld.neuralnetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import wumpusworld.World;

/**
 *
 * @author Dan Printzell
 */
public class Network {

    public enum Action {
        goUp(0, true),
        goRight(1, true),
        goDown(2, true),
        goLeft(3, true),
        doUpShoot(0, false),
        doRightShoot(1, false),
        doDownShoot(2, false),
        doLeftShoot(3, false);

        public int direction;
        public boolean move; // else Shoot

        Action(int direction, boolean move) {
            this.direction = direction;
            this.move = move;
        }
    }

    public InputLayer input;
    public Layer hiddenLayer1;
    public Layer hiddenLayer2;

    public OutputLayer output;

    public ScoreHandler scoreHandler;

    public Network(World world) {
        input = new InputLayer(world);
        hiddenLayer1 = new Layer(input, input.getNeuronCount());
        hiddenLayer1.randomize();
        hiddenLayer2 = new Layer(hiddenLayer1, input.getNeuronCount());
        hiddenLayer2.randomize();
        output = new OutputLayer(hiddenLayer2);
        output.randomize();
        
        scoreHandler = new ScoreHandler();
    }

    public Action update() throws Exception {
        output.update();

        StringBuilder sb = new StringBuilder(1024);
        input.exportState(sb, 0);
        hiddenLayer1.exportState(sb, 1);
        hiddenLayer2.exportState(sb, 2);
        output.exportState(sb, 3);

        try (FileWriter fw = new FileWriter("state.dot")) {
            fw.write("digraph State {\n");
            fw.write("\tnodesep=1;\n");
            fw.write("\tsplines=line\n");
            fw.write("\trankdir=LR;\n");
            fw.write("\tnode[shape=record,width=1,height=0.1];\n");
            fw.write(sb.toString());
            fw.write("}\n");
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (output.goUp)
            return Action.goUp;
        else if (output.goRight)
            return Action.goRight;
        else if (output.goDown)
            return Action.goDown;
        else if (output.goLeft)
            return Action.goLeft;
        else if (output.doUpShoot)
            return Action.doUpShoot;
        else if (output.doRightShoot)
            return Action.doRightShoot;
        else if (output.doDownShoot)
            return Action.doDownShoot;
        else if (output.doLeftShoot)
            return Action.doLeftShoot;

        throw new Exception("Invalid action");
    }

}
