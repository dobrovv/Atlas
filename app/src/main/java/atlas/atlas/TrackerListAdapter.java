package atlas.atlas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

// Recycleview How-to:
//  https://www.intertech.com/Blog/android-v5-lollipop-recyclerview-tutorial/
//  https://developer.android.com/guide/topics/ui/layout/recyclerview

public class TrackerListAdapter extends RecyclerView.Adapter<TrackerListAdapter.TrackerViewHolder> {

    private static final String TAG = "Atlas"+TrackerListAdapter.class.getSimpleName();

    Context context;
    MainActivity mainActivity; // storing MainActivity reference, to update the minimap when tracker is deleted
    ArrayList<Tracker> adapterTrackerList;
    ArrayList<GPSReading> adapterGpsReadings; // stores a GPSReading for each adapter in adapterTrackerList, must have same size
    Location latestAndroidLocation; // may be null


    public class TrackerViewHolder extends RecyclerView.ViewHolder {
        protected TextView trackerIDTextView;
        protected TextView trackerNameTextView;
        protected TextView timestampTextView;
        protected TextView trackerDistanceTextView;
        protected ImageView trackerImageView;
     //   protected Button    trackerEditButton;
      //  protected Button    trackerDeleteButton;
        protected ImageView trackerStatusImage;
        protected ImageView battery;

        public TrackerViewHolder(View itemView) {
            super(itemView);
            trackerIDTextView   = (TextView) itemView.findViewById(R.id.trackerIDTextView);
            trackerNameTextView = (TextView) itemView.findViewById(R.id.trackerNameTextView);
            trackerImageView = (ImageView) itemView.findViewById(R.id.trackerImageView);
            // from GPSReadings
            trackerStatusImage =(ImageView) itemView.findViewById(R.id.trackerStatus);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestampTextView);
            // from AndroidLocationService
            trackerDistanceTextView = (TextView) itemView.findViewById(R.id.trackerDistanceTextView);
            //buttons
            battery = (ImageView) itemView.findViewById(R.id.battery);
       //     trackerEditButton = (Button) itemView.findViewById(R.id.trackerEditButton);
         //   trackerDeleteButton = (Button) itemView.findViewById(R.id.trackerDeleteButton);


        }
    }

    public TrackerListAdapter(Context context, MainActivity mainActivity) {

        this.context = context;
        this.mainActivity = mainActivity;
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

    public void deleteTracker(String TrackerID) {
        int pos = getTrackerPosByID(TrackerID);
        try {
            DatabaseHelper dbh = new DatabaseHelper(context);
            dbh.deleteTracker(TrackerID);
            adapterTrackerList.remove(pos);
            adapterGpsReadings.remove(pos);
            notifyItemRemoved(pos);
        } catch (Exception ex) {
            Log.e(TAG, "deleteTracker(): Exception " + ex.getMessage() );
        }
    }

    public void deleteTracker(int pos) {
        try {
            Tracker tracker = adapterTrackerList.get(pos);
            DatabaseHelper dbh = new DatabaseHelper(context);
            dbh.deleteTracker(tracker.TrackerID);
            adapterTrackerList.remove(pos);
            adapterGpsReadings.remove(pos);
            notifyItemRemoved(pos);
        } catch (Exception ex) {
            Log.e(TAG, "deleteTracker(): Exception " + ex.getMessage() );
        }
    }

    public void editButtonClick(int pos) {
        try {
            // go to the trackerActivity
            Tracker t = adapterTrackerList.get(pos);
            Intent intent = new Intent(context, trackerActivity.class);
            intent.putExtra("TrackerID", t.TrackerID); // add TrackerID to the intent send to the trackerActivity
            context.startActivity(intent);
        } catch (Exception ex) {
            Log.e(TAG, "Edit Button: Exception " + ex.getMessage() );
        }
    }

    // update all trackers
    public void updateTrackerList() {
        latestAndroidLocation = AndroidLocationService.getLastKnownLocation(context);
        getDataFromDB();
        notifyDataSetChanged();
    }

    // update the tracker list items without fetching the data from db
    // used by the trackerListTimer and to update androids location
    // TODO: (visible bugs) this causes the running animations to stop
    public void updateTrackerListViews() {
        latestAndroidLocation = AndroidLocationService.getLastKnownLocation(context);
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
        try {
            Tracker tracker = adapterTrackerList.get(i);
            GPSReading gpsReading = adapterGpsReadings.get(i);

            // set tracker id
            itemViewHolder.trackerIDTextView.setText("ID:"+ tracker.TrackerID);

            // set tracker name
            if(tracker.TrackerName==null || tracker.TrackerName.isEmpty()) {
                itemViewHolder.trackerNameTextView.setText("Unnamed tracker");
            } else {
                itemViewHolder.trackerNameTextView.setText(tracker.TrackerName);
            }

            //set tracker on/off  status image
            boolean offline = true;
            if(offline){ itemViewHolder.trackerStatusImage.setImageResource(R.drawable.userstateon);}

            // set tracker icon
            if(tracker.TrackerIcon == null || tracker.TrackerIcon.isEmpty()) {
                itemViewHolder.trackerImageView.setImageResource(R.drawable.ic_launcher_foreground);
            } else {
                int resID = context.getResources().getIdentifier(tracker.TrackerIcon, "mipmap", context.getPackageName());
                itemViewHolder.trackerImageView.setImageResource(resID);


                 if((tracker.TrackerIcon).equals("ic_tracker_1")) {
                     if ((tracker.TrackerIconNum).equals("girl1")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.girl1);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("boy1")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.boy1);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("boy2")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.boy2);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("boy3")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.boy3);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("boy4")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.boy4);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("girl2")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.girl2);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("girl3")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.girl3);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("girl4")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.girl4);


                         //imageview1.setImageBitmap(bitmap);


                     } else if ((tracker.TrackerIconNum).equals("girl5")) {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.girl5);


                         //imageview1.setImageBitmap(bitmap);


                     } else /*if ((tracker.TrackerIconNum).equals("boy0"))*/ {


                         //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                         itemViewHolder.trackerImageView.setImageResource(R.drawable.boy0);


                         //imageview1.setImageBitmap(bitmap);


                     }

                 }






                else if((tracker.TrackerIcon).equals("ic_tracker_2")) {
                    if ((tracker.TrackerIconNum).equals("pet1")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.pet1);


                        //imageview1.setImageBitmap(bitmap);


                    } else if ((tracker.TrackerIconNum).equals("pet2")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.pet2);


                        //imageview1.setImageBitmap(bitmap);


                    } else if ((tracker.TrackerIconNum).equals("pet3")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.pet3);


                        //imageview1.setImageBitmap(bitmap);


                    } else if ((tracker.TrackerIconNum).equals("pet4")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.pet4);


                        //imageview1.setImageBitmap(bitmap);


                    } else if ((tracker.TrackerIconNum).equals("pet5")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.pet5);


                        //imageview1.setImageBitmap(bitmap);


                    } else /*if ((tracker.TrackerIconNum).equals("pet0"))*/ {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.pet0);


                        //imageview1.setImageBitmap(bitmap);


                    }
                }





               else if((tracker.TrackerIcon).equals("ic_tracker_3")) {
                    if ((tracker.TrackerIconNum).equals("item1")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.item1);


                        //imageview1.setImageBitmap(bitmap);


                    } else if ((tracker.TrackerIconNum).equals("item2")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.item2);


                        //imageview1.setImageBitmap(bitmap);


                    } else if ((tracker.TrackerIconNum).equals("item0")) {


                        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

                        itemViewHolder.trackerImageView.setImageResource(R.drawable.item0);


                        //imageview1.setImageBitmap(bitmap);


                    }

                }






                else {
                    //int resID = context.getResources().getIdentifier(tracker.TrackerIcon, "mipmap", context.getPackageName());
                    itemViewHolder.trackerImageView.setImageResource(resID);
                }



















            }

            // set timestamp // gpsReading may be null
            if (gpsReading != null) {
                long lastSeenMs = (currentTimeMillis() - gpsReading.androidTimestamp);
                if (lastSeenMs < 3000) {
                    itemViewHolder.timestampTextView.setText("Last seen: Just now");
                } else {
                    itemViewHolder.timestampTextView.setText("Last seen: " + lastSeenMs/1000 + " seconds ago");
                }
            } else {
                itemViewHolder.timestampTextView.setText("Not seen yet");
            }

            // set distance
            if(latestAndroidLocation != null && gpsReading != null) {
                float[] distance = new float[1];
                Location.distanceBetween(latestAndroidLocation.getLatitude(), latestAndroidLocation.getLongitude(), gpsReading.Latitude, gpsReading.Longitude, distance);
                itemViewHolder.trackerDistanceTextView.setText(String.format("Distance: %.1f meters", distance[0]));
            } else {
                itemViewHolder.trackerDistanceTextView.setText("Distance: Not in range");
            }

        } catch (Exception ex) {
            Log.e(TAG, "onBindViewHolder() : Exception " + ex.getMessage() );
        }

        // Edit button onClick listener
      /*  itemViewHolder.trackerEditButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    // go to the trackerActivity
                    Tracker t = adapterTrackerList.get(i);
                    Intent intent = new Intent(context, trackerActivity.class);
                    intent.putExtra("TrackerID", t.TrackerID); // add TrackerID to the intent send to the trackerActivity
                    context.startActivity(intent);
                } catch (Exception ex) {
                    Log.e(TAG, "Edit Button: Exception " + ex.getMessage() );
                }
            }
        });*/

        // Delete button onClick listener
/*        itemViewHolder.trackerDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    //delete the tracker
                    Tracker t = adapterTrackerList.get(i);
                    deleteTracker(t.TrackerID);
                    if (mainActivity != null)
                        mainActivity.updateAllMiniMapMarkers();
                } catch (Exception ex) {
                    Log.e(TAG, "Delete Button: Exception " + ex.getMessage() );
                }
            }
        });*/

        itemViewHolder.trackerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GPSReading gpsReading = adapterGpsReadings.get(i); // can be null if there are no gps readings for the current Tracker
                    if (gpsReading == null) {
                        Toast.makeText(context, "Tracker doesn't have location available", Toast.LENGTH_LONG).show();
                    } else {
                        // go to Map activity
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
