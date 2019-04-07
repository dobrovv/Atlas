package atlas.atlas;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

      ProgressBar progressBar;
     // TextView textView_welcome;
      TextView textView_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress_bar);
       // textView_welcome = findViewById(R.id.textView_welcome);
        textView_progress = findViewById(R.id.text_view);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        progressAnim();


    }

    //function to show progress animation
    public void progressAnim(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar,textView_progress,0f, 100f);
        anim.setDuration(6000);
        progressBar.setAnimation(anim);
    }
}
