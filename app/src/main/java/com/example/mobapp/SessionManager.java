package com.example.mobapp;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "mobapp_session";
    private SharedPreferences sp;
    public SessionManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
    public void saveSession(String name,String email,String phone,String address,String mode){
        sp.edit().putString("name",name).putString("email",email)
            .putString("phone",phone).putString("address",address)
            .putString("mode",mode).putBoolean("loggedIn",true).apply();
    }
    public String getName()    { return sp.getString("name",""); }
    public String getEmail()   { return sp.getString("email",""); }
    public String getPhone()   { return sp.getString("phone",""); }
    public String getAddress() { return sp.getString("address",""); }
    public String getMode()    { return sp.getString("mode","OFFLINE"); }
    public boolean isLoggedIn(){ return sp.getBoolean("loggedIn",false); }
    public void logout()       { sp.edit().clear().apply(); }
}
