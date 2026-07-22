package com.example.mobapp;
import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.media.*;
import android.net.Uri;
import android.os.*;
import android.telephony.SmsManager;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class EmergencySOSActivity extends AppCompatActivity {
    private static final int PERM_CODE=101;
    private static final String PREFS="sos_prefs", KEY_CONTACT="emergency_contact", KEY_NAME="contact_name";

    MaterialCardView cardSOS; MaterialButton btnCall,btnSMS,btnShareLocation,btnSettings;
    MaterialSwitch switchFlash,switchSiren;
    TextView txtLocation,txtContact;

    CameraManager cameraManager; String cameraId;
    MediaPlayer mediaPlayer;
    FusedLocationProviderClient fusedClient;
    double lastLat=0,lastLng=0; boolean locationFetched=false;
    boolean flashBlinking=false;
    Handler blinkHandler=new Handler();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_emergency_sosactivity);
        fusedClient=LocationServices.getFusedLocationProviderClient(this);
        initViews(); checkAndRequestPermissions(); initFlash(); initSiren(); fetchLocation(); setClickListeners();
    }

    void initViews(){
        cardSOS=findViewById(R.id.cardSOS);
        btnCall=findViewById(R.id.btnCall); btnSMS=findViewById(R.id.btnSMS);
        btnShareLocation=findViewById(R.id.btnShareLocation); btnSettings=findViewById(R.id.btnSettings);
        switchFlash=findViewById(R.id.switchFlash); switchSiren=findViewById(R.id.switchSiren);
        txtLocation=findViewById(R.id.txtLocation); txtContact=findViewById(R.id.txtContact);
        SharedPreferences prefs=getSharedPreferences(PREFS,MODE_PRIVATE);
        txtContact.setText(prefs.getString(KEY_NAME,"Emergency")+": "+prefs.getString(KEY_CONTACT,"+91XXXXXXXXXX"));
        MaterialButton btnRefresh=findViewById(R.id.btnRefreshLocation);
        MaterialButton btnEdit=findViewById(R.id.btnEditContact);
        if(btnRefresh!=null) btnRefresh.setOnClickListener(v->fetchLocation());
        if(btnEdit!=null) btnEdit.setOnClickListener(v->openSettings());
    }

    void initFlash(){
        try{ cameraManager=(CameraManager)getSystemService(CAMERA_SERVICE);
            if(cameraManager!=null) cameraId=cameraManager.getCameraIdList()[0];
        }catch(Exception e){ e.printStackTrace(); }
    }

    void initSiren(){
        try{
            int resId=getResources().getIdentifier("siren","raw",getPackageName());
            if(resId!=0){ mediaPlayer=MediaPlayer.create(this,resId); mediaPlayer.setLooping(true); }
        }catch(Exception e){ e.printStackTrace(); }
    }

    void fetchLocation(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            txtLocation.setText("Location permission denied"); return;
        }
        txtLocation.setText("Fetching location...");
        fusedClient.getLastLocation().addOnSuccessListener(loc->{
            if(loc!=null){
                lastLat=loc.getLatitude(); lastLng=loc.getLongitude(); locationFetched=true;
                txtLocation.setText(String.format("Lat: %.4f, Lng: %.4f",lastLat,lastLng));
            } else txtLocation.setText("GPS unavailable — turn ON GPS");
        });
    }

    void setClickListeners(){
        cardSOS.setOnClickListener(v->activateFullSOS());
        btnCall.setOnClickListener(v->makeCall());
        btnSMS.setOnClickListener(v->sendSMS());
        btnShareLocation.setOnClickListener(v->shareLocation());
        btnSettings.setOnClickListener(v->openSettings());
        switchFlash.setOnCheckedChangeListener((b,on)->{ if(on) startSOSBlink(); else{ stopBlinking(); flashOff(); } });
        switchSiren.setOnCheckedChangeListener((b,on)->{ if(on) startSiren(); else stopSiren(); });
    }

    void activateFullSOS(){
        Toast.makeText(this,"🚨 SOS ACTIVATED!",Toast.LENGTH_LONG).show();
        if(!switchFlash.isChecked()) switchFlash.setChecked(true);
        if(!switchSiren.isChecked()) switchSiren.setChecked(true);
        sendSMS();
        new Handler().postDelayed(this::makeCall,2000);
    }

    void makeCall(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Call permission not granted",Toast.LENGTH_SHORT).show(); return;
        }
        String contact=getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_CONTACT,"+91XXXXXXXXXX");
        try{ startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+contact))); }
        catch(Exception e){ Toast.makeText(this,"Call failed: "+e.getMessage(),Toast.LENGTH_SHORT).show(); }
    }

    void sendSMS(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"SMS permission not granted",Toast.LENGTH_SHORT).show(); return;
        }
        String contact=getSharedPreferences(PREFS,MODE_PRIVATE).getString(KEY_CONTACT,"+91XXXXXXXXXX");
        String msg=locationFetched?
            "🚨 SOS! I need help!\nLocation: https://maps.google.com/?q="+lastLat+","+lastLng+"\nPlease respond immediately!":
            "🚨 SOS! I need help! Location unavailable. Call me immediately!";
        try{
            SmsManager sm=SmsManager.getDefault();
            sm.sendMultipartTextMessage(contact,null,sm.divideMessage(msg),null,null);
            Toast.makeText(this,"✅ SOS SMS sent!",Toast.LENGTH_LONG).show();
        }catch(Exception e){ Toast.makeText(this,"SMS failed: "+e.getMessage(),Toast.LENGTH_SHORT).show(); }
    }

    void shareLocation(){
        if(!locationFetched){ fetchLocation(); Toast.makeText(this,"Fetching location...",Toast.LENGTH_SHORT).show(); return; }
        Intent i=new Intent(Intent.ACTION_SEND); i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,"🚨 My Emergency Location:\nhttps://maps.google.com/?q="+lastLat+","+lastLng);
        startActivity(Intent.createChooser(i,"Share Location via..."));
    }

    void openSettings(){
        EditText etName=new EditText(this); etName.setHint("Contact Name (e.g. Mom)");
        EditText etPhone=new EditText(this); etPhone.setHint("Phone (+91XXXXXXXXXX)");
        etPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        SharedPreferences prefs=getSharedPreferences(PREFS,MODE_PRIVATE);
        etName.setText(prefs.getString(KEY_NAME,"")); etPhone.setText(prefs.getString(KEY_CONTACT,""));
        LinearLayout layout=new LinearLayout(this); layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48,24,48,0); layout.addView(etName); layout.addView(etPhone);
        new AlertDialog.Builder(this).setTitle("⚙️ Emergency Settings").setView(layout)
            .setPositiveButton("Save",(d,w)->{
                String name=etName.getText().toString().trim(), phone=etPhone.getText().toString().trim();
                if(name.isEmpty()||phone.isEmpty()){ Toast.makeText(this,"Fill both fields",Toast.LENGTH_SHORT).show(); return; }
                prefs.edit().putString(KEY_NAME,name).putString(KEY_CONTACT,phone).apply();
                txtContact.setText(name+": "+phone);
                Toast.makeText(this,"✅ Saved!",Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel",null).show();
    }

    void startSOSBlink(){
        if(cameraId==null){ Toast.makeText(this,"No flash",Toast.LENGTH_SHORT).show(); return; }
        flashBlinking=true; blinkMorse();
    }

    void blinkMorse(){
        if(!flashBlinking) return;
        int[] pattern={200,200,200,200,200,600,600,200,600,200,600,600,200,200,200,200,200,2000};
        scheduleBlink(pattern,0);
    }

    void scheduleBlink(int[] p, int idx){
        if(!flashBlinking||idx>=p.length){ if(flashBlinking) blinkMorse(); return; }
        if(idx%2==0) flashOn(); else flashOff();
        blinkHandler.postDelayed(()->scheduleBlink(p,idx+1),p[idx]);
    }

    void stopBlinking(){ flashBlinking=false; blinkHandler.removeCallbacksAndMessages(null); flashOff(); }

    void flashOn(){
        try{ if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&cameraManager!=null) cameraManager.setTorchMode(cameraId,true); }
        catch(Exception e){ e.printStackTrace(); }
    }
    void flashOff(){
        try{ if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&cameraManager!=null) cameraManager.setTorchMode(cameraId,false); }
        catch(Exception e){ e.printStackTrace(); }
    }

    void startSiren(){
        if(mediaPlayer!=null){ mediaPlayer.start(); Toast.makeText(this,"🔊 Siren ON",Toast.LENGTH_SHORT).show(); return; }
        try{
            Uri alarm=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if(alarm==null) alarm=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mediaPlayer=MediaPlayer.create(this,alarm);
            if(mediaPlayer!=null){ mediaPlayer.setLooping(true); mediaPlayer.start(); Toast.makeText(this,"🔊 Siren ON",Toast.LENGTH_SHORT).show(); }
        }catch(Exception e){ Toast.makeText(this,"Siren error: "+e.getMessage(),Toast.LENGTH_SHORT).show(); }
    }

    void stopSiren(){ if(mediaPlayer!=null&&mediaPlayer.isPlaying()){ mediaPlayer.pause(); mediaPlayer.seekTo(0); } }

    void checkAndRequestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS,Manifest.permission.CAMERA
        },PERM_CODE);
    }

    @Override public void onRequestPermissionsResult(int code,String[] perms,int[] results){
        super.onRequestPermissionsResult(code,perms,results);
        if(code==PERM_CODE) fetchLocation();
    }

    @Override protected void onDestroy(){
        super.onDestroy(); stopSiren(); stopBlinking();
        if(mediaPlayer!=null){ mediaPlayer.release(); mediaPlayer=null; }
    }
}
