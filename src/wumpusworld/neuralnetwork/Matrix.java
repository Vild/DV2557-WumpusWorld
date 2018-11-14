package wumpusworld.neuralnetwork;

import jdk.internal.joptsimple.internal.Strings;

/**
 *
 * @author wild
 */
public class Matrix {

    private float[][] data;

    public Matrix(int row, int column) {
        data = new float[row][column];
    }

    public Matrix(float[][] data) {
        this.data = data;
    }

    public Matrix mul(Matrix other) {
        int rows = data.length;
        int columns = data[0].length;
        int otherRows = other.data.length;
        int otherColumns = other.data[0].length;

        assert (columns == otherRows);

        float[][] outputMatrix = new float[rows][otherColumns];

        for (int row = 0; row < rows; row++)
            for (int otherColum = 0; otherColum < otherColumns; otherColum++)
                for (int column = 0; column < columns; column++)
                    outputMatrix[row][otherColum] += data[row][column] * other.data[column][otherColum];

        return new Matrix(outputMatrix);
    }

    public int getRows() {
        return data.length;
    }

    public int getColumns() {
        return data[0].length;
    }

    public float get(int row, int column) {
        return data[row][column];
    }

    public void set(int row, int column, float val) {
        data[row][column] = val;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix[");
        sb.append(getRows());
        sb.append("][");
        sb.append(getColumns());
        sb.append("]\n");
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                sb.append("\t");
                sb.append(get(row, column));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
