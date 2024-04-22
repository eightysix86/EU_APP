package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.localdatabase.AppDatabase;

public class FriendsActivity extends AppCompatActivity {

    private ViewPager viewPagerFriends;
    private TabLayout tabLayoutFriends;
    private TabsFriendsAdapter friendsAdapter;

    private FirebaseUser currentStudent;
    DatabaseReference studentsReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ProfileStudent profileStudent = new ProfileStudent();

        mAuth = FirebaseAuth.getInstance();
        currentStudent= mAuth.getCurrentUser();

        String senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(FriendsActivity.this));
        studentsReference = FirebaseDatabase.getInstance().getReference("students").child(senderUserId);

        viewPagerFriends = findViewById(R.id.friend_tabs_pages);
        friendsAdapter = new TabsFriendsAdapter(getSupportFragmentManager());
        viewPagerFriends.setAdapter(friendsAdapter);
        tabLayoutFriends = findViewById(R.id.tabs_friends);
        tabLayoutFriends.setupWithViewPager(viewPagerFriends);

       BottomNavigationView bottomNavigationView = (BottomNavigationView)
               findViewById(R.id.bottom_navigation_friends);
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