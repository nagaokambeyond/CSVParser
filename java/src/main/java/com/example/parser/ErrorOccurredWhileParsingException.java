package com.example.parser;

/**
 * ダブルクォートが閉じられていない場合にthrowする
 */
public class ErrorOccurredWhileParsingException extends Exception {
    public ErrorOccurredWhileParsingException(String message) {
        super(message);
    }
}
