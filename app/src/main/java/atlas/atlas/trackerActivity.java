package atlas.atlas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class trackerActivity extends AppCompatActivity {
    private static final String TAG = "Atlas"+trackerActivity.class.getSimpleName();

    //Context context;
    private ImageButton imageview1;
    private ImageButton imageview2;
    private ImageButton imageview3;

    private TextView trackerNameEdit;
    private TextView allowedDistanceEdit;
    private Button saveButton;
    private Button cancelButton;

    private int selectedImageID = R.mipmap.ic_launcher; // set the default image if no image was previously selected

    String TrackerID;

    ArrayList<Tracker> TrackerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        trackerNameEdit = findViewById(R.id.TrackerNameEdit);
        allowedDistanceEdit = findViewById(R.id.AllowedDistanceEdit);
        saveButton = findViewById(R.id.SaveButton);
        cancelButton = findViewById(R.id.CancelButton);

        Intent intent = getIntent();
        TrackerID = intent.getStringExtra("TrackerID");

// layeout used to contain images
        LinearLayout scroller = findViewById(R.id.scroller);
        // layout display
        LayoutInflater inflator = LayoutInflater.from(this);
        //        //layout view with scroll option
        View view = inflator.inflate(R.layout.items, scroller, false);
         scroller.addView(view);
// enable click images
        addListener();

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveContentOfViews();
                reloadContentForViews();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadContentForViews();
            }
        });

        // fill the views with the stored content
        reloadContentForViews();

    }
    // load the trackers data from db and put it into activity's views
    public void reloadContentForViews() {
        try {
            // get trackers data from db
            DatabaseHelper dbh = new DatabaseHelper(this);
            Tracker tracker = dbh.getTrackerByID(TrackerID);
            String TrackerName = tracker.TrackerName;
            Double AllowedDistance = tracker.AllowedDistance;

            // fill in the views
            trackerNameEdit.setText(String.valueOf(tracker.TrackerName));
            allowedDistanceEdit.setText(String.valueOf(AllowedDistance));

            // get the image id from the db, getIdentifier returns 0 if no such resource name exists
            int SelectedImageID =  getResources().getIdentifier(tracker.TrackerIcon, "mipmap", getPackageName());
            if (SelectedImageID != 0)
                setSelectedImage(SelectedImageID);

        } catch (Exception ex) {
            Log.e(TAG, "reloadContentForViews() Error while updating the views: " + ex.getMessage());
        }
    }

    // save the data from the activitiy's views into the db
    public void saveContentOfViews() {
        try {
            // get tracker from db
            DatabaseHelper dbh = new DatabaseHelper(this);
            Tracker tracker = dbh.getTrackerByID(TrackerID);
            String TrackerName = String.valueOf(trackerNameEdit.getText());
            Double AllowedDistance = Double.valueOf(allowedDistanceEdit.getText().toString());
            // save data to db
            tracker.TrackerName = TrackerName;
            tracker.AllowedDistance = AllowedDistance;
            tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);
            dbh.updateTracker(tracker);
            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e(TAG, "saveContentOfViews() Error while saving the content: " + ex.getMessage());
        }
    }

    public void setSelectedImage(int imageId) {

        //reset all backgrounds
        imageview1.setBackground(null);
        imageview2.setBackground(null);
        imageview3.setBackground(null);

        //set background on the selected item
        if (imageId == R.mipmap.ic_tracker_1)
            imageview1.setBackground(getDrawable(R.drawable.item_border));
        else if (imageId == R.mipmap.ic_tracker_2)
            imageview2.setBackground(getDrawable(R.drawable.item_border));
        else if (imageId == R.mipmap.ic_tracker_3)
            imageview3.setBackground(getDrawable(R.drawable.item_border));

        selectedImageID = imageId;
    }


  //  public void updateTracker(string TrackerID)
    public void addListener(){
        imageview1  = (ImageButton) findViewById(R.id.imageView1);
        imageview2 = (ImageButton) findViewById(R.id.imageView2);
        imageview3 = (ImageButton) findViewById(R.id.imageView3);

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
                setSelectedImage(R.mipmap.ic_tracker_1);
                //String iconName = getResources().getResourceEntryName(R.mipmap.ic_tracker_1);
                //Toast.makeText(trackerActivity.this, iconName, Toast.LENGTH_LONG).show();

            }
        });
        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
                setSelectedImage(R.mipmap.ic_tracker_2);
            }
        });

        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
                setSelectedImage(R.mipmap.ic_tracker_3);
              //  imageview3.setBackgroundResource(R.drawable.ic_launcher_background);
                //imageview3.setBackgroundResource(R.drawable.add_icon);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // update the tracker list if the Activity gained focus back without calling the onCreate()

    }


}
