package atlas.atlas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    ListView trackerListMain;
    Button startServiceButton;
    Button stopServiceButton;
    TextView mapTextView; // placeholder, use for debugging

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
        }
    }
    GPSReadingBroadcastReceiver gpsReadingBroadcastReceiver;

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

        // Set on click listeners for buttons
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked");
                Intent intent = new Intent(MainActivity.this, ReceiverServiceMockup.class);
                intent.setAction(ReceiverServiceMockup.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
            }
        });
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiverServiceMockup.class);
                intent.setAction(ReceiverServiceMockup.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);
            }
        });
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
            startActivity(new Intent(this, trackerActivity.class));
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
    }
}
