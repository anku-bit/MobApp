package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class OfflineRegisterActivity extends AppCompatActivity {
    EditText etName,etEmail,etPhone,etAddress,etPassword;
    Button btnReg,btnBack; DBHelper db;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_offline_register);
        db=new DBHelper(this);
        etName=findViewById(R.id.etName); etEmail=findViewById(R.id.etEmail);
        etPhone=findViewById(R.id.etPhone); etAddress=findViewById(R.id.etAddress);
        etPassword=findViewById(R.id.etPassword);
        btnReg=findViewById(R.id.btnReg); btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v->finish());
        btnReg.setOnClickListener(v->register());
    }

    void register() {
        String name=etName.getText().toString().trim(),
               email=etEmail.getText().toString().trim(),
               phone=etPhone.getText().toString().trim(),
               address=etAddress.getText().toString().trim(),
               pass=etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Name, Email & Password required",Toast.LENGTH_SHORT).show(); return;
        }
        if(db.emailExists(email)){ Toast.makeText(this,"Email already registered",Toast.LENGTH_SHORT).show(); return; }
        if(db.registerUser(new User(name,email,phone,address,pass))){
            new SessionManager(this).saveSession(name,email,phone,address,"OFFLINE");
            Intent i=new Intent(this,DashboardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else Toast.makeText(this,"Registration failed",Toast.LENGTH_SHORT).show();
    }
}
