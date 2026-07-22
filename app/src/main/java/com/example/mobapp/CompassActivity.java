package com.example.mobapp;
import android.hardware.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sm; Sensor accelerometer, magnetometer;
    float[] gravity = new float[3]; float[] geomagnetic = new float[3];
    TextView tvDegree, tvDirection; ImageView ivCompass;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_compass);
        tvDegree    = findViewById(R.id.tvDegree);
        tvDirection = findViewById(R.id.tvDirection);
        ivCompass   = findViewById(R.id.ivCompass);
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        sm            = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer  = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override protected void onResume() {
        super.onResume();
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer,  SensorManager.SENSOR_DELAY_UI);
    }

    @Override protected void onPause() {
        super.onPause(); sm.unregisterListener(this);
    }

    @Override public void onSensorChanged(SensorEvent e) {
        if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = Arrays.copyOf(e.values, 3);
        if (e.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = Arrays.copyOf(e.values, 3);

        float[] R = new float[9]; float[] I = new float[9];
        if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            float degree = (float) Math.toDegrees(orientation[0]);
            if (degree < 0) degree += 360;
            tvDegree.setText(String.format("%.1f°", degree));
            tvDirection.setText(getDirection(degree));
            ivCompass.setRotation(-degree);
        }
    }

    String getDirection(float deg) {
        if (deg < 22.5  || deg >= 337.5) return "North ↑";
        if (deg < 67.5)  return "North-East ↗";
        if (deg < 112.5) return "East →";
        if (deg < 157.5) return "South-East ↘";
        if (deg < 202.5) return "South ↓";
        if (deg < 247.5) return "South-West ↙";
        if (deg < 292.5) return "West ←";
        return "North-West ↖";
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
