package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.MessageData;
import com.social_network.pnu_app.functional.LastSeenTime;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messenger extends AppCompatActivity {

    private RecyclerView myMessengersList;

    private TextView textViewDefaultText;
    private ProgressBar progressBar;

    String senderUserId;
    Query myMessengersReference;
    DatabaseReference studentsReference;
    Query lastMyMessageQuery;
    Query lastAlienMessageQuery;

    HashMap<Object, Object> objectLastMyMessage = new HashMap();
    HashMap<Object, Object> objectLastAlienMessage = new HashMap();
    String lastMessage;
    long time;
    String timeLastMessage;
    boolean seen;
    String key;

    long countMessengers;
    int countUnseensMessege;
    String currentMessengers;

     String name;
     String lastName;

    private FirebaseListAdapter<MessageData> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_messenger);
        bottomNavigationView.setSelectedItemId(R.id.action_message);

        textViewDefaultText = findViewById(R.id.defaultTextListMessengers);
        progressBar = findViewById(R.id.progressBarMessengers);
        progressBar.setVisibility(View.VISIBLE);
        myMessengersList = findViewById(R.id.recyclerViewMessengers);

        myMessengersList.setHasFixedSize(true);
        myMessengersList.setLayoutManager(new LinearLayoutManager(this));

        menuChanges(bottomNavigationView);

        ProfileStudent profileStudent = new ProfileStudent();
        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(Messenger.this));
        studentsReference = FirebaseDatabase.getInstance().getReference("students");
        myMessengersReference = FirebaseDatabase.getInstance().getReference("students").child(senderUserId)
                .child("Messages").orderByPriority();



    }

    public void setTextViewForEmptyList() {
        if (countMessengers != 0) {
            textViewDefaultText.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            textViewDefaultText.setText(R.string.DefaultTextListMessengers);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        final int limitLenghtMessage = 21;
        progressBar.setVisibility(View.GONE);
        myMessengersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    countMessengers = dataSnapshot.getChildrenCount();
                    setTextViewForEmptyList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<MessageData, MessengersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MessageData, MessengersViewHolder>
                (
                        MessageData.class,
                        R.layout.messenger_layout,
                        MessengersViewHolder.class,
                        myMessengersReference

                ) {
            @Override
            protected void populateViewHolder(final MessengersViewHolder messengersViewHolder, final MessageData messageData, final int i) {
                currentMessengers = getRef(i).getKey();
                progressBar.setVisibility(View.GONE);
                Query unseens = FirebaseDatabase.getInstance().getReference("students")
                        .child(senderUserId).child("Messages")
                        .child(currentMessengers).orderByChild("seen").equalTo(false);

                unseens.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countUnseensMessege = (int)dataSnapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                lastMyMessageQuery = FirebaseDatabase.getInstance().getReference("students")
                        .child(senderUserId)
                        .child("Messages")
                        .child(currentMessengers)
                        .limitToLast(1);
                lastMyMessageQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            objectLastMyMessage = (HashMap<Object, Object>) snapshot.getValue();

                            try {
                                seen = (boolean) objectLastMyMessage.get("seen"); }
                            catch (Exception e){
                                seen = false; }

                            time = (long) objectLastMyMessage.get("time");
                            LastSeenTime objTimeLastMessage = new LastSeenTime();
                            timeLastMessage= objTimeLastMessage.getTimeMessenger(time);

                            lastMessage = (String) objectLastMyMessage.get("message");
                            if (lastMessage != null) {
                                lastMessage = lastMessage.codePointCount(0, lastMessage.length()) > limitLenghtMessage ?
                                        lastMessage.substring(0, lastMessage.offsetByCodePoints(0, limitLenghtMessage)).concat("...") :
                                        lastMessage;

                                lastMessage = lastMessage.replaceAll("\n", " ");
                            }
                            else {
                                lastMessage = "";
                            }
                            myMessengersList.scrollToPosition(0);


                            if (!seen){
                                messengersViewHolder.setUnseenMessage(countUnseensMessege);
                            }
                            else {

                                messengersViewHolder.offSeenMessage();
                            }


                            key = (String) objectLastMyMessage.get("key");
                            messengersViewHolder.setTimeLastMessage(timeLastMessage);
                            messengersViewHolder.setLastMessage(lastMessage, key, senderUserId);


                                }

                        messengersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                            @Override
                            public void onClick(View v) {
                                String VisitedStudentKey = getRef(i).getKey();
                                Intent intentToMessage = new Intent(Messenger.this, Message.class);
                                intentToMessage.putExtra("VisitedStudentKey", VisitedStudentKey);
                                startActivity(intentToMessage);
                                messengersViewHolder.mView.getId();// .setTop(0);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ///// Alien

                lastAlienMessageQuery = FirebaseDatabase.getInstance().getReference("students")
                        .child(currentMessengers)
                        .child("Messages")
                        .child(senderUserId)
                        .limitToLast(1);
                lastAlienMessageQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            objectLastAlienMessage = (HashMap<Object, Object>) snapshot.getValue();
                            boolean seenAlien;
                            try {
                                seenAlien = (boolean) objectLastAlienMessage.get("seen"); }
                            catch (Exception e){
                                seenAlien = false; }
                            key = (String) objectLastAlienMessage.get("key");

                            if (key.equals(senderUserId)){
                                if (seenAlien){
                                    messengersViewHolder.setSeenAlienMessage();
                                }
                                else {
                                    messengersViewHolder.setSendMessage();
                                }

                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                /////
                studentsReference.child(currentMessengers).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("name").getValue().toString();
                       lastName = dataSnapshot.child("lastName").getValue().toString();

                        boolean online;
                        try {
                            online = (boolean) dataSnapshot.child("online").getValue();
                        }catch (Exception e){
                            online = false;
                        }
                        if (online){
                           messengersViewHolder.setOnlineImage();
                        }

                        String linkFirebaseStorageMainPhoto;
                        try {
                            linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                        }
                        catch (NullPointerException nullPointerException){
                            linkFirebaseStorageMainPhoto ="";
                        }


                        messengersViewHolder.setStudentName(name, lastName);



                        if (linkFirebaseStorageMainPhoto != "" && getApplicationContext() != null) {
                            messengersViewHolder.setStudentImage(getApplicationContext(), linkFirebaseStorageMainPhoto);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        };
                  myMessengersList.setAdapter(firebaseRecyclerAdapter);
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
        studentsReference.child(senderUserId).child("lastSeen").setValue(ServerValue.TIMESTAMP);
    }

    private void onlineStatus(final boolean online) {
        studentsReference.child(senderUserId).child("online").setValue(online);
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

class MessengersViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public MessengersViewHolder(View itemView){
        super(itemView);

        mView= itemView;
    }


    public void setOnlineImage(){
        ImageView imageOnline = mView.findViewById(R.id.img_online_messenger);
        imageOnline.setVisibility(View.VISIBLE);
    }

    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.MessengerUsername);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }

    public void setLastMessage(String lastMessage, String keyMessage, String myKey){
        TextView tvLastMessage = mView.findViewById(R.id.tvLastMessage);
       if (keyMessage.equals(myKey)) {
            tvLastMessage.setText("Ви: " + lastMessage);
        }
        else{
            tvLastMessage.setText(lastMessage);
        }


    }
    public void setTimeLastMessage(String time){
        TextView timeLastMessage = mView.findViewById(R.id.tvMessengerTime);
        timeLastMessage.setText(time);
    }

    public void offSeenMessage(){
        TextView tvUnseenMessage = mView.findViewById(R.id.tvUnseenMessage);
        tvUnseenMessage.setVisibility(View.GONE);
    }

    public void setUnseenMessage(int countUnseenMessage){
        TextView tvUnseenMessage = mView.findViewById(R.id.tvUnseenMessage);
        tvUnseenMessage.setVisibility(View.VISIBLE);
        tvUnseenMessage.setText(String.valueOf(countUnseenMessage));
    }
    public void setSeenAlienMessage(){
        TextView tvConfirmSendMessage = mView.findViewById(R.id.tvConfirmSendLastMessage);
        tvConfirmSendMessage.setVisibility(View.GONE);

        TextView tvConfirmReceivedMessage = mView.findViewById(R.id.tvConfirmReceivedLastMessage);
        tvConfirmReceivedMessage.setVisibility(View.VISIBLE);

    }

    public void setSendMessage(){
        TextView tvConfirmReceivedMessage = mView.findViewById(R.id.tvConfirmReceivedLastMessage);
        tvConfirmReceivedMessage.setVisibility(View.GONE);

        TextView tvConfirmSendMessage = mView.findViewById(R.id.tvConfirmSendLastMessage);
        tvConfirmSendMessage.setVisibility(View.VISIBLE);
    }

    public void setStudentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.image_messenger);

        Picasso.with(context)
                .load(studentImage)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .error(R.drawable.com_facebook_close)
                .centerCrop()
                .fit()
                //.resize(1920,2560)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        if (studentImage != null) {
                            if (!studentImage.isEmpty()) {
                                Picasso.with(context)
                                        .load(studentImage)
                                        .placeholder(R.drawable.logo_pnu)
                                        .error(R.drawable.com_facebook_close)
                                        .centerCrop()
                                        .fit()
                                        //.resize(1920,2560)
                                        .into(image);
                            }
                        } else {
                            Picasso.with(context)
                                    .load(R.drawable.com_facebook_profile_picture_blank_square)
                                    .placeholder(R.drawable.logo_pnu)
                                    .error(R.drawable.com_facebook_close)
                                    .centerCrop()
                                    .fit()
                                    //.resize(1920,2560)
                                    .into(image);
                        }

                    }
                });

    }
}