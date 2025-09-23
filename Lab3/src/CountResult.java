public class CountResult {
    private final int vowels;
    private final int consonants;

    public CountResult(int vowels, int consonants) {
        this.vowels = vowels;
        this.consonants = consonants;
    }

    public int getVowels() {
        return vowels;
    }

    public int getConsonants() {
        return consonants;
    }

    public String getComparison() {
        if (vowels > consonants) {
            return "There are more vowels";
        }
        else if (consonants > vowels) {
            return "There are more consonants";
        }
        else {
            return "Count of vowels and consonants are equal";
        }
    }
}
