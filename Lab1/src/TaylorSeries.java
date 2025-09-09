public class TaylorSeries {
    // ComputeArcsin
    // Approximately calculates the value of arcsin(x)
    // x - argument, |x| < 1
    // epsilon - accuracy. Calculates the value while |term| > epsilon
    public static double ComputeArcsin(double x, double epsilon) {
        double term = x;
        double sum = term;
        int n = 1;
        System.out.println(term);

        while (Math.abs(term) > epsilon) {
            double factor = ((2.0*n - 1) * (2.0*n - 1)) / (2.0*n * (2.0*n + 1));
            term = term * factor * x * x;
            sum += term;
            System.out.println(term);
            n++;
        }

        return sum;
    }
}
