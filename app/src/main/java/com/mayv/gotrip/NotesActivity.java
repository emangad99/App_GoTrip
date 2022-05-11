package com.mayv.gotrip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class NotesActivity extends AppCompatActivity {

    Trip trip;
    int tripId;
    String[] notes;
    DatabaseAdapter databaseAdapter;
    ListView listView;
    NotesAdapter notesAdapter;
    Button addNoteBtn;
    EditText noteBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        initializeComponents();
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noteBody.getText().toString().equals("")) {
                    addNote();
                }
            }
        });
    }

    private void addNote() {
        String newNote = noteBody.getText().toString();
        String oldNotes = trip.getNotes();
        if (oldNotes.equals("")) {
            trip.setNotes(newNote);
        } else {
            trip.setNotes(oldNotes + "." + newNote);
        }
        databaseAdapter.updateTrip(trip);
        databaseAdapter.insertTripIntoFirebase(trip);
        getNotesFromTrip();
        notesAdapter.notifyDataSetChanged();
        noteBody.setText("");
    }

    private void initializeComponents() {
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        tripId = getIntent().getIntExtra("ClickedTripId", 0);
        addNoteBtn = findViewById(R.id.button_add_note);
        noteBody = findViewById(R.id.note_body);
        listView = findViewById(R.id.notes_listView);
        getNotesFromTrip();
    }


    private void getNotesFromTrip() {
        trip = databaseAdapter.getSpecificTrip(tripId);
        notes = trip.getNotes().split("\\.");
        notesAdapter = new NotesAdapter(getApplicationContext(), notes);
        listView.setAdapter(notesAdapter);
    }
}