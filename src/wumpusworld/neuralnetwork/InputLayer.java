package wumpusworld.neuralnetwork;

import wumpusworld.World;

/**
 *
 * @author Dan Printzell
 */
public class InputLayer extends Layer {

    private final World world;

    /*
    public enum Percepts {
        BREEZE,
        STENCH,
        PIT,
        WUMPUS,
        GLITTER,
        UNKNOWN,
    }*/
    public InputLayer(World world) {
        super(6 /* Percepts */ * 8 /* Directions */ + 1);
        this.world = world;
    }

    @Override
    void update() {
        super.update();

        setPercepts(0, 0, -1); // up: x -y
        setPercepts(1, 1, -1); // up right: +x -y

        setPercepts(2, 1, 0); // right: +x y
        setPercepts(3, 1, 1); // down right: +x +y

        setPercepts(4, 0, 1); // down: x +y
        setPercepts(5, -1, 1); // down left: -x +y

        setPercepts(6, -1, 0); // left: -x y
        setPercepts(7, -1, -1); // left up: -x -y

        neurons.set(8 * 6, 0, world.hasArrow() ? 1 : 0);
    }

    void setPercepts(int offset, int xDiff, int yDiff) {
        float[] output = new float[7];

        float distance = 0;

        int x = world.getPlayerX() + xDiff;
        int y = world.getPlayerY() + yDiff;

        while (world.isValidPosition(x, y)) {
            distance += 1;

            if (world.hasBreeze(x, y))
                output[0] += 1 / distance;
            if (world.hasStench(x, y))
                output[1] += 1 / distance;
            if (world.hasPit(x, y))
                output[2] += 1 / distance;
            if (world.hasWumpus(x, y))
                output[3] += 1 / distance;
            if (world.hasGlitter(x, y))
                output[4] += 1 / distance;
            if (world.isUnknown(x, y))
                output[5] += 1 / distance;

            x += xDiff;
            y += yDiff;
        }

        for (int i = 0; i < 6; i++)
            neurons.set(offset * 6 + i, 0, output[i]);
    }

    @Override
    void exportState(StringBuilder sb, int prefix) {
        super.exportState(sb, prefix);
        exportVariable(sb, prefix, 8 * 6, "hasArrow", neurons.get(8 * 6, 0) == 1);
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
            for (int id = 0; id < 6; id++)
                sb.append(String.format("\tinput_perspect:t%d -> matrix%d:f%d [color=\"%s\"];\n", i + 1, prefix, i * 6 + id, "black"));
    }

    void exportTile(StringBuilder sb, int offset) {
        sb.append(String.format("Breeze: %f\\n", neurons.get(offset * 6 + 0, 0)));
        sb.append(String.format("Stench: %f\\n", neurons.get(offset * 6 + 1, 0)));
        sb.append(String.format("Pit: %f\\n", neurons.get(offset * 6 + 2, 0)));
        sb.append(String.format("Wumpus: %f\\n", neurons.get(offset * 6 + 3, 0)));
        sb.append(String.format("Glitter: %f\\n", neurons.get(offset * 6 + 4, 0)));
        sb.append(String.format("Unknown: %f", neurons.get(offset * 6 + 5, 0)));
    }

}
