public class LetterCounter {
    private static final String VOWELS = "аеёиоуыэюя";

    public static CountResult count(String sentence) {
        int v = 0, c = 0;
        for (char ch : sentence.toLowerCase().toCharArray()) {
            if ((ch >= 'а' && ch <= 'я') || ch == 'ё') {
                if (VOWELS.indexOf(ch) >= 0) {
                    v++;
                }
                else {
                    c++;
                }
            }
        }

        return new CountResult(v, c);
    }
}
