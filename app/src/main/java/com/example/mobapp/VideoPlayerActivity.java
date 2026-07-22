package com.example.mobapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_video_player);

        VideoView vv = findViewById(R.id.videoView);
        Button btnPlay = findViewById(R.id.btnPlayVideo);
        Button btnBack = findViewById(R.id.btnBack);

        // Add MediaController for Play, Pause, Seek
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(vv);
        vv.setMediaController(mediaController);

        // Play Video Button
        btnPlay.setOnClickListener(v -> {

            // Load video.mp4 from res/raw
            int resId = getResources().getIdentifier(
                    "video",
                    "raw",
                    getPackageName()
            );

            if (resId != 0) {

                Uri videoUri = Uri.parse(
                        "android.resource://" +
                                getPackageName() +
                                "/" +
                                resId
                );

                vv.setVideoURI(videoUri);
                vv.requestFocus();
                vv.start();

            } else {

                Toast.makeText(
                        this,
                        "Add video.mp4 in res/raw/",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        // Back Button
        btnBack.setOnClickListener(v -> finish());
    }
}