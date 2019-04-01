package atlas.atlas;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


//comment 12

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

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

    // trackerList
    RecyclerView trackerListMain;
    TrackerListAdapter trackerListAdapter;
    Handler trackerListTimer; // handler for the timer to update the tracker list each second

    // map
    TextView mapTextView; // placeholder, use for debugging
    View miniMapView;
    GoogleMap miniMap;
    LinearLayout miniMapLayout;

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

            if (gpsReading != null) {
                String info = String.format("\tTrackerID=\"%s\" (%3.6f, %3.6f)", gpsReading.TrackerID, gpsReading.Latitude, gpsReading.Longitude);
                //Log.e(TAG, "Gpsreading:" + gpsReading.GPSSignal + " "+ gpsReading.GSMSignal + " " + gpsReading.BatteryLevel + " " + gpsReading.PowerStatus);

                // add data to textview
                mapTextView.setText("NEW_GPSREADING Broadcast:" +'\n'+ info + '\n'+ mapTextView.getText());

                // update the tracker list
                trackerListAdapter.updateTracker(TrackerID);

                // update minimap
                updateTrackerMiniMapMarker(TrackerID, new LatLng(gpsReading.Latitude, gpsReading.Longitude));
            }
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
                if (androidLatestLocation != null) {
                    String info = String.format("NEW_ANDROIDLOCATION Broadcast:" +'\n' + "\tAndroid(provider=%s) (%3.6f, %3.6f)", androidLatestLocation.getProvider(), androidLatestLocation.getLatitude(), androidLatestLocation.getLongitude());
                    mapTextView.setText(info + "\n" + mapTextView.getText());
                }
                // update tracker items with new android location (for distance calculation)
                trackerListAdapter.updateTrackerListViews();

                //update minimap
                updateAndroidMiniMapMarker();

                // get wheter gps/network location updates are disabled/enabled
            } else if (action.equals(AndroidLocationService.BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE)) {
                String Provider = intent.getStringExtra("Provider");
                boolean Enabled = intent.getBooleanExtra("Enabled", false);
                mapTextView.setText( "LOCATIONPROVIDER_ENABLED_CHANGE Broadcast:" +'\n'+ "\tCoordinates from provider \""+ Provider + "\" are " + (Enabled?"ENABLED":"DISABLED") +"\n"+ mapTextView.getText());
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
        //startServiceButton = findViewById(R.id.startServiceButton);
        //stopServiceButton = findViewById(R.id.stopServiceButton);

        // add minimap layout
        mapTextView = findViewById(R.id.mapTextView);
        miniMapView = findViewById(R.id.miniMapView);
        miniMapLayout = findViewById(R.id.miniMapLayout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.miniMapView);
        try {
            mapFragment.getMapAsync(this);
        } catch (Exception ex) {
            Log.e(TAG, "Can't start the google map");
        }

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
        /*startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackgroundServices();
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBackgroundServices();
            }
        });*/

        // display the trackers list
        trackerListMain.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        trackerListMain.setLayoutManager(layoutManager);
        //add dividers between tracker items https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
        trackerListMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        trackerListAdapter = new TrackerListAdapter(this, this);
        trackerListMain.setAdapter(trackerListAdapter);

        // timer to update the tracker list periodically (for the "last seen" time)
        trackerListTimer = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                trackerListAdapter.updateTrackerListViews(); // update the last seen time
                updateAllMiniMapMarkers();
                trackerListTimer.postDelayed(this, 5300);
            }
        };
        trackerListTimer.postDelayed(runnable, 5300);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the tracker list if the Activity gained focus back without calling the onCreate()
        trackerListAdapter.updateTrackerList();
        // update the minimap
        updateAllMiniMapMarkers();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    boolean mBackgroundServicesStarted = false; // store whether the background services were started or not
    //boolean mShowMiniMap = true; // store whether the minimap is displayed or not
    int mShowMiniMap = 0; // (0 - show) (1-show debug) (2-hide)

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        // in case we added more to toolbar
        if(id==R.id.addTracker){
            //startActivity(new Intent(this, trackerActivity.class));
            AddTrackerDialog dialog = new AddTrackerDialog();
            dialog.show(getSupportFragmentManager(), "Add new Tracker");

        } else if(id==R.id.startServicesAction) {
            // start or stop services on button click
            if ( mBackgroundServicesStarted == false) {
                startBackgroundServices();
            } else {
                stopBackgroundServices();
            }
            mBackgroundServicesStarted = !mBackgroundServicesStarted;
        } else if (id==R.id.showMiniMapAction) {
            mShowMiniMap = (mShowMiniMap+1)%3;
            if (mShowMiniMap == 0) {
                miniMapView.setVisibility(View.VISIBLE);
                mapTextView.setVisibility(View.GONE);
                miniMapLayout.setVisibility(View.VISIBLE);
            }  else if (mShowMiniMap == 1) {
                miniMapView.setVisibility(View.GONE);
                mapTextView.setVisibility(View.VISIBLE);
                miniMapLayout.setVisibility(View.VISIBLE);
            } else if (mShowMiniMap == 2) {
                miniMapLayout.setVisibility(View.GONE);
            }

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

    public void startBackgroundServices() {
        Intent intent = new Intent(MainActivity.this, ReceiverService.class);
        intent.setAction(ReceiverService.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);

        intent = new Intent(MainActivity.this, ReceiverServiceMockup.class);
        intent.setAction(ReceiverServiceMockup.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canAccessLocation()) {
            requestPermissions( LOCATION_PERMS, LOCATION_REQUEST);
        } else {
            intent = new Intent(MainActivity.this, AndroidLocationService.class);
            intent.setAction(AndroidLocationService.ACTION_START_SERVICE);
            startService(intent);
        }
        Toast.makeText(this, "Background services started", Toast.LENGTH_SHORT).show();
    }

    public void stopBackgroundServices() {
        //stop the receiver mockup
        Intent intent = new Intent(MainActivity.this, ReceiverService.class);
        intent.setAction(ReceiverService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);

        intent = new Intent(MainActivity.this, ReceiverServiceMockup.class);
        intent.setAction(ReceiverServiceMockup.ACTION_STOP_FOREGROUND_SERVICE);
        startService(intent);

        //stop the android location service
        intent = new Intent(MainActivity.this, AndroidLocationService.class);
        intent.setAction(AndroidLocationService.ACTION_STOP_SERVICE);
        startService(intent);

        Toast.makeText(this, "Background services stoped", Toast.LENGTH_SHORT).show();
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

    /**
     *  Minimap related functions
     *  The androids marker displayed on the map is stored in the androidMarker
     *  The markers for each tracker are stored in trackerMarkers (stored in a HashMap, the key is TrackekID)
     *  to update all markes on the mini map when new data is available call updateAllMiniMapMarkers()
     *  to update only the andoirMarker call updateAndroidMiniMapMarker()
     *  to update only one tracker call updateTrackerMiniMapMarker()
     * */
    Marker androidMarker; // Marker of the android phone on the minimap
    HashMap<String, Marker> trackerMarkers; // Markers of the tracked devices, the key is TrackerID;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        miniMap = googleMap;
        trackerMarkers = new HashMap<String, Marker>();
        try {
            updateAllMiniMapMarkers();
        } catch (Exception ex) {
            Log.e(TAG, "onMapReady() Exception: " + ex.getMessage() );
        }
    }

    public void updateAndroidMiniMapMarker() {
        if (miniMap == null)
            return;

        // update the android marker, getLastKnownLocation may fail if either there are no location permissions or latest location is unknown
        try {
            Location androidLatest = AndroidLocationService.getLastKnownLocation(this);
            LatLng androidLatLng = new LatLng(androidLatest.getLatitude(), androidLatest.getLongitude());

            if (androidMarker == null) { // if androids marker doesn't exist create it here
                androidMarker = miniMap.addMarker(new MarkerOptions()
                        .position(androidLatLng)
                        .title("Android")
                        .snippet("Your location"));
                trySetMarkerIcon(androidMarker, R.mipmap.ic_launcher_round);
                        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
                miniMap.moveCamera(CameraUpdateFactory.newLatLngZoom(androidLatLng, 15));
            } else { // update the android's marker on the minimap
                androidMarker.setPosition(androidLatLng);
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateAndroidMiniMapMarker() can't update androids location Exception: " + ex.getMessage());
        }
    }

    public void updateAllMiniMapMarkers() {
        // check if minimap is ready
        if (miniMap == null)
            return;

        // update androids minimap marker
        updateAndroidMiniMapMarker();

        DatabaseHelper dbh = new DatabaseHelper(this);
        ArrayList<Tracker> trackersDB = dbh.getAllTrackers();

        // for each tracker in the db, create/update it's marker on the map
        for (Tracker tracker : trackersDB) {
            // get latest gps data for the tracker
            GPSReading gpsReading = dbh.getLatestGPSReading(tracker.TrackerID);

            if (gpsReading == null) // check if the tracker has a gps reading
                continue;

            try {
                LatLng trackerLatLng = new LatLng(gpsReading.Latitude, gpsReading.Longitude);
                // get image id for the tracker's icon
                int trackerImageID = getResources().getIdentifier(tracker.TrackerIcon + "_round", "mipmap", getPackageName());

                // if minimap doesn't contain a marker for the tracker, create it here
                if (!trackerMarkers.containsKey(tracker.TrackerID)) {
                    // create the marker
                    Marker trackerMarker = miniMap.addMarker(new MarkerOptions()
                            .position(trackerLatLng)
                            .title(String.valueOf(tracker.TrackerName))
                            .snippet("Distance: ??"));
                            //.icon(BitmapDescriptorFactory.fromResource((trackerImageID != 0) ? trackerImageID : R.mipmap.ic_launcher)));
                    trySetMarkerIcon(trackerMarker, trackerImageID);
                    // set the marker in the trackerMarkers hashmap
                    trackerMarkers.put(tracker.TrackerID, trackerMarker);

                } else { // update the trackers's marker on the minimap
                    Marker trackerMarker = trackerMarkers.get(tracker.TrackerID);
                    trackerMarker.setPosition(trackerLatLng);
                    trackerMarker.setTitle(String.valueOf(tracker.TrackerName));
                    // TODO: updating markers icon here (?) (currently no way of knowing if the user changed the icon)
                    //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource((trackerImageID != 0) ? trackerImageID : R.mipmap.ic_launcher));
                    trySetMarkerIcon(trackerMarker, trackerImageID);
                }
            } catch (Exception ex) {
                Log.e(TAG, "updateAllMiniMapMarkers() can't update trackers location Exception: " + ex.getMessage());
            }
        }

        // check if tracker was deleted from the db, but is still present on the minimap
        // and delete it from the minimap.
        try {
            HashSet<String> trackerIds = new HashSet<String>(); // set that contains tracker ids that are in the db
            for (Tracker tracker : trackersDB) {
                trackerIds.add(tracker.TrackerID);
            }

            // check if trackerMarkers countain TrackerIDs that are not any longer present in the db
            // and remove them from the minimap
            for (String markerTrackerID : trackerMarkers.keySet()) {
                if (!trackerIds.contains(markerTrackerID)) {
                    // delete the trackers marker from the minimap
                    trackerMarkers.get(markerTrackerID).remove();
                    trackerMarkers.remove(markerTrackerID);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateAllMiniMapMarkers() can't remove deleted tracker from the minimap Exception: " + ex.getMessage());
        }
    }

    public void updateTrackerMiniMapMarker(String TrackerID, LatLng newMarkerLatLng) {
        // check if minimap is ready
        if (miniMap == null)
            return;

        try {
            // chekck if trackerMarkers contains a marker for the Tracker
            if (trackerMarkers.containsKey(TrackerID)) {
                // check if database still contains the tracker, if no delete the marker
                DatabaseHelper dbh = new DatabaseHelper(this);
                if (!dbh.hasTrackerID(TrackerID)) {
                    trackerMarkers.get(TrackerID).remove();
                    trackerMarkers.remove(TrackerID);
                } else { // update the position of the trackers's marker on the minimap
                    Marker trackerMarker = trackerMarkers.get(TrackerID);

                    // check if coordinates actually changed and then update
                    if (!trackerMarker.getPosition().equals(newMarkerLatLng)) {
                        trackerMarker.setPosition(newMarkerLatLng);

                        // set marker's snippet to display distance
                        if (androidLatestLocation != null) {
                            float[] distance = new float[1];
                            Location.distanceBetween(newMarkerLatLng.latitude, newMarkerLatLng.longitude, androidLatestLocation.getLatitude(), androidLatestLocation.getLongitude(), distance);
                            trackerMarker.setSnippet("Distance: " + distance[0]);
                        }
                    }
                }
            } else { // TODO: create the marker here, calling update all for now
                updateAllMiniMapMarkers();
            }
        } catch (Exception ex) {
            Log.e(TAG, "updateTrackerMiniMapMarker() can't update trackers location Exception: " + ex.getMessage());
        }
    }

    void trySetMarkerIcon(Marker marker, int iconId) {
        try{
            //marker.setIcon(BitmapDescriptorFactory.fromResource((iconId != 0) ? iconId : R.mipmap.ic_launcher));
            Bitmap trackerBitmapIcon = Tracker.getBitmapFromID(getApplicationContext(), iconId);
            trackerBitmapIcon = Bitmap.createScaledBitmap(trackerBitmapIcon, 125, 125, false);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(trackerBitmapIcon));
        } catch (Exception ex) {
            Log.e(TAG, "trySetMarkerIcon() can't set icon to tracker's marker Exception: " + ex.getMessage());
        }
    }

}
