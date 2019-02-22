import java.io.*;
import java.util.*;

public class MatrixGenerator{
    private boolean debug;
    private int max;
    private int min;

    public MatrixGenerator(int min, int max) {
        this(false, min, max);
    }

    public MatrixGenerator(boolean debug, int min, int max) {
        this.debug = debug;
        this.min = min;
        this.max = max;
    }

    public Vector<Vector<Integer>> generate(int max_x, int max_y) {
        Vector<Vector<Integer>> matrix = new Vector<>();
        for (int i = 0; i < max_x; i++) {
            Vector<Integer> row = new Vector<>();
            for (int j = 0; j < max_y; j++) {
                row.add(
                        ((Double)(this.min + (Math.random() * ((this.max - this.min) + 1))))
                                .intValue());
                if (this.debug)
                    System.out.print(row.get(j) + "\t");
            }
            matrix.add(row);
            if (this.debug)
                System.out.print("\n");
        }
        return matrix;
    }

    public Vector<Vector<Integer>> diagonalize(Vector<Vector<Integer>> matrix) {
        Vector<Vector<Integer>> diagonize = new Vector<>();
        for (int i = 0; i < matrix.size(); i++) {
            Vector<Integer> row = new Vector<>();
            for (int j = 0; j < matrix.get(0).size(); j++) {
                if (j <= i) {
                    row.add(matrix.get(i).get(j));
                    if (this.debug)
                        System.out.print(matrix.get(i).get(j) + "\t");
                }
            }
            diagonize.add(row);
            if (this.debug)
                System.out.println("");
        }
        return diagonize;
    }

    public void store(Vector<Vector<Integer>> matrix, Integer startCity, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            // write matrix size
            bw.write(matrix.size() + "");
            bw.newLine();
            // write start city
            bw.write(startCity + "");
            bw.newLine();
            // write city distance matrix
            for (int i = 0; i < matrix.size(); i++) {
                for (int j = 0; j < matrix.get(i).size(); j++) {
                    if(j == matrix.get(i).size()-1) {
                        bw.write(matrix.get(i).get(j) + "");
                    } else {
                        bw.write(matrix.get(i).get(j) + "\t");
                    }
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (debug)
                System.out.println("Operation finished.");
        }
    }

    public static void main(String[] args) {
        int max_x = 100;
        int max_y = max_x;
        String filename = "format.txt";
        Integer startCity = ((Double)(Math.random() * max_x)).intValue();

        assert (max_x == max_y);
        assert (!filename.isEmpty());

        MatrixGenerator matrixGenerator = new MatrixGenerator(1, 255);
        matrixGenerator.store(
                matrixGenerator.diagonalize(
                        matrixGenerator.generate(max_x, max_y))
                , startCity, filename);
    }
}