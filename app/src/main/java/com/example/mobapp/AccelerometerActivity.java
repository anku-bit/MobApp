package com.example.mobapp;

import android.content.Context;
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

public class AccelerometerActivity extends AppCompatActivity
        implements SensorEventListener {

    // Sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor proximitySensor;

    // UI
    private TextView tvShakeStatus;
    private TextView tvTiltStatus;
    private TextView tvMovementStatus;
    private TextView tvMusicStatus;
    private Button btnBack;

    // Torch
    private CameraManager cameraManager;
    private String cameraId;
    private boolean torchOn = false;

    // Music
    private MediaPlayer mediaPlayer;
    private boolean musicOn = false;

    // Shake
    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final long SHAKE_COOLDOWN = 1200;
    private long lastShakeTime = 0;

    // Movement
    private static final float MOVEMENT_THRESHOLD = 1.5f;

    // Proximity
    private boolean wasNear = false;
    private long lastProximityTime = 0;
    private static final long PROXIMITY_COOLDOWN = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accelerometer);

        // Find Views
        tvShakeStatus = findViewById(R.id.tvShakeStatus);
        tvTiltStatus = findViewById(R.id.tvTiltStatus);
        tvMovementStatus = findViewById(R.id.tvMovementStatus);
        tvMusicStatus = findViewById(R.id.tvMusicStatus);
        btnBack = findViewById(R.id.btnBack);


        // =====================================
        // SENSOR MANAGER
        // =====================================

        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer =
                sensorManager.getDefaultSensor(
                        Sensor.TYPE_ACCELEROMETER
                );

        proximitySensor =
                sensorManager.getDefaultSensor(
                        Sensor.TYPE_PROXIMITY
                );


        // Accelerometer Check
        if (accelerometer == null) {

            tvShakeStatus.setText(
                    "❌ Accelerometer not available"
            );

            tvTiltStatus.setText(
                    "Tilt detection unavailable"
            );

            tvMovementStatus.setText(
                    "Movement detection unavailable"
            );
        }


        // Proximity Check
        if (proximitySensor == null) {

            tvMusicStatus.setText(
                    "❌ Proximity sensor not available"
            );

        } else {

            tvMusicStatus.setText(
                    "🎵 Music: OFF\n✋ Cover proximity sensor to play"
            );
        }


        // =====================================
        // TORCH SETUP
        // =====================================

        cameraManager =
                (CameraManager)
                        getSystemService(
                                Context.CAMERA_SERVICE
                        );

        findFlashCamera();


        // =====================================
        // MUSIC SETUP
        // res/raw/y.mp3
        // =====================================

        mediaPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.y
                );

        if (mediaPlayer != null) {

            // Repeat music
            mediaPlayer.setLooping(true);

        } else {

            tvMusicStatus.setText(
                    "❌ y.mp3 not found"
            );
        }


        // =====================================
        // BACK BUTTON
        // =====================================

        btnBack.setOnClickListener(
                v -> finish()
        );
    }


    // =====================================
    // FIND CAMERA WITH FLASH
    // =====================================

    private void findFlashCamera() {

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

                tvShakeStatus.setText(
                        "❌ Torch not available"
                );

            } else {

                tvShakeStatus.setText(
                        "🔦 Torch: OFF\n📱 Shake phone to turn ON"
                );
            }


        } catch (CameraAccessException e) {

            tvShakeStatus.setText(
                    "❌ Unable to access Torch"
            );
        }
    }


    // =====================================
    // SENSOR EVENT
    // =====================================

    @Override
    public void onSensorChanged(
            SensorEvent event
    ) {

        // =====================================
        // ACCELEROMETER
        // =====================================

        if (event.sensor.getType()
                == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            // Calculate acceleration
            float totalAcceleration =
                    (float) Math.sqrt(
                            x * x +
                                    y * y +
                                    z * z
                    );


            float movementForce =
                    Math.abs(
                            totalAcceleration -
                                    SensorManager.GRAVITY_EARTH
                    );


            // =====================================
            // SHAKE → TORCH ON/OFF
            // =====================================

            long currentTime =
                    SystemClock.elapsedRealtime();


            if (movementForce >
                    SHAKE_THRESHOLD) {

                if (currentTime -
                        lastShakeTime >
                        SHAKE_COOLDOWN) {

                    lastShakeTime =
                            currentTime;

                    toggleTorch();
                }
            }


            // =====================================
            // TILT DETECTION
            // =====================================

            String tilt;


            if (x > 4.0f) {

                tilt =
                        "⬅️ Tilt: LEFT";

            } else if (x < -4.0f) {

                tilt =
                        "➡️ Tilt: RIGHT";

            } else if (y > 4.0f &&
                    Math.abs(x) < 4.0f) {

                tilt =
                        "⬆️ Tilt: UP";

            } else if (y < -4.0f) {

                tilt =
                        "⬇️ Tilt: DOWN";

            } else {

                tilt =
                        "📱 Phone Straight";
            }


            tvTiltStatus.setText(
                    tilt
            );


            // =====================================
            // MOVEMENT DETECTION
            // =====================================

            if (movementForce >
                    MOVEMENT_THRESHOLD) {

                tvMovementStatus.setText(
                        "🏃 Movement: MOVING"
                );

            } else {

                tvMovementStatus.setText(
                        "🛑 Movement: STATIONARY"
                );
            }
        }


        // =====================================
        // PROXIMITY SENSOR → MUSIC
        // =====================================

        if (event.sensor.getType()
                == Sensor.TYPE_PROXIMITY) {

            float distance =
                    event.values[0];

            float maximumRange =
                    event.sensor
                            .getMaximumRange();


            // Hand near proximity sensor
            boolean isNear =
                    distance < maximumRange;


            /*
             * Toggle only when sensor goes
             * FAR → NEAR.
             *
             * This prevents music continuously
             * switching ON/OFF while your hand
             * remains over the sensor.
             */

            if (isNear && !wasNear) {

                long currentTime =
                        SystemClock.elapsedRealtime();


                if (currentTime -
                        lastProximityTime >
                        PROXIMITY_COOLDOWN) {

                    lastProximityTime =
                            currentTime;

                    toggleMusic();
                }
            }


            // Save current state
            wasNear = isNear;
        }
    }


    // =====================================
    // TORCH TOGGLE
    // =====================================

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

                tvShakeStatus.setText(
                        "🔦 Torch: ON\n📱 Shake again to turn OFF"
                );


                Toast.makeText(
                        this,
                        "Torch ON",
                        Toast.LENGTH_SHORT
                ).show();


            } else {

                tvShakeStatus.setText(
                        "🔦 Torch: OFF\n📱 Shake phone to turn ON"
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


    // =====================================
    // MUSIC TOGGLE USING PROXIMITY
    // =====================================

    private void toggleMusic() {

        if (mediaPlayer == null) {

            Toast.makeText(
                    this,
                    "Music file y.mp3 not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (musicOn) {

            // MUSIC OFF / PAUSE

            mediaPlayer.pause();

            musicOn = false;


            tvMusicStatus.setText(
                    "🎵 Music: OFF\n" +
                            "✋ Cover sensor again to play"
            );


            Toast.makeText(
                    this,
                    "Music OFF",
                    Toast.LENGTH_SHORT
            ).show();


        } else {

            // MUSIC ON / PLAY

            mediaPlayer.start();

            musicOn = true;


            tvMusicStatus.setText(
                    "🎵 Music: PLAYING\n" +
                            "✋ Cover sensor again to pause"
            );


            Toast.makeText(
                    this,
                    "Music ON",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =====================================
    // SENSOR ACCURACY
    // =====================================

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {

        // No action required
    }


    // =====================================
    // REGISTER SENSORS
    // =====================================

    @Override
    protected void onResume() {
        super.onResume();


        if (sensorManager != null) {


            // Accelerometer
            if (accelerometer != null) {

                sensorManager.registerListener(
                        this,
                        accelerometer,
                        SensorManager
                                .SENSOR_DELAY_NORMAL
                );
            }


            // Proximity
            if (proximitySensor != null) {

                sensorManager.registerListener(
                        this,
                        proximitySensor,
                        SensorManager
                                .SENSOR_DELAY_NORMAL
                );
            }
        }
    }


    // =====================================
    // UNREGISTER SENSORS
    // =====================================

    @Override
    protected void onPause() {
        super.onPause();


        if (sensorManager != null) {

            sensorManager
                    .unregisterListener(this);
        }
    }


    // =====================================
    // CLEANUP
    // =====================================

    @Override
    protected void onDestroy() {
        super.onDestroy();


        // Release Music
        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {

                mediaPlayer.stop();
            }


            mediaPlayer.release();

            mediaPlayer = null;
        }


        // Turn Torch OFF
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