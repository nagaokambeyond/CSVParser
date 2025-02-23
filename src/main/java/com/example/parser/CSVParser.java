package com.example.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVParser {

    private String line = "";
    private int nbLines = 0;
    private final BufferedReader reader;

    public CSVParser(BufferedReader reader) {
        this.reader = reader;
    }

    public String[] splitLine() throws IOException, ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
        nbLines = 0;
        var al = new ArrayList<String>();
        line = nextLine();
        if (line == null)
            return null;

        nbLines = 1;
        int currentPos = 0;
        while (currentPos < line.length()) {
            int startPos = currentPos;
            currentPos = findNextComma(startPos);
            al.add(line.substring(startPos, currentPos));
            currentPos++;
        }

        if (!line.isEmpty() && line.charAt(line.length() - 1) == ',') {
            al.add("");
        }

        return al.toArray(new String[] {});
    }

    private int findNextComma(int currentPos) throws ErrorOccurredWhileParsingException, IOException, UnexpectedTokenFoundException {
        char currentChar = line.charAt(currentPos);

        if (currentChar == ',') {
            // 現在の文字がカンマの場合→空の項目である
            // 111, , 333
            //    　↑
            //    　currentChar
            return currentPos;
        }

        if (currentChar != '"') {
            // 現在の文字がダブルクォートではない場合
            // 　111, , 333, 444
            // 　   　　↑
            //   　 　　currentChar
            int kommaPos = line.indexOf(',', currentPos);
            if (kommaPos == -1) {
                // 現在の文字がダブルクォートではない場合
                // 111, , 333
                //    　　↑
                kommaPos = line.length();
            }
            return kommaPos;
        }

        // 現在の文字がダブルクォートである場合→開始のダブルクォート
        // 　111, , "333", 444
        // 　       ↑
        // 　       currentPos
        currentPos++;

        while (true) {
            currentChar = readNextChar(currentPos);
            currentPos++;

            if (currentChar != '"') {
                // 　111, , "333", 444
                // 　        ↑
                continue;
            }

            if (currentPos == line.length()) {
                // 　111, , "333", "444"
                // 　                   ↑
                return currentPos;
            }

            currentChar = readNextChar(currentPos);
            currentPos++;

            if (currentChar == '"') {
                // 現在の文字がダブルクォートの場合→終端のダブルクォート
                // 　111, , "333", 444
                // 　           ↑
                continue;
            }

            if (currentChar == ',') {
                // 現在の文字がカンマの場合
                // 　111, , "333", 444
                // 　            ↑
                return currentPos - 1;
            }

            throw new UnexpectedTokenFoundException("Unexpected token found");
        }
    }

    private char readNextChar(int currentPos) throws ErrorOccurredWhileParsingException, IOException {
        if (currentPos == line.length()) {
            final var newLine = reader.readLine();
            if (newLine == null)
                throw new ErrorOccurredWhileParsingException("Error occurred while parsing");
            line += "\n" + newLine;
            nbLines++;
        }
        return line.charAt(currentPos);
    }

    public String nextLine() throws IOException {
        String result;
        do {
            result = reader.readLine();
            if (result == null)
                return null;
        } while (result.trim().isEmpty());
        return result;
    }
}