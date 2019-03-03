package atlas.atlas;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

//comment 12

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Atlas"+MainActivity.class.getSimpleName();

    // android location permissions
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    // for permission requests
    private static final int INITIAL_REQUEST=1010;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+1;

    //helper to access GPS permission
    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        }
    }

    //helper to access GPS permission
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }


    RecyclerView trackerListMain;
    TrackerListAdapter trackerListAdapter;
    Button startServiceButton;
    Button stopServiceButton;
    TextView mapTextView; // placeholder, use for debugging
    Location androidLatestLocation; // stores the latest known androids location (may be null)

    /**
     *  Broadcast Receiver for the GPSReading updates that are sent from ReceiverServiceMockup
     *  the NEW_GPSREADING broadcasts contain GPSReadingID, TrackerID, Latitude and Longitude of the tracker
     * */
    class GPSReadingBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "New GPSReading broadcast recieved");
            // get the data from the broadcast
            String TrackerID = intent.getStringExtra("TrackerID");
            Long GPSReadingID = intent.getLongExtra("GPSReadingID", 0);
            Double Latitude = intent.getDoubleExtra("Latitude", 0.0);
            Double Longitude = intent.getDoubleExtra("Longitude", 0.0);

            //retrieving the data again from the db to test the db
            DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
            GPSReading gpsReading = dbh.getLatestGPSReading(TrackerID);
            String info = String.format("TrackerID=\"%s\"(Lat= %3.6f Lng= %3.6f)", gpsReading.TrackerID, gpsReading.Latitude, gpsReading.Longitude);

            // add data to textview
            mapTextView.setText(info + '\n'+ mapTextView.getText());

            // update the tracker list
            trackerListAdapter.updateTracker(TrackerID);
        }
    }
    /**
     *  Broadcast Receiver for androids gps location and changes in gps status
     *  the NEW_ANDROIDLOCATION broadcast contains latitude and longitude, and Provider(gps/network)
     *  the LOCATIONPROVIDER_ENABLED_CHANGE broardcast notifies when gps and network location updates become enabled/disabled
     * */
    class AndroidLocationBroadcastsReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent == null)  return;
            String action = intent.getAction();

            // get new android location
            if (action.equals(AndroidLocationService.BROADCAST_ACTION_NEW_ANDROIDLOCATION)) {
                // get the location data, (it's also present in broadcast's intent)
                androidLatestLocation = AndroidLocationService.getLastKnownLocation(getApplicationContext());
                if (androidLatestLocation != null)
                    mapTextView.setText( "Android location "+androidLatestLocation.getProvider() +" "+ androidLatestLocation.getLatitude() + " "+ androidLatestLocation.getLongitude() +"\n"+ mapTextView.getText());

                // get wheter gps/network location updates are disabled/enabled
            } else if (action.equals(AndroidLocationService.BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE)) {
                String Provider = intent.getStringExtra("Provider");
                boolean Enabled = intent.getBooleanExtra("Enabled", false);
                mapTextView.setText( "Enabled "+ Provider + " "+ Enabled +"\n"+ mapTextView.getText());
            }
        }
    }


    GPSReadingBroadcastReceiver gpsReadingBroadcastReceiver;
    AndroidLocationBroadcastsReceiver androidLocationBroadcastsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackerListMain = findViewById(R.id.trackerListMain);
        startServiceButton = findViewById(R.id.startServiceButton);
        stopServiceButton = findViewById(R.id.stopServiceButton);
        mapTextView = findViewById(R.id.mapTextView);

        // Register the broadcast receiver for GPSReading broadcasts
        gpsReadingBroadcastReceiver = new GPSReadingBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ReceiverServiceMockup.BROADCAST_ACTION_NEW_GPSREADING);
        registerReceiver(gpsReadingBroadcastReceiver, intentFilter);

        // Register the broadcast receiver for android location broadcasts
        androidLocationBroadcastsReceiver = new AndroidLocationBroadcastsReceiver();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(AndroidLocationService.BROADCAST_ACTION_NEW_ANDROIDLOCATION);
        intentFilter2.addAction(AndroidLocationService.BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE);
        registerReceiver(androidLocationBroadcastsReceiver, intentFilter2);

        // Set on click listeners for buttons
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiverServiceMockup.class);
                intent.setAction(ReceiverServiceMockup.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canAccessLocation()) {
                    requestPermissions( LOCATION_PERMS, LOCATION_REQUEST);
                } else {
                    intent = new Intent(MainActivity.this, AndroidLocationService.class);
                    intent.setAction(AndroidLocationService.ACTION_START_SERVICE);
                    startService(intent);
                }

            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop the receiver mockup
                Intent intent = new Intent(MainActivity.this, ReceiverServiceMockup.class);
                intent.setAction(ReceiverServiceMockup.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);

                //stop the android location service
                intent = new Intent(MainActivity.this, AndroidLocationService.class);
                intent.setAction(AndroidLocationService.ACTION_STOP_SERVICE);
                startService(intent);
            }
        });

        // display the trackers list
        trackerListMain.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        trackerListMain.setLayoutManager(layoutManager);

        trackerListAdapter = new TrackerListAdapter(this);
        trackerListMain.setAdapter(trackerListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the tracker list if the Activity gained focus back without calling the onCreate()
        trackerListAdapter.updateTrackerList();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        // in case we added more to toolbar
        if(id==R.id.addTracker){
            //startActivity(new Intent(this, trackerActivity.class));
            AddTrackerDialog dialog = new AddTrackerDialog();
            dialog.show(getSupportFragmentManager(), "Add new Tracker");
        }

        // more if statements can enter here to intent to activities
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the broadcast receiver after finishing of the activity
        if (gpsReadingBroadcastReceiver != null) {
            unregisterReceiver(gpsReadingBroadcastReceiver);
        }
        if (androidLocationBroadcastsReceiver != null) {
            unregisterReceiver(androidLocationBroadcastsReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode) {
            case LOCATION_REQUEST:
                if (canAccessLocation()) {
                    Intent intent = new Intent(MainActivity.this, AndroidLocationService.class);
                    intent.setAction(AndroidLocationService.ACTION_START_SERVICE);
                    startService(intent);
                }
                else {
                   mapTextView.setText("Can't get GPS permission\n"+ mapTextView.getText());
                }
                break;
        }
    }


}
