package wumpusworld.neuralnetwork.layer;

import wumpusworld.neuralnetwork.layer.Layer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import wumpusworld.World;

/**
 *
 * @author Dan Printzell
 */
public class InputLayer extends Layer {

    private World world;

    static final int BREEZE = 0;
    static final int STENCH = 1;
    static final int PIT = 2;
    static final int WUMPUS = 3;
    static final int GLITTER = 4;
    static final int UNKNOWN = 5;
    static final int DISTANCE_TO_WALL = 6;

    static final int PERCEPTS_COUNT = 7;

    public InputLayer(World world) {
        super(PERCEPTS_COUNT /* Percepts */ * 8 /* Directions */ + 1 /* Has arrow */);
        this.world = world;
    }

    public InputLayer(World world, DataInputStream is) throws IOException {
        super(is.readInt());
        this.world = world;
    }

    public InputLayer(InputLayer input) {
        super(input.neurons.getRows());
        world = input.world;
    }

    @Override
    public void update() {
        super.update();

        setPercepts(0, 0, -1); // up: x -y
        setPercepts(1, 1, -1); // up right: +x -y

        setPercepts(2, 1, 0); // right: +x y
        setPercepts(3, 1, 1); // down right: +x +y

        setPercepts(4, 0, 1); // down: x +y
        setPercepts(5, -1, 1); // down left: -x +y

        setPercepts(6, -1, 0); // left: -x y
        setPercepts(7, -1, -1); // left up: -x -y

        neurons.set(8 * PERCEPTS_COUNT, 0, world.hasArrow() ? 1 : 0);
    }

    public void setPercepts(int offset, int xDiff, int yDiff) {
        float[] output = new float[PERCEPTS_COUNT];

        float distance = 0;

        int x = world.getPlayerX() + xDiff;
        int y = world.getPlayerY() + yDiff;

        while (world.isValidPosition(x, y)) {
            distance += 1;

            if (world.hasBreeze(x, y))
                output[BREEZE] += 1 / distance;
            if (world.hasStench(x, y))
                output[STENCH] += 1 / distance;
            if (world.hasPit(x, y))
                output[PIT] += 1 / distance;
            if (world.hasWumpus(x, y))
                output[WUMPUS] += 1 / distance;
            if (world.hasGlitter(x, y))
                output[GLITTER] += 1 / distance;
            if (world.isUnknown(x, y))
                output[UNKNOWN] += 1 / distance;

            x += xDiff;
            y += yDiff;
        }

        output[DISTANCE_TO_WALL] = (float) (Math.sqrt(
                Math.pow(Math.abs(world.getPlayerX() - x), 2))
                + Math.pow(Math.abs(world.getPlayerY() - y), 2));

        for (int i = 0; i < PERCEPTS_COUNT; i++)
            neurons.set(offset * PERCEPTS_COUNT + i, 0, output[i]);
    }

    @Override
    public void exportState(StringBuilder sb, int prefix) {
        super.exportState(sb, prefix);
        exportVariable(sb, prefix, 8 * PERCEPTS_COUNT, "hasArrow", neurons.get(8 * PERCEPTS_COUNT, 0) == 1);
        exportPerspect(sb, prefix);
    }

    private void exportVariable(StringBuilder sb, int prefix, int id, String name, boolean enabled) {
        sb.append(String.format("\tinput_%s [label=\"%s\",color=\"%s\"];\n", name, name, enabled ? "green" : "red"));
        sb.append(String.format("\tinput_%s -> matrix%d:f%d [color=\"%s\"];\n", name, prefix, id, enabled ? "green" : "red"));
    }

    private void exportPerspect(StringBuilder sb, int prefix) {
        sb.append("\tinput_perspect [label=\"{<t1>");
        exportTile(sb, 7); // left up: -x -y
        sb.append("|<t2>");
        exportTile(sb, 6); // left: -x y
        sb.append("|<t3>");
        exportTile(sb, 5); // down left: -x +y

        sb.append("}|{<t4>");

        exportTile(sb, 0); // up: x -y
        sb.append("| Player |<t5>");
        exportTile(sb, 4); // down: x +y

        sb.append("}|{<t6>");

        exportTile(sb, 1); // up right: +x -y
        sb.append("|<t7>");
        exportTile(sb, 2); // right: +x y
        sb.append("|<t8>");
        exportTile(sb, 3); // down right: +x +y

        sb.append("}\"];\n");

        for (int i = 0; i < 8; i++)
            for (int id = 0; id < PERCEPTS_COUNT; id++)
                sb.append(String.format("\tinput_perspect:t%d -> matrix%d:f%d [color=\"%s\"];\n", i + 1, prefix, i * PERCEPTS_COUNT + id, "black"));
    }

    public void exportTile(StringBuilder sb, int offset) {
        sb.append(String.format("Breeze: %f\\n", neurons.get(offset * PERCEPTS_COUNT + BREEZE, 0)));
        sb.append(String.format("Stench: %f\\n", neurons.get(offset * PERCEPTS_COUNT + STENCH, 0)));
        sb.append(String.format("Pit: %f\\n", neurons.get(offset * PERCEPTS_COUNT + PIT, 0)));
        sb.append(String.format("Wumpus: %f\\n", neurons.get(offset * PERCEPTS_COUNT + WUMPUS, 0)));
        sb.append(String.format("Glitter: %f\\n", neurons.get(offset * PERCEPTS_COUNT + GLITTER, 0)));
        sb.append(String.format("Unknown: %f\\n", neurons.get(offset * PERCEPTS_COUNT + UNKNOWN, 0)));
        sb.append(String.format("Wall distance: %f", neurons.get(offset * PERCEPTS_COUNT + DISTANCE_TO_WALL, 0)));
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void save(DataOutputStream os) throws IOException {
        os.writeInt(neurons.getRows());
    }

}
