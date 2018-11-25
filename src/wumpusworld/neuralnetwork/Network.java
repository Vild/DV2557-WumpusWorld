package wumpusworld.neuralnetwork;

import wumpusworld.neuralnetwork.layer.OutputLayer;
import wumpusworld.neuralnetwork.layer.InputLayer;
import wumpusworld.neuralnetwork.layer.Layer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import wumpusworld.GUI;
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
    //public Layer hiddenLayer3;

    public OutputLayer output;

    public ScoreHandler scoreHandler;

    private float finalScore;

    static AtomicBoolean updatingUI = new AtomicBoolean();

    public Network(World world) {
        input = new InputLayer(world);
        hiddenLayer1 = new Layer(input, input.getNeuronCount());
        hiddenLayer1.randomize();
        hiddenLayer2 = new Layer(hiddenLayer1, input.getNeuronCount());
        hiddenLayer2.randomize();
        /*hiddenLayer3 = new Layer(hiddenLayer2, input.getNeuronCount());
        hiddenLayer3.randomize();
        output = new OutputLayer(hiddenLayer3);*/
        output = new OutputLayer(hiddenLayer2);
        output.randomize();

        scoreHandler = new ScoreHandler();
    }

    public Network(World world, String filePath) {
        try (DataInputStream is = new DataInputStream(new FileInputStream(filePath))) {
            input = new InputLayer(world, is);
            hiddenLayer1 = new Layer(input, is);
            hiddenLayer2 = new Layer(hiddenLayer1, is);
            //hiddenLayer3 = new Layer(hiddenLayer2, is);
            //output = new OutputLayer(hiddenLayer3, is);
            output = new OutputLayer(hiddenLayer2, is);
            scoreHandler = new ScoreHandler();
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Network(Network other) {
        input = new InputLayer(other.input);
        hiddenLayer1 = new Layer(this.input, other.hiddenLayer1);
        hiddenLayer2 = new Layer(this.hiddenLayer1, other.hiddenLayer2);
        //hiddenLayer3 = new Layer(this.hiddenLayer2, other.hiddenLayer3);
        //output = new OutputLayer(this.hiddenLayer3, other.output);
        output = new OutputLayer(this.hiddenLayer2, other.output);
        scoreHandler = new ScoreHandler();
    }
    
    @Override
    public Network clone() {
        return new Network(this);
    }

    public Network combine(Network other) {
        Network newNetwork = new Network(this);

        newNetwork.hiddenLayer1.combine(other.hiddenLayer1);
        newNetwork.hiddenLayer2.combine(other.hiddenLayer2);
        //newNetwork.hiddenLayer3.combine(other.hiddenLayer3);
        newNetwork.output.combine(other.output);

        return newNetwork;
    }

    public void addMutations() {
        hiddenLayer1.mutate();
        hiddenLayer2.mutate();
        //hiddenLayer3.mutate();
        output.mutate();
    }

    public void setWorld(World world) {
        input.setWorld(world);
        finalScore += scoreHandler.getScore();
        scoreHandler.reset();
    }

    public float getFinalScore() {
        finalScore += scoreHandler.getScore();
        scoreHandler.reset();
        return finalScore;
    }

    public Action update() throws Exception {
        output.update();

        StringBuilder sb = new StringBuilder(1024);
        input.exportState(sb, 0);
        hiddenLayer1.exportState(sb, 1);
        hiddenLayer2.exportState(sb, 2);
        //hiddenLayer3.exportState(sb, 3);
        //output.exportState(sb, 4);
        output.exportState(sb, 3);

        if (GUI.instance != null)
            try (FileWriter fw = new FileWriter("state.dot")) {
                fw.write("digraph State {\n");
                fw.write("graph [fontname=\"Monospace\",fontsize=10];\n");
                fw.write("node [fontname=\"Monospace\",fontsize=10];\n");
                fw.write("edge [fontname=\"Monospace\",fontsize=10];\n");
                fw.write("\tnodesep=1;\n");
                fw.write("\tsplines=line\n");
                fw.write("\trankdir=LR;\n");
                fw.write("\tnode[shape=record,width=0.9,height=0.1];\n");
                fw.write(sb.toString());
                fw.write("}\n");
                fw.close();

                Thread t = new Thread(() -> {
                    if (updatingUI.compareAndExchange(false, true) == false)
                        return;
                    try {
                        Runtime.getRuntime().exec("dot -Tpng state.dot -o state.png").waitFor();
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    SwingUtilities.invokeLater(() -> {
                        GUI.updateNeuralStatePanel();
                        updatingUI.set(false);
                    });
                });
                t.start();

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

    public boolean save(String folderPath, String filePath) {
        try {
            Files.createDirectories(Path.of(folderPath));

            try (DataOutputStream os = new DataOutputStream(new FileOutputStream(Path.of(folderPath, filePath).toString()))) {
                input.save(os);
                hiddenLayer1.save(os);
                hiddenLayer2.save(os);
//                hiddenLayer3.save(os);
                output.save(os);
            }

            return true;

        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
