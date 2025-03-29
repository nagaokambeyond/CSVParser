import java.io.BufferedReader;
import java.io.StringReader;

import com.example.parser.CSVParser;

public class Main {
    public static void main(String[] args) throws Exception {
        final String doc = """
            a,a  ab,c,d a
            ,1 a
            1,\s
            a,
            1,"v ""a v\"""";
        System.out.println("String to be parsed = " + doc);

        try (var reader = new BufferedReader(new StringReader(doc))) {
            final var parser = new CSVParser(reader);
            String[] columns;

            while ((columns = parser.splitLine()) != null) {
                for (final var value : columns) {
                    System.out.println("Token Found [" + value + "] \n");
                }
            }
        }
    }
}
