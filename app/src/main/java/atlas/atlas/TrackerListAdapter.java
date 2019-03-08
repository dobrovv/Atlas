package atlas.atlas;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// Recycleview How-to:
//  https://www.intertech.com/Blog/android-v5-lollipop-recyclerview-tutorial/
//  https://developer.android.com/guide/topics/ui/layout/recyclerview

public class TrackerListAdapter extends RecyclerView.Adapter<TrackerListAdapter.TrackerViewHolder> {

    private static final String TAG = "Atlas"+TrackerListAdapter.class.getSimpleName();

    Context context;
    ArrayList<Tracker> adapterTrackerList;
    ArrayList<GPSReading> adapterGpsReadings; // stores a GPSReading for each adapter in adapterTrackerList, must have same size
    Location latestAndroidLocation; // may be null

    public class TrackerViewHolder extends RecyclerView.ViewHolder {
        protected TextView trackerIDTextView;
        protected TextView trackerNameTextView;
        protected TextView timestampTextView;
        protected TextView trackerDistanceTextView;
        protected ImageView trackerImageView;
        protected Button    trackerEditButton;
        protected Button    trackerDeleteButton;


        public TrackerViewHolder(View itemView) {
            super(itemView);
            trackerIDTextView   = (TextView) itemView.findViewById(R.id.trackerIDTextView);
            trackerNameTextView = (TextView) itemView.findViewById(R.id.trackerNameTextView);
            trackerImageView = (ImageView) itemView.findViewById(R.id.trackerImageView);
            // from GPSReadings
            timestampTextView = (TextView) itemView.findViewById(R.id.timestampTextView);
            // from AndroidLocationService
            trackerDistanceTextView = (TextView) itemView.findViewById(R.id.trackerDistanceTextView);
            //buttons
            trackerEditButton = (Button) itemView.findViewById(R.id.trackerEditButton);
            trackerDeleteButton = (Button) itemView.findViewById(R.id.trackerDeleteButton);
        }
    }

    public TrackerListAdapter(Context context) {

        this.context = context;
        getDataFromDB();

        latestAndroidLocation = AndroidLocationService.getLastKnownLocation(context);
    }

    private void getDataFromDB() {
        DatabaseHelper dbh = new DatabaseHelper(context);
        // get trackers from db
        adapterTrackerList = dbh.getAllTrackers();
        adapterGpsReadings = new ArrayList<GPSReading>();;

        // get gps readings from db
        for (int i = 0; i < adapterTrackerList.size(); i++) {
            Tracker t = adapterTrackerList.get(i);
            GPSReading reading = dbh.getLatestGPSReading(t.TrackerID);
            adapterGpsReadings.add(i, reading);
        }
    }

    private int getTrackerPosByID(String TrackerID) {
        for (int i = 0; i< adapterTrackerList.size(); i++) {
            Tracker t = adapterTrackerList.get(i);
            if ((t.TrackerID != null) && t.TrackerID.equals(TrackerID) ) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return adapterTrackerList.size();
    }

    public void updateTracker(String TrackerID) {
        latestAndroidLocation = AndroidLocationService.getLastKnownLocation(context);
        int pos = getTrackerPosByID(TrackerID);
        if (pos >= 0) {
            DatabaseHelper dbh = new DatabaseHelper(context);
            adapterTrackerList.set(pos, dbh.getTrackerByID(TrackerID));
            adapterGpsReadings.set(pos, dbh.getLatestGPSReading(TrackerID));
            notifyItemChanged(pos);
        } else {
            Log.d(TAG, String.format("Updating TrackerID=\"%s\", but it's not in the trackerList", TrackerID));
        }

    }

    // update all trackers
    public void updateTrackerList() {
        latestAndroidLocation = AndroidLocationService.getLastKnownLocation(context);
        getDataFromDB();
        notifyDataSetChanged();

    }

    @Override
    public TrackerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.listview_tracker_row, viewGroup, false);
        return new TrackerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final TrackerViewHolder itemViewHolder, final int i) {
        Tracker tracker = adapterTrackerList.get(i);
        if (tracker != null) {
            itemViewHolder.trackerIDTextView.setText("ID:"+ tracker.TrackerID);
            if(tracker.TrackerName==null || tracker.TrackerName.isEmpty()) {
                itemViewHolder.trackerNameTextView.setText("Unnamed tracker");
            } else {
                itemViewHolder.trackerNameTextView.setText(tracker.TrackerName);
            }
            if(tracker.TrackerIcon == null || tracker.TrackerIcon.isEmpty()) {
                itemViewHolder.trackerImageView.setImageResource(R.drawable.ic_launcher_foreground);
            } else {
                int resID = context.getResources().getIdentifier(tracker.TrackerIcon, "mipmap", context.getPackageName());
                itemViewHolder.trackerImageView.setImageResource(resID);
            }
        } else {
            Log.d(TAG, "Tracker is null"); //shouldn't happen
        }

        GPSReading gpsReading = adapterGpsReadings.get(i);
        if (gpsReading != null) { // gpsReading may be null
            itemViewHolder.timestampTextView.setText("Timestamp: "+ gpsReading.androidTimestamp/1000 );
        } else {
            itemViewHolder.timestampTextView.setText("Not seen yet");
        }
        if(latestAndroidLocation != null && gpsReading != null) {
            float[] distance = new float[1];
            Location.distanceBetween(latestAndroidLocation.getLatitude(), latestAndroidLocation.getLongitude(), gpsReading.Latitude, gpsReading.Longitude, distance);
            itemViewHolder.trackerDistanceTextView.setText(String.format("Distance: %.1f meters", distance[0]));
        } else {
            itemViewHolder.trackerDistanceTextView.setText("Distance: Not in range");
        }

        // Edit button onClick listener
        itemViewHolder.trackerEditButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: (bugy!?) the adapter position may not match the viewholder during layout updates
                int pos = itemViewHolder.getAdapterPosition();
                if (pos==RecyclerView.NO_POSITION || pos >= getItemCount()) //check the position
                    return;

                // go to the trackerActivity
                Tracker t = adapterTrackerList.get(pos);
                Intent intent = new Intent(context, trackerActivity.class);
                intent.putExtra("TrackerID", t.TrackerID); // add TrackerID to the intent send to the trackerActivity
                context.startActivity(intent);
            }
        });

        // Delete button onClick listener
        itemViewHolder.trackerDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: (bugy!?) the adapter position may not match the viewholder during layout updates
                int pos = itemViewHolder.getAdapterPosition();
                if (pos==RecyclerView.NO_POSITION || pos >= getItemCount()) //check the position
                    return;

                //delete the tracker
                Tracker t = adapterTrackerList.get(pos);
                DatabaseHelper dbh = new DatabaseHelper(context);
                dbh.deleteTracker(t.TrackerID);
                updateTrackerList();
            }
        });

        itemViewHolder.trackerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), i + " position", Toast.LENGTH_SHORT).show();
                // go to Map activity
                try {
                    GPSReading gpsReading = adapterGpsReadings.get(i); // can be null if there are no gps readings for the current Tracker
                    if (gpsReading == null) {
                        Toast.makeText(context, "Tracker doesn't have location available", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(context, MapActivity.class);
                        intent.putExtra("TrackerID", gpsReading.TrackerID);
                        intent.putExtra("Latitude", gpsReading.Latitude);
                        intent.putExtra("Longitude", gpsReading.Longitude);
                        intent.putExtra("AndroidLatitude", latestAndroidLocation.getLatitude());
                        intent.putExtra("AndroidLongitude", latestAndroidLocation.getLongitude());
                        context.startActivity(intent);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Can't start map activity: Exception " + ex.getMessage() );
                }
            }
        });

    }
}
