package com.social_network.pnu_app.firebase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.pages.ProfileStudent;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class OfflineCapabilities extends Application {

    private DatabaseReference studentsReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentStudent;

    @Override
    public void onCreate(){
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // load picture offline- Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = FirebaseAuth.getInstance();
        currentStudent= mAuth.getCurrentUser();

        ProfileStudent profileStudent = new ProfileStudent();
       final String senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(getApplicationContext()));

        if(currentStudent != null){

            studentsReference = FirebaseDatabase.getInstance().getReference("students").child(senderUserId);

            studentsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    studentsReference.child("online").onDisconnect().setValue(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
