import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileTextReader {
    public String readTextFromFile(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(filename));
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
