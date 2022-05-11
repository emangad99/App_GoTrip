package com.mayv.gotrip;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

public class TripHistoryAdapter extends ArrayAdapter<ArrayList<PastTrip>> {

    private final ArrayList<PastTrip> trips;
    private final Context context;

    public TripHistoryAdapter(@NonNull Context context, @NonNull ArrayList<PastTrip> trips) {
        super(context, R.layout.trip_history_listitem, R.id.history_trip_name, Collections.singletonList(trips));
        this.trips = trips;
        this.context = context;
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder myViewHolder;
        PastTrip currentItem = trips.get(position);
        int status = currentItem.getStatus();
        if(convertView == null) {
            LayoutInflater myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = myInflater.inflate(R.layout.trip_history_listitem, parent, false);
            myViewHolder = new ViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else{
            myViewHolder = (ViewHolder) convertView.getTag();
        }
        myViewHolder.getTripName().setText(currentItem.getTripName());
        myViewHolder.getStartPoint().setText(currentItem.getStartPoint());
        myViewHolder.getEndPoint().setText(currentItem.getEndPoint());
        myViewHolder.getDistancePerDuration().setText(currentItem.getDistancePerDuration());
        myViewHolder.getDuration().setText(currentItem.getDuration());
        myViewHolder.getAverageSpeed().setText(currentItem.getAverageSpeed());
        if(status == PastTrip.IS_DONE){
            myViewHolder.getStatusImage().setImageResource(R.drawable.ic_baseline_done);
            myViewHolder.getStatusImage().setColorFilter(Color.argb(255, 69, 229, 33));
            myViewHolder.getStatus().setText(R.string.done);
        }else if(status == PastTrip.IS_CANCELED){
            myViewHolder.getStatusImage().setImageResource(R.drawable.ic_baseline_canceled);
            myViewHolder.getStatusImage().setColorFilter(Color.argb(255, 197, 66, 69));
            myViewHolder.getStatus().setText(R.string.canceled);
        }else {
            myViewHolder.getStatusImage().setImageResource(R.drawable.ic_baseline_delete);
            myViewHolder.getStatusImage().setColorFilter(Color.argb(255, 197, 66, 69));
            myViewHolder.getStatus().setText(R.string.deleted);
        }
        return convertView;
    }

    class ViewHolder{

        private TextView tripName;
        private TextView startPoint;
        private TextView endPoint;
        private TextView distancePerDuration;
        private TextView duration;
        private TextView averageSpeed;
        private TextView status;
        private ImageView statusImage;
        private View view;

        public ViewHolder(View view) {
            this.view = view;
        }

        public TextView getTripName() {
            if(tripName == null){
                tripName = view.findViewById(R.id.history_trip_name);
            }
            return tripName;
        }

        public TextView getStartPoint() {
            if(startPoint == null){
                startPoint = view.findViewById(R.id.history_start_point);
            }
            return startPoint;
        }

        public TextView getEndPoint() {
            if(endPoint == null){
                endPoint = view.findViewById(R.id.history_end_point);
            }
            return endPoint;
        }

        public TextView getDistancePerDuration() {
            if(distancePerDuration == null){
                distancePerDuration = view.findViewById(R.id.trip_date);
            }
            return distancePerDuration;
        }

        public TextView getDuration() {
            if(duration == null){
                duration = view.findViewById(R.id.trip_repeat);
            }
            return duration;
        }

        public TextView getAverageSpeed() {
            if(averageSpeed == null){
                averageSpeed = view.findViewById(R.id.average_speed);
            }
            return averageSpeed;
        }

        public TextView getStatus() {
            if(status == null){
                status = view.findViewById(R.id.history_trip_status);
            }
            return status;
        }

        public ImageView getStatusImage() {
            if(statusImage == null){
                statusImage = view.findViewById(R.id.label_status);
            }
            return statusImage;
        }
    }
}
