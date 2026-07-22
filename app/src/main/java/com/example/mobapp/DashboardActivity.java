package com.example.mobapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    SessionManager session;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_dashboard);
        session = new SessionManager(this);

        ((TextView)findViewById(R.id.tvWelcome)).setText("Welcome, " + session.getName() + "!");
        ((TextView)findViewById(R.id.tvMode)).setText("Mode: " + session.getMode());

        // Original Features
        bind(R.id.btnProfile,       ProfileActivity.class);
        bind(R.id.btnCalc,          CalculatorActivity.class);
        bind(R.id.btnSciCalc,       ScientificCalcActivity.class);
        bind(R.id.btnTorch,         TorchActivity.class);
        bind(R.id.btnAccel,         AccelerometerActivity.class);
        bind(R.id.btnSensor,        SensorActivity.class);
        bind(R.id.btnBluetooth,     BluetoothActivity.class);
        bind(R.id.btnWifi,          WifiActivity.class);
        bind(R.id.btnCamera,        CameraActivity.class);
        bind(R.id.btnMedia,         MediaPlayerActivity.class);
        bind(R.id.btnVideo,         VideoPlayerActivity.class);
        bind(R.id.btnNotes,         NotesActivity.class);
        bind(R.id.btnBattery,       BatteryInfoActivity.class);
        bind(R.id.btnQuiz,          QuizActivity.class);
        bind(R.id.btnEmergencySOS,  EmergencySOSActivity.class);

        // New Features
        bind(R.id.btnQRScanner,     QRScannerActivity.class);
        bind(R.id.btnCompass,       CompassActivity.class);
        bind(R.id.btnBMI,           BMIActivity.class);
        bind(R.id.btnUnitConverter, UnitConverterActivity.class);
        bind(R.id.btnStopwatch,     StopwatchActivity.class);
        bind(R.id.btnSystemInfo,    SystemInfoActivity.class);
        bind(R.id.btnAgeCalc,       AgeCalculatorActivity.class);

        // Logout
        ((Button)findViewById(R.id.btnLogout)).setOnClickListener(v -> {
            if (session.getMode().equals("ONLINE")) FirebaseAuth.getInstance().signOut();
            session.logout();
            Toast.makeText(this,"Logged out",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, AuthChoiceActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }

    void bind(int id, Class<?> target) {
        Button b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> startActivity(new Intent(this, target)));
    }
}
