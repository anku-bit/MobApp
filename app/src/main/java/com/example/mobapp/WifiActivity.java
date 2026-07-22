package com.example.mobapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WifiActivity extends AppCompatActivity {

    private TextView tvWifiInfo;

    private Button btnWifiOn;
    private Button btnWifiOff;
    private Button btnWifiSettings;
    private Button btnBack;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        // Find Views
        tvWifiInfo = findViewById(R.id.tvWifiInfo);

        btnWifiOn = findViewById(R.id.btnWifiOn);
        btnWifiOff = findViewById(R.id.btnWifiOff);
        btnWifiSettings = findViewById(R.id.btnWifiSettings);
        btnBack = findViewById(R.id.btnBack);

        // Get WiFi Manager
        wifiManager = (WifiManager)
                getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);

        // Show WiFi Information
        updateWifiInfo();


        // =================================
        // WIFI ON
        // =================================

        btnWifiOn.setOnClickListener(v -> {

            if (wifiManager.isWifiEnabled()) {

                Toast.makeText(
                        this,
                        "WiFi is already ON",
                        Toast.LENGTH_SHORT
                ).show();

                updateWifiInfo();

            } else {

                /*
                 * Android 10+ does not allow normal apps
                 * to directly enable WiFi.
                 *
                 * Open Internet/WiFi Panel.
                 */

                openWifiPanel();

                Toast.makeText(
                        this,
                        "Turn ON WiFi",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // =================================
        // WIFI OFF
        // =================================

        btnWifiOff.setOnClickListener(v -> {

            if (!wifiManager.isWifiEnabled()) {

                Toast.makeText(
                        this,
                        "WiFi is already OFF",
                        Toast.LENGTH_SHORT
                ).show();

                updateWifiInfo();

            } else {

                /*
                 * Open WiFi panel so user can
                 * turn WiFi OFF.
                 */

                openWifiPanel();

                Toast.makeText(
                        this,
                        "Turn OFF WiFi",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // =================================
        // WIFI SETTINGS
        // =================================

        btnWifiSettings.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Settings.ACTION_WIFI_SETTINGS);

            startActivity(intent);
        });


        // =================================
        // BACK
        // =================================

        btnBack.setOnClickListener(v -> finish());
    }


    // =================================
    // OPEN WIFI / INTERNET PANEL
    // =================================

    private void openWifiPanel() {

        try {

            if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.Q) {

                // Android 10+
                Intent panelIntent =
                        new Intent(
                                Settings.Panel.ACTION_WIFI
                        );

                startActivity(panelIntent);

            } else {

                // Android 9 and below
                Intent intent =
                        new Intent(
                                Settings.ACTION_WIFI_SETTINGS
                        );

                startActivity(intent);
            }

        } catch (Exception e) {

            // Fallback
            Intent intent =
                    new Intent(
                            Settings.ACTION_WIFI_SETTINGS
                    );

            startActivity(intent);
        }
    }


    // =================================
    // UPDATE WIFI INFORMATION
    // =================================

    private void updateWifiInfo() {

        if (wifiManager == null) {

            tvWifiInfo.setText(
                    "WiFi service not available"
            );

            return;
        }

        StringBuilder sb =
                new StringBuilder();

        boolean wifiEnabled =
                wifiManager.isWifiEnabled();

        if (wifiEnabled) {

            sb.append("📶 WiFi Status: ON\n\n");

            try {

                WifiInfo info =
                        wifiManager.getConnectionInfo();

                if (info != null) {

                    sb.append("SSID: ")
                            .append(
                                    info.getSSID()
                            )
                            .append("\n");

                    sb.append("Speed: ")
                            .append(
                                    info.getLinkSpeed()
                            )
                            .append(" Mbps\n");

                    sb.append("Signal: ")
                            .append(
                                    info.getRssi()
                            )
                            .append(" dBm\n");

                    int ip =
                            info.getIpAddress();

                    if (ip != 0) {

                        String ipAddress =
                                String.format(
                                        "%d.%d.%d.%d",

                                        ip & 0xff,

                                        (ip >> 8)
                                                & 0xff,

                                        (ip >> 16)
                                                & 0xff,

                                        (ip >> 24)
                                                & 0xff
                                );

                        sb.append("IP: ")
                                .append(
                                        ipAddress
                                )
                                .append("\n");

                    } else {

                        sb.append(
                                "IP: Not connected\n"
                        );
                    }

                } else {

                    sb.append(
                            "Not connected to a WiFi network"
                    );
                }
            }

            catch (SecurityException e) {

                sb.append(
                        "WiFi connection information unavailable"
                );
            }

        } else {

            sb.append(
                    "📵 WiFi Status: OFF\n\n"
            );

            sb.append(
                    "Turn ON WiFi to view network information."
            );
        }

        tvWifiInfo.setText(
                sb.toString()
        );
    }


    // =================================
    // REFRESH AFTER RETURNING
    // FROM WIFI SETTINGS
    // =================================

    @Override
    protected void onResume() {
        super.onResume();

        if (wifiManager != null) {

            updateWifiInfo();
        }
    }
}