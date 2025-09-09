import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.NumberFormat;

public class Main {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        NumberFormat fmt = NumberFormat.getNumberInstance();
        fmt.setMaximumFractionDigits(7);
        int k;
        System.out.print("Enter k (order of accuracy): ");

        try {
            String line = br.readLine();
            k = Integer.parseInt(line);
        }
        catch (NumberFormatException e) {
            System.out.println("Error: entered number is not integer!");
            return;
        }
        catch (IOException e) {
            System.out.println("Keyboard reading error!");
            return;
        }

        if (k <= 0) {
            System.err.println("k should be > 0");
            return;
        }

        double epsilon = Math.pow(10, -k);
        double x;
        System.out.print("Enter x (|x| < 1): ");

        try {
            String line = br.readLine();
            line = line.replace(',', '.');
            x = Double.parseDouble(line);
        }
        catch (NumberFormatException e) {
            System.out.println("Error: a non-numeric value has been entered!");
            return;
        }
        catch (IOException e) {
            System.out.println("Keyboard reading error!");
            return;
        }

        if (Math.abs(x) >= 1.0) {
            System.err.println("x must be in the range of (-1, +1).");
            return;
        }

        System.out.println("\nThe terms of the Taylor series for arcsin(" + x + "):");
        double approx = TaylorSeries.ComputeArcsin(x, epsilon);
        System.out.println("\nApproximate value of arcsin(" + x + ") = " + fmt.format(approx));
        System.out.println("Math.asin(" + x + ") = " + fmt.format(Math.asin(x)));
    }
}
