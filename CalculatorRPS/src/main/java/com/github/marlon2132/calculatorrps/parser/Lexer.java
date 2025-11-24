package com.github.marlon2132.calculatorrps.parser;

import java.util.*;
import java.util.regex.*;

public final class Lexer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\s*(?:(\\d+(?:\\.\\d+)?)|([A-Za-zА-Яа-яёЁ][A-Za-z0-9_А-Яа-яёЁ]*)|(\\^|\\+|\\-|\\*|/)|([()\\[\\]\\{\\}]))");

    public static List<Token> tokenize(String s) throws CalcException {
        List<Token> out = new ArrayList<>();
        Pattern p = Pattern.compile("\\s*(\\d+(?:\\.\\d+)?|[A-Za-zА-Яа-яёЁ][A-Za-z0-9_А-Яа-яёЁ]*|\\^|\\+|\\-|\\*|/|[()\\[\\]\\{\\}])");
        Matcher m = p.matcher(s);
        int idx = 0;
        while (idx < s.length()) {
            m.region(idx, s.length());
            if (!m.lookingAt()) throw new CalcException("Неизвестный токен в позиции " + idx);
            String tok = m.group(1);
            if (tok.matches("\\d+(?:\\.\\d+)?")) out.add(new NumberToken(Double.parseDouble(tok)));
            else if (tok.matches("[A-Za-zА-Яа-яёЁ].*")) out.add(new VariableToken(tok));
            else if ("+-*/^".contains(tok)) out.add(new OperatorToken(tok));
            else if ("()[]{}".contains(tok)) out.add(new ParenToken(tok.charAt(0)));
            idx = m.end();
        }
        return out;
    }

}
