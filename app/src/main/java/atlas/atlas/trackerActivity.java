package atlas.atlas;
//package test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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




//import android.text.InputFilter;

//import android.text.Spanned;
// more imports






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

        //Tracker tracker = db.get

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




            imageview1  = (ImageButton) findViewById(R.id.imageView1);
            if(tracker.TrackerIconNum != null)
                if((tracker.TrackerIconNum).equals("girl1")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);


                    imageview1.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("boy1")) {


                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);


                imageview1.setImageBitmap(bitmap);


            }

                else if((tracker.TrackerIconNum).equals("boy2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy2);


                    imageview1.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("boy3")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy3);


                    imageview1.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("boy4")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy4);


                    imageview1.setImageBitmap(bitmap);


                }




                else if((tracker.TrackerIconNum).equals("girl2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl2);


                    imageview1.setImageBitmap(bitmap);


                }



                else if((tracker.TrackerIconNum).equals("girl3")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl3);


                    imageview1.setImageBitmap(bitmap);


                }



                else if((tracker.TrackerIconNum).equals("girl4")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl4);


                    imageview1.setImageBitmap(bitmap);


                }



                else if((tracker.TrackerIconNum).equals("girl5")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl5);


                    imageview1.setImageBitmap(bitmap);


                }

                else {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy0);


                    imageview1.setImageBitmap(bitmap);


                }



            imageview2  = (ImageButton) findViewById(R.id.imageView2);
            if(tracker.TrackerIconNum != null)
                if((tracker.TrackerIconNum).equals("pet1")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet1);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("pet2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet2);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("pet3")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet3);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("pet4")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet4);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("pet5")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet5);


                    imageview2.setImageBitmap(bitmap);


                }




                else if((tracker.TrackerIconNum).equals("pet0")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet0);


                    imageview2.setImageBitmap(bitmap);
                }






            imageview3  = (ImageButton) findViewById(R.id.imageView3);
            if(tracker.TrackerIconNum != null)
                if((tracker.TrackerIconNum).equals("item1")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.item1);


                    imageview3.setImageBitmap(bitmap);


                }

                else if((tracker.TrackerIconNum).equals("item2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.item2);


                    imageview3.setImageBitmap(bitmap);


                }






                else if((tracker.TrackerIconNum).equals("item0")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.item0);


                    imageview3.setImageBitmap(bitmap);
                }











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
            Toast.makeText(trackerActivity.this, tracker.TrackerIconNum, Toast.LENGTH_SHORT).show();

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


        imageview1.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {

                setSelectedImage(R.mipmap.ic_tracker_1);

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);

                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(trackerActivity.this, IconsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);

                trackerActivity.this.startActivity(myIntent);

                return true;




            }
        });




        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(trackerActivity.this, "ImageButton is clicked!", Toast.LENGTH_SHORT).show();
                setSelectedImage(R.mipmap.ic_tracker_2);
            }
        });





        imageview2.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {

                setSelectedImage(R.mipmap.ic_tracker_2);

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);

                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(trackerActivity.this, IconsPetsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);

                trackerActivity.this.startActivity(myIntent);

                return true;




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


        imageview3.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {

                setSelectedImage(R.mipmap.ic_tracker_3);

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);


                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(trackerActivity.this, IconsItemsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);

                trackerActivity.this.startActivity(myIntent);

                return true;




            }
        });




     /*   imageview3.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {

                Intent myIntent = new Intent(trackerActivity.this, IconsPetsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);

                trackerActivity.this.startActivity(myIntent);

                return true;




            }
        });*/




    }
    @Override
    protected void onResume() {
        super.onResume();
        // update the tracker list if the Activity gained focus back without calling the onCreate()

    }


}
