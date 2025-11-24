package com.github.marlon2132.calculatorrps.parser;

public class NumberToken extends Token {
    public final double value;
    public NumberToken(double v) { value = v; }
    public String toString() { return Double.toString(value); }
}
