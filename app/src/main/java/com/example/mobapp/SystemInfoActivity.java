package com.example.mobapp;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class SystemInfoActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_system_info);
        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        TextView tvInfo = findViewById(R.id.tvInfo);

        StringBuilder sb = new StringBuilder();
        sb.append("📱 DEVICE INFORMATION\n\n");
        sb.append("Brand: ").append(Build.BRAND).append("\n");
        sb.append("Model: ").append(Build.MODEL).append("\n");
        sb.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        sb.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Device: ").append(Build.DEVICE).append("\n");
        sb.append("Board: ").append(Build.BOARD).append("\n");
        sb.append("Hardware: ").append(Build.HARDWARE).append("\n\n");

        sb.append("📦 APP INFORMATION\n\n");
        try {
            sb.append("Package: ").append(getPackageName()).append("\n");
            sb.append("Version: ").append(getPackageManager()
                .getPackageInfo(getPackageName(),0).versionName).append("\n\n");
        } catch (PackageManager.NameNotFoundException ignored) {}

        sb.append("💾 MEMORY\n\n");
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        sb.append("Total RAM: ").append(mi.totalMem / (1024*1024)).append(" MB\n");
        sb.append("Available RAM: ").append(mi.availMem / (1024*1024)).append(" MB\n");
        sb.append("Low Memory: ").append(mi.lowMemory).append("\n\n");

        sb.append("💿 STORAGE\n\n");
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize    = stat.getBlockSizeLong();
        long totalBlocks  = stat.getBlockCountLong();
        long availBlocks  = stat.getAvailableBlocksLong();
        sb.append("Total Storage: ").append((totalBlocks*blockSize)/(1024*1024*1024)).append(" GB\n");
        sb.append("Free Storage: ").append((availBlocks*blockSize)/(1024*1024*1024)).append(" GB\n\n");

        sb.append("🔲 DISPLAY\n\n");
        android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
        sb.append("Screen Width: ").append(dm.widthPixels).append(" px\n");
        sb.append("Screen Height: ").append(dm.heightPixels).append(" px\n");
        sb.append("DPI: ").append(dm.densityDpi).append("\n");
        sb.append("Density: ").append(dm.density).append("x\n\n");

        sb.append("⚙️ PROCESSOR\n\n");
        sb.append("CPU Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        try {
            java.lang.Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Hardware") || line.contains("Processor")) {
                    sb.append(line).append("\n");
                }
            }
        } catch (Exception ignored) {}

        tvInfo.setText(sb.toString());
    }
}
