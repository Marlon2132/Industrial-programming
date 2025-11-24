import java.io.*;
import java.util.*;
import java.util.regex.*;
import com.google.gson.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<GradeBook> grade_books = new ArrayList<>();
        Map<String, GradeBook> grade_book_map = new HashMap<>();

        try {
            List<GradeBook> loaded = loadFromJson("input.json", grade_book_map);
            grade_books.addAll(loaded);
        } catch (IOException e) {
            System.err.println("Error of reading input.json: " + e.getMessage());

            return;
        }


//        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
//            String line;
//            GradeBook current_book = null;
//            GradeBook.Session current_session = null;
//
//            outer: while((line = br.readLine()) != null) {
//                line = line.trim();
//
//                if (line.isEmpty()) {
//                    continue;
//                }
//
//                String[] arguments = line.split("\\s+");
//
//                if (arguments.length == 5 &&
//                        isString(arguments[0]) &&
//                        isByte(arguments[3]) &&
//                        isByte(arguments[4])) {
//                    String surname = arguments[0];
//                    String name = arguments[1];
//                    String middle_name = arguments[2];
//                    byte course = Byte.parseByte(arguments[3]);
//                    byte group = Byte.parseByte(arguments[4]);
//
//                    current_book = new GradeBook(surname,
//                            name,
//                            middle_name,
//                            course,
//                            group);
//                    grade_books.add(current_book);
//                    String key = surname + " " + name + " " + middle_name;
//                    grade_book_map.put(key, current_book);
//                    current_session = null;
//                }
//                else if (arguments.length >= 4 &&
//                        isByte(arguments[0]) &&
//                        isByte(arguments[arguments.length - 1]) &&
//                        isBoolean(arguments[arguments.length - 2])) {
//                    for (int i = 1; i < arguments.length - 2; i++) {
//                        if (!isString(arguments[i])){
//                            System.out.println("The string could not be recognized: " + line);
//                            continue outer;
//                        }
//                    }
//
//                    byte session_number = Byte.parseByte(arguments[0]);
//                    current_session = current_book.getSession(session_number);
//                    byte mark = Byte.parseByte(arguments[arguments.length - 1]);
//                    boolean is_exam = Boolean.parseBoolean(arguments[arguments.length - 2]);
//                    int index = line.lastIndexOf(arguments[arguments.length - 2]);
//                    String discipline_name = String.join(" ", Arrays.copyOfRange(arguments, 1 , arguments.length - 2));
//                    current_session.addDiscipline(discipline_name, is_exam, mark);
//                }
//                else {
//                    System.out.println("The string could not be recognized: " + line);
//                }
//            }
//        }
//        catch (IOException e) {
//            System.err.println("Error of reading input.txt: " + e.getMessage());
//            return;
//        }

        System.out.println("Count of students: " + grade_books.size());

        for (GradeBook gb : grade_books) {
            System.out.printf("%s %d курс %d группа%n", gb.getFullName(), gb.getCourse(), gb.getGroup());
//                    output.printf("%s %d курс %d группа%n", gb.getFullName(), gb.getCourse(), gb.getGroup());
            for (byte i = 1; i <= 9; i++) {
                if (!gb.getSession(i).isDisciplinesEmpty()) {
                    System.out.printf("Номер сессии: %d%nСредний балл за эту сессию: %f%n", i, gb.getSession(i).getAverage());
//                            output.printf("Номер сессии: %d%nСредний балл за эту сессию: %f%n", i, gb.getSession(i).getAverage());
                    for (GradeBook.Session.Discipline disc : gb.getSession(i).getDisciplines()) {
                        System.out.printf("%s по дисциплине \"%s\". Результат: %s%n",
                                (disc.isExam() ? "Экзамен" : "Зачёт"),
                                disc.getNameOfDiscipline(),
                                (disc.isExam() ? disc.getMark() : ((disc.getMark() == 1) ? "Зачтено" : "Незачтено")));
//                                output.printf("%s по дисциплине \"%s\". Результат: %s%n",
//                                        (disc.isExam() ? "Экзамен" : "Зачёт"),
//                                        disc.getNameOfDiscipline(),
//                                        (disc.isExam() ? disc.getMark() : ((disc.getMark() == 1) ? "Зачтено" : "Незачтено")));
                    }
                }
            }
            System.out.printf("Средний балл за все сессии: %f", gb.getAverageAll());
//                    output.printf("Средний балл за все сессии: %f", gb.getAverageAll());
            System.out.println("\n");
//                    output.printf("%n");
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

        try (Writer output = new OutputStreamWriter(new FileOutputStream("output.json"))) {
            List<GradeBook> toWrite = new ArrayList<GradeBook>();

            boolean excellent_exist = false;
            System.out.println("\nСписок отличников:\n");

            for (GradeBook gb : grade_books) {
                if (gb.isExcellent()) {
                    excellent_exist = true;

                    toWrite.add(gb);

                    System.out.printf("%s %d курс %d группа%n", gb.getFullName(), gb.getCourse(), gb.getGroup());
//                    output.printf("%s %d курс %d группа%n", gb.getFullName(), gb.getCourse(), gb.getGroup());
                    for (byte i = 1; i <= 9; i++) {
                        if (!gb.getSession(i).isDisciplinesEmpty()) {
                            System.out.printf("Номер сессии: %d%nСредний балл за эту сессию: %f%n", i, gb.getSession(i).getAverage());
//                            output.printf("Номер сессии: %d%nСредний балл за эту сессию: %f%n", i, gb.getSession(i).getAverage());
                            for (GradeBook.Session.Discipline disc : gb.getSession(i).getDisciplines()) {
                                System.out.printf("%s по дисциплине \"%s\". Результат: %s%n",
                                        (disc.isExam() ? "Экзамен" : "Зачёт"),
                                        disc.getNameOfDiscipline(),
                                        (disc.isExam() ? disc.getMark() : ((disc.getMark() == 1) ? "Зачтено" : "Незачтено")));
//                                output.printf("%s по дисциплине \"%s\". Результат: %s%n",
//                                        (disc.isExam() ? "Экзамен" : "Зачёт"),
//                                        disc.getNameOfDiscipline(),
//                                        (disc.isExam() ? disc.getMark() : ((disc.getMark() == 1) ? "Зачтено" : "Незачтено")));
                            }
                        }
                    }
                    System.out.printf("Средний балл за все сессии: %f", gb.getAverageAll());
//                    output.printf("Средний балл за все сессии: %f", gb.getAverageAll());
                    System.out.println("\n");
//                    output.printf("%n");
                }
            }
            if (toWrite.isEmpty()) {
                System.out.println("Отличников нет :(");
            }

            output.write("{\n  \"students\": [\n");
            boolean firstStudent = true;
            for (GradeBook gb : toWrite) {
                if (!firstStudent) output.write(",\n");
                firstStudent = false;
                writeStudentJson(output, gb);
            }
            output.write("\n  ]\n}\n");
        }
        catch (IOException e) {
            System.err.println("Error writing to file output.json: " + e.getMessage());
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

    private static void writeStudentJson(Writer out, GradeBook gb) throws IOException {
        String indent = "    ";
        out.write(indent + "{\n");
        out.write(indent + "  \"fullName\": " + jsonEscape(gb.getFullName()) + ",\n");
        out.write(indent + "  \"course\": " + gb.getCourse() + ",\n");
        out.write(indent + "  \"group\": " + gb.getGroup() + ",\n");
        out.write(indent + "  \"averageAll\": " + gb.getAverageAll() + ",\n");
        out.write(indent + "  \"sessions\": [\n");

        boolean firstSession = true;
        for (byte i = 1; i <= 9; i++) {
            GradeBook.Session sess = gb.getSession(i);
            if (sess.isDisciplinesEmpty()) continue;
            if (!firstSession) out.write(",\n");
            firstSession = false;

            out.write(indent + "    {\n");
            out.write(indent + "      \"sessionNumber\": " + i + ",\n");
            out.write(indent + "      \"average\": " + sess.getAverage() + ",\n");
            out.write(indent + "      \"disciplines\": [\n");

            boolean firstDisc = true;
            for (GradeBook.Session.Discipline disc : sess.getDisciplines()) {
                if (!firstDisc) out.write(",\n");
                firstDisc = false;

                out.write(indent + "        {\n");
                out.write(indent + "          \"name\": " + jsonEscape(disc.getNameOfDiscipline()) + ",\n");
                out.write(indent + "          \"type\": " + (disc.isExam() ? "\"Exam\"" : "\"Credit\"") + ",\n");
                out.write(indent + "          \"result\": ");
                if (disc.isExam()) {
                    out.write(String.valueOf(disc.getMark()) + "\n");
                } else {
                    String res = (disc.getMark() == 1) ? "Зачтено" : "Незачтено";
                    out.write(jsonEscape(res) + "\n");
                }
                out.write(indent + "        }");
            }

            out.write("\n" + indent + "      ]\n");
            out.write(indent + "    }");
        }

        out.write("\n" + indent + "  ]\n");
        out.write(indent + "}");
    }

    private static String jsonEscape(String s) {
        if (s == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('"');

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch >= 0x20 && ch != '\u2028' && ch != '\u2029') {
                        sb.append(ch);
                    }
                    else {
                        sb.append(String.format("\\u%04x", (int) ch));
                    }
            }
        }

        sb.append('"');

        return sb.toString();
    }

    // Вставь эти классы в Main.java (как static nested classes) или в отдельный файл.
    static class JsonDiscipline {
        String name;
        String type; // "Exam" или "Credit"
        // result может быть число или строка -> используем String и потом парсим
        // но Gson может читать into JsonElement; для простоты используем String
        // мы поддержим оба варианта ниже
        com.google.gson.JsonElement result;
    }

    static class JsonSession {
        int sessionNumber;
        double average;
        List<JsonDiscipline> disciplines;
    }

    static class JsonStudent {
        String fullName;
        int course;
        int group;
        double averageAll;
        List<JsonSession> sessions;
    }

    static class JsonRoot {
        List<JsonStudent> students;
    }

    private static List<GradeBook> readStudentsFromJson(String filename, Map<String, GradeBook> gradeBookMap) throws IOException {
        // gradeBookMap — можно передать пустую map, или существующую, если хочешь сопоставлять по fullName
        List<GradeBook> result = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(filename), java.nio.charset.StandardCharsets.UTF_8)) {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            JsonRoot root = gson.fromJson(reader, JsonRoot.class);
            if (root == null || root.students == null) return result;

            for (JsonStudent js : root.students) {
                // fullName: "Surname Name Middle"
                String[] parts = js.fullName.split("\\s+", 3);
                String surname = parts.length > 0 ? parts[0] : "";
                String name = parts.length > 1 ? parts[1] : "";
                String middle = parts.length > 2 ? parts[2] : "";

                byte course = (byte) js.course;
                byte group = (byte) js.group;

                GradeBook gb = new GradeBook(surname, name, middle, course, group);

                // Пробежим по сессиям и дисциплинам
                if (js.sessions != null) {
                    for (JsonSession jsess : js.sessions) {
                        int sessNum = jsess.sessionNumber;
                        if (sessNum < 1 || sessNum > 9) continue;
                        GradeBook.Session session = gb.getSession((byte) sessNum);
                        // установим среднее, если задано
                        try {
                            session.setAverage(jsess.average);
                        } catch (IllegalArgumentException ignored) {}

                        if (jsess.disciplines != null) {
                            for (JsonDiscipline jdisc : jsess.disciplines) {
                                boolean isExam = "Exam".equalsIgnoreCase(jdisc.type);
                                byte mark = 0;
                                // result может быть числом (JsonPrimitive) или строкой
                                try {
                                    if (jdisc.result != null && jdisc.result.isJsonPrimitive()) {
                                        com.google.gson.JsonPrimitive prim = jdisc.result.getAsJsonPrimitive();
                                        if (prim.isNumber()) {
                                            mark = prim.getAsNumber().byteValue();
                                        } else if (prim.isString()) {
                                            String s = prim.getAsString();
                                            // для зачёта: "Зачтено"/"Незачтено"
                                            if (!isExam) {
                                                mark = ("Зачтено".equalsIgnoreCase(s) ? (byte)1 : (byte)0);
                                            } else {
                                                // если вдруг число в строке
                                                try { mark = Byte.parseByte(s); } catch (Exception ex) { mark = 0; }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    mark = 0;
                                }
                                session.addDiscipline(jdisc.name, isExam, mark);
                            }
                        }
                    }
                }

                // Пересчёт средних, если нужно
                try { gb.calculateAllAverages(); } catch (Exception ignored) {}
                result.add(gb);
                if (gradeBookMap != null) {
                    gradeBookMap.put(gb.getFullName(), gb);
                }
            }
        }

        return result;
    }

    private static List<GradeBook> loadFromJson(String filename, Map<String, GradeBook> gradeBookMap) throws IOException {
        List<GradeBook> result = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(filename), java.nio.charset.StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            JsonRoot root = gson.fromJson(reader, JsonRoot.class);
            if (root == null || root.students == null) return result;

            for (JsonStudent js : root.students) {
                // fullName: "Surname Name Middle"
                String[] parts = js.fullName != null ? js.fullName.split("\\s+", 3) : new String[0];
                String surname = parts.length > 0 ? parts[0] : "";
                String name = parts.length > 1 ? parts[1] : "";
                String middle = parts.length > 2 ? parts[2] : "";

                byte course = (byte) js.course;
                byte group = (byte) js.group;

                GradeBook gb = new GradeBook(surname, name, middle, course, group);

                if (js.sessions != null) {
                    for (JsonSession jsess : js.sessions) {
                        if (jsess == null) continue;
                        int sessNum = jsess.sessionNumber;
                        if (sessNum < 1 || sessNum > 9) continue;
                        GradeBook.Session session = gb.getSession((byte) sessNum);
                        try {
                            session.setAverage(jsess.average);
                        } catch (IllegalArgumentException ignored) {}

                        if (jsess.disciplines != null) {
                            for (JsonDiscipline jdisc : jsess.disciplines) {
                                if (jdisc == null) continue;
                                boolean isExam = "Exam".equalsIgnoreCase(jdisc.type);
                                byte mark = 0;
                                try {
                                    if (jdisc.result != null && jdisc.result.isJsonPrimitive()) {
                                        JsonPrimitive prim = jdisc.result.getAsJsonPrimitive();
                                        if (prim.isNumber()) {
                                            mark = prim.getAsNumber().byteValue();
                                        } else if (prim.isString()) {
                                            String s = prim.getAsString();
                                            if (!isExam) {
                                                mark = ("Зачтено".equalsIgnoreCase(s) ? (byte)1 : (byte)0);
                                            } else {
                                                try { mark = Byte.parseByte(s); } catch (Exception ex) { mark = 0; }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    mark = 0;
                                }
                                session.addDiscipline(jdisc.name, isExam, mark);
                            }
                        }
                    }
                }

                try { gb.calculateAllAverages(); } catch (Exception ignored) {}
                result.add(gb);
                if (gradeBookMap != null) {
                    gradeBookMap.put(gb.getFullName(), gb);
                }
            }
        }
        return result;
    }
}