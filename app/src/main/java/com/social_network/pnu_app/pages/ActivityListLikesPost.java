package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.Friends;
import com.social_network.pnu_app.entity.Likes;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityListLikesPost extends AppCompatActivity {

    RecyclerView recyclerViewListLikesPost;
    ValueEventListener valueEventListener;
    String SerieIDCard;
    String senderUserId;
    NetworkStatus network = new NetworkStatus();


    private DatabaseReference students;

    TextView defaultTextListFriend;
    TextView tvLikesPost;

    DatabaseReference studentsReferenceMy;
    private FirebaseAuth mAuth;
    private FirebaseUser currentStudent;
    DatabaseReference referenceLikesPost;
    Button btnBackFromLikesPost;

    String keySenderPost;
    String keyPost;
    long countLikes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_likes_post);

        Intent intentGetKeysPost= getIntent();
        ArrayList<String> listKeys = intentGetKeysPost.getStringArrayListExtra("keysPost");

        System.out.println("keySender = " + listKeys.get(0));
        System.out.println("keyPost= " + listKeys.get(1));
        keySenderPost = listKeys.get(0);
        keyPost = listKeys.get(1);


        referenceLikesPost = FirebaseDatabase.getInstance().getReference("students")
                .child(keySenderPost).child("Posts").child(keyPost).child("likes");


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_likes_post);
        bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));
        menuChanges(bottomNavigationView);

        defaultTextListFriend = findViewById(R.id.defaultTextListFriend);
        tvLikesPost = findViewById(R.id.tvLikesPost);
        btnBackFromLikesPost = findViewById(R.id.btnBackFromLikesPost);

        btnBackFromLikesPost.setOnClickListener(btnlistener);
        mAuth = FirebaseAuth.getInstance();
        currentStudent = mAuth.getCurrentUser();

        ProfileStudent profileStudent = new ProfileStudent();

        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(ActivityListLikesPost.this));

        studentsReferenceMy = FirebaseDatabase.getInstance().getReference("students").child(senderUserId);

        students = FirebaseDatabase.getInstance().getReference("students");

        recyclerViewListLikesPost = findViewById(R.id.recyclerViewLikesPost);
        recyclerViewListLikesPost.setHasFixedSize(true);
        recyclerViewListLikesPost.setLayoutManager(new LinearLayoutManager(this));


    }


    public String getStudentSeriesIDCard(final AppDatabase db){
        int currentStudent = Integer.parseInt(db.studentDao().getCurrentStudent());
        String SeriesIDCard = db.studentDao().getSeriesBYId(currentStudent);
        return SeriesIDCard;
    }

    String getKeyCurrentStudend(final AppDatabase db) {
        String keyStudent = db.studentDao().getKeyStudent();
        return keyStudent;
    }

    public void setTextViewForEmptyList() {
        if (countLikes != 0) {
            tvLikesPost.setText(getResources().getText(R.string.likesPost) + " " + countLikes);
            defaultTextListFriend.setText("");
        } else {
            tvLikesPost.setText(getResources().getText(R.string.likesPost) + " " + countLikes);
            defaultTextListFriend.setText(R.string.DefaultTextListLikesPost);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        referenceLikesPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countLikes = dataSnapshot.getChildrenCount();
                setTextViewForEmptyList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(ActivityListLikesPost.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        SerieIDCard = getStudentSeriesIDCard(AppDatabase.getAppDatabase(ActivityListLikesPost.this));

        FirebaseRecyclerAdapter<Likes, AllAuthUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Likes, AllAuthUsersViewHolder>(
                Likes.class,
                R.layout.all_users_display_layout,
                AllAuthUsersViewHolder.class,
                referenceLikesPost

        ) {
            @Override
            protected void populateViewHolder(final AllAuthUsersViewHolder likesPostViewHolder, final Likes likes, final int i) {



                final String currentFriend = getRef(i).getKey();
                students.child(currentFriend).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String lastName = dataSnapshot.child("lastName").getValue().toString();
                        String grop = dataSnapshot.child("group").getValue().toString();
                        final String seriesIDcard = dataSnapshot.child("seriesIDcard").getValue().toString();
                        boolean online;
                        try {
                            online = (boolean) dataSnapshot.child("online").getValue();
                        } catch (Exception e) {
                            online = false;
                        }
                        if (online) {
                            likesPostViewHolder.setOnlineImage();
                        }
                        String linkFirebaseStorageMainPhoto;
                        try {
                            linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                        } catch (NullPointerException nullPointerException) {
                            linkFirebaseStorageMainPhoto = "";
                        }
                        likesPostViewHolder.setStudentName(name, lastName);
                        likesPostViewHolder.setStudentGroup(grop);
                        if (linkFirebaseStorageMainPhoto != "" && getApplicationContext() != null) {
                            likesPostViewHolder.setStudentImage(getApplicationContext(), linkFirebaseStorageMainPhoto);
                        }

                        likesPostViewHolder.checkFrendships(currentFriend, getApplicationContext());
                        likesPostViewHolder.checkSendered(currentFriend, getApplicationContext());
                        likesPostViewHolder.checkReceiver(currentFriend, getApplicationContext());

                        likesPostViewHolder.checkYourSubscibers(currentFriend, getApplicationContext());
                        likesPostViewHolder.checkOnYouAreSubscibed(currentFriend, getApplicationContext());

                        if (senderUserId.equals(currentFriend)) {
                            likesPostViewHolder.checkMySelf(currentFriend, getApplicationContext());
                        }

                        likesPostViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!senderUserId.equals(currentFriend)) {
                                    String VisitedStudentKey = getRef(i).getKey();
                                    Intent profileIntent = new Intent(ActivityListLikesPost.this, ProfileStudent.class);
                                    profileIntent.putExtra("VisitedStudentKey", VisitedStudentKey);
                                    startActivity(profileIntent);
                                } else {
                                    Intent myProfileIntent = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
                                    startActivity(myProfileIntent);

                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //      allAuthUsersViewHolder.setStudentName(allAuthUsers.getName(), allAuthUsers.getLastName());
                //       allAuthUsersViewHolder.setStudentGroup(allAuthUsers.getGroup());
                //      allAuthUsersViewHolder.setStudentImage(getApplicationContext(), (allAuthUsers.getLinkFirebaseStorageMainPhoto()));


            }
        };
        recyclerViewListLikesPost.setAdapter(firebaseRecyclerAdapter);

    }


    View.OnClickListener btnlistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBackFromLikesPost:
                    onBackPressed();


                    break;

            }
        }
    };

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
        studentsReferenceMy.child("lastSeen").setValue(ServerValue.TIMESTAMP);
    }

    private void onlineStatus(final boolean online) {
        studentsReferenceMy.child("online").setValue(online);
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


