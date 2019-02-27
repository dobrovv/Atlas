package atlas.atlas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

//comment 12

public class MainActivity extends AppCompatActivity {

    ListView trackerListMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackerListMain = findViewById(R.id.trackerListMain);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();


        // in case we added more to toolbar
        if(id==R.id.addTracker){
            startActivity(new Intent(this, trackerActivity.class));
        }

        // more if statements can enter here to intent to activities
        return super.onOptionsItemSelected(item);
    }
}
