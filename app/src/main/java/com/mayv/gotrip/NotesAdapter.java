package com.mayv.gotrip;


import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

class NotesAdapter extends ArrayAdapter<String[]> {

    private final String[] notes;
    private final Context context;

    public NotesAdapter(@NonNull Context context, @NonNull String[] notes) {
        super(context, R.layout.notes_listitem, R.id.note, Collections.singletonList(notes));
        this.notes = notes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notes.length;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder myViewHolder;
        String currentNote = notes[position];
        if (convertView == null) {
            LayoutInflater myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = myInflater.inflate(R.layout.notes_listitem, parent, false);
            myViewHolder = new ViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }
        if (!currentNote.equals("")) {
            myViewHolder.getNoteBody().setText(currentNote);
            myViewHolder.getCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        myViewHolder.getNoteBody().setPaintFlags(myViewHolder.getNoteBody().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        myViewHolder.getNoteBody().setPaintFlags(myViewHolder.getNoteBody().getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {

        private TextView noteBody;
        private CheckBox checkbox;
        private View view;

        public ViewHolder(View view) {
            this.view = view;
        }

        public TextView getNoteBody() {
            if (noteBody == null) {
                noteBody = view.findViewById(R.id.note);
            }
            return noteBody;
        }

        public CheckBox getCheckbox() {
            if (checkbox == null) {
                checkbox = view.findViewById(R.id.checkbox_note);
            }
            return checkbox;
        }
    }
}


