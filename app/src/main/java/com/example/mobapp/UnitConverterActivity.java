package com.example.mobapp;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class UnitConverterActivity extends AppCompatActivity {
    Spinner spinnerCategory, spinnerFrom, spinnerTo;
    EditText etValue; TextView tvResult; Button btnConvert, btnBack;

    String[][] units = {
        {"km","m","cm","mm","miles","feet","inches","yards"},
        {"kg","g","mg","lb","oz","ton"},
        {"Celsius","Fahrenheit","Kelvin"}
    };
    String[] categories = {"Length","Weight","Temperature"};

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_unit_converter);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom     = findViewById(R.id.spinnerFrom);
        spinnerTo       = findViewById(R.id.spinnerTo);
        etValue         = findViewById(R.id.etValue);
        tvResult        = findViewById(R.id.tvResult);
        btnConvert      = findViewById(R.id.btnConvert);
        btnBack         = findViewById(R.id.btnBack);

        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories));
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                ArrayAdapter<String> a = new ArrayAdapter<>(UnitConverterActivity.this,
                    android.R.layout.simple_spinner_item, units[pos]);
                spinnerFrom.setAdapter(a); spinnerTo.setAdapter(a);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
        btnConvert.setOnClickListener(v -> convert());
        btnBack.setOnClickListener(v -> finish());
    }

    void convert() {
        String vs = etValue.getText().toString().trim();
        if (vs.isEmpty()) { Toast.makeText(this, "Enter a value", Toast.LENGTH_SHORT).show(); return; }
        double val = Double.parseDouble(vs);
        int cat = spinnerCategory.getSelectedItemPosition();
        String from = spinnerFrom.getSelectedItem().toString();
        String to   = spinnerTo.getSelectedItem().toString();
        double result = 0;

        if (cat == 0) { // Length — convert to meters first
            double meters = toMeters(from, val);
            result = fromMeters(to, meters);
        } else if (cat == 1) { // Weight — convert to grams first
            double grams = toGrams(from, val);
            result = fromGrams(to, grams);
        } else { // Temperature
            result = convertTemp(from, to, val);
        }
        tvResult.setText(String.format("%s %s = %.4f %s", vs, from, result, to));
    }

    double toMeters(String u, double v) {
        switch(u) { case "km": return v*1000; case "cm": return v/100; case "mm": return v/1000;
                    case "miles": return v*1609.34; case "feet": return v*0.3048;
                    case "inches": return v*0.0254; case "yards": return v*0.9144; default: return v; }
    }
    double fromMeters(String u, double v) {
        switch(u) { case "km": return v/1000; case "cm": return v*100; case "mm": return v*1000;
                    case "miles": return v/1609.34; case "feet": return v/0.3048;
                    case "inches": return v/0.0254; case "yards": return v/0.9144; default: return v; }
    }
    double toGrams(String u, double v) {
        switch(u) { case "kg": return v*1000; case "mg": return v/1000; case "lb": return v*453.592;
                    case "oz": return v*28.3495; case "ton": return v*1000000; default: return v; }
    }
    double fromGrams(String u, double v) {
        switch(u) { case "kg": return v/1000; case "mg": return v*1000; case "lb": return v/453.592;
                    case "oz": return v/28.3495; case "ton": return v/1000000; default: return v; }
    }
    double convertTemp(String from, String to, double v) {
        double celsius = from.equals("Celsius") ? v : from.equals("Fahrenheit") ? (v-32)*5/9 : v-273.15;
        return to.equals("Celsius") ? celsius : to.equals("Fahrenheit") ? celsius*9/5+32 : celsius+273.15;
    }
}
