package com.example.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CSVParser {
    private String line = "";
    private final BufferedReader reader;

    public CSVParser(final BufferedReader reader) {
        if (Objects.isNull(reader)) {
            throw new IllegalArgumentException("BufferedReader cannot be null.");
        }
        this.reader = reader;
    }

    public String[] splitLine() throws IOException, ErrorOccurredWhileParsingException, UnexpectedTokenFoundException {
        line = nextLine();
        if (Objects.isNull(line) || isLineComment(line)) {
            return null;
        }

        var result = new ArrayList<String>();
        int currentPos = 0;
        while (currentPos < line.length()) {
            final int valueStartPos = currentPos;
            currentPos = getValueEndPos(valueStartPos);
            result.add(line.substring(valueStartPos, currentPos));
            currentPos++;
        }

        if (!line.isEmpty() && isComma(line.charAt(line.length() - 1))) {
            result.add("");
        }

        return result.toArray(new String[]{});
    }

    private int getValueEndPos(int currentPos) throws ErrorOccurredWhileParsingException, IOException, UnexpectedTokenFoundException {
        var currentChar = line.charAt(currentPos);

        if (isComma(currentChar)) {
            // 現在の文字がカンマの場合→空の項目である
            // 111,,333
            //    ^
            //    currentChar==3
            return currentPos;
        }

        if (isNotDoubleQuote(currentChar)) {
            // 現在の文字がダブルクォートではない場合
            // 　111,,333,444
            //   ^
            //   currentChar==0
            final int commaPos = line.indexOf(',', currentPos);
            if (notExists(commaPos)) {
                // 現在の文字がダブルクォートではなく
                // 現在の文字より後にダブルクォートが存在しない場合
                // 　その１　カンマなし
                // 　　111
                // 　　^
                //    currentChar==0
                // 　その２　以降にカンマなし
                // 　　111,,333
                // 　　     ^
                //         currentChar==5
                return line.length();   // 乱暴ではある
            }
            return commaPos;
        }

        // 現在の文字がダブルクォートである場合→開始のダブルクォート
        // 　111,,"333",444
        // 　     ^
        // 　     currentPos==5

        // 次へ
        // 　111,,"333",444
        // 　      ^
        // 　      currentPos==6
        currentPos++;

        while (true) {
            currentChar = readNextChar(currentPos);
            currentPos++;

            if (isNotDoubleQuote(currentChar)) {
                // 現在の文字がダブルクォートではない場合
                // 　111,,"333",444
                // 　      ^^
                // 　      |currentPos==7
                // 　      |currentChar
                continue;
            }

            if (isEndPos(currentPos)) {
                // 現在の文字が最後の文字の場合
                // 　111,,"33
                // 　       ^
                // 　       currentPos==7
                return currentPos;
            }

            currentChar = readNextChar(currentPos);
            currentPos++;

            if (isDoubleQuote(currentChar)) {
                // 現在の文字がダブルクォートの場合→終端のダブルクォート
                // 　111,,"333",444
                // 　         ^
                // 　         currentPos==9
                continue;
            }

            if (isComma(currentChar)) {
                // 現在の文字がカンマの場合
                // 　111, , "333", 444
                // 　            ^
                return currentPos - 1;
            }

            throw new UnexpectedTokenFoundException("Unexpected token found.");
        }
    }

    private char readNextChar(final int currentPos) throws ErrorOccurredWhileParsingException, IOException {
        if (currentPos == line.length()) {
            final var newLine = reader.readLine();
            if (Objects.isNull(newLine)) {
                throw new ErrorOccurredWhileParsingException("Error occurred while parsing.");
            }
            line += "\n" + newLine;
        }
        return line.charAt(currentPos);
    }

    private String nextLine() throws IOException {
        String result;
        do {
            result = reader.readLine();
            if (Objects.isNull(result)) {
                return null;
            }
        } while (result.trim().isEmpty());
        return result;
    }

    private boolean isDoubleQuote(final char val) {
        return val == '"';
    }

    private boolean isNotDoubleQuote(final char val) {
        return !isDoubleQuote(val);
    }

    private boolean isComma(final char val) {
        return val == ',';
    }

    private boolean isEndPos(final int pos) {
        return pos == line.length();
    }

    private boolean notExists(final int pos) {
        return pos == -1;
    }

    private boolean isLineComment(final String val) {
        return !val.isEmpty() && val.charAt(0) == '#';
    }
}
