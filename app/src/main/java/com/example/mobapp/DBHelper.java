package com.example.mobapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mobapp.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,email TEXT UNIQUE,phone TEXT,address TEXT,password TEXT)");
        db.execSQL("CREATE TABLE notes(id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT,note TEXT,created_at INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE quiz_scores(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,email TEXT,score INTEGER,total INTEGER,ts INTEGER)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int o, int n) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS quiz_scores");
        onCreate(db);
    }

    public boolean registerUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("name",u.name); cv.put("email",u.email); cv.put("phone",u.phone);
        cv.put("address",u.address); cv.put("password",u.password);
        return getWritableDatabase().insert("users",null,cv) != -1;
    }

    public User loginUser(String email, String password) {
        Cursor c = getReadableDatabase().query("users",null,"email=? AND password=?",
            new String[]{email,password},null,null,null);
        User u = null;
        if (c.moveToFirst()) u = new User(
            c.getString(c.getColumnIndexOrThrow("name")),
            c.getString(c.getColumnIndexOrThrow("email")),
            c.getString(c.getColumnIndexOrThrow("phone")),
            c.getString(c.getColumnIndexOrThrow("address")),password);
        c.close(); return u;
    }

    public boolean emailExists(String email) {
        Cursor c = getReadableDatabase().query("users",null,"email=?",new String[]{email},null,null,null);
        boolean ex = c.getCount()>0; c.close(); return ex;
    }

    public void addNote(String email, String note) {
        ContentValues cv = new ContentValues();
        cv.put("email",email); cv.put("note",note);
        cv.put("created_at", System.currentTimeMillis());
        getWritableDatabase().insert("notes",null,cv);
    }

    public void deleteNote(int id) {
        getWritableDatabase().delete("notes","id=?",new String[]{String.valueOf(id)});
    }

    public Cursor getNotes(String email) {
        return getReadableDatabase().query("notes",null,"email=?",new String[]{email},null,null,"id DESC");
    }

    public void saveQuizScore(String name, String email, int score, int total) {
        ContentValues cv = new ContentValues();
        cv.put("name",name); cv.put("email",email);
        cv.put("score",score); cv.put("total",total);
        cv.put("ts",System.currentTimeMillis());
        getWritableDatabase().insert("quiz_scores",null,cv);
    }

    public Cursor getTopScores(int limit) {
        return getReadableDatabase().query("quiz_scores",null,null,null,null,null,
            "score DESC, ts DESC",String.valueOf(limit));
    }
}
