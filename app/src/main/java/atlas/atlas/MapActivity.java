package atlas.atlas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Atlas"+MapActivity.class.getSimpleName();

    private GoogleMap mMap;
    String TrackerID;
    Double Latitude;
    Double Longitude;
    Double AndroidLatitude;
    Double AndroidLongitude;

    Marker androidMarker;

    Marker trackerMarker;
    Circle allowedDistanceCircle;


    GPSReadingBroadcastReceiver gpsReadingBroadcastReceiver;
    AndroidLocationBroadcastsReceiver androidLocationBroadcastsReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gpsReadingBroadcastReceiver = new GPSReadingBroadcastReceiver();
        androidLocationBroadcastsReceiver = new AndroidLocationBroadcastsReceiver();

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




        //get parameters from the intent (sent in TrackerListAdapter)
        Intent intent = getIntent();
        if (intent != null) {
            TrackerID = intent.getStringExtra("TrackerID");
            Latitude = intent.getDoubleExtra("Latitude", 0.0);
            Longitude = intent.getDoubleExtra("Longitude", 0.0);
            AndroidLatitude = intent.getDoubleExtra("AndroidLatitude", 0.0);
            AndroidLongitude = intent.getDoubleExtra("AndroidLongitude", 0.0);
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng androidLoc = new LatLng(AndroidLatitude, AndroidLongitude);
        LatLng trackerLoc = new LatLng(Latitude, Longitude);
        androidMarker = mMap.addMarker(new MarkerOptions().position(androidLoc).title("Android"));
        trackerMarker = mMap.addMarker(new MarkerOptions().position(trackerLoc).title("TrackerID"+TrackerID));

        //int SelectedImageID =  getResources().getIdentifier("bob", "mipmap", getPackageName());
        //if(SelectedImageID = )
        //getResources().getResourceEntryName(selectedImageID);


        DatabaseHelper dbh = new DatabaseHelper(this);
        Tracker tracker = dbh.getTrackerByID(TrackerID);


        //Set the icon of the marker depending on it's type (depending on image selected in tacker activity)
        if((tracker.TrackerIcon).equals("ic_tracker_1"))
            trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.child));


        else if((tracker.TrackerIcon).equals("ic_tracker_2"))

            trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pets));

        else if((tracker.TrackerIcon).equals("ic_tracker_3"))
            trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.item));










            allowedDistanceCircle = mMap.addCircle(new CircleOptions()
                .center(androidLoc)
                .radius(tracker.AllowedDistance)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 100, 255, 255)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trackerLoc, 15));
    }

    /**
     *  Broadcast Receiver for the GPSReading updates that are sent from ReceiverServiceMockup
     *  the NEW_GPSREADING broadcasts contain GPSReadingID, TrackerID, Latitude and Longitude of the tracker
     * */
    class GPSReadingBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "New GPSReading broadcast recieved");

            // get the data from the broadcasts intent
            String updatedTrackerID = intent.getStringExtra("TrackerID");

            // check if the updated tracker is the tracker that the map displays
            if (updatedTrackerID.equals(TrackerID)) {
                Long GPSReadingID = intent.getLongExtra("GPSReadingID", 0);
                Double Latitude = intent.getDoubleExtra("Latitude", 0.0);
                Double Longitude = intent.getDoubleExtra("Longitude", 0.0);
                if (trackerMarker != null)
                    trackerMarker.setPosition(new LatLng(Latitude, Longitude));

                //Geocoding
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


                try {
                    //Code for Reverse Geocoding (tranforms latitude and longitude coordiantes into street address)
                    List<Address> listAddresses = geocoder.getFromLocation(trackerMarker.getPosition().latitude, trackerMarker.getPosition().longitude, 1);

                    if(listAddresses != null && listAddresses.size() >0){
                        // log returning the whole address (indice is zero because we want only one address)
                        Log.i("PlaceInfo", listAddresses.get(0).toString());

                        String address = "";







                        if (listAddresses.get(0).getSubThoroughfare() != null) {
                            //returns the street number of the address
                            address += listAddresses.get(0).getSubThoroughfare() + " ";

                        }



                        if (listAddresses.get(0).getThoroughfare() != null) {
                            //returns the thoroughfare name of the address
                            address += listAddresses.get(0).getThoroughfare() + ", ";

                        }

                        if (listAddresses.get(0).getLocality() != null) {
                            //returns the locality of the address
                            address += listAddresses.get(0).getLocality() + ", ";

                        }

                        if (listAddresses.get(0).getPostalCode() != null) {
                            //returns the postal code
                            address += listAddresses.get(0).getPostalCode() + ", ";

                        }

                        if (listAddresses.get(0).getCountryName() != null) {
                            //returns the country name
                            address += listAddresses.get(0).getCountryName();

                        }

                        Toast.makeText(MapActivity.this, address, Toast.LENGTH_SHORT).show();

                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }


                // or retrieve the data again from the db
                DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
                GPSReading gpsReading = dbh.getLatestGPSReading(TrackerID);
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
                // get the latest location data, (it's also present in broadcast's intent)
                Location androidLatestLocation = AndroidLocationService.getLastKnownLocation(getApplicationContext());
                if(androidMarker != null)
                    androidMarker.setPosition(new LatLng(androidLatestLocation.getLatitude(), androidLatestLocation.getLongitude()));



            // get wheter gps/network location updates are disabled/enabled
            } else if (action.equals(AndroidLocationService.BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE)) {
                // String Provider = intent.getStringExtra("Provider");
                // boolean Enabled = intent.getBooleanExtra("Enabled", false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the broadcast receivers after finishing of the activity
        if (gpsReadingBroadcastReceiver != null) {
            unregisterReceiver(gpsReadingBroadcastReceiver);
        }
        if (androidLocationBroadcastsReceiver != null) {
            unregisterReceiver(androidLocationBroadcastsReceiver);
        }
    }
}