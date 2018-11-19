package wumpusworld.neuralnetwork.layer;

import wumpusworld.neuralnetwork.layer.Layer;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Dan Printzell
 */
public class OutputLayer extends Layer {

    public boolean goUp;
    public boolean goRight;
    public boolean goDown;
    public boolean goLeft;

    public boolean doUpShoot;
    public boolean doRightShoot;
    public boolean doDownShoot;
    public boolean doLeftShoot;

    public OutputLayer(Layer before) {
        super(before, 8);
    }

    public OutputLayer(Layer before, DataInputStream is) throws IOException {
        super(before, is);
    }

    public OutputLayer(Layer before, OutputLayer outputLayer) {
        super(before, outputLayer);
    }

    @Override
    public void update() {
        super.update();

        goUp = goRight = goDown = goLeft = false;
        doUpShoot = doRightShoot = doDownShoot = doLeftShoot = false;

        float max = get(0);
        int id = 0;

        for (int i = 1; i < 8; i++)
            if (max < get(i))
                id = i;

        switch (id) {
            case 0:
                goUp = true;
                break;
            case 1:
                goRight = true;
                break;
            case 2:
                goDown = true;
                break;
            case 3:
                goLeft = true;
                break;
            case 4:
                doUpShoot = true;
                break;
            case 5:
                doRightShoot = true;
                break;
            case 6:
                doDownShoot = true;
                break;
            case 7:
                doLeftShoot = true;
                break;
        }
    }

    @Override
    public void exportState(StringBuilder sb, int prefix) {
        super.exportState(sb, prefix);
        exportVariable(sb, prefix, 0, "goUp", goUp);
        exportVariable(sb, prefix, 1, "goRight", goRight);
        exportVariable(sb, prefix, 2, "goDown", goDown);
        exportVariable(sb, prefix, 3, "goLeft", goLeft);

        exportVariable(sb, prefix, 4, "doUpShoot", doUpShoot);
        exportVariable(sb, prefix, 5, "doRightShoot", doRightShoot);
        exportVariable(sb, prefix, 6, "doDownShoot", doDownShoot);
        exportVariable(sb, prefix, 7, "doLeftShoot", doLeftShoot);
    }

    private void exportVariable(StringBuilder sb, int prefix, int id, String name, boolean enabled) {
        sb.append(String.format("\toutput_%s [label=\"%s\",color=\"%s\"];\n", name, name, enabled ? "green" : "red"));
        sb.append(String.format("\tmatrix%d:f%d -> output_%s [color=\"%s\"];\n", prefix, id, name, enabled ? "green" : "red"));
    }

}
