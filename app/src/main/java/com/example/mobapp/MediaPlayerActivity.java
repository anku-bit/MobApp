package com.example.mobapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MediaPlayerActivity extends AppCompatActivity {

    MediaPlayer mp;
    boolean playing = false;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_media_player);

        TextView tvSong = findViewById(R.id.tvSong);
        Button btnPlay = findViewById(R.id.btnPlayPause);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnBack = findViewById(R.id.btnBack);

        // Load y.mp3 from res/raw folder
        int resId = getResources().getIdentifier(
                "y",
                "raw",
                getPackageName()
        );

        if (resId != 0) {
            mp = MediaPlayer.create(this, resId);
            tvSong.setText("y.mp3");
        } else {
            tvSong.setText("Add y.mp3 in res/raw/");
        }

        // Play / Pause Button
        btnPlay.setOnClickListener(v -> {

            if (mp == null) {
                Toast.makeText(
                        this,
                        "No audio file found in res/raw/",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (playing) {
                mp.pause();
            } else {
                mp.start();
            }

            playing = !playing;

            btnPlay.setText(
                    playing ? "⏸ PAUSE" : "▶ PLAY"
            );
        });

        // Stop Button
        btnStop.setOnClickListener(v -> {
            if (mp != null) {
                mp.pause();
                mp.seekTo(0);
                playing = false;
                btnPlay.setText("▶ PLAY");
            }
        });

        // Back Button
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mp != null) {
            mp.release();
            mp = null;
        }
    }
}