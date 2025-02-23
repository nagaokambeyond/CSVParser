import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import com.example.parser.CSVParser;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = null;
        try {
            String doc = """
                a,a  ab,c,d a
                ,1 a
                1,\s
                a,
                1,"v ""a v\"""";

            System.out.println("String to be parsed = " + doc);
            reader = new BufferedReader(new StringReader(doc));
            var parser = new CSVParser(reader);
            String[] columns;

            while ((columns = parser.splitLine()) != null) {
                for (final var value : columns) {
                    System.out.println("Token Found [" + value + "] \n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                reader.close();
            }
        }
    }
}
