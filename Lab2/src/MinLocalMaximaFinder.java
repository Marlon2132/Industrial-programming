public class MinLocalMaximaFinder {
    // FindMinLocalMax
    // Searches for the minimum among all local maxima in an integer matrix.
    // A local maximum is an element strictly greater than each neighbor.
    // Returns the found minimum or Integer.MAX_VALUE if none.
    public static int FindMinLocalMax(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int minVal = Integer.MAX_VALUE;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (IsLocalMax(matrix, i, j) && matrix[i][j] < minVal) {
                    minVal = matrix[i][j];
                }
            }
        }

        return minVal;
    }

    private static boolean IsLocalMax(int[][] mat, int i, int j) {
        int current = mat[i][j];
        int m = mat.length;
        int n = mat[0].length;

        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) {
                    continue;
                }

                int ni = i + di;
                int nj = j + dj;

                if (ni >= 0 && ni < m && nj >= 0 && nj < n) {
                    if (!(current > mat[ni][nj])) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
