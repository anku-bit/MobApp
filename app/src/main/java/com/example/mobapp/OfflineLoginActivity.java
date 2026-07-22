package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class OfflineLoginActivity extends AppCompatActivity {
    EditText etEmail,etPassword; Button btnLogin,btnBack; DBHelper db;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_offline_login);
        db=new DBHelper(this);
        etEmail=findViewById(R.id.etEmail); etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnLogin); btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v->finish());
        btnLogin.setOnClickListener(v->login());
    }

    void login() {
        String email=etEmail.getText().toString().trim(),
               pass=etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Email & Password required",Toast.LENGTH_SHORT).show(); return;
        }
        User u=db.loginUser(email,pass);
        if(u!=null){
            new SessionManager(this).saveSession(u.name,u.email,u.phone,u.address,"OFFLINE");
            Intent i=new Intent(this,DashboardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else Toast.makeText(this,"Invalid credentials",Toast.LENGTH_SHORT).show();
    }
}
