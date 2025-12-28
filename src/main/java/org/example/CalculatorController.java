package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.ArrayList;
import java.util.List;

public class CalculatorController {
    @FXML
    private TextField field;
    // These characters will not be allowed to be the first on the text field
    private List<String> disallowedFirstChars = List.of("0", "+", "-", "/", "*");
    private boolean shouldPrint = true;

    @FXML
    public void evaluate() {
        String exprStr = field.getText();
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("JavaScript");

        try {
            Object result = se.eval(exprStr);
            if (result != null) {
                field.setText(result.toString());
            }
        } catch (ScriptException e) {
            System.out.println("Couldn't evaluate results: " + e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid expression");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage().toLowerCase().contains("expected an operand but found eof") ?
                "You've entered an invalid expression, please try again." : "Couldn't evaluate results: " + e);
            alert.showAndWait();
        }
    }

    @FXML
    public void printValue(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String btnText = btn.getText();
        String fieldText = field.getText();

        switch (btnText) {
            case "=" -> {
                evaluate();
                return;
            }

            case "X" -> {
                if (fieldText != null && !fieldText.isEmpty()) {
                    String newText = fieldText.substring(0, fieldText.length() - 1);
                    field.setText(newText);
                }
                return;
            }

            case "CE" -> {
                field.setText("");
                return;
            }
        }
        if (disallowedFirstChars.contains(btnText) && field.getText().isEmpty()) {
            return;
        }
        if (!fieldText.isEmpty()) {
            char lastChar = fieldText.charAt(fieldText.length() - 1);
            // 0 ამოვაგდოთ, რადგან მხოლოდ ოპერატორები არ უნდა ისპამებოდეს
            List<String> operators = new ArrayList<>(disallowedFirstChars);
            operators.remove("0");
            // თუ ახლა დაწერილი სიმბოლო არის ოპერატორი
            // და ტექსტურ ველზე ბოლოს აკრეფილი სიმბოლოც არის ოპერატორი, დაბრუნდეს
            // ეს თავიდან აიცილებს ოპერატორების გასპამვას და ისეთი გამოსახულებების მიღებას, როგორებიცაა:
            // 456---- ან 342++++
            if (operators.contains(btnText) &&
                operators.contains(String.valueOf(lastChar))) {
                return;
            }
        }

        field.appendText(btnText);
    }
}
