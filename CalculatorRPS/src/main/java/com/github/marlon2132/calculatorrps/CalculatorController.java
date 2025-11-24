package com.github.marlon2132.calculatorrps;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import com.github.marlon2132.calculatorrps.parser.*;
import javafx.scene.control.TextInputDialog;

import java.util.*;

public class CalculatorController {
    @FXML private TextField inputField;
    @FXML private TextField rpnField;
    @FXML private TextField resultField;
    @FXML private Button computeBtn;
    @FXML private Button clearBtn;

    @FXML
    private void initialize() {
        // диагностическая печать (удалите после отладки)
        System.out.println("Controller initialized: inputField=" + (inputField != null)
                + " rpnField=" + (rpnField != null) + " resultField=" + (resultField != null)
                + " computeBtn=" + (computeBtn != null) + " clearBtn=" + (clearBtn != null));

        // назначаем обработчики кнопок
        computeBtn.setOnAction(e -> onCompute());
        clearBtn.setOnAction(e -> onClear());
    }

    @FXML
    private void onCompute() {
        String expr = inputField.getText();
        if (expr == null) expr = "";
        expr = expr.trim();
        if (expr.isEmpty()) {
            resultField.setText("Введите выражение");
            rpnField.setText("");
            return;
        }

        try {
            // 1) Лексер -> токены
            List<com.github.marlon2132.calculatorrps.parser.Token> tokens =
                    com.github.marlon2132.calculatorrps.parser.Lexer.tokenize(expr);

            // 2) Инфикс -> RPN
            List<com.github.marlon2132.calculatorrps.parser.Token> rpn =
                    com.github.marlon2132.calculatorrps.parser.Parser.infixToRPN(tokens);

            // Показываем RPN строкой
            rpnField.setText(com.github.marlon2132.calculatorrps.parser.Parser.rpnToString(rpn));

            // 3) Вычисление: будем запрашивать переменные при необходимости
            Map<String, Double> vars = new HashMap<>();
            while (true) {
                try {
                    double value = com.github.marlon2132.calculatorrps.parser.Evaluator.evalRPN(rpn, vars);
                    resultField.setText(Double.toString(value));
                    break;
                } catch (com.github.marlon2132.calculatorrps.parser.CalcException ex) {
                    String msg = ex.getMessage();
                    // ожидаем, что Evaluator бросает ошибку вида "Неизвестная переменная: name"
                    String prefix = "Неизвестная переменная: ";
                    if (msg != null && msg.startsWith(prefix)) {
                        String varName = msg.substring(prefix.length());
                        TextInputDialog dlg = new TextInputDialog();
                        dlg.setTitle("Значение переменной");
                        dlg.setHeaderText("Введите значение переменной: " + varName);
                        dlg.setContentText(varName + " = ");
                        var input = dlg.showAndWait();
                        if (input.isPresent()) {
                            try {
                                double v = Double.parseDouble(input.get().trim());
                                vars.put(varName, v);
                                // повторим вычисление в цикле
                                continue;
                            } catch (NumberFormatException nfe) {
                                resultField.setText("Неверное число для " + varName);
                                break;
                            }
                        } else {
                            resultField.setText("Ввод переменной отменён");
                            break;
                        }
                    } else {
                        // обычная ошибка вычисления (деление на ноль, синтаксис и т.п.)
                        resultField.setText("Ошибка: " + msg);
                        break;
                    }
                }
            }
        } catch (com.github.marlon2132.calculatorrps.parser.CalcException ex) {
            rpnField.setText("");
            resultField.setText("Ошибка: " + ex.getMessage());
        } catch (Exception ex) {
            rpnField.setText("");
            resultField.setText("Внутренняя ошибка: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    @FXML
    private void onClear() {
        inputField.clear();
        rpnField.clear();
        resultField.clear();
    }
}
