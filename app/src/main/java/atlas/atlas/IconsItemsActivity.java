package atlas.atlas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class IconsItemsActivity extends AppCompatActivity {


    private ImageButton item1Button;
    private ImageButton item2Button;
    private ImageButton item3Button;
    //private ImageButton boy4Button;
    String TrackerID;
    String imageViewIcon;
    String trackerNameInTextV;
    Double allowedDistanceInTextV;

    DatabaseHelper dbh;

    // DatabaseHelper dbh = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_icons);




        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        float density  = getResources().getDisplayMetrics().density;










        FrameLayout mFrame=(FrameLayout) findViewById(R.id.frameLayout1);
        mFrame.setPadding(width/2-(int)(150*density/2), 0, 0, 0);




        FrameLayout mFrame2=(FrameLayout) findViewById(R.id.frameLayout2);
        mFrame2.setPadding(width/2-(int)(150*density/2), 0, 0, 0);




        FrameLayout mFrame3=(FrameLayout) findViewById(R.id.frameLayout3);
        mFrame3.setPadding(width/2-(int)(150*density/2), 0, 0, 0);






        Intent intent = getIntent();
        TrackerID = intent.getStringExtra("TrackerID");
        imageViewIcon = intent.getStringExtra("imageViewIcon");
        trackerNameInTextV = intent.getStringExtra("trackerNameInTextV");
        allowedDistanceInTextV = intent.getDoubleExtra("allowedDistanceInTextV", -1);

        dbh = new DatabaseHelper(this);

        Tracker tracker = dbh.getTrackerByID(TrackerID);

        //WHy this doens't not change???
        // tracker.TrackerIconNum = "girl1";









        addListener();









    }


    public void addListener(){
        item1Button = findViewById(R.id.imageButton1);
        item2Button = findViewById(R.id.imageButton2);
        item3Button = findViewById(R.id.imageButton3);
        //imageview2 = (ImageButton) findViewById(R.id.imageView2);
        //imageview3 = (ImageButton) findViewById(R.id.imageView3);










        item1Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "item1";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "item1";

                //  Toast.makeText(IconsItemsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsItemsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsItemsActivity.this.startActivity(myIntent);






            }
        });


        item2Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "item2";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "item2";

                //     Toast.makeText(IconsItemsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsItemsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsItemsActivity.this.startActivity(myIntent);






            }
        });



        item3Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "item0";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "item0";

                //     Toast.makeText(IconsItemsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsItemsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);


                IconsItemsActivity.this.startActivity(myIntent);






            }
        });
























    }
}