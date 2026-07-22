package com.example.mobapp;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TorchActivity extends AppCompatActivity {
    boolean isOn=false; CameraManager cm; String cameraId;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_torch);
        TextView tvStatus=findViewById(R.id.tvStatus);
        Button btnToggle=findViewById(R.id.btnToggle);
        Button btnBack=findViewById(R.id.btnBack);
        try { cm=(CameraManager)getSystemService(CAMERA_SERVICE); cameraId=cm.getCameraIdList()[0]; }
        catch(Exception e){ Toast.makeText(this,"No flash",Toast.LENGTH_SHORT).show(); }
        btnToggle.setOnClickListener(v->{
            try { isOn=!isOn; cm.setTorchMode(cameraId,isOn); tvStatus.setText("Torch is "+(isOn?"ON":"OFF")); }
            catch(Exception e){ Toast.makeText(this,"Flash error",Toast.LENGTH_SHORT).show(); }
        });
        btnBack.setOnClickListener(v->finish());
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        if(isOn) try{ cm.setTorchMode(cameraId,false); }catch(Exception ignored){}
    }
}
