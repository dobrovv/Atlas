package atlas.atlas;

import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import static java.lang.System.currentTimeMillis;
import java.util.Random;

/* ReceiverServiceMockup
 * A mockup service to simulate the communication between the android phone and the arduino receiver
 * The service creates new GPSReadings and stores them into Database
 * When a new GPSReading is created the service sends a broadcast BROADCAST_ACTION_NEW_GPSREADING
 * The Intent of the broadcast contains the TrackerID, GPSReadingID and the coordinates
 * */
public class ReceiverServiceMockup extends Service {
    private static final String TAG = "Atla"+ReceiverServiceMockup.class.getSimpleName();

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final String BROADCAST_ACTION_NEW_GPSREADING = "BROADCAST_ACTION_NEW_GPSREADING";

    private static final int BROADCAST_DELAY_MS = 3000; // frequency of the gps broadcasts in milliseconds

    // Test data to generate new GPSReadings, arrays must have equal size
    private static String[] TestTrackerIDs = {"00001", "00002", "00003", "00004", "00005"};
    private static double[] TrackersLatitude = {1e-3,1e-3,-1e-3,-1e-3,-1e-3};
    private static double[] TrackersLongitude = {-1e-3,1e-3,-1e-3,1e-3,1e-3};
    private static double[] TrackersSpeed = {1e-5,1e-5,1e-5,1e-5,1e-5,1e-5};


    /* Timer that generates new GPSReadings mockups
     * Timer how-to https://stackoverflow.com/questions/4597690/android-timer-how-to
     * */

    // Handler for the timer, uses the main thread of the app
    Handler timerHandler = new Handler();
    // Runnable for the timer
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            // send a gps update broadcast
            sendGpsUpdateBroadcast();

            // timer is rescheduled by posting itself to the handler's message queue with a delay
            timerHandler.postDelayed(this, BROADCAST_DELAY_MS);
        }

        // create a new GPSReading, save it in the db, and send a NEW_GPSREADING broadcast.
        public void sendGpsUpdateBroadcast() {
            // create a new GPSReading
            Random rnd = new Random();

            int id = rnd.nextInt(TestTrackerIDs.length);
            Double angle = rnd.nextDouble() * 2*Math.PI;

            String TrackerID = TestTrackerIDs[id];
            Double Latitude = TrackersLatitude[id] += TrackersSpeed[id]*Math.sin(angle);
            Double Longitude = TrackersLongitude[id] += TrackersSpeed[id]*Math.cos(angle);

            Location latestAndroidLocation = AndroidLocationService.getLastKnownLocation(getApplicationContext());
            if (latestAndroidLocation != null) {
                Latitude += latestAndroidLocation.getLatitude();
                Longitude += latestAndroidLocation.getLongitude();
            }

            Integer GSMSignal =  rnd.nextInt(2); // (0 or 1)
            Integer GPSSignal = rnd.nextInt(31);  //(0 to 30)
            Integer BatteryLevel = rnd.nextInt(101); //(0 to 100)
            Integer PowerStatus = rnd.nextInt(2);//(0 or 1)

            GPSReading newGpsReading = new GPSReading(0L, TrackerID, currentTimeMillis(), currentTimeMillis() * 1., Latitude, Longitude, TrackersSpeed[id], GSMSignal, GPSSignal, BatteryLevel, PowerStatus);

            // get database handler
            DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
            //save the newGpsReading and get its id
            Long newGPSReadingID = dbh.addGPSReading(newGpsReading);

            // Create a new broadcast
            Intent broadCastIntent = new Intent();
            // Include TrackerID, GPSReadingID and coordinates into the broadcast
            broadCastIntent.putExtra("GPSReadingID", newGPSReadingID);
            broadCastIntent.putExtra("TrackerID", TrackerID);
            broadCastIntent.putExtra("Latitude", Latitude);
            broadCastIntent.putExtra("Longitude", Longitude);
            broadCastIntent.setAction(ReceiverServiceMockup.BROADCAST_ACTION_NEW_GPSREADING);

            // send broadcast
            Log.d(TAG, "Sending NEW_GPSUPDATE Broadcast" + broadCastIntent);
            getApplicationContext().sendBroadcast(broadCastIntent);
        }
    };

    public void restartTimer() {
        // stop the timer if it is still running
        stopTimer();
        // start the timer
        timerHandler.postDelayed(timerRunnable, BROADCAST_DELAY_MS);
    }

    public void stopTimer() {
        // stop the timer by removing all timer callbacks from the message queue
        timerHandler.removeCallbacks(timerRunnable);
    }

    /* Timer end */


    public ReceiverServiceMockup() {

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
        Log.d(TAG, "Received start id " + startId + ": " + intent);

        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    restartTimer();
                    startForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "ReceiverServiceMockup service is started.");
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopTimer();
                    stopForegroundService();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "ReceiverServiceMockup service is stopped.");
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Log.d(TAG, "Starting mockup service.");

        // start foreground service
        //startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private void stopForegroundService()
    {
        Log.d(TAG, "Stopping mockup service.");
        // Stop foreground service and remove the notification.
        //stopForeground(true);

        // Stop the mockup service.
        stopSelf();
    }
}
