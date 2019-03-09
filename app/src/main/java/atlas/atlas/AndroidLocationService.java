package atlas.atlas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

// https://stackoverflow.com/questions/28535703/best-way-to-get-user-gps-location-in-background-in-android

public class AndroidLocationService extends Service {
    private static final String TAG = "Atla"+AndroidLocationService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 3000;
    private static final float LOCATION_DISTANCE = 0f;

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    public static final String BROADCAST_ACTION_NEW_ANDROIDLOCATION = "BROADCAST_ACTION_NEW_ANDROIDLOCATION";
    public static final String BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE = "BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE";

    // Creates the BROADCAST_ACTION_NEW_ANDROIDLOCATION broadcast when the new android lccation is available
    // The intent contains as extras the Provider (gps or network) Latitude and Longitude
    public void sendNewAndroidLocationBroadCast(String provider, double Latitude, double Longitude) {
        // Create a new broadcast
        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra("Provider", provider);
        broadCastIntent.putExtra("Latitude", Latitude);
        broadCastIntent.putExtra("Longitude", Longitude);
        broadCastIntent.setAction(AndroidLocationService.BROADCAST_ACTION_NEW_ANDROIDLOCATION);
        getApplicationContext().sendBroadcast(broadCastIntent);
    }
    // Creates the BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE when the provider (gps or network) becomes enabled/disabled
    // The intent contains as extras the Provider (gps or network) and Enabled boolean, true if provider is enabled false if disabled.
    public void sendLocationEnabledChangeBroadCast(String provider, boolean enabled) {
        // Create a new broadcast
        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra("Provider", provider);
        broadCastIntent.putExtra("Enabled", enabled);
        broadCastIntent.setAction(AndroidLocationService.BROADCAST_ACTION_LOCATIONPROVIDER_ENABLED_CHANGE);
        getApplicationContext().sendBroadcast(broadCastIntent);
    }

    public static boolean isGPSEnabled(Context context) {
        boolean gps_enabled = false;
        try {
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        return gps_enabled;
    }

    public static boolean isNetworkEnabled(Context context) {
        boolean network_enabled = false;
        try {
            LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        return network_enabled;
    }

    public static Location getLastKnownLocation(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location locGps = null;
        Location locNet = null;
        Location loc = null;
        try {
            locGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locGps==null) { return locNet; }
            if (locNet==null) { return locGps; }
            loc = locGps.getTime()>locNet.getTime()? locGps : locNet;
        } catch (SecurityException ex) {
            Log.d(TAG, "Security");
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "IllegalArgument");
        } catch (Exception ex) {
        }
        return loc;
    }


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        String mProvider;

        public LocationListener(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            mProvider = provider;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            sendNewAndroidLocationBroadCast(mProvider, location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            sendLocationEnabledChangeBroadCast(provider, false);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
            sendLocationEnabledChangeBroadCast(provider, true);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand()");
        if(intent != null)
        {
            String action = intent.getAction();
            switch (action)
            {
                case ACTION_START_SERVICE:
                    super.onStartCommand(intent, flags, startId);
                    Log.d(TAG, "Android location service is started.");
                    break;
                case ACTION_STOP_SERVICE:
                    stopSelf();
                    Log.d(TAG, "Android location service is stoped.");
                    return super.onStartCommand(intent, flags, startId);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
