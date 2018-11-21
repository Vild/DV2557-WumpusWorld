package wumpusworld.neuralnetwork.layer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import wumpusworld.neuralnetwork.Maths;
import wumpusworld.neuralnetwork.Matrix;

/**
 *
 * @author wild
 */
public class Layer {

    protected enum Activator {
        sigmoid,
        relu
    }
    protected Activator activator = Activator.relu;
    protected Layer before;
    protected Matrix neurons;
    protected Matrix weights;

    public Layer(Layer before, int neuronCount) {
        this.before = before;
        neurons = new Matrix(neuronCount, 1);
        weights = new Matrix(neuronCount, before.neurons.getRows());
    }

    public Layer(Layer before, DataInputStream is) throws IOException {
        this.before = before;
        weights = new Matrix(is);
        neurons = new Matrix(weights.getRows(), 1);
    }

    public Layer(int neuronCount) {
        neurons = new Matrix(neuronCount, 1);
    }

    public Layer(Layer before, Layer other) {
        this.before = before;
        neurons = new Matrix(other.neurons.getRows(), 1);
        weights = other.weights.clone();
    }

    public int getNeuronCount() {
        return neurons.getRows();
    }

    public float get(int id) {
        return neurons.get(id, 0);
    }

    public void update() {
        if (before == null)
            return;
        before.update();
        neurons = weights.mul(before.neurons);

        if (activator == Activator.relu)
            for (int i = 0; i < neurons.getRows(); i++)
                neurons.set(i, 0, Maths.relu(neurons.get(i, 0)));
        else if (activator == Activator.sigmoid)
            for (int i = 0; i < neurons.getRows(); i++)
                neurons.set(i, 0, Maths.sigmoid(neurons.get(i, 0)));
    }

    public void randomize() {
        for (int row = 0; row < weights.getRows(); row++)
            for (int column = 0; column < weights.getColumns(); column++)
                weights.set(row, column, (float) Math.random() * 2 - 1);
    }

    public void exportState(StringBuilder sb, int prefix) {
        sb.append(String.format("\tmatrix%d [label=\"- %s -", prefix, getClass().getSimpleName()));
        for (int row = 0; row < neurons.getRows(); row++) {
            float val = neurons.get(row, 0);
            sb.append(String.format("| <f%d> %f", row, val));
        }
        sb.append("\"];\n");

        if (before != null)
            for (int row = 0; row < weights.getRows(); row++)
                for (int column = 0; column < weights.getColumns(); column++) {
                    final float weight = weights.get(row, column);
                    int red = (int) (weight < 0 ? Math.abs(weight) * 255 : 0);
                    int green = (int) (weight > 0 ? weight * 255 : 0);
                    int blue = 0;
                    sb.append(String.format("\tmatrix%d:f%d -> matrix%d:f%d [color=\"#%02X%02X%02X\"];\n", prefix - 1, column, prefix, row, red, green, blue));
                    //sb.append(String.format("\tmatrix%d:f%d -> matrix%d:f%d [color=\"#%02X%02X%02X\",label=\"%s\"];\n", prefix - 1, column, prefix, row, red, green, blue, weight));
                }
    }

    public void save(DataOutputStream os) throws IOException {
        weights.save(os);
    }

    public void combine(Layer other) {
        Random r = new Random();
        float combineFactor = r.nextFloat();

        int randColumn = r.nextInt(weights.getColumns());
        int randRow = r.nextInt(weights.getRows());

        for (int row = 0; row < weights.getRows(); row++)
            for (int column = 0; column < weights.getColumns(); column++)
                //if (row >= randRow && (row != randRow || column > randColumn))
                if (r.nextFloat() <= combineFactor) 
                    weights.set(row, column, other.weights.get(row, column));
    }

    public void mutate() {
        Random r = new Random();
        float mutateFactor = r.nextFloat();
        for (int row = 0; row < weights.getRows(); row++)
            for (int column = 0; column < weights.getColumns(); column++)
                if (r.nextFloat() <= mutateFactor) {
                    float newVal = weights.get(row, column) + ((float) r.nextGaussian() * 2 - 1);
                    newVal = Math.min(1, Math.max(-1, newVal));
                    weights.set(row, column, newVal);
                }
    }
}
