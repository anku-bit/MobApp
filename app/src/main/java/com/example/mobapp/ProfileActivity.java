package com.example.mobapp;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_profile);
        SessionManager sm=new SessionManager(this);
        ((TextView)findViewById(R.id.tvName)).setText("Name: "+sm.getName());
        ((TextView)findViewById(R.id.tvEmail)).setText("Email: "+sm.getEmail());
        ((TextView)findViewById(R.id.tvPhone)).setText("Phone: "+sm.getPhone());
        ((TextView)findViewById(R.id.tvAddress)).setText("Address: "+sm.getAddress());
        ((TextView)findViewById(R.id.tvMode)).setText("Account Type: "+sm.getMode());
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v->finish());
    }
}
