package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ModeChoiceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_choice);
        String action = getIntent().getStringExtra("action");
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(action.equals("LOGIN") ? "Login - Choose Mode" : "Register - Choose Mode");
        Button btnOffline = findViewById(R.id.btnOffline);
        Button btnOnline  = findViewById(R.id.btnOnline);
        Button btnBack    = findViewById(R.id.btnBack);
        btnOffline.setOnClickListener(v -> {
            Class<?> t = action.equals("LOGIN") ? OfflineLoginActivity.class : OfflineRegisterActivity.class;
            startActivity(new Intent(this,t));
        });
        btnOnline.setOnClickListener(v -> {
            Class<?> t = action.equals("LOGIN") ? OnlineLoginActivity.class : OnlineRegisterActivity.class;
            startActivity(new Intent(this,t));
        });
        btnBack.setOnClickListener(v -> finish());
    }
}
