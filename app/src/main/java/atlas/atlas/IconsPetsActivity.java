package atlas.atlas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class IconsPetsActivity extends AppCompatActivity {


    private ImageButton pet1Button;
    private ImageButton pet2Button;
    private ImageButton pet3Button;
    private ImageButton pet4Button;
    private ImageButton pet5Button;
    private ImageButton pet6Button;
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
        setContentView(R.layout.pets_icons);







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




        FrameLayout mFrame4=(FrameLayout) findViewById(R.id.frameLayout4);
        mFrame4.setPadding(width/2-(int)(150*density/2), 0, 0, 0);




        FrameLayout mFrame5=(FrameLayout) findViewById(R.id.frameLayout5);
        mFrame5.setPadding(width/2-(int)(150*density/2), 0, 0, 0);




        FrameLayout mFrame6=(FrameLayout) findViewById(R.id.frameLayout6);
        mFrame6.setPadding(width/2-(int)(150*density/2), 0, 0, 0);




















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
        pet1Button = findViewById(R.id.imageButton1);
        pet2Button = findViewById(R.id.imageButton2);
        pet3Button = findViewById(R.id.imageButton3);
        pet4Button = findViewById(R.id.imageButton4);
        pet5Button = findViewById(R.id.imageButton5);
        pet6Button = findViewById(R.id.imageButton6);
        //imageview2 = (ImageButton) findViewById(R.id.imageView2);
        //imageview3 = (ImageButton) findViewById(R.id.imageView3);
















        pet1Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "pet1";


/*

                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet1";

                // Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsPetsActivity.this.startActivity(myIntent);



            }
        });


        pet2Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "pet2";


/*

                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet2";

                //  Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);
*/
                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsPetsActivity.this.startActivity(myIntent);



            }
        });



        pet3Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "pet3";


/*

                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet3";

                //   Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/




                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsPetsActivity.this.startActivity(myIntent);


            }
        });




        pet4Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "pet4";



/*
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet4";

                // Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsPetsActivity.this.startActivity(myIntent);


            }
        });




        pet5Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "pet5";



/*
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet5";

                //    Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsPetsActivity.this.startActivity(myIntent);



            }
        });




        pet6Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);


                tracker.TrackerIconNum = "pet0";

                //    Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsPetsActivity.this.startActivity(myIntent);


            }
        });


































    }
}
