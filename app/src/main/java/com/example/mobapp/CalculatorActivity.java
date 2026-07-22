package com.example.mobapp;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {
    EditText etDisplay;
    StringBuilder input = new StringBuilder();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_calculator);
        etDisplay = findViewById(R.id.etDisplay);
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
    }

    public void onDigit(View v) {
        input.append(((Button)v).getText().toString());
        etDisplay.setText(input.toString());
    }

    public void onOp(View v) {
        String op = ((Button)v).getText().toString();
        if (op.equals("×")) op = "*";
        if (op.equals("÷")) op = "/";
        input.append(op);
        etDisplay.setText(input.toString());
    }

    public void onClear(View v) {
        input.setLength(0);
        etDisplay.setText("");
    }

    public void onEquals(View v) {
        try {
            double result = evaluate(input.toString());
            String res = result == (long)result ?
                String.valueOf((long)result) : String.valueOf(result);
            etDisplay.setText(res);
            input.setLength(0);
            input.append(res);
        } catch (Exception e) {
            etDisplay.setText("Error");
            input.setLength(0);
        }
    }

    double evaluate(String expr) {
        java.util.List<Double> nums = new java.util.ArrayList<>();
        java.util.List<Character> ops = new java.util.ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (char c : expr.toCharArray()) {
            if (Character.isDigit(c) || c == '.') num.append(c);
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                nums.add(Double.parseDouble(num.toString())); num.setLength(0); ops.add(c);
            }
        }
        nums.add(Double.parseDouble(num.toString()));
        for (int i = 0; i < ops.size(); ) {
            if (ops.get(i) == '*' || ops.get(i) == '/') {
                double r = ops.get(i) == '*' ? nums.get(i)*nums.get(i+1) : nums.get(i)/nums.get(i+1);
                nums.set(i, r); nums.remove(i+1); ops.remove(i);
            } else i++;
        }
        double r = nums.get(0);
        for (int i = 0; i < ops.size(); i++)
            r = ops.get(i) == '+' ? r + nums.get(i+1) : r - nums.get(i+1);
        return r;
    }
}
