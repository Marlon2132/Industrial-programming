import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String number = scanner.nextLine().trim();
        char[] a = number.toCharArray();
        int sum = 0;

        for (char n : a) {
            sum += n - '0';
        }

        int remainder = sum % 9;

        if (remainder == 0) {
            for (int i = 0; i < a.length; i++) {
                int curr = a[i] - '0';

                if (curr == 0) {
                    char[] v = number.toCharArray();
                    v[i] = '9';
                    show(v);
                }
                else if (curr == 9 && i != 0) {
                    char[] v = number.toCharArray();
                    v[i] = '0';
                    show(v);
                }
            }
        }
        else {
            int lower_minus = remainder;
            int upper_plus = 9 - remainder;

            for (int i = 0; i < a.length; i++) {
                int curr = a[i] - '0';

                if (curr - lower_minus >= 0) {
                    if (!(i == 0 && curr - lower_minus == 0)) {
                        char[] v = number.toCharArray();
                        v[i] = (char)((curr - lower_minus) + '0');
                        show(v);
                    }
                }

                if (curr + upper_plus <= 9) {
                    char[] v = number.toCharArray();
                    v[i] = (char)((curr + upper_plus) + '0');
                    show(v);
                }
            }
        }
    }

    public static void show(char[] v) {
        for (char k : v) {
            System.out.print(k);
        }

        System.out.println();
    }
}