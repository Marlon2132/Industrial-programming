import java.io.*;

public class TextReader {
    public String readText() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            sb.append(line).append(" ");
        }

        return sb.toString().trim();
    }
}
