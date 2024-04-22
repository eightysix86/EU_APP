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
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindNewFriends extends AppCompatActivity {

    RecyclerView allUsersList;
    private Query allDatabaseUsersReference;
    Button btnBackFromlUserList;
    ValueEventListener valueEventListener;
    String SerieIDCard;
    String senderUserId;
    NetworkStatus network = new NetworkStatus();
    Button btnAddFriendAllUsers;

    private DatabaseReference students;


    DatabaseReference studentsReferenceMy;
    private FirebaseAuth mAuth;
    private FirebaseUser currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_new_friends);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_users);
        bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));
        menuChanges(bottomNavigationView);

        mAuth = FirebaseAuth.getInstance();
        currentStudent = mAuth.getCurrentUser();

        ProfileStudent profileStudent = new ProfileStudent();

        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(FindNewFriends.this));

        studentsReferenceMy = FirebaseDatabase.getInstance().getReference("students").child(senderUserId);

        students = FirebaseDatabase.getInstance().getReference("students");


        btnAddFriendAllUsers = findViewById(R.id.btnAddFriendAllUsers);

        btnBackFromlUserList = findViewById(R.id.btnBackFromUserList);
        btnBackFromlUserList.setOnClickListener(btnlistener);


        allUsersList = findViewById(R.id.recyclerViewAllUsers);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseUsersReference = FirebaseDatabase.getInstance().getReference().child("students")
                .orderByChild("verify")
                .equalTo(true)
                .limitToLast(100);


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

    @Override
    public void onStart() {
        super.onStart();
        SerieIDCard = getStudentSeriesIDCard(AppDatabase.getAppDatabase(FindNewFriends.this));


        FirebaseRecyclerAdapter<Friends, AllAuthUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, AllAuthUsersViewHolder>(
                        Friends.class,
                        R.layout.all_users_display_layout,
                        AllAuthUsersViewHolder.class,
                        allDatabaseUsersReference

                ) {


            @Override
            protected void populateViewHolder(final AllAuthUsersViewHolder allAuthUsersViewHolder, final Friends allAuthUsers, final int i) {

                final String currentFriend = getRef(i).getKey();
                students.child(currentFriend).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            allAuthUsersViewHolder.setOnlineImage();
                        }
                        String linkFirebaseStorageMainPhoto;
                        try {
                            linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                        } catch (NullPointerException nullPointerException) {
                            linkFirebaseStorageMainPhoto = "";
                        }
                        allAuthUsersViewHolder.setStudentName(name, lastName);
                        allAuthUsersViewHolder.setStudentGroup(grop);

                        // TODO like below notEmpty() in all list in other classes

                        if (getApplicationContext() != null) {
                            allAuthUsersViewHolder.setStudentImage(getApplicationContext(), linkFirebaseStorageMainPhoto);
                        }

                        final String VisitedKey = getRef(i).getKey();
                        allAuthUsersViewHolder.checkFrendships(VisitedKey, getApplicationContext());
                        allAuthUsersViewHolder.checkSendered(VisitedKey, getApplicationContext());
                        allAuthUsersViewHolder.checkReceiver(VisitedKey, getApplicationContext());

                        allAuthUsersViewHolder.checkYourSubscibers(VisitedKey, getApplicationContext());
                        allAuthUsersViewHolder.checkOnYouAreSubscibed(VisitedKey, getApplicationContext());

                        if (senderUserId.equals(VisitedKey)) {
                            allAuthUsersViewHolder.checkMySelf(VisitedKey, getApplicationContext());
                        }

                        allAuthUsersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                 if (!senderUserId.equals(VisitedKey)) {
                                    String VisitedStudentKey = getRef(i).getKey();
                                    Intent profileIntent = new Intent(FindNewFriends.this, ProfileStudent.class);
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
        allUsersList.setAdapter(firebaseRecyclerAdapter);

    }


    View.OnClickListener btnlistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBackFromUserList:
                    Intent intentlistMyFriends;
                    intentlistMyFriends = new Intent( "com.social_network.pnu_app.pages.FriendsActivity");
                    startActivity(intentlistMyFriends);
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

    public void SendRequestOnAFriendship(final String ReceiverStudentKey, final Context context) {


      senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));
       final DatabaseReference FriendRequestsReferenceAlienReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).
                child("FriendRequestReceiver").child(senderUserId).child("requestType");

        final DatabaseReference FriendRequestsReferenceMySender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("FriendRequestSender").child(ReceiverStudentKey).child("requestType");


        final DatabaseReference studentsReference = FirebaseDatabase.getInstance().getReference("students");
        FriendRequestsReferenceAlienReceiver.setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FriendRequestsReferenceMySender.setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                HashMap<String, String> notificationData = new HashMap<String, String>();
                                notificationData.put("from", senderUserId);
                                notificationData.put("type", "requestSent");
                                studentsReference.child(ReceiverStudentKey).child("Notification").push().setValue(notificationData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {


                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (!network.isOnline()) {
                                                    //        progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(context, " Please Connect to Internet",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (!network.isOnline()) {
                                //        progressBar.setVisibility(View.GONE);
                                Toast.makeText(context, " Please Connect to Internet",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (!network.isOnline()) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
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

 class AllAuthUsersViewHolder extends RecyclerView.ViewHolder {
    View mView;


     Button btnAddFriendAllUsers;


     public void changeStateBtn(){
             mView.getContext();
             int activeColorButtonAddToFriends;
             int activeColorTextButtonAddToFriends;
             int disactivateColorButtonAddToFriends;
             int disactivateColorTextButtonAddToFriends;

             activeColorButtonAddToFriends = mView.getResources().getColor(R.color.lines);
             activeColorTextButtonAddToFriends = mView.getResources().getColor(R.color.btn_sign_in);

             btnAddFriendAllUsers= mView.findViewById(R.id.btnAddFriendAllUsers);
         btnAddFriendAllUsers.setVisibility(View.GONE);
         btnAddFriendAllUsers.setEnabled(false);
           //  btnAddFriendAllUsers.setText("Скасувати заявку");
            // btnAddFriendAllUsers.setBackground(btnAddFriendAllUsers.getResources().getDrawable(R.drawable.arrow_back));
           //  btnAddFriendAllUsers.setBackgroundColor(activeColorButtonAddToFriends);
             btnAddFriendAllUsers.setTextColor(activeColorTextButtonAddToFriends);

           /*  if (context != null) {
                 Toast.makeText(context, " Заявку надіслано",
                         Toast.LENGTH_LONG).show();
             }*/

     }

     public void checkMySelf(String visitedKey, Context context) {
         final String senderUserId;
         senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));

             btnAddFriendAllUsers.setVisibility(View.GONE);
             btnAddFriendAllUsers.setEnabled(false);
         }


     public void checkYourSubscibers(final String ReceiverKey, final Context context){
         btnAddFriendAllUsers= mView.findViewById(R.id.btnAddFriendAllUsers);

         DatabaseReference myReferenceSubsrcibers;
         final String senderUserId;
         senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));
         myReferenceSubsrcibers = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                 child("Subscribers");

         myReferenceSubsrcibers.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.hasChild(ReceiverKey)){

                     btnAddFriendAllUsers.setVisibility(View.GONE);
                     btnAddFriendAllUsers.setEnabled(false);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 FindNewFriends findNewFriends= new FindNewFriends();
                 if (!findNewFriends.network.isOnline()) {
                     //        progressBar.setVisibility(View.GONE);
                     Toast.makeText(context, " Please Connect to Internet",
                             Toast.LENGTH_LONG).show();
                 }
             }
         });
     }


     public void checkOnYouAreSubscibed(final String ReceiverKey, final Context context){
         final NetworkStatus network = new NetworkStatus();
         btnAddFriendAllUsers= mView.findViewById(R.id.btnAddFriendAllUsers);

         DatabaseReference myReferenceSubsrcibed;
         final String senderUserId;
         senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));
         myReferenceSubsrcibed = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                 child("Subscribed");

         myReferenceSubsrcibed.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.hasChild(ReceiverKey)){

                     btnAddFriendAllUsers.setVisibility(View.GONE);
                     btnAddFriendAllUsers.setEnabled(false);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 FindNewFriends findNewFriends= new FindNewFriends();
                 if (!findNewFriends.network.isOnline()) {
                     //        progressBar.setVisibility(View.GONE);
                     Toast.makeText(context, " Please Connect to Internet",
                             Toast.LENGTH_LONG).show();
                 }
             }
         });
     }


     public void checkReceiver(final String ReceiverKey, final Context context){
         final NetworkStatus network = new NetworkStatus();
         btnAddFriendAllUsers= mView.findViewById(R.id.btnAddFriendAllUsers);

         DatabaseReference myReferenceReceiverRequest;
         final String senderUserId;
         senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));
         myReferenceReceiverRequest = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                 child("FriendRequestReceiver");

         myReferenceReceiverRequest.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.hasChild(ReceiverKey)){

                     btnAddFriendAllUsers.setVisibility(View.GONE);
                     btnAddFriendAllUsers.setEnabled(false);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 FindNewFriends findNewFriends= new FindNewFriends();
                 if (!findNewFriends.network.isOnline()) {
                     //        progressBar.setVisibility(View.GONE);
                     Toast.makeText(context, " Please Connect to Internet",
                             Toast.LENGTH_LONG).show();
                 }
             }
         });


     }

     public void checkSendered(final String ReceiverKey, final Context context){
         final NetworkStatus network = new NetworkStatus();
         btnAddFriendAllUsers= mView.findViewById(R.id.btnAddFriendAllUsers);

         DatabaseReference myReferenceSenderedRequest;
         final String senderUserId;
         senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));
         myReferenceSenderedRequest = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                 child("FriendRequestSender");

         myReferenceSenderedRequest.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.hasChild(ReceiverKey)){

                     btnAddFriendAllUsers.setVisibility(View.GONE);
                     btnAddFriendAllUsers.setEnabled(false);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {
                 FindNewFriends findNewFriends= new FindNewFriends();
                 if (!findNewFriends.network.isOnline()) {
                     //        progressBar.setVisibility(View.GONE);
                     Toast.makeText(context, " Please Connect to Internet",
                             Toast.LENGTH_LONG).show();
                 }
             }
         });


     }

  public void checkFrendships(final String ReceiverKey, final Context context){
      final NetworkStatus network = new NetworkStatus();
      btnAddFriendAllUsers= mView.findViewById(R.id.btnAddFriendAllUsers);

      DatabaseReference myFriendsReference;
      final String senderUserId;
      senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(context));
      if (senderUserId !=null) {
          myFriendsReference = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                  child("Friends");

          myFriendsReference.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.hasChild(ReceiverKey)) {

                      btnAddFriendAllUsers.setVisibility(View.GONE);
                      btnAddFriendAllUsers.setEnabled(false);
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {
                  FindNewFriends findNewFriends = new FindNewFriends();
                  if (!findNewFriends.network.isOnline()) {
                      //        progressBar.setVisibility(View.GONE);
                      Toast.makeText(context, " Please Connect to Internet",
                              Toast.LENGTH_LONG).show();
                  }
              }
          });
      }
      btnAddFriendAllUsers.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if (network.isOnline()) {
                  btnAddFriendAllUsers = mView.findViewById(R.id.btnAddFriendAllUsers);
                  btnAddFriendAllUsers.setEnabled(false);
                  FindNewFriends findNewFriends = new FindNewFriends();
                  findNewFriends.SendRequestOnAFriendship(ReceiverKey, mView.getContext());

                  changeStateBtn();
              }
              else {
                  Toast.makeText(context, " Please Connect to Internet",
                          Toast.LENGTH_LONG).show();
              }




          }
      });

  }

     String getKeyCurrentStudend(final AppDatabase db) {
         String keyStudent = db.studentDao().getKeyStudent();
         return keyStudent;
     }


     public AllAuthUsersViewHolder(View itemView){
        super(itemView);

        mView= itemView;
    }

     public void setOnlineImage(){

         ImageView imageOnline = mView.findViewById(R.id.img_online_all_users);
         imageOnline.setVisibility(View.VISIBLE);
     }
    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.all_users_username);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }
     public void setStudentGroup(String studentGroup){
         TextView group = mView.findViewById(R.id.all_users_status);
         group.setText(studentGroup);
     }

     public void setStudentImage( final Context context, final String studentImage) {
         final CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
         if (!studentImage.isEmpty() && studentImage != "") {
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
         else {

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


 }
