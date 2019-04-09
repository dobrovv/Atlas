package atlas.atlas;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;


import android.hardware.SensorEventListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, SensorEventListener {

    private static final String TAG = "Atlas" + MapActivity.class.getSimpleName();

    private GoogleMap mMap;
    String TrackerID;
    Double Latitude;
    Double Longitude;
    Double AndroidLatitude;
    Double AndroidLongitude;

    Marker androidMarker;

    Marker trackerMarker;
    Circle allowedDistanceCircle;


    Tracker tracker;


    GPSReadingBroadcastReceiver gpsReadingBroadcastReceiver;
    AndroidLocationBroadcastsReceiver androidLocationBroadcastsReceiver;


    private Polyline currentPolyline;


    Handler handler = new Handler();


    final int DIRECTIONS_UPDATE_INTERVAL = 2000;

    final int ANGLE_UPDATE_INTERVAL = 50;


    LatLng oldLocation;


    //private SensorManager senSensorManager;
    //private Sensor senAccelerometer;

    public SensorManager mSensorManager;
    public Sensor accelerometer;
    public Sensor magnetometer;

    //SensorEventListener sensorEventListener;

    public float[] mAccelerometer = null;
    public float[] mGeomagnetic = null;


    public static float swRoll;
    public static float swPitch;
    public static float swAzimuth;


    public double azimuth = 0;


    boolean ShowDirections = false;

    boolean internetLoop = true;


    //Location Androidlocation;

    LocationListener locationListener;


    // Handler to update the routes
    Handler fetchDirections = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what != 1) { // code if not connected

                // internetLoop = true;


                if (ShowDirections) {
                    if (internetLoop)
                        Toast.makeText(MapActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
                    internetLoop = false;

                } else if (/*!ShowDirections &&*/ currentPolyline != null)
                    currentPolyline.remove();


                //Location prevLoc = android;
                //Location newLoc = ... ;
                //float bearing = prevLoc.bearingTo(newLoc) ;


                oldLocation = new LatLng(androidMarker.getPosition().latitude, androidMarker.getPosition().longitude);




            } else { // code if connected

                if (!internetLoop)
                    internetLoop = true;


                if (ShowDirections && androidMarker.getPosition() != oldLocation)
                    new FetchURL(MapActivity.this).execute(getUrl(androidMarker.getPosition(), trackerMarker.getPosition(), "driving"), "driving");

                else if (!ShowDirections && currentPolyline != null)
                    currentPolyline.remove();


                oldLocation = new LatLng(androidMarker.getPosition().latitude, androidMarker.getPosition().longitude);


            }
        }
    };


    // Handler to update the address from the reverse geocoding
    Handler updateGeocoder = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what != 1) { // code if not connected to the internet

                Toast.makeText(MapActivity.this, "No internet available", Toast.LENGTH_SHORT).show();


            } else { // code if connected to the internet

                try {
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    //Code for Reverse Geocoding (tranforms latitude and longitude coordiantes into street address)
                    List<Address> listAddresses = geocoder.getFromLocation(Latitude, Longitude, 1);

                    if (listAddresses != null && listAddresses.size() > 0) {
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
                            address += listAddresses.get(0).getCountryName() + " ";

                            //parameters of method getDistance can be obviously improved




                            //if bigger than 1 km
                            if(getDistance(androidMarker.getPosition(), trackerMarker.getPosition())>1000) {
                                DecimalFormat df = new DecimalFormat("0.#");
                                address += df.format(getDistance(androidMarker.getPosition(), trackerMarker.getPosition()) / 1000) + " km away";
                            }
                            // else smaller than 1 km
                            else {
                                DecimalFormat df = new DecimalFormat("0");
                                address += df.format(getDistance(androidMarker.getPosition(), trackerMarker.getPosition())) + " m away";
                            }



                            //DatabaseHelper dbh = new DatabaseHelper(this);\
                            //address+= "Number "+tracker.TrackerIconNum;

                           // TrackerID;





                            //androidMarker.setRotation(getBearing(new LatLng(AndroidLocationService.getLastKnownLocation(getApplicationContext()).getLatitude(),AndroidLocationService.getLastKnownLocation(getApplicationContext()).getLongitude()) ,oldLocation));


                            //androidMarker.setRotation(AndroidLocationService.getLastKnownLocation(getApplicationContext()).getBearing());


                            //getBearing(new LatLng(AndroidLocationService.getLastKnownLocation(getApplicationContext()).getLatitude(),AndroidLocationService.getLastKnownLocation(getApplicationContext()).getLongitude()) ,oldLocation);


                           // if(oldLocation != null)
                            //address += "speed : "+AndroidLocationService.getLastKnownLocation(getApplicationContext()).getSpeed()+" Bearing: "+AndroidLocationService.getLastKnownLocation(getApplicationContext()).getBearing();


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


            }
        }
    };


    Runnable updateDirections = new Runnable() {
        @Override
        public void run() {


            isNetworkAvailable(fetchDirections, 2000);



/*

            if(ShowDirections && androidMarker.getPosition() != oldLocation)
            new FetchURL(MapActivity.this).execute(getUrl(androidMarker.getPosition(), trackerMarker.getPosition(), "driving"), "driving");

            else if(!ShowDirections && currentPolyline != null)
                currentPolyline.remove();





             oldLocation = new LatLng(androidMarker.getPosition().latitude,androidMarker.getPosition().longitude );








*/


            handler.postDelayed(this, DIRECTIONS_UPDATE_INTERVAL);
        }
    };


    Runnable updateAngle = new Runnable() {
        @Override
        public void run() {
            //place1.setPosition(new LatLng(22,78));
            //place1 = map.addMarker(new MarkerOptions().position(location));

            //if(AndroidLocationService.getLastKnownLocation(getApplicationContext()).getSpeed() !=0.0)
           //
            //
             androidMarker.setRotation((float) azimuth /*+ (float) 180*/);



            handler.postDelayed(this, ANGLE_UPDATE_INTERVAL);
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






        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);




        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);












        handler.postDelayed(updateDirections, DIRECTIONS_UPDATE_INTERVAL);

        //isNetworkAvailable(updateMarker,2000);

        handler.postDelayed(updateAngle, ANGLE_UPDATE_INTERVAL);








        //get parameters from the intent (sent in TrackerListAdapter)
        Intent intent = getIntent();
        if (intent != null) {
            TrackerID = intent.getStringExtra("TrackerID");
            Latitude = intent.getDoubleExtra("Latitude", 0.0);
            Longitude = intent.getDoubleExtra("Longitude", 0.0);
            AndroidLatitude = intent.getDoubleExtra("AndroidLatitude", 0.0);
            AndroidLongitude = intent.getDoubleExtra("AndroidLongitude", 0.0);
        }

        DatabaseHelper db = new DatabaseHelper(this);

        if( !db.hasTrackerID( TrackerID))
            finish();






        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);


        if(isFirstMap()){

            Toast.makeText(MapActivity.this, "You might need to do an eight motion with your phone to recalibrate the compass", Toast.LENGTH_LONG).show();


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
         tracker = dbh.getTrackerByID(TrackerID);

        // Add a marker in Sydney and move the camera
        LatLng androidLoc = new LatLng(AndroidLatitude, AndroidLongitude);
        LatLng trackerLoc = new LatLng(Latitude, Longitude);
        androidMarker = mMap.addMarker(new MarkerOptions().position(androidLoc).flat(true).title("Your Location"));


        Bitmap  androidBitmapIcon;

        androidBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.usermarker);

        androidBitmapIcon = Bitmap.createScaledBitmap(androidBitmapIcon, 125, 125, false);

        androidMarker.setIcon(BitmapDescriptorFactory.fromBitmap(androidBitmapIcon));



        trackerMarker = mMap.addMarker(new MarkerOptions().position(trackerLoc).title(tracker.TrackerName));
















        //Set the icon of the marker depending on it's type (depending on image selected in tacker activity)
        if((tracker.TrackerIcon).equals("ic_tracker_1")) {


            Bitmap  trackerBitmapIcon;// = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);

            if(tracker.TrackerIconNum.equals("girl1"))
            //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl1));
            //Bitmap image = BitmapDescriptorFactory.fromResource(R.drawable.girl1);
            trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);

            else if(tracker.TrackerIconNum.equals("boy1"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy1));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);
            else if(tracker.TrackerIconNum.equals("boy2"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy2));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy2);
            else if(tracker.TrackerIconNum.equals("boy3"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy3));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy3);
            else if(tracker.TrackerIconNum.equals("boy4"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy4));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy4);
            else if(tracker.TrackerIconNum.equals("girl2"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl2));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl2);
            else if(tracker.TrackerIconNum.equals("girl3"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl3));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl3);
            else if(tracker.TrackerIconNum.equals("girl4"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl4));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl4);
            else if(tracker.TrackerIconNum.equals("girl5"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl5));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl5);
            else
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.child));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy0);

            trackerBitmapIcon = Bitmap.createScaledBitmap(trackerBitmapIcon, 125, 125, false);

            trackerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(trackerBitmapIcon));



        }


        else if((tracker.TrackerIcon).equals("ic_tracker_2")) {


            Bitmap  trackerBitmapIcon;// = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);

            if(tracker.TrackerIconNum.equals("pet1"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl1));
                //Bitmap image = BitmapDescriptorFactory.fromResource(R.drawable.girl1);
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet1);

            else if(tracker.TrackerIconNum.equals("pet2"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy1));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet2);
            else if(tracker.TrackerIconNum.equals("pet3"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy2));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet3);
            else if(tracker.TrackerIconNum.equals("pet4"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy3));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet4);
            else if(tracker.TrackerIconNum.equals("pet5"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy4));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet5);
            else if(tracker.TrackerIconNum.equals("pet0"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl2));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet0);
            else
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.child));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet0);

            trackerBitmapIcon = Bitmap.createScaledBitmap(trackerBitmapIcon, 125, 125, false);

            trackerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(trackerBitmapIcon));



        }





        else if((tracker.TrackerIcon).equals("ic_tracker_3")) {


            Bitmap  trackerBitmapIcon;// = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);

            if(tracker.TrackerIconNum.equals("item1"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.girl1));
                //Bitmap image = BitmapDescriptorFactory.fromResource(R.drawable.girl1);
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item1);

            else if(tracker.TrackerIconNum.equals("item2"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy1));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item2);
            else if(tracker.TrackerIconNum.equals("item0"))
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.boy2));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item0);
            else
                //trackerMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.child));
                trackerBitmapIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.item0);

            trackerBitmapIcon = Bitmap.createScaledBitmap(trackerBitmapIcon, 125, 125, false);

            trackerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(trackerBitmapIcon));



        }























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

                isNetworkAvailable(updateGeocoder,2000);













            }

        });






        final Button buttonShowDirections;



        buttonShowDirections = findViewById(R.id.buttonShowDirections);
        //final Marker finalMarker = marker2;
        buttonShowDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!internetLoop)
                    Toast.makeText(MapActivity.this, "No internet available", Toast.LENGTH_SHORT).show();








                if(!ShowDirections) {
                    ShowDirections = true;
                   // new FetchURL(MapActivity.this).execute(getUrl(androidMarker.getPosition(), trackerMarker.getPosition(), "driving"), "driving");

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













                if (trackerMarker != null)
                    trackerMarker.setPosition(new LatLng(Latitude, Longitude));



















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

                   // Toast.makeText(MapActivity.this, "update address", Toast.LENGTH_SHORT).show();



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



//Function to fetch the Url of the route directions
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



//Funciton to get distance between two LatLngs
    private static double getDistance(LatLng latLngA,LatLng latLngB) {



        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        double distance = locationA.distanceTo(locationB);

        return distance;







    }



    private static float getBearing(LatLng latLngA,LatLng latLngB) {



        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);

        float bearing = locationA.bearingTo(locationB);

        return bearing;




    }




   /* public void onLocationChanged(Location location) {

        Androidlocation = location;

    }*/








//Function to read the sensors

    public void onSensorChanged(SensorEvent event ) {
        // onSensorChanged gets called for each sensor so we have to remember the values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometer = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction), pitch and roll values.
                 azimuth = 180 * orientation[0] / Math.PI;
                double pitch = 180 * orientation[1] / Math.PI;
                double roll = 180 * orientation[2] / Math.PI;
            }
        }
    }

    //Function to register change in the accuracy of the sensors
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




//Function to determine if the network is available
    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)

        new Thread() {
            private boolean responded = false;
            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        HttpGet requestForTest = new HttpGet("http://m.google.com");
                        try {
                            new DefaultHttpClient().execute(requestForTest); // can last...
                            responded = true;
                        }
                        catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while(!responded && (waited < timeout)) {
                        sleep(100);
                        if(!responded ) {
                            waited += 100;
                        }
                    }
                }
                catch(InterruptedException e) {} // do nothing
                finally {
                    if (!responded) { handler.sendEmptyMessage(0); }
                    else { handler.sendEmptyMessage(1); }
                }
            }
        }.start();
    }



    private boolean isFirstMap() {


        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_MAP_OPENED = "FirstEverMap";
        final int DOESNT_EXIST = 0;


        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedFirstMap = prefs.getInt(PREF_MAP_OPENED, DOESNT_EXIST);

        prefs.edit().putInt(PREF_MAP_OPENED, 1).apply();


        if(savedFirstMap == 0){
            return true;
        }
        else
            return false;


    }



   /* func mapView(_ mapView: GMSMapView, idleAt position: GMSCameraPosition) {
        print(position.bearing);
    }*/








}





