package atlas.atlas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

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




    boolean isMarkerRotating = false;





    private Polyline currentPolyline;


    Handler handler = new Handler();


    final int MARKER_UPDATE_INTERVAL = 5000;



    LatLng oldLocation;



    Runnable updateMarker = new Runnable() {
        @Override
        public void run() {
            //place1.setPosition(new LatLng(22,78));
            //place1 = map.addMarker(new MarkerOptions().position(location));

            new FetchURL(MapActivity.this).execute(getUrl(androidMarker.getPosition(), trackerMarker.getPosition(), "driving"), "driving");













             oldLocation = new LatLng(androidMarker.getPosition().latitude,androidMarker.getPosition().longitude );


            //LatLng newLocation = new LatLng(reverseGeocoding.NewLatitude, reverseGeocoding.NewLongitude);








          //  float bearing = (float) bearingBetweenLocations(oldLocation, newLocation);
            //rotateMarker(androidMarker, bearing);












            handler.postDelayed(this, MARKER_UPDATE_INTERVAL);
        }
    };








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







        handler.postDelayed(updateMarker, MARKER_UPDATE_INTERVAL);








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

        //Before code modification, This was just before the code to set the icons
        DatabaseHelper dbh = new DatabaseHelper(this);
        Tracker tracker = dbh.getTrackerByID(TrackerID);

        // Add a marker in Sydney and move the camera
        LatLng androidLoc = new LatLng(AndroidLatitude, AndroidLongitude);
        LatLng trackerLoc = new LatLng(Latitude, Longitude);
        androidMarker = mMap.addMarker(new MarkerOptions().position(androidLoc).title("Your Location"));
        trackerMarker = mMap.addMarker(new MarkerOptions().position(trackerLoc).title(tracker.TrackerName));


        //androidMarker = mMap.addMarker(new MarkerOptions().position(androidLoc).title("Android"));
        //trackerMarker = mMap.addMarker(new MarkerOptions().position(trackerLoc).title("TrackerID"+TrackerID));

        //int SelectedImageID =  getResources().getIdentifier("bob", "mipmap", getPackageName());
        //if(SelectedImageID = )
        //getResources().getResourceEntryName(selectedImageID);













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



        Button buttonShowAddress;


        buttonShowAddress = findViewById(R.id.buttonShowAddress);
        //final Marker finalMarker = marker2;
        buttonShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // if(reverseGeocoding.NewLatitude != 0 && reverseGeocoding.NewLongitude != 0)
                //reverseGeocoding.getAddress(MapActivity.this);

                    try {
                        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                        //Code for Reverse Geocoding (tranforms latitude and longitude coordiantes into street address)
                        List<Address> listAddresses = geocoder.getFromLocation(Latitude,Longitude, 1);

                        if(listAddresses != null && listAddresses.size() >0){
                            // log returning the whole address (index is zero because we want only one address)
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
                                address += listAddresses.get(0).getCountryName()+" ";

                                //parameters of method getDistance can be obviously improved

                                DecimalFormat df = new DecimalFormat("0.####");

                                address += df.format(getDistance(androidMarker.getPosition(),trackerMarker.getPosition()))+" km away";





                            }

              // if(OldLatitude !=0 && OldLongitude !=0)
                {   //returns the speed of the address in #.00 format
                  //  address += numberFormat.format(speed)+" Km/h";


                  //  address += " Latitude"+ NewLatitude +" Longitude"+NewLongitude;
                }

                            Toast.makeText(MapActivity.this, address, Toast.LENGTH_SHORT).show();

                        }

                    } catch (
                            IOException e) {

                        e.printStackTrace();

                    }

               // else{
                 //   reverseGeocoding.NewLatitude = Latitude;
                   // reverseGeocoding.NewLongitude = Longitude;
                  //  reverseGeocoding.getAddress(MapActivity.this);









                //}


            }

        });






        final Button buttonShowDirections;



        buttonShowDirections = findViewById(R.id.buttonShowDirections);
        //final Marker finalMarker = marker2;
        buttonShowDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {







                if(!ShowDirections) {
                    ShowDirections = true;
                    new FetchURL(MapActivity.this).execute(getUrl(androidMarker.getPosition(), trackerMarker.getPosition(), "driving"), "driving");

                }
                else
                    ShowDirections =false;










            }
        });














    }

    /**
     *  Broadcast Receiver for the GPSReading updates that are sent from ReceiverServiceMockup
     *  the NEW_GPSREADING broadcasts contain GPSReadingID, TrackerID, Latitude and Longitude of the tracker
     * */





ReverseGeocoding reverseGeocoding = new ReverseGeocoding(MapActivity.this,trackerMarker);

    boolean ShowDirections = false;









    /**
     * Gets distance in meters, coordinates in RADIAN
     */




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


                //Log.i("ppoosition", Double.toString(Latitude));




               //To change or remove after we get speed from gsm
               reverseGeocoding.UpdateGeocoder( Latitude,  Longitude);











                if (trackerMarker != null)
                    trackerMarker.setPosition(new LatLng(Latitude, Longitude));




                if(trackerMarker != null && ShowDirections)
                new FetchURL(MapActivity.this).execute(getUrl(androidMarker.getPosition(), trackerMarker.getPosition(), "driving"), "driving");
                else if (!ShowDirections && currentPolyline != null)
                currentPolyline.remove();


















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
                if(androidMarker != null) {
                    androidMarker.setPosition(new LatLng(androidLatestLocation.getLatitude(), androidLatestLocation.getLongitude()));
                    //if(oldLocation != null )
                     // float bearing = (float) bearingBetweenLocations(oldLocation, androidMarker.getPosition());
                   // rotateMarker(androidMarker, bearing);


                    Log.i(TAG, "Updateeee ");

                    //androidMarker.setRotation(bearing);



                }



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




    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
         currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);




    }




    private static double getDistance(LatLng latLngA,LatLng latLngB) {



        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        double distance = locationA.distanceTo(locationB)/1000;

        return distance;



        /*double R = 6371000; // for haversine use R = 6372.8 km instead of 6371 km
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        //double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        // simplify haversine:
        //return 2 * R * 1000 * Math.asin(Math.sqrt(a));*/
    }







    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {



        double PI = 3.14159;
        double lat1;
        double long1;
        double lat2 ;
        double long2 ;

        double dLon;


       // if(latLng1.longitude != 0 && latLng1.latitude !=0) {
            lat1 = latLng1.latitude * PI / 180;
            long1 = latLng1.longitude * PI / 180;
            lat2 = latLng2.latitude * PI / 180;
            long2 = latLng2.longitude * PI / 180;
       // }



         dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }



    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final LinearInterpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing =  -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    Log.d(TAG, "Rotatingggg ");

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

















}