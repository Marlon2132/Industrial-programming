import java.util.*;

public class SentenceSplitter {
    public static List<String> splitIntoSentences(String text) {
        String[] parts = text.split("[.!?]+");
        List<String> sentences = new ArrayList<>();

        for (String p : parts) {
            String s = p.trim();

            if (!s.isEmpty()) {
                sentences.add(s);
            }
        }

        return sentences;
    }
}
