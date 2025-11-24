package com.github.marlon2132.calculatorrps.parser;

public class ParenToken extends Token {
    public final char ch;
    public ParenToken(char c) { ch = c; }
    public String toString() { return Character.toString(ch); }
}
