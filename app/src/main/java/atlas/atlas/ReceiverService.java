package atlas.atlas;


import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.Random;

/* ReceiverService
 * A service that reads GPS Readings from the web server
 * The service creates new GPSReadings and stores them into Database
 * When a new GPSReading is created the service sends a broadcast BROADCAST_ACTION_NEW_GPSREADING
 * The Intent of the broadcast contains the TrackerID, GPSReadingID and the coordinates
 * */
public class ReceiverService extends Service {
    private static final String TAG = "Atlas"+ReceiverService.class.getSimpleName();
    private static final String REQ_TAG = "REQ_TAG";

    public static final int ONGOING_NOTIFICATION_ID = 1; // id of the displayed notification

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String BROADCAST_ACTION_NEW_GPSREADING = "BROADCAST_ACTION_NEW_GPSREADING";

    private static final int GPSREADINGS_REQUEST_INTERVAL = 15000; // interval between requests to the server for updates in ms

    RequestQueue mRequestQueue;

    // create a request url for the server
    // ex: http://dobrovv.pythonanywhere.com/getLatestGPSReadings?TrackerID=debug1&TrackerID=debug2
    String createNewGPSReadingsRequestURL() {
        String root = "http://dobrovv.pythonanywhere.com/getLatestGPSReadings";
        String url = root;

        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
        ArrayList<Tracker> trackerList = dbh.getAllTrackers();

        //check if there are any trackers to update
        if (trackerList.size() == 0)
            return "";

        for ( int i = 0; i < trackerList.size(); i++) {
            if (i==0) url += '?';
            url += String.format("TrackerID=%s", trackerList.get(i).TrackerID);
            if (i + 1 < trackerList.size()) url += '&';
        }
        return url;
    }

    void createNewGPSReadingsRequest() {
        String url = createNewGPSReadingsRequestURL();
        if (url.isEmpty()) return;
        Log.d(TAG, "Sending server request url=" + url);

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, "Response for NewGPSReadings request:" + response);

                        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());

                        for(int i=0; i<response.length(); i++){
                            try {
                                JSONObject jGpsReading = response.getJSONObject(i);
                                String TrackerID = jGpsReading.getString("TrackerID");
                                Double Longitude = jGpsReading.getDouble("Longitude");
                                Double Latitude = jGpsReading.getDouble("Latitude");
                                Double serverTimestamp = jGpsReading.getDouble("serverTimestamp");
                                Integer GPSSignal = jGpsReading.getInt("GPSSignal");
                                Integer GSMSignal = jGpsReading.getInt("GSMSignal");
                                Integer BatteryLevel = jGpsReading.getInt("BatteryLevel");
                                Integer PowerStatus = jGpsReading.getInt("PowerStatus");

                                Location latestAndroidLocation = AndroidLocationService.getLastKnownLocation(getApplicationContext());
                                if (latestAndroidLocation != null && TrackerID.contains("debug")) {
                                    Latitude += latestAndroidLocation.getLatitude();
                                    Longitude += latestAndroidLocation.getLongitude();
                                }

                                GPSReading latestGpsReading = dbh.getLatestGPSReading(TrackerID);

                                if (latestGpsReading == null || latestGpsReading.serverTimestamp + 0.1f < serverTimestamp ) {
                                    GPSReading serverGPSReading = new GPSReading(0L, TrackerID, currentTimeMillis(), serverTimestamp, Latitude, Longitude, 0.0, GPSSignal,GSMSignal, BatteryLevel, PowerStatus);
                                    Long newGpsReadingId = dbh.addGPSReading(serverGPSReading);
                                    sendNewGPSReadingBroadcast(newGpsReadingId, TrackerID, Latitude, Longitude);
                                    Log.d(TAG, "Updating: " + TrackerID + jGpsReading);
                                }
                            } catch (Exception ex) {
                                Log.d(TAG, "Error parsing json response/db access:" + ex.getMessage());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, "Error for /getLatestGPSReadings request:" + error);
                    }
                }
        );

        jsonRequest.setTag(REQ_TAG);

        if (mRequestQueue != null)
            mRequestQueue.add(jsonRequest);
    }

    /* Timer that generates new requests to the server
     * Timer how-to https://stackoverflow.com/questions/4597690/android-timer-how-to
     * */

    Handler timerHandler = new Handler();
    // Runnable for the timer
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            createNewGPSReadingsRequest();
            // timer is rescheduled by posting itself to the handler's message queue with a delay
            timerHandler.postDelayed(this, GPSREADINGS_REQUEST_INTERVAL);
        }
    };

    public void sendNewGPSReadingBroadcast(Long newGPSReadingID, String TrackerID, Double Latitude, Double Longitude) {

        // Create a new broadcast
        Intent broadCastIntent = new Intent();
        // Include TrackerID, GPSReadingID and coordinates into the broadcast
        broadCastIntent.putExtra("GPSReadingID", newGPSReadingID);
        broadCastIntent.putExtra("TrackerID", TrackerID);
        broadCastIntent.putExtra("Latitude", Latitude);
        broadCastIntent.putExtra("Longitude", Longitude);
        broadCastIntent.setAction(ReceiverService.BROADCAST_ACTION_NEW_GPSREADING);

        // send broadcast
        getApplicationContext().sendBroadcast(broadCastIntent);
    }

    public void restartTimer() {
        // stop the timer if it is still running
        stopTimer();
        // start the timer
        timerHandler.postDelayed(timerRunnable, GPSREADINGS_REQUEST_INTERVAL);
    }

    public void stopTimer() {
        // stop the timer by removing all timer callbacks from the message queue
        timerHandler.removeCallbacks(timerRunnable);
    }

    /* Timer end */


    public ReceiverService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Binding not implemented
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() call");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() with startId=" + startId + " intent:" + intent);

        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Log.d(TAG, "ReceiverService service is started.");
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Log.d(TAG, "ReceiverService service is stopped.");
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Log.d(TAG, "Starting foreground service.");
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(REQ_TAG);
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        restartTimer();

        // build the notification
        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //TODO: doesn't work on api 28 (needs a notification channel)
        Notification notification =
                new Notification.Builder(this)
                        .setContentTitle("Notification title")
                        .setContentText("Notification text")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        //.setTicker(getText(R.string.ticker_text))
                        .build();

        // start foreground service
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private void stopForegroundService()
    {
        Log.d(TAG, "stopForegroundService(): Stopping foreground service.");
        stopTimer();
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }
}
