package wumpusworld.neuralnetwork;

import java.io.OutputStream;

/**
 *
 * @author wild
 */
public class Layer {

    protected Layer before;
    //private float[/* Output Neuron ID*/][/* Input Weight */] inputWeights;
    protected Matrix weights;
    protected Matrix neurons;

    public Layer(Layer before, int neuronCount) {
        this.before = before;
        weights = new Matrix(neuronCount, before.neurons.getRows());
        neurons = new Matrix(neuronCount, 1);
    }

    public Layer(int neuronCount) {
        neurons = new Matrix(neuronCount, 1);
    }

    int getNeuronCount() {
        return neurons.getRows();
    }

    float get(int id) {
        return neurons.get(id, 0);
    }

    void update() {
        if (before == null)
            return;
        before.update();
        neurons = weights.mul(before.neurons);

        for (int i = 0; i < neurons.getRows(); i++)
            neurons.set(i, 0, Maths.sigmoid(neurons.get(i, 0)));
    }

    void randomize() {
        for (int row = 0; row < weights.getRows(); row++)
            for (int column = 0; column < weights.getColumns(); column++)
                weights.set(row, column, (float) Math.random() * 2 - 1);
    }

    void exportState(StringBuilder sb, int prefix) {
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
}
