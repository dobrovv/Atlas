package atlas.atlas;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager screenPager;
    OnBoardViewPagerAdapter onBoardViewPagerAdapter;
    TabLayout tabIndicator;
    Button nxtButton;
    Button getstartedbtn;
    Button backButton;
    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        //make activity appear on the full screen

        //hide action bar
        getSupportActionBar().hide();

        //indicator views
        tabIndicator = findViewById(R.id.tab_indicator);
        nxtButton = findViewById(R.id.next_button);
        getstartedbtn = findViewById(R.id.getstarted_button);
        backButton = findViewById(R.id.back_button);

        //fill list screen
        final List<ScreenItem>mList = new ArrayList<>();
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_1));
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_2));
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_3));
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_4));
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_5));
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_6));
        mList.add(new ScreenItem("Welcome","",R.drawable.intro_7));


        //setup of viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        onBoardViewPagerAdapter = new OnBoardViewPagerAdapter(this,mList);
        screenPager.setAdapter(onBoardViewPagerAdapter);

        //setup tablayout with view pager
        tabIndicator.setupWithViewPager(screenPager);

        //set up the next button for clicks
        nxtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < mList.size()){

                    position++;
                    screenPager.setCurrentItem(position);

                }


                if (position == mList.size()-1){

                    //show get started button and hide next button
                    loadFinalScreen();


                }
            }
        });

        //got to main when get started is click
        getstartedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        //set up back button to go back through images when clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    --position;
                    screenPager.setCurrentItem(position);


                if (position == 0){

                    //show forward button hide get started and back button
                    loadBeginScreen();


                }
            }
        });
    }

    private void loadFinalScreen() {
        tabIndicator.setVisibility(View.INVISIBLE);
        nxtButton.setVisibility(View.INVISIBLE);
        getstartedbtn.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
    }

    private void loadBeginScreen() {
        tabIndicator.setVisibility(View.VISIBLE);
        nxtButton.setVisibility(View.VISIBLE);
        getstartedbtn.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
    }
}
