package atlas.atlas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class IconsActivity extends AppCompatActivity {


    private ImageButton girl1Button;
    private ImageButton boy1Button;
    private ImageButton boy2Button;
    private ImageButton boy3Button;
    private ImageButton boy4Button;
    private ImageButton girl2Button;
    private ImageButton girl3Button;
    private ImageButton girl4Button;
    private ImageButton girl5Button;
    private ImageButton boy5Button;
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
        setContentView(R.layout.child_icons);


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
        girl1Button = findViewById(R.id.imageButton1);
        boy1Button = findViewById(R.id.imageButton2);
        boy2Button = findViewById(R.id.imageButton3);
        boy3Button = findViewById(R.id.imageButton4);
        boy4Button = findViewById(R.id.imageButton5);
        girl2Button = findViewById(R.id.imageButton6);
        girl3Button = findViewById(R.id.imageButton7);
        girl4Button = findViewById(R.id.imageButton8);
        girl5Button = findViewById(R.id.imageButton9);
        boy5Button = findViewById(R.id.imageButton10);
        //imageview2 = (ImageButton) findViewById(R.id.imageView2);
        //imageview3 = (ImageButton) findViewById(R.id.imageView3);







        girl1Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                imageViewIcon = "girl1";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl1";

            //    Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);


            }
        });


        boy1Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "boy1";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy1";

        //        Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);


            }
        });


        boy2Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "boy2";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy2";

         //       Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);



            }
        });



        boy3Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "boy3";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy3";

        //        Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);



            }
        });



        boy4Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "boy4";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy4";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);


            }
        });



        girl2Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "girl2";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl2";

        //        Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);



            }
        });


        girl3Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "girl3";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl3";

       //         Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);


            }
        });



        girl4Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "girl4";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl4";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);


            }
        });



        girl5Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "girl5";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl5";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);

            }
        });


        boy5Button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                imageViewIcon = "boy0";




/*                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy0";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);*/

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                IconsActivity.this.startActivity(myIntent);






            }
        });










    }
}
