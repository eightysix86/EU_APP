package com.social_network.pnu_app.pages;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.RequestsFriends;
import com.social_network.pnu_app.functional.AcceptFriendRecyclerView;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFriendsFragment extends Fragment {

    private View myMainView;

    Button btnFriends;
    Button btnFindNewFriends;

    private RecyclerView myRequestsFriendsList;
    private DatabaseReference myFriendRequestReceiverReference;
    private DatabaseReference students;

    private TextView textViewDefaultText;
    String SerieIDCard;

    String senderUserId;
    long countFriends;
    NetworkStatus network = new NetworkStatus();

   static Context myContex;


    public RequestsFriendsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_requests_friends, container, false);

        textViewDefaultText = myMainView.findViewById(R.id.defaultTextListRequestsFriend);

        myRequestsFriendsList = myMainView.findViewById(R.id.requestsFriendsRecyclerView);

        senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(getContext()));
        myFriendRequestReceiverReference = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("FriendRequestReceiver");

        students = FirebaseDatabase.getInstance().getReference("students");

        myRequestsFriendsList.setHasFixedSize(true);
        myRequestsFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        btnFriends=myMainView.findViewById(R.id.btnRequestsFriends);
        btnFriends.setOnClickListener(listenerBtn);

        btnFindNewFriends = myMainView.findViewById(R.id.btnReguestsFindNewFriends);
        btnFindNewFriends.setOnClickListener(listenerBtn);

        // Inflate the layout for this fragment
        return myMainView;
    }

    String getKeyCurrentStudend(final AppDatabase db) {
        String keyStudent = db.studentDao().getKeyStudent();
        return keyStudent;
    }


    View.OnClickListener listenerBtn = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRequestsFriends:
                    Intent intentFriends;
                    intentFriends = new Intent( "com.social_network.pnu_app.pages.FriendsActivity");
                    startActivity(intentFriends);
                    break;
                case R.id.btnReguestsFindNewFriends:
                    Intent intentFindNewFriends;
                    intentFindNewFriends = new Intent( "com.social_network.pnu_app.pages.FindNewFriends");
                    startActivity(intentFindNewFriends);
                    break;
            } }};

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myContex = context;
    }

    public void setTextViewForEmptyList() {
        if (countFriends != 0) {
            textViewDefaultText.setText("");
        } else {
            textViewDefaultText.setText(R.string.DefaultTextRequestsFriendsList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();



        myFriendRequestReceiverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFriends = dataSnapshot.getChildrenCount();
                setTextViewForEmptyList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline() && myContex != null) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(myContex, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        students.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        FindNewFriends findNewFriends = new FindNewFriends();
                        SerieIDCard = findNewFriends.getStudentSeriesIDCard(AppDatabase.getAppDatabase(getContext()));
                        FirebaseRecyclerAdapter<RequestsFriends, RequestsFriendsViewHolder> firebaseRecyclerAdapter
                                = new FirebaseRecyclerAdapter<RequestsFriends, RequestsFriendsViewHolder>
                                (
                                        RequestsFriends.class,
                                        R.layout.requests_friends_layout,
                                        RequestsFriendsViewHolder.class,
                                        myFriendRequestReceiverReference

                                ) {
                            @Override
                            protected void populateViewHolder(final RequestsFriendsViewHolder requestsFriendsViewHolder, final RequestsFriends requestsFriends, final int i) {
                                setTextViewForEmptyList();
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
                                        }catch (Exception e){
                                            online = false;
                                        }
                                        if (online){
                                            requestsFriendsViewHolder.setOnlineImage();
                                        }

                                        String linkFirebaseStorageMainPhoto;
                                        try {

                                            linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                                        }
                                        catch (NullPointerException nullPointerException){
                                            linkFirebaseStorageMainPhoto ="";
                                        }
                                        requestsFriendsViewHolder.setStudentName(name, lastName);
                                        requestsFriendsViewHolder.setStudentGroup(grop);
                                        if (linkFirebaseStorageMainPhoto != "" && myContex != null) {
                                            requestsFriendsViewHolder.setStudentImage(myContex, linkFirebaseStorageMainPhoto);
                                        }
                                //        String VisitedKey = getRef(i).getKey();
                                        requestsFriendsViewHolder.actionButton(currentFriend, senderUserId, getContext());
                                        requestsFriendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (!seriesIDcard.equals(SerieIDCard)) {
                                                    String VisitedStudentKey = getRef(i).getKey();
                                                    Intent profileIntent = new Intent("com.social_network.pnu_app.pages.ProfileStudent");
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
                                        if (!network.isOnline() && myContex != null) {
                                            //        progressBar.setVisibility(View.GONE);
                                            Toast.makeText(myContex, " Please Connect to Internet",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                            }
                        };
                        myRequestsFriendsList.setAdapter(firebaseRecyclerAdapter);

            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
                if (!network.isOnline() && myContex != null) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(myContex, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    public void SubscribeFriendRequest(final String VisitedStudentKey) {
        final String ReceiverStudentKey = VisitedStudentKey;
        DateFormat calForDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentDate = new Date();
        final String saveCurrentDate = calForDate.format(currentDate);

        senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(getContext()));

        DatabaseReference SubscribersReferenceMy = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).child("Subscribers");
        SubscribersReferenceMy.keepSynced(true);

        SubscribersReferenceMy.child(ReceiverStudentKey).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                DatabaseReference SubscribedReferenceAlien = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).child("Subscribed");
                SubscribedReferenceAlien.keepSynced(true);

                SubscribedReferenceAlien.child(senderUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        DatabaseReference CheckFriendReferenceRequestMyReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                                child("FriendRequestReceiver");
                        CheckFriendReferenceRequestMyReceiver.keepSynced(true);

                        CheckFriendReferenceRequestMyReceiver.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    DatabaseReference CheckFriendReferenceRequestAlienSender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey)
                                            .child("FriendRequestSender");
                                    CheckFriendReferenceRequestAlienSender.keepSynced(true);

                                    CheckFriendReferenceRequestAlienSender.child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                      /*          btnAddToFriends.setEnabled(true);
                                                CurrentStateFriend ="onYouSubscribed";
                                                btnAddToFriends.setText("Ваш підписник");
                                                btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                                                btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);*/

                                                // Remove My Sender and Alien Receiver (if in receiver happens interrupt internet connection)

                                               DatabaseReference FriendRequestsReferenceMySender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                                                        child("FriendRequestSender").child(ReceiverStudentKey).child("requestType");
                                                FriendRequestsReferenceMySender.keepSynced(true);

                                               DatabaseReference FriendRequestsReferenceAlienReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).
                                                        child("FriendRequestReceiver").child(senderUserId).child("requestType");
                                                FriendRequestsReferenceAlienReceiver.keepSynced(true);

                                                FriendRequestsReferenceMySender.getParent().removeValue();
                                                FriendRequestsReferenceAlienReceiver.getParent().removeValue();

                                                if (myContex != null) {
                                                    Toast.makeText(myContex, "Додано в підписники",
                                                            Toast.LENGTH_LONG).show();
                                                }

                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (!network.isOnline() && myContex != null) {
                                                //        progressBar.setVisibility(View.GONE);
                                                Toast.makeText(myContex, " Please Connect to Internet",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (!network.isOnline() && myContex != null) {
                                    //     progressBar.setVisibility(View.GONE);
                                    Toast.makeText(myContex, " Please Connect to Internet",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!network.isOnline() && myContex != null) {
                            //        progressBar.setVisibility(View.GONE);
                            Toast.makeText(myContex, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            //////////
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (!network.isOnline() && myContex != null) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(myContex, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //////////


    }


}

class RequestsFriendsViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public RequestsFriendsViewHolder(View itemView){
        super(itemView);

        mView= itemView;

    }

    Button btnAddFriend;
    Button btnDeclimeFriend;

    public void actionButton(final String VisitedStudentKey, final String senderUserId, final Context myContex) {

        btnAddFriend =mView.findViewById(R.id.btnAddRequestFriends);
        btnDeclimeFriend =mView.findViewById(R.id.btnDeclineRequestFriends);

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFriend.setEnabled(false);
                AcceptFriendRecyclerView acceptFriendRecyclerView = new AcceptFriendRecyclerView();
                acceptFriendRecyclerView.AcceptFriendRequst(VisitedStudentKey, senderUserId, myContex);
                btnAddFriend.setEnabled(true);
            }
        });

               btnDeclimeFriend.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       btnDeclimeFriend.setEnabled(false);
                       RequestsFriendsFragment requestsDeclineFriends = new RequestsFriendsFragment();
                       requestsDeclineFriends.SubscribeFriendRequest(VisitedStudentKey);
                       btnDeclimeFriend.setEnabled(true);
                   }
               });

                }
    public void setOnlineImage(){

        ImageView imageOnline = mView.findViewById(R.id.img_online_requests);
        imageOnline.setVisibility(View.VISIBLE);
    }

    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.request_friend_username);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }
    public void setStudentGroup(String studentGroup){
        TextView group = mView.findViewById(R.id.request_friend_status);
        group.setText(studentGroup);
    }

    public void setStudentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.request_friend_profile_image);
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