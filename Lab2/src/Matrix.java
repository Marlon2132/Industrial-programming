import java.io.*;
import java.util.*;

public class Matrix {
    private static int ReadPositiveInt(BufferedReader br, String prompt) throws IOException {
        int value;

        while (true) {
            System.out.print(prompt);
            String line = br.readLine();

            try {
                value = Integer.parseInt(line.trim());

                if (value > 0) {
                    return value;
                }
                else {
                    System.out.println("Error: The number must be positive. Please enter again.");
                }

            }
            catch (NumberFormatException e) {
                System.out.println("Error: An invalid number was entered. Please enter again.");
            }
        }
    }

    public static int[][] ReadIntMatrix(BufferedReader br,
                                        String promptRows,
                                        String promptCols,
                                        String promptData) throws IOException {
        int m = ReadPositiveInt(br, promptRows);
        int n = ReadPositiveInt(br, promptCols);
        int[][] matrix = new int[m][n];
        System.out.println(promptData);

        for (int i = 0; i < m; i++) {
            String[] parts;

            while (true) {
                parts = br.readLine().trim().split("\\s+");

                if (parts.length == n) {
                    break;
                }

                System.out.println("Error: expected " + n + " numbers in the line. Repeat the input.");
            }

            for (int j = 0; j < n; j++) {
                matrix[i][j] = Integer.parseInt(parts[j]);
            }
        }

        return matrix;
    }

    public static double[][] ReadDoubleMatrix(BufferedReader br,
                                              String promptSize,
                                              String promptData) throws IOException {
        int n = ReadPositiveInt(br, promptSize);
        double[][] matrix = new double[n][n];
        System.out.println(promptData);

        for (int i = 0; i < n; i++) {
            String[] parts;

            while (true) {
                parts = br.readLine().trim().split("\\s+");

                if (parts.length == n) {
                    break;
                }

                System.out.println("Error: expected " + n + " numbers in the line. Repeat the input.");
            }

            for (int j = 0; j < n; j++) {
                matrix[i][j] = Double.parseDouble(parts[j]);
            }
        }

        return matrix;
    }

    public static void print(int[][] matrix) {
        for (int[] row : matrix) {
            for (int v : row) {
                System.out.print(v + " ");
            }

            System.out.println();
        }
    }

    public static void print(double[][] matrix) {
        for (double[] row : matrix) {
            for (double v : row) {
                System.out.print(v + " ");
            }

            System.out.println();
        }
    }

    public static int[][] ReadIntMatrixFromFile(String filename) throws IOException {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                int[] row = new int[parts.length];

                for (int j = 0; j < parts.length; j++) {
                    row[j] = Integer.parseInt(parts[j]);
                }

                rows.add(row);
            }
        }

        if (rows.isEmpty()) {
            return new int[0][0];
        }

        int m = rows.size();
        int n = rows.get(0).length;
        int[][] matrix = new int[m][n];

        for (int i = 0; i < m; i++) {
            if (rows.get(i).length != n) {
                throw new IOException("Inconsistent row length in file " + filename + " at line " + (i + 1));
            }

            matrix[i] = rows.get(i);
        }

        return matrix;
    }

    public static double[][] ReadDoubleMatrixFromFile(String filename) throws IOException {
        List<double[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                double[] row = new double[parts.length];

                for (int j = 0; j < parts.length; j++) {
                    row[j] = Double.parseDouble(parts[j]);
                }

                rows.add(row);
            }
        }
        int n = rows.size();

        if (n == 0) {
            return new double[0][0];
        }

        double[][] matrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            if (rows.get(i).length != n) {
                throw new IOException(
                        "Matrix in " + filename + " is not square (row " + (i + 1)
                                + " length = " + rows.get(i).length + ", expected " + n + ")."
                );
            }

            matrix[i] = rows.get(i);
        }

        return matrix;
    }
}
