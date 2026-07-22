package com.example.mobapp;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AgeCalculatorActivity extends AppCompatActivity {
    TextView tvDOB, tvResult; Button btnPickDate, btnCalc, btnBack;
    int selYear=0, selMonth=0, selDay=0;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_age_calculator);
        tvDOB    = findViewById(R.id.tvDOB);
        tvResult = findViewById(R.id.tvAgeResult);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnCalc  = findViewById(R.id.btnCalc);
        btnBack  = findViewById(R.id.btnBack);

        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                selYear=y; selMonth=m; selDay=d;
                tvDOB.setText("Date of Birth: " + d+"/"+(m+1)+"/"+y);
            }, c.get(Calendar.YEAR)-25, c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnCalc.setOnClickListener(v -> {
            if (selYear == 0) { Toast.makeText(this,"Pick date of birth first",Toast.LENGTH_SHORT).show(); return; }
            Calendar dob = Calendar.getInstance();
            dob.set(selYear, selMonth, selDay);
            Calendar now = Calendar.getInstance();

            int years  = now.get(Calendar.YEAR)  - dob.get(Calendar.YEAR);
            int months = now.get(Calendar.MONTH) - dob.get(Calendar.MONTH);
            int days   = now.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH);
            if (days   < 0) { months--; days += 30; }
            if (months < 0) { years--;  months += 12; }

            long totalDays   = (now.getTimeInMillis() - dob.getTimeInMillis()) / (1000*60*60*24);
            long totalWeeks  = totalDays / 7;
            long totalMonths = years * 12L + months;
            long totalHours  = totalDays * 24;

            tvResult.setText(
                "🎂 Age: " + years + " Years, " + months + " Months, " + days + " Days\n\n" +
                "📅 Total Days: "   + totalDays   + "\n" +
                "📅 Total Weeks: "  + totalWeeks  + "\n" +
                "📅 Total Months: " + totalMonths + "\n" +
                "⏰ Total Hours: "  + totalHours
            );
        });
        btnBack.setOnClickListener(v -> finish());
    }
}
