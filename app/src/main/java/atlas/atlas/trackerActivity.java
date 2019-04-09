package atlas.atlas;
//package test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;

import me.toptas.fancyshowcase.FancyShowCaseView;

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

    private ToggleButton toggleButton;

    private int selectedImageID = R.mipmap.ic_launcher; // set the default image if no image was previously selected

    String TrackerID;

    String imageViewIcon;


    ArrayList<Tracker> TrackerList;


    String trackerNameInTextV = null; //= String.valueOf(trackerNameEdit.getText());
    Double allowedDistanceInTextV = null; //= Double.valueOf(allowedDistanceEdit.getText().toString());




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


        toggleButton = findViewById(R.id.toggleButton);

        Intent intent = getIntent();

        boolean firstUse = intent.getBooleanExtra("FirstRun",false);

        //boolean startInstruction = true;

       // MainActivity activity = (MainActivity) MainActivity.this;
       // String myDataFromActivity = activity.getMyData();
        //Activity activity = MainActivity;






        if(firstUse) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {



                    new FancyShowCaseView.Builder(trackerActivity.this)
                            .titleStyle(R.style.ShowCaseTitleStyle, Gravity.BOTTOM | Gravity.CENTER)
                            .title("Press to chose the type of tracker, and long press to chose the icon ")
                            .focusOn(findViewById(R.id.imageView2))
                            //.backgroundColor(Color.parseColor("#333639"))
                            .backgroundColor(Color.parseColor("#AAa55353"))
                            .focusCircleRadiusFactor(2.0)

                            .build()
                            .show();


                }
            }, 2000);

        }












        //toggleclick(View );



        TrackerID = intent.getStringExtra("TrackerID");
        imageViewIcon = intent.getStringExtra("imageViewIcon");
         trackerNameInTextV = intent.getStringExtra("trackerNameInTextV");
         allowedDistanceInTextV = intent.getDoubleExtra("allowedDistanceInTextV",-1);
        //reloadContentForViews();

        DatabaseHelper dbh = new DatabaseHelper(this);
        Tracker tracker = dbh.getTrackerByID(TrackerID);

        if(tracker.EnableNotification == 1)
            toggleButton.setChecked(true);
        else
            toggleButton.setChecked(false);



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








       // allowedDistanceEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

     /*   allowedDistanceEdit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               // Toast.makeText(trackerActivity.this, "Bellow, You can set the notifications ON/OFF when the tracker leaves the region", Toast.LENGTH_SHORT).show();



            }



        });*/



        //final EditText edittext = (EditText) findViewById(R.id.edittext);
        allowedDistanceEdit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(isFirstRadius() )
                    Toast.makeText(trackerActivity.this, "Bellow, You can set the notifications ON/OFF when the tracker leaves the region", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });






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
                imageViewIcon = null;
                allowedDistanceInTextV = null;
                trackerNameInTextV = null;
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



            String Icon = null;

            if(imageViewIcon != null)
                Icon =imageViewIcon;
            else if(tracker.TrackerIconNum != null)
                Icon = tracker.TrackerIconNum;




            imageview1  = (ImageButton) findViewById(R.id.imageView1);
            if(Icon != null)
                if((Icon).equals("girl1")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl1);


                    imageview1.setImageBitmap(bitmap);


                }

                else if((Icon).equals("boy1")) {


                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy1);


                imageview1.setImageBitmap(bitmap);


            }

                else if((Icon).equals("boy2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy2);


                    imageview1.setImageBitmap(bitmap);


                }

                else if((Icon).equals("boy3")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy3);


                    imageview1.setImageBitmap(bitmap);


                }

                else if((Icon).equals("boy4")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy4);


                    imageview1.setImageBitmap(bitmap);


                }




                else if((Icon).equals("girl2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl2);


                    imageview1.setImageBitmap(bitmap);


                }



                else if((Icon).equals("girl3")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl3);


                    imageview1.setImageBitmap(bitmap);


                }



                else if((Icon).equals("girl4")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl4);


                    imageview1.setImageBitmap(bitmap);


                }



                else if((Icon).equals("girl5")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.girl5);


                    imageview1.setImageBitmap(bitmap);


                }

                else {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.boy0);


                    imageview1.setImageBitmap(bitmap);


                }



            imageview2  = (ImageButton) findViewById(R.id.imageView2);
            if(Icon != null)
                if((Icon).equals("pet1")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet1);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((Icon).equals("pet2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet2);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((Icon).equals("pet3")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet3);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((Icon).equals("pet4")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet4);


                    imageview2.setImageBitmap(bitmap);


                }

                else if((Icon).equals("pet5")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet5);


                    imageview2.setImageBitmap(bitmap);


                }




                else /*if((tracker.TrackerIconNum).equals("pet0"))*/ {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pet0);


                    imageview2.setImageBitmap(bitmap);
                }








            imageview3  = (ImageButton) findViewById(R.id.imageView3);
            if(Icon != null)
                if((Icon).equals("item1")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.item1);


                    imageview3.setImageBitmap(bitmap);


                }

                else if((Icon).equals("item2")) {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.item2);


                    imageview3.setImageBitmap(bitmap);


                }






                else /*if((tracker.TrackerIconNum).equals("item0"))*/ {


                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.item0);


                    imageview3.setImageBitmap(bitmap);
                }











            // fill in the views
            if(trackerNameInTextV == null)
                trackerNameEdit.setText(String.valueOf(tracker.TrackerName));
            else
                trackerNameEdit.setText(trackerNameInTextV);

            if(allowedDistanceInTextV == null|| allowedDistanceInTextV == -1)
                allowedDistanceEdit.setText(String.valueOf(tracker.AllowedDistance));
            else
                allowedDistanceEdit.setText(String.valueOf(allowedDistanceInTextV));





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


            if(imageViewIcon != null)
            tracker.TrackerIconNum = imageViewIcon;

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

                 trackerNameInTextV = String.valueOf(trackerNameEdit.getText());
                 allowedDistanceInTextV = Double.valueOf(allowedDistanceEdit.getText().toString());

                setSelectedImage(R.mipmap.ic_tracker_1);

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);

                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(trackerActivity.this, IconsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

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

                trackerNameInTextV = String.valueOf(trackerNameEdit.getText());
                allowedDistanceInTextV = Double.valueOf(allowedDistanceEdit.getText().toString());

                setSelectedImage(R.mipmap.ic_tracker_2);

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);

                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(trackerActivity.this, IconsPetsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

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

                trackerNameInTextV = String.valueOf(trackerNameEdit.getText());
                allowedDistanceInTextV = Double.valueOf(allowedDistanceEdit.getText().toString());

                setSelectedImage(R.mipmap.ic_tracker_3);

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);

                tracker.TrackerIcon = getResources().getResourceEntryName(selectedImageID);


                dbh.updateTracker(tracker);

                Intent myIntent = new Intent(trackerActivity.this, IconsItemsActivity.class);

                myIntent.putExtra("TrackerID", TrackerID);
                myIntent.putExtra("imageViewIcon", imageViewIcon);
                myIntent.putExtra("trackerNameInTextV", trackerNameInTextV);
                myIntent.putExtra("allowedDistanceInTextV", allowedDistanceInTextV);

                trackerActivity.this.startActivity(myIntent);

                return true;




            }
        });



        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DatabaseHelper dbh = new DatabaseHelper(trackerActivity.this);
                Tracker tracker = dbh.getTrackerByID(TrackerID);
                dbh.updateTracker(tracker);

                if (isChecked) {
                    tracker.EnableNotification = 1;
                } else {
                    tracker.EnableNotification = 0;
                }

                dbh.updateTracker(tracker);
            }
        });







    }


    private boolean isFirstRadius() {


        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_FirstEver_Radius_KEY = "FirstEverRadius";
        final int DOESNT_EXIST = 0;


        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedFirstEverTracker = prefs.getInt(PREF_FirstEver_Radius_KEY, DOESNT_EXIST);

        prefs.edit().putInt(PREF_FirstEver_Radius_KEY, 1).apply();


        if(savedFirstEverTracker == 0){
            return true;
        }
        else
            return false;


    }







    @Override
    protected void onResume() {
        super.onResume();
        // update the tracker list if the Activity gained focus back without calling the onCreate()

    }










}
