import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите имя первого входного файла: ");
            String inputFile1 = scanner.nextLine().trim();
            System.out.print("Введите имя второго входного файла: ");
            String inputFile2 = scanner.nextLine().trim();
            System.out.print("Введите имя выходного файла: ");
            String outputFile = scanner.nextLine().trim();

            List<Student> students1 = readStudents(inputFile1);
            List<Student> students2 = readStudents(inputFile2);
            List<Student> output = new ArrayList<>();

            StudentUtils studUt = new StudentUtils(students1, students2, output);

            System.out.println("\nПервый файл");

            for (Student s : students1) {
                String name = new String(s.getName()).trim();
                System.out.printf(
                        "№%d  %s  Group-%d  Grade-%.2f%n",
                        s.getNumber(), name, s.getGroup(), s.getGrade()
                );
            }

            System.out.println("\nВторой файл");

            for (Student s : students2) {
                String name = new String(s.getName()).trim();
                System.out.printf(
                        "№%d  %s  Group-%d  Grade-%.2f%n",
                        s.getNumber(), name, s.getGroup(), s.getGrade()
                );
            }

            System.out.println("\nВведите операцию:\n1 - объединение\n2 - пересечение\n3 - разность\n");
            int value = scanner.nextInt();
            scanner.nextLine();

            if (value == 1) {
                studUt.union();
                System.out.println("\nВыходной файл (объединение)");
            }
            else if (value == 2) {
                studUt.intersection();
                System.out.println("\nВыходной файл (пересечение)");
            }
            else if (value == 3) {
                studUt.difference();
                System.out.println("\nВыходной файл (разность)");
            }
            else {
                throw new IllegalArgumentException("Номер операции должен быть целым от 1 до 3");
            }

            writeStudents(studUt.getOutp(), outputFile);
        }
        catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return;
        }
    }

    public static List<Student> readStudents(String filePath) throws IOException {
        try (var reader = Files.newBufferedReader(Paths.get(filePath))) {
            List<Student> students = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");

                if (parts.length < 4) {
                    System.err.println("Пропущена строка, неверный формат: " + line);
                    continue;
                }

                long number = Long.parseLong(parts[0]);
                String nameStr = parts[1];
                int group = Integer.parseInt(parts[2]);
                double grade = Double.parseDouble(parts[3]);
                Student s = new Student(number, nameStr.toCharArray(), group, grade);
                students.add(s);
            }

            return students;
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return null;
        }
    }

    public static void writeStudents(List<Student> students, String outputPath) throws IOException {
        try (PrintWriter outputf = new PrintWriter(new FileWriter(outputPath))) {
            if (students.isEmpty()) {
                System.out.println("Пустое множество");
                outputf.println("Пустое множество");
            }

            for (Student s : students) {
                String name = new String(s.getName()).trim();
                System.out.printf(
                        "№%d  %s  Group-%d  Grade-%.2f%n",
                        s.getNumber(), name, s.getGroup(), s.getGrade()
                );
                outputf.printf(
                        "№%d  %s  Group-%d  Grade-%.2f%n",
                        s.getNumber(), name, s.getGroup(), s.getGrade()
                );
            }
        }
        catch (IOException e) {
            System.err.println("Ошибка записи: " + e.getMessage());
            return;
        }
    }
}