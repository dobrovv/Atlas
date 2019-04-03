package atlas.atlas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    DatabaseHelper dbh;

    // DatabaseHelper dbh = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pets_icons);


        Intent intent = getIntent();
        TrackerID = intent.getStringExtra("TrackerID");
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







        pet1Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet1";

                Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsPetsActivity.this.startActivity(myIntent);

                return true;




            }
        });


        pet2Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet2";

                Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsPetsActivity.this.startActivity(myIntent);

                return true;




            }
        });



        pet3Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet3";

                Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsPetsActivity.this.startActivity(myIntent);

                return true;




            }
        });




        pet4Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet4";

                Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsPetsActivity.this.startActivity(myIntent);

                return true;




            }
        });




        pet5Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet5";

                Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsPetsActivity.this.startActivity(myIntent);

                return true;




            }
        });




        pet6Button.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {




                Tracker tracker = dbh.getTrackerByID(TrackerID);

                //WHy this doens't not change???
                tracker.TrackerIconNum = "pet0";

                Toast.makeText(IconsPetsActivity.this, tracker.TrackerIconNum+TrackerID, Toast.LENGTH_SHORT).show();



                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(IconsPetsActivity.this, trackerActivity.class);


                myIntent.putExtra("TrackerID", TrackerID);

                IconsPetsActivity.this.startActivity(myIntent);

                return true;




            }
        });

















    }
}
