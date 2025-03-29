package com.example.parser;

public class UnexpectedTokenFoundException extends Exception {
    public UnexpectedTokenFoundException(String message){
        super(message);
    }
}
