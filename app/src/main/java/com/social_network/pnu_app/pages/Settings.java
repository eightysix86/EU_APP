package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.localdatabase.AppDatabase;

public class Settings extends AppCompatActivity {

    String senderUserId;
    DatabaseReference studentsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ProfileStudent profileStudent = new ProfileStudent();

        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(Settings.this));
        studentsReference = FirebaseDatabase.getInstance().getReference("students").child(senderUserId);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_settings);
        bottomNavigationView.setSelectedItemId(R.id.action_settings);

        menuChanges(bottomNavigationView);

        Button Exit;
        Exit = findViewById(R.id.btnExit);
        Exit.setOnClickListener(listenerBtnExit);
    }


    View.OnClickListener listenerBtnExit = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnExit) {
                Exit(AppDatabase.getAppDatabase(Settings.this));
            }
        }
    };

    private void Exit(final AppDatabase db) {
        FirebaseAuth.getInstance().signOut();
        db.studentDao().updateCurrentStudent(Integer.parseInt(db.studentDao().getCurrentStudent()));
        MainStudentPage.finalLocalFile = null;
        MainStudentPage.linkStorageFromFireBase = null;
        Intent intentSignOut = new Intent(this, MainActivity.class);
        intentSignOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentSignOut);
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

    private void lastSeen() {
        studentsReference.child("lastSeen").setValue(ServerValue.TIMESTAMP);
    }

    private void onlineStatus(final boolean online) {
        studentsReference.child("online").setValue(online);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO delete comment  if (currentStudent != null){
        onlineStatus(false);
        lastSeen();
        //  }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO delete comment     if (currentStudent != null){
        onlineStatus(true);
        //    }
    }
}
