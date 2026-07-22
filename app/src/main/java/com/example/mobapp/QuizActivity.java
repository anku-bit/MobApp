package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class QuizActivity extends AppCompatActivity {
    List<Question> questions = new ArrayList<>();
    int currentIndex = 0, score = 0;
    TextView tvQNo, tvScore, tvQuestion;
    Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    ProgressBar pbQuiz;
    boolean answered = false;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_quiz);
        tvQNo     = findViewById(R.id.tvQNo);
        tvScore   = findViewById(R.id.tvScore);
        tvQuestion= findViewById(R.id.tvQuestion);
        btnOpt1   = findViewById(R.id.btnOpt1);
        btnOpt2   = findViewById(R.id.btnOpt2);
        btnOpt3   = findViewById(R.id.btnOpt3);
        btnOpt4   = findViewById(R.id.btnOpt4);
        pbQuiz    = findViewById(R.id.pbQuiz);
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        loadQuestions();
        showQuestion();
    }

    void loadQuestions() {
        questions.add(new Question("Which language is officially recommended for Android development by Google?",
            new String[]{"Swift","Kotlin","Python","Ruby"}, 1));
        questions.add(new Question("Firebase Realtime Database stores data in which format?",
            new String[]{"XML","CSV","JSON tree","Binary"}, 2));
        questions.add(new Question("Which Android component manages a single screen with a user interface?",
            new String[]{"Service","Activity","BroadcastReceiver","ContentProvider"}, 1));
        questions.add(new Question("SQLite in Android is used for:",
            new String[]{"Online data sync","Cloud storage","Local device storage","Push notifications"}, 2));
        questions.add(new Question("Which sensor detects device acceleration and tilt?",
            new String[]{"Gyroscope","Microphone","Accelerometer","Barometer"}, 2));
        questions.add(new Question("What does APK stand for?",
            new String[]{"Android Package Kit","Application Package Key","Android Platform Kit","App Package Kernel"}, 0));
        questions.add(new Question("Which class is used to play audio files in Android?",
            new String[]{"AudioManager","SoundPlayer","MediaPlayer","MusicPlayer"}, 2));
        questions.add(new Question("What is the minimum SDK version required for this MobApp project?",
            new String[]{"API 16","API 21","API 23","API 26"}, 2));
        questions.add(new Question("Firebase Authentication supports which login method?",
            new String[]{"Email/Password only","Google only","Phone OTP only","All of the above"}, 3));
        questions.add(new Question("SharedPreferences in Android stores data as:",
            new String[]{"JSON objects","Key-Value pairs","SQL tables","Binary files"}, 1));
        questions.add(new Question("Which method is used to toggle the device flashlight in Android?",
            new String[]{"setFlashMode()","toggleLight()","setTorchMode()","enableFlash()"}, 2));
        questions.add(new Question("What is MVVM in Android development?",
            new String[]{"Model View ViewModel","Multiple Virtual Machine View","Mobile Video Media Version","None of these"}, 0));
        questions.add(new Question("Which layout allows children to overlap each other?",
            new String[]{"LinearLayout","RelativeLayout","FrameLayout","GridLayout"}, 2));
        questions.add(new Question("What is the purpose of AndroidManifest.xml?",
            new String[]{"Store app data","Define app UI","Declare app components and permissions","Configure database"}, 2));
        questions.add(new Question("Which class helps detect nearby Bluetooth devices?",
            new String[]{"WifiManager","BluetoothAdapter","NetworkManager","ConnectivityManager"}, 1));
        questions.add(new Question("Room Database in Android is built on top of:",
            new String[]{"Firebase","SQLite","MySQL","MongoDB"}, 1));
        questions.add(new Question("What does the onPause() lifecycle method indicate?",
            new String[]{"App is running","Activity is partially hidden","App is closed","App is starting"}, 1));
        questions.add(new Question("Which permission is required to send SMS in Android?",
            new String[]{"SEND_MESSAGE","SMS_PERMISSION","SEND_SMS","ACCESS_SMS"}, 2));
        questions.add(new Question("What is a Fragment in Android?",
            new String[]{"A small piece of data","A reusable portion of UI","A type of Service","A database component"}, 1));
        questions.add(new Question("Which Google API provides real-time location updates efficiently?",
            new String[]{"LocationManager","GPSProvider","FusedLocationProviderClient","NetworkProvider"}, 2));
        Collections.shuffle(questions);
    }

    void showQuestion() {
        if (currentIndex >= questions.size()) { finishQuiz(); return; }
        answered = false;
        Question q = questions.get(currentIndex);
        pbQuiz.setMax(questions.size());
        pbQuiz.setProgress(currentIndex + 1);
        tvQNo.setText("Question " + (currentIndex+1) + "/" + questions.size());
        tvScore.setText("Score: " + score);
        tvQuestion.setText(q.question);
        btnOpt1.setText(q.options[0]); btnOpt2.setText(q.options[1]);
        btnOpt3.setText(q.options[2]); btnOpt4.setText(q.options[3]);
        resetColors();

        View.OnClickListener l = v -> {
            if (answered) return;
            answered = true;
            int sel = v==btnOpt1?0:v==btnOpt2?1:v==btnOpt3?2:3;
            highlightAnswer(sel, q.correctIndex);
            if (sel == q.correctIndex) score++;
            currentIndex++;
            new android.os.Handler().postDelayed(this::showQuestion, 900);
        };
        btnOpt1.setOnClickListener(l); btnOpt2.setOnClickListener(l);
        btnOpt3.setOnClickListener(l); btnOpt4.setOnClickListener(l);
    }

    void highlightAnswer(int sel, int correct) {
        Button[] btns = {btnOpt1,btnOpt2,btnOpt3,btnOpt4};
        for (int i=0;i<4;i++) {
            if (i==correct) btns[i].setBackgroundColor(0xFF27AE60);
            else if (i==sel) btns[i].setBackgroundColor(0xFFE74C3C);
        }
    }

    void resetColors() {
        int c = 0xFFC9B6F8;
        btnOpt1.setBackgroundColor(c); btnOpt2.setBackgroundColor(c);
        btnOpt3.setBackgroundColor(c); btnOpt4.setBackgroundColor(c);
    }

    void finishQuiz() {
        Intent i = new Intent(this, QuizResultActivity.class);
        i.putExtra("score",score); i.putExtra("total",questions.size());
        startActivity(i); finish();
    }
}
