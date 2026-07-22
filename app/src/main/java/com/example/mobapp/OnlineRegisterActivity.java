package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class OnlineRegisterActivity extends AppCompatActivity {
    EditText etName,etEmail,etPhone,etAddress,etPassword;
    Button btnReg,btnBack; ProgressBar pb;
    FirebaseAuth auth; DatabaseReference db;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_online_register);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");
        etName=findViewById(R.id.etName); etEmail=findViewById(R.id.etEmail);
        etPhone=findViewById(R.id.etPhone); etAddress=findViewById(R.id.etAddress);
        etPassword=findViewById(R.id.etPassword);
        btnReg=findViewById(R.id.btnReg); btnBack=findViewById(R.id.btnBack);
        pb=findViewById(R.id.progressBar);
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
        if(pass.length()<6){ Toast.makeText(this,"Password min 6 chars",Toast.LENGTH_SHORT).show(); return; }
        pb.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task->{
            pb.setVisibility(View.GONE);
            if(task.isSuccessful()){
                String uid=auth.getCurrentUser().getUid();
                db.child(uid).setValue(new User(name,email,phone,address,""));
                new SessionManager(this).saveSession(name,email,phone,address,"ONLINE");
                Toast.makeText(this,"Registered!",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(this,DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else Toast.makeText(this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
        });
    }
}
