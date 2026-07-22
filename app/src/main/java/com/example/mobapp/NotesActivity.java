package com.example.mobapp;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    DBHelper db; SessionManager session;
    EditText etNote; ListView lvNotes;
    List<String> notesList = new ArrayList<>();
    List<Integer> noteIds  = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s); setContentView(R.layout.activity_notes);
        db      = new DBHelper(this);
        session = new SessionManager(this);
        etNote  = findViewById(R.id.etNote);
        lvNotes = findViewById(R.id.lvNotes);
        Button btnAdd  = findViewById(R.id.btnAddNote);
        Button btnBack = findViewById(R.id.btnBack);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesList);
        lvNotes.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String note = etNote.getText().toString().trim();
            if (TextUtils.isEmpty(note)) { Toast.makeText(this,"Write something first",Toast.LENGTH_SHORT).show(); return; }
            db.addNote(session.getEmail(), note);
            etNote.setText(""); loadNotes();
            Toast.makeText(this,"Note saved!",Toast.LENGTH_SHORT).show();
        });

        // Long press to delete
        lvNotes.setOnItemLongClickListener((parent, view, pos, id) -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Delete this note?\n\n\"" + notesList.get(pos) + "\"")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteNote(noteIds.get(pos));
                    loadNotes();
                    Toast.makeText(this,"Note deleted",Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null).show();
            return true;
        });

        btnBack.setOnClickListener(v -> finish());
        loadNotes();
    }

    void loadNotes() {
        notesList.clear(); noteIds.clear();
        Cursor c = db.getNotes(session.getEmail());
        while (c.moveToNext()) {
            noteIds.add(c.getInt(c.getColumnIndexOrThrow("id")));
            notesList.add(c.getString(c.getColumnIndexOrThrow("note")));
        }
        c.close(); adapter.notifyDataSetChanged();
        ((TextView)findViewById(R.id.tvNoteCount)).setText(notesList.size() + " note(s)");
    }
}
