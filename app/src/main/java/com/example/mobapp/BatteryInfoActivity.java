package com.example.mobapp;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class BatteryInfoActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_battery_info);
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v->finish());
        BatteryManager bm=(BatteryManager)getSystemService(BATTERY_SERVICE);
        int level=bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        boolean charging=bm.isCharging();
        ((TextView)findViewById(R.id.tvBattery)).setText(
            "Battery Level: "+level+"%\n\nCharging: "+(charging?"Yes ⚡":"No 🔋")
        );
    }
}
