package com.example.mobapp;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class StopwatchActivity extends AppCompatActivity {
    TextView tvTime; Button btnStart, btnStop, btnReset, btnLap, btnBack;
    ListView lvLaps;
    Handler handler = new Handler();
    long startTime = 0, elapsedTime = 0; boolean running = false;
    ArrayList<String> laps = new ArrayList<>(); ArrayAdapter<String> adapter;
    int lapCount = 0;

    Runnable updater = new Runnable() {
        @Override public void run() {
            if (running) {
                elapsedTime = System.currentTimeMillis() - startTime;
                tvTime.setText(formatTime(elapsedTime));
                handler.postDelayed(this, 10);
            }
        }
    };

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_stopwatch);
        tvTime   = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop  = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnLap   = findViewById(R.id.btnLap);
        btnBack  = findViewById(R.id.btnBack);
        lvLaps   = findViewById(R.id.lvLaps);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, laps);
        lvLaps.setAdapter(adapter);

        btnStart.setOnClickListener(v -> {
            if (!running) {
                startTime = System.currentTimeMillis() - elapsedTime;
                running = true; handler.post(updater);
                btnStart.setEnabled(false); btnStop.setEnabled(true); btnLap.setEnabled(true);
            }
        });

        btnStop.setOnClickListener(v -> {
            running = false;
            btnStart.setEnabled(true); btnStop.setEnabled(false); btnLap.setEnabled(false);
        });

        btnReset.setOnClickListener(v -> {
            running = false; elapsedTime = 0; lapCount = 0;
            tvTime.setText("00:00:00.00");
            laps.clear(); adapter.notifyDataSetChanged();
            btnStart.setEnabled(true); btnStop.setEnabled(false); btnLap.setEnabled(false);
        });

        btnLap.setOnClickListener(v -> {
            lapCount++;
            laps.add(0, "Lap " + lapCount + "  →  " + formatTime(elapsedTime));
            adapter.notifyDataSetChanged();
        });

        btnBack.setOnClickListener(v -> finish());
        btnStop.setEnabled(false); btnLap.setEnabled(false);
    }

    String formatTime(long ms) {
        long centisec = (ms % 1000) / 10;
        long seconds  = (ms / 1000) % 60;
        long minutes  = (ms / 60000) % 60;
        long hours    = ms / 3600000;
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, centisec);
    }

    @Override protected void onDestroy() { super.onDestroy(); running = false; handler.removeCallbacks(updater); }
}
