package atlas.atlas;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.transform.OutputKeys;

/* ReceiverService
 * A service that reads GPS Readings from the web server
 * The service creates new GPSReadings and stores them into Database
 * When a new GPSReading is created the service sends a broadcast BROADCAST_ACTION_NEW_GPSREADING
 * The Intent of the broadcast contains the TrackerID, GPSReadingID and the coordinates
 * */
public class ReceiverService extends Service {
    private static final String TAG = "Atlas"+ReceiverService.class.getSimpleName();
    private static final String REQ_TAG = "REQ_TAG";

    public static final String CHANNEL_ID = "AtlasChannel";
    public static final int ONGOING_NOTIFICATION_ID = 1; // id of the displayed notification

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String BROADCAST_ACTION_NEW_GPSREADING = "BROADCAST_ACTION_NEW_GPSREADING";
    public static final String BROADCAST_ACTION_CONNECTION_STATE_CHANGE = "BROADCAST_ACTION_CONNECTION_STATE_CHANGE";

    private static final int GPSREADINGS_REQUEST_INTERVAL = 15000; // interval between requests to the server for updates in ms

    // Connection state of the receiver service
    public static final int CONNECTION_STATE_DISCONNECTED = 0; // If the communication with the web server failed in some way
    public static final int CONNECTION_STATE_CONNECTED = 1; // If fetching from the web server was done without failures

    public static int ConnectionState = CONNECTION_STATE_DISCONNECTED;

    RequestQueue mRequestQueue; // request queue used by Volley library

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
                                updateConnectionState(CONNECTION_STATE_CONNECTED);
                            } catch (Exception ex) {
                                Log.d(TAG, "Error parsing json response/db access:" + ex.getMessage());
                                updateConnectionState(CONNECTION_STATE_DISCONNECTED);
                            }
                        }

                        updateTrackerNofications();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d(TAG, "Error for /getLatestGPSReadings request:" + error);
                        updateConnectionState(CONNECTION_STATE_DISCONNECTED);
                    }
                }
        );

        jsonRequest.setTag(REQ_TAG);

        if (mRequestQueue != null)
            mRequestQueue.add(jsonRequest);
    }

    public void updateTrackerNofications() {

        DatabaseHelper dbh = new DatabaseHelper(this);

        ArrayList<Tracker> trackersDB = dbh.getAllTrackers();
        Location androidLoc = AndroidLocationService.getLastKnownLocation(this);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // for each tracker in the db update marker
        for (Tracker tracker : trackersDB) {
            try {
                // get latest gps data for the tracker
                GPSReading gpsReading = dbh.getLatestGPSReading(tracker.TrackerID);

                if (gpsReading == null) // check if the tracker has a gps reading
                    continue;

                // find distance between the tracker and the pohone
                float[] tmp = new float[1];
                Location.distanceBetween(androidLoc.getLatitude(), androidLoc.getLongitude(), gpsReading.Latitude, gpsReading.Longitude, tmp);
                float distance = tmp[0];

                // check if the distance crosses the treshold
                if (distance < tracker.AllowedDistance)
                    continue;

                // check if tracker has notifications enabled
                if (tracker.EnableNotification == 0)
                    continue;

                Notification notification = createTrackerNotification(tracker, gpsReading, androidLoc, distance);
                notificationManager.notify(tracker.TrackerID.hashCode(), notification);

            } catch (Exception ex) {
                Log.e(TAG, "updateAllMiniMapMarkers() can't update trackers location Exception: " + ex.getMessage());
            }
        }
    }

    public void updateConnectionState(int newConnectionState) {
        if (newConnectionState != ConnectionState) {
            ConnectionState = newConnectionState;
            //sendConnectionStateChangeBroadcast();

            // update notification of the ReceiverService
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(ONGOING_NOTIFICATION_ID, createServiceNotification());
        }
    }

    public static int getConnectionState() {
        return ConnectionState;
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

    public void sendConnectionStateChangeBroadcast() {
        Intent broadCastIntent = new Intent();
        // Include TrackerID, GPSReadingID and coordinates into the broadcast
        broadCastIntent.putExtra("ConnectionState", ConnectionState);
        broadCastIntent.setAction(ReceiverService.BROADCAST_ACTION_CONNECTION_STATE_CHANGE);

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
        ConnectionState = CONNECTION_STATE_DISCONNECTED;
        restartTimer();

        createNotificationChannel();
        Notification notification = createServiceNotification();

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AtlasChannel";
            String description = "Channel for Atlas notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createServiceNotification() {
        // build the notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_service_notification)
                .setContentTitle("Atlas Service")
                .setContentText(ConnectionState == CONNECTION_STATE_CONNECTED ? "Connection to trackers established" : "Connecting to trackers...")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .build();

        return notification;
    }

    private Notification createTrackerNotification(Tracker tracker, GPSReading gpsReading, Location androidLoc, float distance) {
        // Create the intent that will fire when the user taps the notification
        Intent intent = new Intent(this, MapActivity.class);
        // go to Map activity
        intent.putExtra("TrackerID", gpsReading.TrackerID);
        intent.putExtra("Latitude", gpsReading.Latitude);
        intent.putExtra("Longitude", gpsReading.Longitude);
        intent.putExtra("AndroidLatitude", androidLoc.getLatitude());
        intent.putExtra("AndroidLongitude", androidLoc.getLongitude());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String trackerName = tracker.TrackerName.isEmpty() ? "Unnamed tracker": tracker.TrackerName;
        String contentTitle = "Tracker Alert";
        String contentText = String.format("'%s' has left the specified region", trackerName);
        String bigText = String.format("'%s' is %.0f meters away / Allowed %.0f", trackerName, distance, tracker.AllowedDistance);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_service_notification)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent) // Set the intent that will fire when the user taps the notification
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setAutoCancel(true);
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            long[] vibratePattern = { 0, 100, 200, 300 };
            builder.setVibrate(vibratePattern);
            builder.setSound(alarmSound);
        } catch (Exception ex) {
            Log.e(TAG, "Cant't set notification sound Exception:" + ex.getMessage());
        }

        // set Notification's image
        try {
            // get image id for the tracker's icon
            int trackerImageID = getResources().getIdentifier(tracker.TrackerIcon + "_round", "mipmap", getPackageName());
            trackerImageID = (trackerImageID != 0) ? trackerImageID : R.mipmap.ic_launcher;
            //Bitmap trackerIcon = BitmapFactory.decodeResource(getResources(), trackerImageID);
            Bitmap trackerIcon = Tracker.getBitmapFromID(this, trackerImageID);
            builder.setLargeIcon(trackerIcon);
        } catch (Exception ex) {
            Log.e(TAG, "Cant't set trackerIcon for notification Exception:" + ex.getMessage());
        }

        return builder.build();
    }


}
