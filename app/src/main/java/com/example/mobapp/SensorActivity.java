package com.example.mobapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SensorActivity extends AppCompatActivity
        implements SensorEventListener {

    // Sensor Manager
    private SensorManager sensorManager;

    // Sensors
    private Sensor accelerometer;
    private Sensor proximitySensor;

    // UI
    private TextView tvSensors;
    private TextView tvSensorStatus;
    private Button btnBack;

    // Torch
    private CameraManager cameraManager;
    private String cameraId;
    private boolean torchOn = false;

    // Music
    private MediaPlayer mediaPlayer;
    private boolean musicOn = false;

    // Shake Detection
    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final long SHAKE_COOLDOWN = 1000;
    private long lastShakeTime = 0;

    // Proximity Detection
    private boolean wasNear = false;
    private long lastProximityTime = 0;
    private static final long PROXIMITY_COOLDOWN = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor);

        // ==============================
        // FIND VIEWS
        // ==============================

        tvSensors = findViewById(R.id.tvSensors);
        tvSensorStatus = findViewById(R.id.tvSensorStatus);
        btnBack = findViewById(R.id.btnBack);

        tvSensorStatus.setText(
                "Shake Phone → Torch ON/OFF\n" +
                        "Cover Proximity Sensor → Music ON/OFF"
        );


        // ==============================
        // SENSOR MANAGER
        // ==============================

        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        // Accelerometer for Shake Detection
        accelerometer =
                sensorManager.getDefaultSensor(
                        Sensor.TYPE_ACCELEROMETER
                );

        // Proximity Sensor
        proximitySensor =
                sensorManager.getDefaultSensor(
                        Sensor.TYPE_PROXIMITY
                );


        // ==============================
        // DISPLAY ALL DEVICE SENSORS
        // ==============================

        List<Sensor> sensors =
                sensorManager.getSensorList(
                        Sensor.TYPE_ALL
                );

        StringBuilder sensorList =
                new StringBuilder();

        for (Sensor sensor : sensors) {

            sensorList
                    .append("• ")
                    .append(sensor.getName())
                    .append("\n");
        }

        tvSensors.setText(sensorList.toString());


        // ==============================
        // CHECK REQUIRED SENSORS
        // ==============================

        if (accelerometer == null) {

            Toast.makeText(
                    this,
                    "Accelerometer sensor not available",
                    Toast.LENGTH_LONG
            ).show();
        }

        if (proximitySensor == null) {

            Toast.makeText(
                    this,
                    "Proximity sensor not available",
                    Toast.LENGTH_LONG
            ).show();
        }


        // ==============================
        // SETUP TORCH
        // ==============================

        cameraManager =
                (CameraManager)
                        getSystemService(
                                Context.CAMERA_SERVICE
                        );

        setupTorchCamera();


        // ==============================
        // SETUP MUSIC
        // res/raw/y.mp3
        // ==============================

        mediaPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.y
                );

        if (mediaPlayer != null) {

            // Music automatically restart
            // when it finishes
            mediaPlayer.setLooping(true);
        }


        // ==============================
        // BACK BUTTON
        // ==============================

        btnBack.setOnClickListener(
                v -> finish()
        );
    }


    // ==================================
    // FIND CAMERA WITH FLASH
    // ==================================

    private void setupTorchCamera() {

        try {

            String[] cameraIds =
                    cameraManager.getCameraIdList();

            for (String id : cameraIds) {

                CameraCharacteristics characteristics =
                        cameraManager
                                .getCameraCharacteristics(id);

                Boolean flashAvailable =
                        characteristics.get(
                                CameraCharacteristics
                                        .FLASH_INFO_AVAILABLE
                        );

                if (Boolean.TRUE.equals(
                        flashAvailable
                )) {

                    cameraId = id;

                    break;
                }
            }

            if (cameraId == null) {

                Toast.makeText(
                        this,
                        "Torch not available on this device",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (CameraAccessException e) {

            Toast.makeText(
                    this,
                    "Unable to access camera",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ==================================
    // SENSOR VALUE CHANGED
    // ==================================

    @Override
    public void onSensorChanged(
            SensorEvent event
    ) {

        // ==============================
        // SHAKE DETECTION
        // ==============================

        if (event.sensor.getType()
                == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate acceleration
            // excluding normal gravity

            float acceleration =
                    (float) Math.sqrt(
                            x * x +
                                    y * y +
                                    z * z
                    );

            float shakeForce =
                    Math.abs(
                            acceleration -
                                    SensorManager.GRAVITY_EARTH
                    );

            long currentTime =
                    SystemClock.elapsedRealtime();

            // Detect strong shake
            if (shakeForce
                    > SHAKE_THRESHOLD) {

                if (currentTime -
                        lastShakeTime
                        > SHAKE_COOLDOWN) {

                    lastShakeTime =
                            currentTime;

                    toggleTorch();
                }
            }
        }


        // ==============================
        // PROXIMITY SENSOR
        // ==============================

        if (event.sensor.getType()
                == Sensor.TYPE_PROXIMITY) {

            float distance =
                    event.values[0];

            float maxRange =
                    event.sensor
                            .getMaximumRange();

            boolean isNear =
                    distance < maxRange;

            /*
             * Music toggle only when
             * sensor changes from
             * FAR -> NEAR.
             *
             * This prevents repeated
             * music ON/OFF while hand
             * stays over sensor.
             */

            if (isNear && !wasNear) {

                long currentTime =
                        SystemClock
                                .elapsedRealtime();

                if (currentTime -
                        lastProximityTime
                        > PROXIMITY_COOLDOWN) {

                    lastProximityTime =
                            currentTime;

                    toggleMusic();
                }
            }

            wasNear = isNear;
        }
    }


    // ==================================
    // TORCH TOGGLE
    // ==================================

    private void toggleTorch() {

        if (cameraManager == null ||
                cameraId == null) {

            Toast.makeText(
                    this,
                    "Torch not available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            torchOn = !torchOn;

            cameraManager.setTorchMode(
                    cameraId,
                    torchOn
            );

            if (torchOn) {

                tvSensorStatus.setText(
                        "🔦 Torch ON\n" +
                                "Shake again to turn OFF"
                );

                Toast.makeText(
                        this,
                        "Torch ON",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                tvSensorStatus.setText(
                        "🔦 Torch OFF\n" +
                                "Shake phone to turn ON"
                );

                Toast.makeText(
                        this,
                        "Torch OFF",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (CameraAccessException |
                 SecurityException e) {

            Toast.makeText(
                    this,
                    "Unable to control Torch",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ==================================
    // MUSIC TOGGLE
    // ==================================

    private void toggleMusic() {

        if (mediaPlayer == null) {

            Toast.makeText(
                    this,
                    "Music file not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (musicOn) {

            // Pause Music
            mediaPlayer.pause();

            musicOn = false;

            tvSensorStatus.setText(
                    "🎵 Music OFF\n" +
                            "Cover proximity sensor to play"
            );

            Toast.makeText(
                    this,
                    "Music OFF",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // Play Music
            mediaPlayer.start();

            musicOn = true;

            tvSensorStatus.setText(
                    "🎵 Music ON\n" +
                            "Cover proximity sensor again to pause"
            );

            Toast.makeText(
                    this,
                    "Music ON",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // ==================================
    // SENSOR ACCURACY
    // ==================================

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {

        // No action required
    }


    // ==================================
    // REGISTER SENSORS
    // ==================================

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager != null) {

            if (accelerometer != null) {

                sensorManager
                        .registerListener(
                                this,
                                accelerometer,
                                SensorManager
                                        .SENSOR_DELAY_NORMAL
                        );
            }

            if (proximitySensor != null) {

                sensorManager
                        .registerListener(
                                this,
                                proximitySensor,
                                SensorManager
                                        .SENSOR_DELAY_NORMAL
                        );
            }
        }
    }


    // ==================================
    // UNREGISTER SENSORS
    // ==================================

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {

            sensorManager
                    .unregisterListener(this);
        }
    }


    // ==================================
    // RELEASE RESOURCES
    // ==================================

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop Music
        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.stop();
            }

            mediaPlayer.release();

            mediaPlayer = null;
        }


        // Turn OFF Torch
        if (torchOn &&
                cameraManager != null &&
                cameraId != null) {

            try {

                cameraManager.setTorchMode(
                        cameraId,
                        false
                );

            } catch (CameraAccessException |
                     SecurityException e) {

                e.printStackTrace();
            }
        }
    }
}