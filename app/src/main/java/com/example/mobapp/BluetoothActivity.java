package com.example.mobapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSION = 101;
    private static final int REQUEST_ENABLE_BLUETOOTH = 102;

    private BluetoothAdapter bluetoothAdapter;

    private TextView tvStatus;
    private ListView lvDevices;

    private Button btnBluetoothOn;
    private Button btnBluetoothOff;
    private Button btnEnable;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // Find Views
        tvStatus = findViewById(R.id.tvBtStatus);
        lvDevices = findViewById(R.id.lvDevices);

        btnBluetoothOn = findViewById(R.id.btnBluetoothOn);
        btnBluetoothOff = findViewById(R.id.btnBluetoothOff);
        btnEnable = findViewById(R.id.btnEnable);
        btnBack = findViewById(R.id.btnBack);

        // Get Bluetooth Adapter
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        bluetoothAdapter = bluetoothManager.getAdapter();

        // Check Bluetooth Support
        if (bluetoothAdapter == null) {

            tvStatus.setText("Bluetooth not supported on this device");

            btnBluetoothOn.setEnabled(false);
            btnBluetoothOff.setEnabled(false);
            btnEnable.setEnabled(false);

            btnBack.setOnClickListener(v -> finish());

            return;
        }

        // Request Bluetooth Permission
        requestBluetoothPermission();

        // Update Bluetooth Status
        updateBluetoothStatus();


        // ============================
        // BLUETOOTH ON
        // ============================

        btnBluetoothOn.setOnClickListener(v -> {

            if (!hasBluetoothPermission()) {
                requestBluetoothPermission();
                return;
            }

            if (bluetoothAdapter.isEnabled()) {

                Toast.makeText(
                        this,
                        "Bluetooth is already ON",
                        Toast.LENGTH_SHORT
                ).show();

                updateBluetoothStatus();

            } else {

                // Open Android Bluetooth enable dialog
                Intent enableBtIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                try {

                    startActivityForResult(
                            enableBtIntent,
                            REQUEST_ENABLE_BLUETOOTH
                    );

                } catch (SecurityException e) {

                    Toast.makeText(
                            this,
                            "Bluetooth permission required",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });


        // ============================
        // BLUETOOTH OFF
        // ============================

        btnBluetoothOff.setOnClickListener(v -> {

            if (!hasBluetoothPermission()) {
                requestBluetoothPermission();
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {

                Toast.makeText(
                        this,
                        "Bluetooth is already OFF",
                        Toast.LENGTH_SHORT
                ).show();

                updateBluetoothStatus();

            } else {

                /*
                 * Modern Android versions do not allow
                 * normal third-party apps to directly
                 * disable Bluetooth.
                 *
                 * Open Bluetooth Settings so user can
                 * turn it OFF manually.
                 */

                Toast.makeText(
                        this,
                        "Turn OFF Bluetooth from Settings",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent =
                        new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);

                startActivity(intent);
            }
        });


        // ============================
        // SHOW PAIRED DEVICES
        // ============================

        btnEnable.setOnClickListener(v -> {

            if (!hasBluetoothPermission()) {
                requestBluetoothPermission();
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {

                Toast.makeText(
                        this,
                        "Please turn ON Bluetooth first",
                        Toast.LENGTH_SHORT
                ).show();

                updateBluetoothStatus();

                return;
            }

            try {

                Set<BluetoothDevice> pairedDevices =
                        bluetoothAdapter.getBondedDevices();

                List<String> deviceList =
                        new ArrayList<>();

                for (BluetoothDevice device : pairedDevices) {

                    String deviceName = device.getName();

                    if (deviceName == null) {
                        deviceName = "Unknown Device";
                    }

                    deviceList.add(
                            deviceName +
                                    "\n" +
                                    device.getAddress()
                    );
                }

                if (deviceList.isEmpty()) {

                    deviceList.add(
                            "No paired devices found"
                    );
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                deviceList
                        );

                lvDevices.setAdapter(adapter);

                tvStatus.setText(
                        "Bluetooth ON - Found " +
                                pairedDevices.size() +
                                " paired device(s)"
                );

            } catch (SecurityException e) {

                Toast.makeText(
                        this,
                        "Bluetooth permission required",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // ============================
        // BACK BUTTON
        // ============================

        btnBack.setOnClickListener(v -> finish());
    }


    // Check Bluetooth Permission
    private boolean hasBluetoothPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            return ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }


    // Request Bluetooth Permission
    private void requestBluetoothPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!hasBluetoothPermission()) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT
                        },
                        REQUEST_BLUETOOTH_PERMISSION
                );
            }
        }
    }


    // Update Bluetooth Status
    private void updateBluetoothStatus() {

        if (!hasBluetoothPermission()) {

            tvStatus.setText(
                    "Bluetooth permission required"
            );

            return;
        }

        try {

            if (bluetoothAdapter.isEnabled()) {

                tvStatus.setText(
                        "Bluetooth is ON"
                );

            } else {

                tvStatus.setText(
                        "Bluetooth is OFF"
                );
            }

        } catch (SecurityException e) {

            tvStatus.setText(
                    "Bluetooth permission required"
            );
        }
    }


    // Update status when returning from Settings
    @Override
    protected void onResume() {
        super.onResume();

        if (bluetoothAdapter != null) {
            updateBluetoothStatus();
        }
    }


    // Permission Result
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {

            if (grantResults.length > 0 &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {

                updateBluetoothStatus();

            } else {

                Toast.makeText(
                        this,
                        "Bluetooth permission denied",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}