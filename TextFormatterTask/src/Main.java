import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

class FileHelper {
    private final Path input;
    private final Path output;

    public FileHelper(String inputFileName, String outputFileName) {
        this.input = Paths.get(inputFileName);
        this.output = Paths.get(outputFileName);
    }

    public List<String> readAllLines() throws IOException {
        if (!Files.exists(input)) {
            throw new FileNotFoundException("Input file not found: " + input.toAbsolutePath());
        }
        return Files.readAllLines(input, StandardCharsets.UTF_8);
    }

    public void writeAllLines(List<String> lines) throws IOException {
        Files.write(output, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

public class Main {
    private static String justifyLine(List<String> words, int maxWidth) {
        if (words.isEmpty()) return " ".repeat(Math.max(0, maxWidth));
        if (words.size() == 1) {
            String w = words.get(0);
            if (w.length() >= maxWidth) return w.substring(0, maxWidth);
            return w + " ".repeat(maxWidth - w.length());
        }
        int totalWordsLen = 0;
        for (String w : words) totalWordsLen += w.length();
        int totalSpaces = maxWidth - totalWordsLen;
        int gaps = words.size() - 1;
        int baseSpace = totalSpaces / gaps;
        int extra = totalSpaces % gaps;

        StringBuilder sb = new StringBuilder(maxWidth);
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < gaps) {
                int spacesHere = baseSpace + (i < extra ? 1 : 0);
                for (int s = 0; s < spacesHere; s++) sb.append(' ');
            }
        }
        return sb.toString();
    }

    private static List<String> justifyText(List<String> words, int maxWidth) {
        List<String> result = new ArrayList<>();
        List<String> lineWords = new ArrayList<>();
        int lineLen = 0;

        for (String w : words) {
            if (w.length() > maxWidth) {
                if (!lineWords.isEmpty()) {
                    result.add(justifyLine(lineWords, maxWidth));
                    lineWords.clear();
                    lineLen = 0;
                }
                // слово длиннее ширины — поместим как отдельную строку (может превышать maxWidth)
                result.add(w);
                continue;
            }

            if (lineWords.isEmpty()) {
                lineWords.add(w);
                lineLen = w.length();
            } else {
                int currentMinLen = lineLen + (lineWords.size() - 1);
                int newMinLen = currentMinLen + 1 + w.length();
                if (newMinLen <= maxWidth) {
                    lineWords.add(w);
                    lineLen += w.length();
                } else {
                    result.add(justifyLine(lineWords, maxWidth));
                    lineWords.clear();
                    lineWords.add(w);
                    lineLen = w.length();
                }
            }
        }

        if (!lineWords.isEmpty()) {
            result.add(justifyLine(lineWords, maxWidth));
        }
        return result;
    }

    private static String leftJustifyLine(List<String> words, int maxWidth) {
        if (words.isEmpty()){
            return " ".repeat(Math.max(0, maxWidth));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(words.get(0));

        for (int i = 1; i < words.size(); i++) {
            sb.append(' ');
            sb.append(words.get(i));
        }

        int curLen = sb.length();

        if (curLen < maxWidth){
            sb.append(" ".repeat(maxWidth - curLen));
        }

        return sb.toString();
    }

    private static List<String> justifyTextWithIndent(List<String> words, int maxWidth, int firstLineIndent) {
        List<String> result = new ArrayList<>();
        List<String> lineWords = new ArrayList<>();

        int pos = 0;
        boolean isFirstLine = true;

        while (pos < words.size()) {
            int available = isFirstLine ? Math.max(0, maxWidth - firstLineIndent) : maxWidth;
            lineWords.clear();
            int lineLen = 0;

            while (pos < words.size()) {
                String w = words.get(pos);
                if (w.length() > available) {
                    if (lineWords.isEmpty()) {
                        StringBuilder sb = new StringBuilder();

                        if (isFirstLine && firstLineIndent > 0){
                            sb.append(" ".repeat(firstLineIndent));
                        }

                        sb.append(w);
                        result.add(sb.toString());
                        pos++;
                        isFirstLine = false;
                    }

                    break;
                }
                else {
                    if (lineWords.isEmpty()) {
                        lineWords.add(w);
                        lineLen = w.length();
                        pos++;
                    }
                    else {
                        int minLenWithThis = lineLen + 1 + w.length();

                        if (minLenWithThis <= available) {
                            lineWords.add(w);
                            lineLen += 1 + w.length();
                            pos++;
                        }
                        else {
                            break;
                        }
                    }
                }
            }

            if (!lineWords.isEmpty()) {
                boolean isLastLine = (pos >= words.size());

                String built;
                if (isLastLine) {
                    String base = leftJustifyLine(lineWords, isFirstLine ? (maxWidth - firstLineIndent) : maxWidth);

                    if (isFirstLine && firstLineIndent > 0) {
                        built = " ".repeat(firstLineIndent) + base;
                    }
                    else {
                        built = base;
                    }
                }
                else {
                    String base = justifyLine(lineWords, isFirstLine ? (maxWidth - firstLineIndent) : maxWidth);

                    if (isFirstLine && firstLineIndent > 0) {
                        built = " ".repeat(firstLineIndent) + base;
                    }
                    else {
                        built = base;
                    }
                }

                result.add(built);
            }

            isFirstLine = false;
        }

        return result;
    }

    // разбивает список входных строк на абзацы; пустая строка (после trim) — разделитель
    private static List<List<String>> splitToParagraphs(List<String> lines) {
        List<List<String>> paragraphs = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line : lines) {
            String normalized = (line == null) ? "" : line.replace('\u00A0', ' ').trim();
            if (normalized.isEmpty()) {
                if (!current.isEmpty()) {
                    paragraphs.add(new ArrayList<>(current));
                    current.clear();
                } else {
                    // последовательные пустые строки — пропускаем, чтобы не создавать пустые абзацы подряд
                }
            } else {
                current.add(line);
            }
        }
        if (!current.isEmpty()) paragraphs.add(current);
        return paragraphs;
    }

    // разбивает строки одного абзаца на слова
    private static List<String> splitParagraphToWords(List<String> paraLines) {
        List<String> words = new ArrayList<>();
        for (String line : paraLines) {
            if (line == null) continue;
            String normalized = line.replace('\u00A0', ' ').trim();
            if (normalized.isEmpty()) continue;
            String[] parts = normalized.split("\\s+");
            for (String p : parts) if (!p.isEmpty()) words.add(p);
        }

        return words;
    }

    public static void main(String[] args) {
        BufferedReader brInt = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        int n;

        System.out.print("Введите максимальное число символов в строке: ");

        try {
            String s = brInt.readLine();
            n = Integer.parseInt(s.trim());

            if (n < 1) {
                System.out.println("Число символов должно быть >= 1");

                return;
            }
        }
        catch (IOException | NumberFormatException ex) {
            System.out.println("Ошибка ввода: " + ex.getMessage());

            return;
        }

        FileHelper fh = new FileHelper("input.txt", "output.txt");
        List<String> inputLines;
        try {
            inputLines = fh.readAllLines();
        }
        catch (IOException e) {
            System.err.println("Ошибка чтения входного файла: " + e.getMessage());

            return;
        }

        List<List<String>> paragraphs = splitToParagraphs(inputLines);

        List<String> outputLines = new ArrayList<>();

        for (int i = 0; i < paragraphs.size(); i++) {
            List<String> para = paragraphs.get(i);
            List<String> words = splitParagraphToWords(para);

            if (words.isEmpty()) {
                outputLines.add("");
            }
            else {
                List<String> justified = justifyTextWithIndent(words, n, 4);
                outputLines.addAll(justified);
            }

            if (i < paragraphs.size() - 1){
                outputLines.add("");
            }
        }

        try {
            fh.writeAllLines(outputLines);
            System.out.println("Готово. Результат записан в output.txt");
        }
        catch (IOException e) {
            System.err.println("Ошибка записи в output.txt: " + e.getMessage());
        }
    }
}
