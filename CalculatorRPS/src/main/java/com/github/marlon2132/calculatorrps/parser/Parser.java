package com.github.marlon2132.calculatorrps.parser;

import java.util.*;

public final class Parser {
    public static List<Token> infixToRPN(List<Token> tokens) throws CalcException {
        List<Token> out = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();
        Set<Character> opening = Set.of('(', '[', '{');
        Map<Character,Character> closeToOpen = Map.of(')', '(', ']', '[', '}', '{');

        Token prev = null;
        for (Token t : tokens) {
            if (t instanceof NumberToken || t instanceof VariableToken) {
                out.add(t);
                prev = t;
            } else if (t instanceof OperatorToken) {
                String op = ((OperatorToken)t).op;
                boolean isUnary = "-".equals(op) && (prev == null || prev instanceof OperatorToken || (prev instanceof ParenToken && opening.contains(((ParenToken)prev).ch)));
                String cur = isUnary ? "u-" : op;
                OperatorToken curTok = new OperatorToken(cur);
                while (!stack.isEmpty() && stack.peek() instanceof OperatorToken) {
                    OperatorToken top = (OperatorToken) stack.peek();
                    int pTop = precedence(top.op), pCur = precedence(curTok.op);
                    if (pTop > pCur || (pTop == pCur && !isRightAssociative(curTok.op))) out.add(stack.pop());
                    else break;
                }
                stack.push(curTok);
                prev = curTok;
            } else if (t instanceof ParenToken) {
                char c = ((ParenToken)t).ch;
                if (opening.contains(c)) {
                    stack.push(t);
                    prev = t;
                } else {
                    char need = closeToOpen.get(c);
                    boolean found = false;
                    while (!stack.isEmpty()) {
                        Token top = stack.pop();
                        if (top instanceof ParenToken) {
                            if (((ParenToken)top).ch == need) { found = true; break; }
                            else throw new CalcException("Несовпадающие скобки");
                        } else out.add(top);
                    }
                    if (!found) throw new CalcException("Несбалансированные скобки");
                    prev = t;
                }
            }
        }
        while (!stack.isEmpty()) {
            Token tk = stack.pop();
            if (tk instanceof ParenToken) throw new CalcException("Несбалансированные скобки");
            out.add(tk);
        }
        return out;
    }


    public static String rpnToString(List<Token> rpn) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<rpn.size();i++) {
            sb.append(rpn.get(i).toString());
            if (i+1<rpn.size()) sb.append(' ');
        }
        return sb.toString();
    }

    private static int precedence(String op) {
        switch (op) {
            case "u-": return 5;   // унарный минус — самый высокий приоритет
            case "^":  return 4;   // степень — правоассоциативный
            case "*": case "/": return 3;
            case "+": case "-": return 2;
            default:   return 0;   // неизвестный оператор
        }
    }

    private static boolean isRightAssociative(String op) {
        return "^".equals(op) || "u-".equals(op);
    }
}
