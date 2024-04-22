package com.social_network.pnu_app.pages;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.Friends;
import com.social_network.pnu_app.functional.AcceptFriendRecyclerView;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;




/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private View myMainView;
    Button btnRequests;
    ProgressBar progressBar;

    private RecyclerView myFriendsList;
    private DatabaseReference myFriendsReference;
    private DatabaseReference students;

    private TextView textViewDefaultText;
    String SerieIDCard;

    String senderUserId;
    long countFriends;
    NetworkStatus network = new NetworkStatus();

    static Context myContex;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        textViewDefaultText = myMainView.findViewById(R.id.defaultTextListFriend);
        progressBar = myMainView.findViewById(R.id.progressBarMyFriends);
        progressBar.setVisibility(View.VISIBLE);

        myFriendsList = myMainView.findViewById(R.id.friendsRecyclerView);
        ProfileStudent profileStudent = new ProfileStudent();
        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(getContext()));

        myFriendsReference = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("Friends");
        myFriendsReference.keepSynced(true);

        students = FirebaseDatabase.getInstance().getReference("students");
        students.keepSynced(true);

        myFriendsList.setHasFixedSize(true);
        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        //  BottomNavigationView bottomNavigationView = (BottomNavigationView) myMainView.findViewById(R.id.bottom_navigation_friends);
        //  bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));


        Button btnFindNewFriends;
        btnFindNewFriends = myMainView.findViewById(R.id.btnFindNewFriends);
        btnRequests = myMainView.findViewById(R.id.btnFindRequests);

        btnFindNewFriends.setOnClickListener(listenerBtn);
        btnRequests.setOnClickListener(listenerBtn);

        // Inflate the layout for this fragment
        return myMainView;
    }

    View.OnClickListener listenerBtn = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnFindNewFriends:
                    Intent intentFindNewFriends;
                    intentFindNewFriends = new Intent("com.social_network.pnu_app.pages.FindNewFriends");
                    startActivity(intentFindNewFriends);
                    break;
                case R.id.btnFindRequests:
                    Intent intentBackFriendList;
                    intentBackFriendList = new Intent("com.social_network.pnu_app.pages.RequestsFriendsActivity");
                    startActivity(intentBackFriendList);
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myContex = context;
    }
    public void setTextViewForEmptyList() {
        if (countFriends != 0) {
            textViewDefaultText.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            textViewDefaultText.setText(R.string.DefaultTextListFriend);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        myFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFriends = dataSnapshot.getChildrenCount();
                setTextViewForEmptyList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

                    progressBar.setVisibility(View.GONE);
                    FindNewFriends findNewFriends = new FindNewFriends();
                    SerieIDCard = findNewFriends.getStudentSeriesIDCard(AppDatabase.getAppDatabase(getContext()));

                    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter
                            = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                            (
                                    Friends.class,
                                    R.layout.friends_layout,
                                    FriendsViewHolder.class,
                                    myFriendsReference

                            ) {
                        @Override
                        protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, final Friends friends, final int i) {
                            setTextViewForEmptyList();
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
                                    }catch (Exception e){
                                        online = false;
                                    }
                                    if (online){
                                        friendsViewHolder.setOnlineImage();
                                    }
                                    String linkFirebaseStorageMainPhoto;
                                    try {
                                        linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                                    }
                                    catch (NullPointerException nullPointerException){
                                        linkFirebaseStorageMainPhoto ="";
                                    }
                                    friendsViewHolder.setStudentName(name, lastName);
                                    friendsViewHolder.setStudentGroup(grop);
                                    if (linkFirebaseStorageMainPhoto != "" && myContex != null) {
                                        friendsViewHolder.setStudentImage(myContex, linkFirebaseStorageMainPhoto);
                                    }


                                    friendsViewHolder.mView.findViewById(R.id.btnSendMessageLayout).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intentSendMessageFriends= new Intent("com.social_network.pnu_app.pages.Message");
                                            intentSendMessageFriends.putExtra("VisitedStudentKey", currentFriend);
                                            startActivity(intentSendMessageFriends);
                                        }
                                    });
                                    friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
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
                                    if (!network.isOnline()) {
                                        //        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), " Please Connect to Internet",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        }
                    };
                    myFriendsList.setAdapter(firebaseRecyclerAdapter);


            }


}

class FriendsViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public FriendsViewHolder(View itemView){
        super(itemView);

        mView= itemView;
    }

    public void setOnlineImage(){

        ImageView imageOnline = mView.findViewById(R.id.img_online_friends);
        imageOnline.setVisibility(View.VISIBLE);
    }

    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.friends_username);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }
    public void setStudentGroup(String studentGroup){
        TextView group = mView.findViewById(R.id.friends_status);
        group.setText(studentGroup);
    }

    public void setStudentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.friends_profile_image);
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




