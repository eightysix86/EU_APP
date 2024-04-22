package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.social_network.pnu_app.R;

public class PollsActivity extends AppCompatActivity {

    Button btnBackFromPolls;
    Button btnCreatePoll;
    Button btnYourPolls;
    Button btnNewPolls;
    Button btnFinishedPolls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        btnBackFromPolls = findViewById(R.id.btnBackPolls);
        btnCreatePoll = findViewById(R.id.btnCreatePolls);
        btnYourPolls = findViewById(R.id.btnYourPolls);
        btnNewPolls = findViewById(R.id.btnNewPolls);
        btnFinishedPolls = findViewById(R.id.btnFinishedPolls);

        btnBackFromPolls.setOnClickListener(btnlistener);
        btnCreatePoll.setOnClickListener(btnlistener);
        btnYourPolls.setOnClickListener(btnlistener);
        btnNewPolls.setOnClickListener(btnlistener);
        btnFinishedPolls.setOnClickListener(btnlistener);


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_polls);
        bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));
        menuChanges(bottomNavigationView);
    }
    public void menuChanges(BottomNavigationView bottomNavigationView){

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intentMenu;
                        switch (item.getItemId()) {
                            case R.id.action_search:
                                intentMenu = new Intent( "com.social_network.pnu_app.pages.Search");
                                startActivity(intentMenu);

                                break;
                            case R.id.action_message:
                                intentMenu = new Intent( "com.social_network.pnu_app.pages.Messenger");
                                startActivity(intentMenu);


                                break;
                            case R.id.action_main_student_page:
                                intentMenu = new Intent( "com.social_network.pnu_app.pages.MainStudentPage");
                                startActivity(intentMenu);

                                break;
                            case R.id.action_schedule:
                                intentMenu = new Intent( "com.social_network.pnu_app.pages.Schedule");
                                startActivity(intentMenu);

                                break;
                            case R.id.action_settings:
                                intentMenu = new Intent( "com.social_network.pnu_app.pages.Settings");
                                startActivity(intentMenu);

                                break;
                        }
                        return false;
                    }
                });
    }

    View.OnClickListener btnlistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnBackPolls:
                    Intent intentBackPolls;
                    intentBackPolls = new Intent( "com.social_network.pnu_app.pages.MainStudentPage");
                    startActivity(intentBackPolls);
                    break;

                case R.id.btnCreatePolls:

                    Intent intentCreatePolls;
                    intentCreatePolls = new Intent( "com.social_network.pnu_app.pages.BuildPollsActivity");
                    startActivity(intentCreatePolls);
                    break;

                case R.id.btnYourPolls:
                    Intent intentYourPolls;
                    intentYourPolls = new Intent( "com.social_network.pnu_app.pages.YourPollsActivity");
                    startActivity(intentYourPolls);
                    break;

                case R.id.btnNewPolls:
                    Intent intentNewPolls;
                    intentNewPolls = new Intent( "com.social_network.pnu_app.pages.NewPollsActivity");
                    startActivity(intentNewPolls);
                    break;

                case R.id.btnFinishedPolls:
                    Intent intentFinishedPolls;
                    intentFinishedPolls = new Intent( "com.social_network.pnu_app.pages.FinishedPollsActivity");
                    startActivity(intentFinishedPolls);
                    break;
        }
        }
    };


}
