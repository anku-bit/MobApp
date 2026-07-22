package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            SessionManager sm = new SessionManager(this);
            Intent i;
            if (sm.isLoggedIn()) i = new Intent(this, DashboardActivity.class);
            else i = new Intent(this, AuthChoiceActivity.class);
            startActivity(i);
            finish();
        }, 2000);
    }
}
