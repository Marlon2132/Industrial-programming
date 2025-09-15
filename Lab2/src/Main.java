import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.print("Enter filename for the integer matrix: ");
        String intFile = br.readLine();
        File fInt = new File(intFile);

        if (!fInt.exists() || !fInt.isFile()) {
            System.err.println("Error: file not found — " + intFile);
            return;
        }

        int[][] intMatrix = Matrix.ReadIntMatrixFromFile(intFile);
        System.out.println("The original integer matrix:");
        Matrix.print(intMatrix);
        System.out.println("\nTask 24: minimum among the local maxima of an integer matrix.");
        int minLocalMax = MinLocalMaximaFinder.FindMinLocalMax(intMatrix);
        System.out.println(
                minLocalMax == Integer.MAX_VALUE
                        ? "Local maxima wasn't found."
                        : "A minimum among local maxima: " + minLocalMax
        );

        System.out.println("\nTask 7: maximum in ordered rows.");
        List<Integer> maxList = OrderedRowsMaxFinder.FindMaxInOrderedRows(intMatrix);

        if (maxList.isEmpty()) {
            System.out.println("No ordered rows found.");
        }
        else {
            System.out.print("Maximum elements of ordered rows: ");

            for (int v : maxList) {
                System.out.print(v + " ");
            }

            System.out.println();
        }

        System.out.print("\nEnter filename for the square double matrix: ");
        String dblFile = br.readLine();
        File fDbl = new File(dblFile);

        if (!fDbl.exists() || !fDbl.isFile()) {
            System.err.println("Error: file not found — " + dblFile);
            return;
        }

        double[][] doubleMatrix = Matrix.ReadDoubleMatrixFromFile(dblFile);
        System.out.println("The original double matrix:");
        Matrix.print(doubleMatrix);
        System.out.println("\nTask 38: delete the row and column with the max element by abs.");
        double[][] reduced = MatrixReducer.RemoveRowAndColumn(doubleMatrix);
        System.out.println("Matrix after deletion (order " + reduced.length + "):");
        Matrix.print(reduced);

        br.close();
    }
}
