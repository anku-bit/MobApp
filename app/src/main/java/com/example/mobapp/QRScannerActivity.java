package com.example.mobapp;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {
    TextView tvResult, tvType;
    Button btnScan, btnCopy, btnOpen, btnBack;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_qr_scanner);
        tvResult = findViewById(R.id.tvResult);
        tvType   = findViewById(R.id.tvType);
        btnScan  = findViewById(R.id.btnScan);
        btnCopy  = findViewById(R.id.btnCopy);
        btnOpen  = findViewById(R.id.btnOpen);
        btnBack  = findViewById(R.id.btnBack);

        btnScan.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan a QR Code or Barcode");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        });

        btnCopy.setOnClickListener(v -> {
            String text = tvResult.getText().toString();
            if (!text.isEmpty() && !text.equals("Scan result will appear here")) {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("QR Result", text));
                Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        btnOpen.setOnClickListener(v -> {
            String text = tvResult.getText().toString();
            if (text.startsWith("http://") || text.startsWith("https://")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(text)));
            } else {
                Toast.makeText(this, "Not a URL", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override protected void onActivityResult(int req, int res, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(req, res, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                tvResult.setText(result.getContents());
                tvType.setText("Format: " + result.getFormatName());
                btnOpen.setVisibility(result.getContents().startsWith("http") ?
                    android.view.View.VISIBLE : android.view.View.GONE);
            }
        } else {
            super.onActivityResult(req, res, data);
        }
    }
}
