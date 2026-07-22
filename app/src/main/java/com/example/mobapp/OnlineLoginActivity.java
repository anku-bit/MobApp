package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class OnlineLoginActivity extends AppCompatActivity {
    EditText etEmail,etPassword; Button btnLogin,btnBack; ProgressBar pb;
    FirebaseAuth auth; DatabaseReference db;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_online_login);
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance().getReference("users");
        etEmail=findViewById(R.id.etEmail); etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnLogin); btnBack=findViewById(R.id.btnBack);
        pb=findViewById(R.id.progressBar);
        btnBack.setOnClickListener(v->finish());
        btnLogin.setOnClickListener(v->login());
    }

    void login() {
        String email=etEmail.getText().toString().trim(),
               pass=etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Email & Password required",Toast.LENGTH_SHORT).show(); return;
        }
        pb.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task->{
            if(task.isSuccessful()){
                String uid=auth.getCurrentUser().getUid();
                db.child(uid).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override public void onDataChange(DataSnapshot snap){
                        pb.setVisibility(View.GONE);
                        User u=snap.getValue(User.class);
                        String name=u!=null?u.name:"User",
                               phone=u!=null?u.phone:"",
                               address=u!=null?u.address:"";
                        new SessionManager(OnlineLoginActivity.this).saveSession(name,email,phone,address,"ONLINE");
                        Intent i=new Intent(OnlineLoginActivity.this,DashboardActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                    @Override public void onCancelled(DatabaseError e){
                        pb.setVisibility(View.GONE);
                        Toast.makeText(OnlineLoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            } else { pb.setVisibility(View.GONE); Toast.makeText(this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_LONG).show(); }
        });
    }
}
