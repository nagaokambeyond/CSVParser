package example.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVParser {

    String oneRes;
    private String line = "";
    private int nbLines = 0;
    private final BufferedReader reader;

    public CSVParser(BufferedReader reader) {
        this.reader = reader;
    }

    public String[] splitLine() throws Exception {
        nbLines = 0;
        var al = new ArrayList<String>();
        line = nextLine();
        if (line == null)
            return null;

        nbLines = 1;
        int pos = 0;

        while (pos < line.length()) {
            pos = findNextComma(pos);
            al.add(oneRes);
            pos++;
        }

        if (line.length() > 0 && line.charAt(line.length() - 1) == ',') {
            al.add("");
        }

        return al.toArray(new String[] {});
    }

    private int findNextComma(int p) throws Exception {
        char c;
        int i;
        oneRes = "";
        c = line.charAt(p);

        // empty field
        if (c == ',') {
            oneRes = "";
            return p;
        }

        // not escape char
        if (c != '"') {
            i = line.indexOf(',', p);
            if (i == -1)
                i = line.length();
            oneRes = line.substring(p, i);
            return i;
        }

        // start with "
        p++;

        var sb = new StringBuilder(200);
        while (true) {
            c = readNextChar(p);
            p++;

            // not a "
            if (c != '"') {
                sb.append(c);
                continue;
            }

            // ", last char -> ok
            if (p == line.length()) {
                oneRes = sb.toString();
                return p;
            }

            c = readNextChar(p);
            p++;

            // "" -> just print one
            if (c == '"') {
                sb.append('"');
                continue;
            }

            // ", -> return
            if (c == ',') {
                oneRes = sb.toString();
                return p - 1;
            }

            throw new Exception("Unexpected token found");
        }
    }

    private char readNextChar(int p) throws Exception {
        if (p == line.length()) {
            String newLine = reader.readLine();
            if (newLine == null)
                throw new Exception("Error occurred while parsing");
            line += "\n" + newLine;
            nbLines++;
        }
        return line.charAt(p);
    }

    public String nextLine() throws IOException {
        String result = "";
        do {
            result = reader.readLine();
            if (result == null)
                return null;
        } while (result.trim().equals(""));
        return result;
    }
}