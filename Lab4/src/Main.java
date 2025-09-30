import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<GradeBook> grade_books = new ArrayList<>();
        Map<String, GradeBook> grade_book_map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            GradeBook current_book = null;
            GradeBook.Session current_session = null;

            outer: while((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] arguments = line.split("\\s+");

                if (arguments.length == 5 &&
                        isString(arguments[0]) &&
                        isByte(arguments[3]) &&
                        isByte(arguments[4])) {
                    String surname = arguments[0];
                    String name = arguments[1];
                    String middle_name = arguments[2];
                    byte course = Byte.parseByte(arguments[3]);
                    byte group = Byte.parseByte(arguments[4]);

                    current_book = new GradeBook(surname,
                            name,
                            middle_name,
                            course,
                            group);
                    grade_books.add(current_book);
                    String key = surname + " " + name + " " + middle_name;
                    grade_book_map.put(key, current_book);
                    current_session = null;
                }
                else if (arguments.length >= 4 &&
                        isByte(arguments[0]) &&
                        isByte(arguments[arguments.length - 1]) &&
                        isBoolean(arguments[arguments.length - 2])) {
                    for (int i = 1; i < arguments.length - 2; i++) {
                        if (!isString(arguments[i])){
                            System.out.println("The string could not be recognized: " + line);
                            continue outer;
                        }
                    }

                    byte session_number = Byte.parseByte(arguments[0]);
                    current_session = current_book.getSession(session_number);
                    byte mark = Byte.parseByte(arguments[arguments.length - 1]);
                    boolean is_exam = Boolean.parseBoolean(arguments[arguments.length - 2]);
                    int index = line.lastIndexOf(arguments[arguments.length - 2]);
                    String discipline_name = String.join(" ", Arrays.copyOfRange(arguments, 1 , arguments.length - 2));
                    current_session.addDiscipline(discipline_name, is_exam, mark);
                }
                else {
                    System.out.println("The string could not be recognized: " + line);
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error of reading input.txt: " + e.getMessage());
            return;
        }

        System.out.println("Count of students: " + grade_books.size());

        for (GradeBook gb : grade_books) {
            System.out.println(gb.getFullName());
        }

        for (GradeBook gb : grade_books) {
            gb.calculateAllAverages();
        }

        BufferedReader br_inp = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter student's name to start searching: ");

        try {
            String lookupKey = br_inp.readLine();
            GradeBook book = grade_book_map.get(lookupKey);

            if (book != null){
                System.out.println("Student found: " + lookupKey);
            }
            else {
                System.out.println("Student " + lookupKey + " is not found");
            }
        }
        catch (IOException e) {
            System.err.println("Input error!");
        }

        try (PrintWriter output = new PrintWriter(new FileWriter("output.txt"))) {
            boolean excellent_exist = false;
            System.out.println("\nСписок отличников:\n");
            for (GradeBook gb : grade_books) {
                if (gb.isExcellent()) {
                    excellent_exist = true;
                    System.out.printf("%s %d курс %d группа%n", gb.getFullName(), gb.getCourse(), gb.getGroup());
                    output.printf("%s %d курс %d группа%n", gb.getFullName(), gb.getCourse(), gb.getGroup());
                    for (byte i = 1; i <= 9; i++) {
                        if (!gb.getSession(i).isDisciplinesEmpty()) {
                            System.out.printf("Номер сессии: %d%nСредний балл за эту сессию: %f%n", i, gb.getSession(i).getAverage());
                            output.printf("Номер сессии: %d%nСредний балл за эту сессию: %f%n", i, gb.getSession(i).getAverage());
                            for (GradeBook.Session.Discipline disc : gb.getSession(i).getDisciplines()) {
                                System.out.printf("%s по дисциплине \"%s\". Результат: %s%n",
                                        (disc.isExam() ? "Экзамен" : "Зачёт"),
                                        disc.getNameOfDiscipline(),
                                        (disc.isExam() ? disc.getMark() : ((disc.getMark() == 1) ? "Зачтено" : "Незачтено")));
                                output.printf("%s по дисциплине \"%s\". Результат: %s%n",
                                        (disc.isExam() ? "Экзамен" : "Зачёт"),
                                        disc.getNameOfDiscipline(),
                                        (disc.isExam() ? disc.getMark() : ((disc.getMark() == 1) ? "Зачтено" : "Незачтено")));
                            }
                        }
                    }
                    System.out.printf("Средний балл за все сессии: %f", gb.getAverageAll());
                    output.printf("Средний балл за все сессии: %f", gb.getAverageAll());
                    System.out.println("\n");
                    output.printf("%n");
                }
            }
            if (!excellent_exist) {
                System.out.println("Отличников нет :(");
            }
        }
        catch (IOException e) {
            System.err.println("Error writing to file output.txt: " + e.getMessage());
            return;
        }
    }

    private static boolean isByte(String s) {
        if (s == null) {
            return false;
        }

        String input = s.trim();
        final Pattern BYTE_PATTERN = Pattern.compile("-?\\d{1,3}");

        if (!BYTE_PATTERN.matcher(input).matches()) {
            return false;
        }

        try {
            Byte.parseByte(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isBoolean(String s) {
        if (s == null) {
            return false;
        }

        String input = s.trim();

        if (containsDigit(input)) {
            return false;
        }

        return input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false");
    }

    public static boolean isString(String s) {
        return !(isByte(s) || isBoolean(s));
    }

    private static boolean containsDigit(String s) {
        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) {
                return true;
            }
        }

        return false;
    }
}