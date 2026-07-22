package com.example.mobapp;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class BMIActivity extends AppCompatActivity {
    EditText etWeight, etHeight; TextView tvResult, tvCategory, tvAdvice;
    Button btnCalc, btnBack;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_bmi);
        etWeight   = findViewById(R.id.etWeight);
        etHeight   = findViewById(R.id.etHeight);
        tvResult   = findViewById(R.id.tvBMIResult);
        tvCategory = findViewById(R.id.tvCategory);
        tvAdvice   = findViewById(R.id.tvAdvice);
        btnCalc    = findViewById(R.id.btnCalc);
        btnBack    = findViewById(R.id.btnBack);

        btnCalc.setOnClickListener(v -> calculate());
        btnBack.setOnClickListener(v -> finish());
    }

    void calculate() {
        String ws = etWeight.getText().toString().trim();
        String hs = etHeight.getText().toString().trim();
        if (ws.isEmpty() || hs.isEmpty()) {
            Toast.makeText(this, "Enter weight and height", Toast.LENGTH_SHORT).show(); return;
        }
        float weight = Float.parseFloat(ws);
        float heightCm = Float.parseFloat(hs);
        float heightM = heightCm / 100f;
        float bmi = weight / (heightM * heightM);

        tvResult.setText(String.format("BMI: %.1f", bmi));

        String cat, advice;
        int color;
        if      (bmi < 18.5) { cat = "Underweight 🟡"; advice = "Eat more nutritious food and consult a doctor."; color = 0xFFFF9800; }
        else if (bmi < 25.0) { cat = "Normal Weight ✅"; advice = "Great! Maintain your healthy lifestyle."; color = 0xFF27AE60; }
        else if (bmi < 30.0) { cat = "Overweight 🟠"; advice = "Exercise regularly and eat balanced diet."; color = 0xFFFF5722; }
        else                  { cat = "Obese 🔴"; advice = "Consult a doctor immediately for a health plan."; color = 0xFFE74C3C; }

        tvCategory.setText(cat);
        tvCategory.setTextColor(color);
        tvAdvice.setText(advice);
    }
}
