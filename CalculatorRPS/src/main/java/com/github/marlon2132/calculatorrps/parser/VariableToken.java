package com.github.marlon2132.calculatorrps.parser;

public class VariableToken extends Token {
    public final String name;
    public VariableToken(String n) { name = n; }
    public String toString() { return name; }
}
