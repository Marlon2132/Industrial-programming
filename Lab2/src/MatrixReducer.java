public class MatrixReducer {
    // RemoveRowAndColumn
    // Finds the element with max absolute value in a square matrix
    // and removes its row and column.
    public static double[][] RemoveRowAndColumn(double[][] matrix) {
        int n = matrix.length;
        int maxI = 0, maxJ = 0;
        double maxAbs = Math.abs(matrix[0][0]);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double abs = Math.abs(matrix[i][j]);

                if (abs > maxAbs) {
                    maxAbs = abs;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        double[][] result = new double[n - 1][n - 1];
        int ri = 0;

        for (int i = 0; i < n; i++) {
            if (i == maxI) {
                continue;
            }

            int rj = 0;

            for (int j = 0; j < n; j++) {
                if (j == maxJ) {
                    continue;
                }

                result[ri][rj++] = matrix[i][j];
            }

            ri++;
        }

        return result;
    }
}
