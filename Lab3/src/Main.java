import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        String text;

        try {
            System.out.print("Enter a file name (or leave it empty for the console): ");
            String filename = br.readLine().trim();

            if (!filename.isEmpty()) {
                text = new FileTextReader().readTextFromFile(filename);
            }
            else {
                System.out.println("Enter text from the console (end with an empty line):");
                text = new TextReader().readText();
            }
        }
        catch (IOException e) {
            System.err.println("Error reading the text: " + e.getMessage());
            return;
        }

        System.out.println("\n------- Your text -------");
        System.out.println(text);
        System.out.println("-------------------------\n");

        List<String> sentences = SentenceSplitter.splitIntoSentences(text);
        int idx = 1;

        for (String s : sentences) {
            CountResult res = LetterCounter.count(s);
            System.out.printf(
                    "Sentence %d: %s (Vowel=%d, Consonant=%d)%n",
                    idx++, res.getComparison(), res.getVowels(), res.getConsonants()
            );
        }
    }
}
