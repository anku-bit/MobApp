package com.example.mobapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class CameraActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private Button btnCapture;
    private Button btnBack;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ivPhoto = findViewById(R.id.ivPhoto);
        btnCapture = findViewById(R.id.btnCapture);
        btnBack = findViewById(R.id.btnBack);

        // Camera result
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null
                            && result.getData().getExtras() != null) {

                        Object data =
                                result.getData()
                                        .getExtras()
                                        .get("data");

                        if (data instanceof Bitmap) {

                            Bitmap photo = (Bitmap) data;

                            ivPhoto.setImageBitmap(photo);

                            Toast.makeText(
                                    this,
                                    "Photo captured successfully",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } else {

                        Toast.makeText(
                                this,
                                "Photo capture cancelled",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        // Camera permission result
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {

                    if (isGranted) {

                        openCamera();

                    } else {

                        Toast.makeText(
                                this,
                                "Camera permission is required",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        // Open Camera Button
        btnCapture.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                permissionLauncher.launch(
                        Manifest.permission.CAMERA
                );
            }
        });

        // Back
        btnBack.setOnClickListener(v -> finish());
    }

    private void openCamera() {

        Intent cameraIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {

            cameraLauncher.launch(cameraIntent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open Camera",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}