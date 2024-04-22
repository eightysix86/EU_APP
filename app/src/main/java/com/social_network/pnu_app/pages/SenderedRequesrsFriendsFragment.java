package com.social_network.pnu_app.pages;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.RequestsFriends;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SenderedRequesrsFriendsFragment extends Fragment {

    private View myMainView;

    Button btnFriends;
    Button btnFindNewFriends;

    private RecyclerView mySenderedFriendsList;
    private DatabaseReference mySenderedFriendRequestReference;
    private DatabaseReference students;

    private TextView textViewDefaultText;
    String SerieIDCard;

    String senderUserId;
    long countFriends;
    NetworkStatus network = new NetworkStatus();


   static Context myContex;

    public SenderedRequesrsFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_sendered_requesrs_friends, container, false);

        textViewDefaultText = myMainView.findViewById(R.id.defaultTextListSenderedFriend);

        mySenderedFriendsList = myMainView.findViewById(R.id.senderedFriendsRecyclerView);

        senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(getContext()));

        mySenderedFriendRequestReference = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("FriendRequestSender");

        students = FirebaseDatabase.getInstance().getReference("students");

        mySenderedFriendsList.setHasFixedSize(true);
        mySenderedFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        btnFriends = myMainView.findViewById(R.id.btnSenderedFriends);
        btnFriends.setOnClickListener(listenerBtn);

        btnFindNewFriends = myMainView.findViewById(R.id.btnSenderedFindNewFriends);
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
                case R.id.btnSenderedFriends:
                    Intent intentFriends;
                    intentFriends = new Intent( "com.social_network.pnu_app.pages.FriendsActivity");
                    startActivity(intentFriends);
                    break;
                case R.id.btnSenderedFindNewFriends:
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
            textViewDefaultText.setText(R.string.DefaultTextSenderedFriendsRequestsList);
        }
    }

    @Override
    public void onStart() {
        super.onStart();



        mySenderedFriendRequestReference.addValueEventListener(new ValueEventListener() {
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
                    FirebaseRecyclerAdapter<RequestsFriends, SenderedFriendsViewHolder> firebaseRecyclerAdapter
                            = new FirebaseRecyclerAdapter<RequestsFriends, SenderedFriendsViewHolder>
                            (
                                    RequestsFriends.class,
                                    R.layout.sendered_friends_layout,
                                    SenderedFriendsViewHolder.class,
                                    mySenderedFriendRequestReference

                            ) {
                        @Override
                        protected void populateViewHolder(final SenderedFriendsViewHolder senderedFriendsViewHolder, final RequestsFriends requestsFriends, final int i) {
                            setTextViewForEmptyList();
                            String currentFriend = getRef(i).getKey();
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
                                    }catch (Exception e){
                                        online = false;
                                    }
                                    if (online){
                                        senderedFriendsViewHolder.setOnlineImage();
                                    }

                                    String linkFirebaseStorageMainPhoto;
                                    try {
                                      linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                                    }
                                    catch (NullPointerException nullPointerException){
                                        linkFirebaseStorageMainPhoto = "";
                                    }
                                    senderedFriendsViewHolder.setStudentName(name, lastName);
                                    senderedFriendsViewHolder.setStudentGroup(grop);
                                    if(linkFirebaseStorageMainPhoto != "" && myContex != null) {
                                        senderedFriendsViewHolder.setStudentImage(myContex, linkFirebaseStorageMainPhoto);
                                    }
                                    String VisitedKey = getRef(i).getKey();
                                    senderedFriendsViewHolder.actionButton(VisitedKey);
                                    senderedFriendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
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
                    mySenderedFriendsList.setAdapter(firebaseRecyclerAdapter);

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


    public void CancelSederedFriendRequest(String VisitedStudentKey) {

        senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(getContext()));

        final String ReceiverStudentKey = VisitedStudentKey;
        // SEND AND CANCEL REQUESTS
        DatabaseReference FriendRequestsReferenceAlienReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(VisitedStudentKey).
                child("FriendRequestReceiver").child(senderUserId).child("requestType");
        FriendRequestsReferenceAlienReceiver.keepSynced(true);

        final DatabaseReference FriendRequestsReferenceMySender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("FriendRequestSender").child(ReceiverStudentKey).child("requestType");
        FriendRequestsReferenceMySender.keepSynced(true);

        final DatabaseReference  CheckFriendReferenceRequestMyReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("FriendRequestReceiver");
        CheckFriendReferenceRequestMyReceiver.keepSynced(true);

        final DatabaseReference CheckFriendReferenceRequestAlienSender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey)
                .child("FriendRequestSender");
        CheckFriendReferenceRequestAlienSender.keepSynced(true);



        FriendRequestsReferenceAlienReceiver.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FriendRequestsReferenceMySender.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                CheckFriendReferenceRequestMyReceiver.child(ReceiverStudentKey).removeValue();
                                CheckFriendReferenceRequestAlienSender.child(senderUserId).removeValue();
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
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(myContex, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

  }

  class SenderedFriendsViewHolder extends RecyclerView.ViewHolder {
             View mView;

    public SenderedFriendsViewHolder(View itemView){
        super(itemView);

        mView= itemView;
    }

    Button btnCancelRequest;

    public void actionButton(final String VisitedStudentKey) {

        btnCancelRequest =mView.findViewById(R.id.btnCancelSenderedRequestFriends);

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCancelRequest.setEnabled(false);
                SenderedRequesrsFriendsFragment senderedFragment = new SenderedRequesrsFriendsFragment();
                senderedFragment.CancelSederedFriendRequest(VisitedStudentKey);
                btnCancelRequest.setEnabled(true);
            }
        });


    }

      public void setOnlineImage(){

          ImageView imageOnline = mView.findViewById(R.id.img_online_sendered);
          imageOnline.setVisibility(View.VISIBLE);
      }

    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.sendered_friend_username);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }
    public void setStudentGroup(String studentGroup){
        TextView group = mView.findViewById(R.id.sendered_friend_status);
        group.setText(studentGroup);
    }

    public void setStudentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.sendered_friend_profile_image);
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
