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
    DatabaseHelper dbh;

   // DatabaseHelper dbh = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_icons);


        Intent intent = getIntent();
        TrackerID = intent.getStringExtra("TrackerID");
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







        girl1Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl1";

            //    Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });


        boy1Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy1";

        //        Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });


        boy2Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy2";

         //       Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });



        boy3Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy3";

        //        Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });



        boy4Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy4";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });



        girl2Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl2";

        //        Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });


        girl3Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl3";

       //         Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });



        girl4Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl4";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });



        girl5Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "girl5";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });


        boy5Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "boy0";

          //      Toast.makeText(IconsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsActivity.this.startActivity(myIntent);

                return true;




            }
        });










    }
}
