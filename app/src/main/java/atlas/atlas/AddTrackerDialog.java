package atlas.atlas;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTrackerDialog extends DialogFragment {
    private static final String TAG = "Atlas"+AddTrackerDialog.class.getSimpleName();

    //EditText titleEditText;
    EditText trackerIDEdit;
    Button cancelButton;
    Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        View view = inflator.inflate(R.layout.dialog_addtracker, container, false);


        trackerIDEdit = view.findViewById(R.id.trackerIDEdit);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: validate the new TrackerID here
                String TrackerID = trackerIDEdit.getText().toString();
                DatabaseHelper dbh = new DatabaseHelper(getContext());

                // add new Tracker to the database and go to the trackerActivity
                if (dbh.addNewTracker(new Tracker(TrackerID, "", "", 300.0))) {
                    Intent intent = new Intent(getActivity(), trackerActivity.class);
                    // add TrackerID to the intent send to the trackerActivity
                    intent.putExtra("TrackerID", TrackerID);
                    startActivity(intent);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "The tracker already exists", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "couldn't add a new tracker to the database");
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }
}
