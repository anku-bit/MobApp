package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AuthChoiceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_choice);
        Button btnLogin    = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(this, ModeChoiceActivity.class);
            i.putExtra("action","LOGIN"); startActivity(i);
        });
        btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(this, ModeChoiceActivity.class);
            i.putExtra("action","REGISTER"); startActivity(i);
        });
    }
}
