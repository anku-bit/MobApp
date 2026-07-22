package com.example.mobapp;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class QuizResultActivity extends AppCompatActivity {
    SessionManager session; DBHelper db;
    int score, total;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_quiz_result);
        session=new SessionManager(this); db=new DBHelper(this);
        score=getIntent().getIntExtra("score",0);
        total=getIntent().getIntExtra("total",10);
        TextView tvScore=findViewById(R.id.tvFinalScore);
        TextView tvSave=findViewById(R.id.tvSaveStatus);
        ListView lv=findViewById(R.id.lvLeaderboard);
        Button btnRetry=findViewById(R.id.btnRetry);
        Button btnBack=findViewById(R.id.btnBack);

        tvScore.setText("Your Score: "+score+"/"+total);

        String grade = score>=8?"Excellent! 🏆":score>=6?"Good! 👍":score>=4?"Average 😊":"Keep Practicing 💪";
        ((TextView)findViewById(R.id.tvGrade)).setText(grade);

        btnRetry.setOnClickListener(v->{ startActivity(new Intent(this,QuizActivity.class)); finish(); });
        btnBack.setOnClickListener(v->{ startActivity(new Intent(this,DashboardActivity.class)); finish(); });

        if(session.getMode().equals("ONLINE")){
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("quiz_scores");
            Map<String,Object> data=new HashMap<>();
            data.put("name",session.getName()); data.put("email",session.getEmail());
            data.put("score",score); data.put("total",total);
            data.put("timestamp",System.currentTimeMillis());
            ref.push().setValue(data);
            tvSave.setText("Score synced to Firebase ✅");
            loadOnlineLeaderboard(lv);
        } else {
            db.saveQuizScore(session.getName(),session.getEmail(),score,total);
            tvSave.setText("Score saved locally ✅");
            loadOfflineLeaderboard(lv);
        }
    }

    void loadOnlineLeaderboard(ListView lv){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("quiz_scores");
        ref.orderByChild("score").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override public void onDataChange(DataSnapshot snap){
                List<String> list=new ArrayList<>();
                for(DataSnapshot child:snap.getChildren()){
                    String name=child.child("name").getValue(String.class);
                    Long sc=child.child("score").getValue(Long.class);
                    Long tot=child.child("total").getValue(Long.class);
                    if(name!=null) list.add(0, name+" — "+sc+"/"+tot);
                }
                lv.setAdapter(new ArrayAdapter<>(QuizResultActivity.this,android.R.layout.simple_list_item_1,list));
            }
            @Override public void onCancelled(DatabaseError e){}
        });
    }

    void loadOfflineLeaderboard(ListView lv){
        Cursor c=db.getTopScores(10);
        List<String> list=new ArrayList<>();
        while(c.moveToNext()){
            list.add(c.getString(c.getColumnIndexOrThrow("name"))+" — "+
                c.getInt(c.getColumnIndexOrThrow("score"))+"/"+
                c.getInt(c.getColumnIndexOrThrow("total")));
        }
        c.close();
        lv.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list));
    }
}
