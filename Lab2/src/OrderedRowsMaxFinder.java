import java.util.*;

public class OrderedRowsMaxFinder {

//    FindMaxInOrderedRows
//    Collects the maximum of each row,
//    which either strictly increases or strictly decreases.
//    Returns a list of found values.
    public static List<Integer> FindMaxInOrderedRows(int[][] matrix) {
        List<Integer> maxima = new ArrayList<>();

        for (int[] row : matrix) {
            if (IsOrdered(row)) {
                int max = row[0];

                for (int v : row) {
                    if (v > max) {
                        max = v;
                    }
                }

                maxima.add(max);
            }
        }

        return maxima;
    }

    private static boolean IsOrdered(int[] row) {
        boolean asc = true, desc = true;

        for (int i = 1; i < row.length; i++) {
            if (row[i] < row[i - 1]) {
                asc = false;
            }

            if (row[i] > row[i - 1]) {
                desc = false;
            }
        }

        return asc || desc;
    }
}
