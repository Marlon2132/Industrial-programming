package com.github.marlon2132.calculatorrps.parser;

public class OperatorToken extends Token {
    public final String op; // "+", "-", "*", "/", "^", "u-"
    public OperatorToken(String o) { op = o; }
    public String toString() { return op; }
}
