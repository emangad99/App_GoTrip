package com.mayv.gotrip;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

public class OngoingTripAdapter extends ArrayAdapter<ArrayList<Trip>> {

    private final ArrayList<Trip> trips;
    private final Context context;
    Communicator activity;
    DatabaseAdapter databaseAdapter;
    static Intent intent;

    public OngoingTripAdapter(@NonNull Context context, @NonNull ArrayList<Trip> trips, Communicator activity) {
        super(context, R.layout.ongoing_trips_listitem, R.id.trip_id, Collections.singletonList(trips));
        this.trips = trips;
        this.context = context;
        this.activity = activity;
        databaseAdapter = new DatabaseAdapter(context);
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OngoingTripAdapter.ViewHolder myViewHolder;
        Trip currentItem = trips.get(position);
        if (convertView == null) {
            LayoutInflater myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = myInflater.inflate(R.layout.ongoing_trips_listitem, parent, false);
            myViewHolder = new OngoingTripAdapter.ViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (OngoingTripAdapter.ViewHolder) convertView.getTag();
        }
        myViewHolder.getId().setText(String.valueOf(currentItem.getId()));
        myViewHolder.getTripName().setText(currentItem.getTripName());
        myViewHolder.getStartPoint().setText(currentItem.getStartPoint());
        myViewHolder.getEndPoint().setText(currentItem.getEndPoint());
        myViewHolder.getDate().setText(currentItem.getDate());
        myViewHolder.getHour().setText(currentItem.getHour());
        myViewHolder.getRepeat().setText(currentItem.getRepeat());
        myViewHolder.getDirection().setText(currentItem.getDirection());
        myViewHolder.getStartButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripReminder tripReminder = TripReminder.getTripReminder();
                if(tripReminder != null){
                    tripReminder.cancelReminder();
                }
                startTrip(position);
            }
        });
        myViewHolder.getEditButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTripInfo(position);
            }
        });
        myViewHolder.getNotesButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotes(position);
            }
        });
        myViewHolder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripReminder tripReminder = TripReminder.getTripReminder();
                if (tripReminder != null) {
                    tripReminder.cancelReminder();
                }
                activity.deleteTripFromList(currentItem.getId(), position);
                databaseAdapter.deleteTripFromFirebase(currentItem.getTripName());
                PastTrip pastTrip = new PastTrip(currentItem.getTripName(), currentItem.getStartPoint(), currentItem.getEndPoint(), currentItem.getStartLatLong(), currentItem.getEndLatLong(), context.getString(R.string.nothing), context.getString(R.string.nothing), context.getString(R.string.nothing), PastTrip.IS_DELETED);
                databaseAdapter.insertPastTrip(pastTrip);
                databaseAdapter.insertPastTripFirebase(pastTrip);
            }
        });
        return convertView;
    }


    private void setNotes(int position) {
        Trip trip = trips.get(position);
        Intent intent = new Intent(context, NotesActivity.class);
        intent.putExtra("ClickedTripId", trip.getId());
        context.startActivity(intent);
    }

    private void editTripInfo(int position) {
        Trip currentTrip = trips.get(position);
        Intent intent = new Intent(getContext(), AddTripActivity.class);
        intent.putExtra("Edit", true);
        intent.putExtra("CurrentId", currentTrip.getId());
        TripReminder tripReminder = TripReminder.getTripReminder();
        if(tripReminder != null){
            tripReminder.cancelReminder();
        }
        context.startActivity(intent);
    }

    private void startTrip(int position) {
        Trip currentTrip = trips.get(position);
        TripReminder tripReminder = TripReminder.getTripReminder();
        if(tripReminder != null){
            tripReminder.cancelReminder();
        }
        intent = new Intent(context, LocationJobService.class);
        intent.putExtra("StartLatLong", currentTrip.getStartLatLong());
        intent.putExtra("EndLatLong", currentTrip.getEndLatLong());
        intent.putExtra("Direction", currentTrip.getDirection());
        intent.putExtra("Repeat", currentTrip.getRepeat());
        intent.putExtra("TripId", currentTrip.getId());
        LocationJobService.enqueueWork(context, intent);
        DisplayTrack(currentTrip.getStartPoint(), currentTrip.getEndPoint());
    }

    private void DisplayTrack(String startPoint, String endPoint) {
        try {
            Uri uri = Uri.parse("http://www.google.co.in/maps/dir/" + startPoint + "/" + endPoint);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


    class ViewHolder {
        private TextView id;
        private TextView tripName;
        private TextView startPoint;
        private TextView endPoint;
        private TextView date;
        private TextView hour;
        private TextView repeat;
        private TextView direction;
        private Button startButton;
        private ImageButton editButton;
        private ImageButton notesButton;
        private ImageButton deleteButton;
        private View view;

        public ViewHolder(View view) {
            this.view = view;
        }

        public TextView getId() {
            if (id == null) {
                id = view.findViewById(R.id.trip_id);
            }
            return id;
        }

        public TextView getTripName() {
            if (tripName == null) {
                tripName = view.findViewById(R.id.ongoing_trip_name);
            }
            return tripName;
        }

        public TextView getStartPoint() {
            if (startPoint == null) {
                startPoint = view.findViewById(R.id.ongoing_start_point);
            }
            return startPoint;
        }

        public TextView getEndPoint() {
            if (endPoint == null) {
                endPoint = view.findViewById(R.id.ongoing_end_point);
            }
            return endPoint;
        }

        public TextView getDate() {
            if (date == null) {
                date = view.findViewById(R.id.trip_date);
            }
            return date;
        }

        public TextView getHour() {
            if (hour == null) {
                hour = view.findViewById(R.id.trip_hour);
            }
            return hour;
        }

        public TextView getRepeat() {
            if (repeat == null) {
                repeat = view.findViewById(R.id.trip_repeat);
            }
            return repeat;
        }

        public TextView getDirection() {
            if (direction == null) {
                direction = view.findViewById(R.id.trip_directions);
            }
            return direction;
        }

        public Button getStartButton() {
            if (startButton == null) {
                startButton = view.findViewById(R.id.button_start_trip);
            }
            return startButton;
        }

        public ImageButton getEditButton() {
            if (editButton == null) {
                editButton = view.findViewById(R.id.button_edit);
            }
            return editButton;
        }

        public ImageButton getNotesButton() {
            if (notesButton == null) {
                notesButton = view.findViewById(R.id.button_notes);
            }
            return notesButton;
        }

        public ImageButton getDeleteButton() {
            if (deleteButton == null) {
                deleteButton = view.findViewById(R.id.button_delete);
            }
            return deleteButton;
        }
    }

}
