package atlas.atlas;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class trackerActivity extends AppCompatActivity {
    Context context;
    private ImageButton imageview1;
    private ImageButton imageview2;
    private ImageButton imageview3;

    String TrackerID;


    ArrayList<Tracker> TrackerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        Intent intent = getIntent();
        TrackerID = intent.getStringExtra("TrackerID");


// layeout used to contain images
        LinearLayout scroller = findViewById(R.id.scroller);
        // layout display
        LayoutInflater inflator = LayoutInflater.from(this);
        //layout view with scroll option
        View view = inflator.inflate(R.layout.items, scroller, false);
         scroller.addView(view);
// enable click images
        addListener();
    }
  //  public void updateTracker(string TrackerID)
    public void addListener(){
        imageview1  = (ImageButton) findViewById(R.id.imageview1);
        imageview2 = (ImageButton) findViewById(R.id.imageView2);
        imageview3 = (ImageButton) findViewById(R.id.imageView4);

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
               imageview1 = (ImageButton) findViewById(R.id.imageview1);
                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);
                String iconName = getResources().getResourceEntryName(R.mipmap.ic_launcher);
                tracker.TrackerIcon = iconName;
                dbh.updateTracker(tracker);

                Toast.makeText(trackerActivity.this, iconName, Toast.LENGTH_LONG).show();

            }
        });
        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
                imageview2.setBackgroundResource(R.drawable.add_icon);
              //  imageview2.setVisibility(View.INVISIBLE);
              //  imageview2 = (ImageButton) findViewById(R.id.imageView2);
               // imageview2.setVisibility(View.VISIBLE);
                imageview2 = (ImageButton) findViewById(R.id.imageView2);
            }
        });
        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
              //  imageview3.setBackgroundResource(R.drawable.ic_launcher_background);
                imageview3.setBackgroundResource(R.drawable.add_icon);
                imageview3 = (ImageButton) findViewById(R.id.imageView4);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // update the tracker list if the Activity gained focus back without calling the onCreate()

    }


}
