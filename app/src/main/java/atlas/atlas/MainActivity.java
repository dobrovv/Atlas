package atlas.atlas;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnViewInflateListener;


//import me.toptas.ShowCaseView.ShowCaseView;



//comment 12

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //splash team time out
  //  private static int SPLASH_TIME_OUT = 5;

    private static final String TAG = "Atlas"+MainActivity.class.getSimpleName();

    //This is only used to display instructions for the app's first use
    FancyShowCaseView mFancyShowCaseView;

    ShowcaseView.Builder showCaseView;


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
   swipeControl swipeControl = null;
    // map
    TextView mapTextView; // placeholder, use for debugging
    View miniMapView;
    GoogleMap miniMap;
    LinearLayout miniMapLayout;

    Location androidLatestLocation; // stores the latest known androids location (may be null)

    boolean FirstRun = false;

    final int SHOWCASEVIEWID_1D = 34;

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








                if(isFirstTracker()) {


                    //trackerListAdapter.getItemCount();



                   // mFancyShowCaseView







                            new FancyShowCaseView.Builder(MainActivity.this)
                                    .title("Swipe right to edit, and left to  delete a tracker")
                                    .focusOn(findViewById(R.id.trackerListMain))
                                    .customView(R.layout.custom_view, new OnViewInflateListener() {


                                        @Override
                                        public void onViewInflated(View view) {

                                            //ImageView imageView;

                                            //imageView=view.findViewById(R.id.image);

                                            //imageview.setpo
                                        }
                                    })
                                //    .customView(R.id.custom_view)
                                   // .focusRectAtPosition(260, 85,  480, 80)
                                   // .focusCircleRadiusFactor()
                                   // .se
                                    //.backgroundColor(Color.parseColor("#77b0bf1a"))
                                    .build()
                                    .show();










                }













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
        setupRecyclerView();
        trackerListMain = findViewById(R.id.trackerListMain);
        trackerListMain.setBackgroundColor(android.R.color.holo_red_dark);
        RelativeLayout layout = new RelativeLayout(this);
       // layout = findViewById(R.id.RLayout);
       // layout.setBackgroundColor(android.R.color.white);
        //startServiceButton = findViewById(R.id.startServiceButton);
        //stopServiceButton = findViewById(R.id.stopServiceButton);





        if(checkFirstRun()) {

            FirstRun = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    new FancyShowCaseView.Builder(MainActivity.this)
                       //     .withMaterialShowCase(

                 /*   showCaseView = new ShowcaseView.Builder(MainActivity.this)
                            .withMaterialShowcase()
                            .setTarget(new ViewTarget(findViewById(R.id.addTracker)))
                            .setContentTitle("We are here")
                            .setContentText("Press here to add a tracker")
                            .setStyle(R.style.ShowCaseViewStyle)
                            .singleShot(SHOWCASEVIEWID_1D)
                            .setShowcaseEventListener(new OnShowcaseEventListener() {
                                @Override
                                public void onShowcaseViewHide(ShowcaseView showcaseView) {

                                }

                                @Override
                                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                }

                                @Override
                                public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                }

                                @Override
                                public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                }
                            });

                            showCaseView.build();*/
                    //showShowCaseView();











                            .title("Press here to add a  tracker")
                            .focusOn(findViewById(R.id.addTracker))
                            //.backgroundColor(Color.parseColor("#77b0bf1a"))
                            .build()
                            .show();

                }
            }, 2000);

        }
        else
            FirstRun = false;





































        Context context;
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
        ItemTouchHelper ith = new ItemTouchHelper(swipeControl);
        try {
            ith.attachToRecyclerView(trackerListMain);

            trackerListMain.setBackgroundColor(android.R.color.white);
        } catch(Exception ex){
            Log.e(TAG, "ItemTouchHolder can't connect to recyclerView: " + ex.getMessage());
        }
        //add dividers between tracker items https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
        trackerListMain.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        trackerListAdapter = new TrackerListAdapter(this, this);
        trackerListMain.setAdapter(trackerListAdapter);


      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(welcomeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);*/

    }


    private void setupRecyclerView(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.trackerListMain);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(trackerListAdapter);

        swipeControl = new swipeControl(new swipeControllerActions() {
            @Override
            public void onRightClicked(int position) {

                Toast.makeText(MainActivity.this, " Delete! pos " + position, Toast.LENGTH_SHORT).show();
                trackerListAdapter.deleteTracker(position);
                //trackerListAdapter.notifyItemRangeChanged(position,);

            }
            public void onLeftClicked(int position) {

                Toast.makeText(MainActivity.this, " Info!!", Toast.LENGTH_SHORT).show();
                trackerListAdapter.editButtonClick(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeControl);
        itemTouchhelper.attachToRecyclerView(trackerListMain);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeControl.onDraw(c);
            }
        });

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

            Bundle bundle = new Bundle();
            bundle.putBoolean("FirstRun",FirstRun);

            AddTrackerDialog dialog = new AddTrackerDialog();
            dialog.setArguments(bundle);



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
                //Tracker tracker(0, "android", "", String iconNum,Double allowedDistance, String trackerType, Integer enableNotification);
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
                int trackerImageID = getResources().getIdentifier(tracker.TrackerIcon + "_foreground", "mipmap", getPackageName());

                // if minimap doesn't contain a marker for the tracker, create it here
                if (!trackerMarkers.containsKey(tracker.TrackerID)) {
                    // create the marker
                    Marker trackerMarker = miniMap.addMarker(new MarkerOptions()
                            .position(trackerLatLng)
                            .title(String.valueOf(tracker.TrackerName))
                            .snippet("Distance: ??"));
                            //.icon(BitmapDescriptorFactory.fromResource((trackerImageID != 0) ? trackerImageID : R.mipmap.ic_launcher)));
                    trySetMarkerIcon(trackerMarker, trackerImageID, tracker);
                    // set the marker in the trackerMarkers hashmap
                    trackerMarkers.put(tracker.TrackerID, trackerMarker);

                } else { // update the trackers's marker on the minimap
                    Marker trackerMarker = trackerMarkers.get(tracker.TrackerID);
                    trackerMarker.setPosition(trackerLatLng);
                    trackerMarker.setTitle(String.valueOf(tracker.TrackerName));
                   // Toast.makeText(MainActivity.this,"Notifi are: "+ tracker.EnableNotification, Toast.LENGTH_SHORT).show();
                    // TODO: updating markers icon here (?) (currently no way of knowing if the user changed the icon)
                    //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource((trackerImageID != 0) ? trackerImageID : R.mipmap.ic_launcher));
                    trySetMarkerIcon(trackerMarker, trackerImageID, tracker);
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

    void trySetMarkerIcon(Marker marker, int iconId, Tracker tracker) {
        try{
            //marker.setIcon(BitmapDescriptorFactory.fromResource((iconId != 0) ? iconId : R.mipmap.ic_launcher));
            Bitmap trackerBitmapIcon = Tracker.getBitmapFromID(getApplicationContext(), iconId);

            if(tracker.TrackerIconNum != null) {



                if((tracker.TrackerIcon).equals("ic_tracker_1")) {


                    if ((tracker.TrackerIconNum).equals("girl1")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);


                    } else if ((tracker.TrackerIconNum).equals("boy1")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);


                    } else if ((tracker.TrackerIconNum).equals("boy2")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy2);


                    } else if ((tracker.TrackerIconNum).equals("boy3")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy3);


                    } else if ((tracker.TrackerIconNum).equals("boy4")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy4);


                    } else if ((tracker.TrackerIconNum).equals("girl2")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl2);


                    } else if ((tracker.TrackerIconNum).equals("girl3")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl3);


                    } else if ((tracker.TrackerIconNum).equals("girl4")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl4);


                    } else if ((tracker.TrackerIconNum).equals("girl5")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl5);


                    } else /*if ((tracker.TrackerIconNum).equals("boy0"))*/ {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy0);


                    }

                }


                else if((tracker.TrackerIcon).equals("ic_tracker_2")) {


                 if ((tracker.TrackerIconNum).equals("pet1")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet1);


                    } else if ((tracker.TrackerIconNum).equals("pet2")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet2);


                    } else if ((tracker.TrackerIconNum).equals("pet3")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet3);


                    } else if ((tracker.TrackerIconNum).equals("pet4")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet4);


                    } else if ((tracker.TrackerIconNum).equals("pet5")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet5);


                    } else /*if ((tracker.TrackerIconNum).equals("pet0"))*/ {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet0);


                    }

                }



                else if((tracker.TrackerIcon).equals("ic_tracker_3")) {


                 if ((tracker.TrackerIconNum).equals("item1")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item1);


                    } else if ((tracker.TrackerIconNum).equals("item2")) {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item2);


                    }  else /*((tracker.TrackerIconNum).equals("item0"))*/ {


                        trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item0);


                    }

                }



















            }



               // else
                 //   trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy0);












            trackerBitmapIcon = Bitmap.createScaledBitmap(trackerBitmapIcon, 125, 125, false);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(trackerBitmapIcon));
        } catch (Exception ex) {
            Log.e(TAG, "trySetMarkerIcon() can't set icon to tracker's marker Exception: " + ex.getMessage());
        }
    }







    void trySetMarkerIcon(Marker marker, int iconId) {
        try{
            //marker.setIcon(BitmapDescriptorFactory.fromResource((iconId != 0) ? iconId : R.mipmap.ic_launcher));
            Bitmap trackerBitmapIcon = Tracker.getBitmapFromID(getApplicationContext(), iconId);
/*
            if(Tracker.TrackerIconNum != null)
                if((Tracker.TrackerIconNum).equals("girl1")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);




                }

                else if((Tracker.TrackerIconNum).equals("boy1")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);




                }

                else if((tracker.TrackerIconNum).equals("boy2")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy2);




                }

                else if((tracker.TrackerIconNum).equals("boy3")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy3);




                }

                else if((tracker.TrackerIconNum).equals("boy4")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy4);




                }




                else if((tracker.TrackerIconNum).equals("girl2")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl2);




                }



                else if((tracker.TrackerIconNum).equals("girl3")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl3);




                }



                else if((tracker.TrackerIconNum).equals("girl4")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl4);



                }



                else if((tracker.TrackerIconNum).equals("girl5")) {


                     trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl5);




                }

                else
                     trackerBitmapIcon = Tracker.getBitmapFromID(getApplicationContext(), iconId);

*/










            trackerBitmapIcon = Bitmap.createScaledBitmap(trackerBitmapIcon, 125, 125, false);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(trackerBitmapIcon));
        } catch (Exception ex) {
            Log.e(TAG, "trySetMarkerIcon() can't set icon to tracker's marker Exception: " + ex.getMessage());
        }
    }











    private boolean checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;
        boolean firstRun;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            firstRun = false;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)
            firstRun = true;

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
            firstRun = false;
        }
        else
            firstRun = false;

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return firstRun;
    }













    private boolean isFirstTracker() {


        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_FirstEver_Tracker_KEY = "FirstEverTracker";
        final int DOESNT_EXIST = 0;


        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedFirstEverTracker = prefs.getInt(PREF_FirstEver_Tracker_KEY, DOESNT_EXIST);

        prefs.edit().putInt(PREF_FirstEver_Tracker_KEY, 1).apply();


        if(savedFirstEverTracker == 0){
            return true;
        }
        else
            return false;


    }







    public void showShowCaseView(){


        showCaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ViewTarget(R.id.addTracker,this))
                .setContentTitle("We are here")
                .setContentText("Press here to add a tracker")
                .setStyle(R.style.ShowCaseViewStyle)
                //.singleShot(SHOWCASEVIEWID_1D)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                });

        showCaseView.build().show();





    }


 /*   private void showWithCustoView(View v){
        mFancyShowCaseView = new FancyShowCaseView.Builder(this)
                .focusOn(v)
                .customView(R.layout.custom_view), new OnViewInflateListener() {
            @





        }







    }*/





















}




