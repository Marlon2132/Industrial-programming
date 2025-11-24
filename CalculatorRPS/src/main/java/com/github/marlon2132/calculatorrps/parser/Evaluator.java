package com.github.marlon2132.calculatorrps.parser;

import java.util.*;

public final class Evaluator {
    // vars: map переменных передаётся извне; если null — бросаем CalcException при неизвестной переменной
    public static double evalRPN(List<Token> rpn, Map<String,Double> vars) throws CalcException {
        Deque<Double> st = new ArrayDeque<>();
        for (Token t : rpn) {
            if (t instanceof NumberToken) st.push(((NumberToken)t).value);
            else if (t instanceof VariableToken) {
                Double v = vars.get(((VariableToken)t).name);
                if (v == null) throw new CalcException("Неизвестная переменная: " + ((VariableToken)t).name);
                st.push(v);
            } else if (t instanceof OperatorToken) {
                String op = ((OperatorToken)t).op;
                if ("u-".equals(op)) {
                    if (st.isEmpty()) throw new CalcException("недостаточно операндов");
                    st.push(-st.pop());
                } else {
                    if (st.size() < 2) throw new CalcException("Недостаточно операндов для " + op);
                    double b = st.pop(), a = st.pop();
                    switch (op) {
                        case "+": st.push(a+b); break;
                        case "-": st.push(a-b); break;
                        case "*": st.push(a*b); break;
                        case "/": if (b==0) throw new CalcException("Деление на ноль"); st.push(a/b); break;
                        case "^": st.push(Math.pow(a,b)); break;
                        default: throw new CalcException("Неизвестный оп " + op);
                    }
                }
            } else throw new CalcException("Неизвестный токен в RPN");
        }
        if (st.size() != 1) throw new CalcException("Неверное выражение: на стеке " + st.size());
        return st.pop();
    }

}
