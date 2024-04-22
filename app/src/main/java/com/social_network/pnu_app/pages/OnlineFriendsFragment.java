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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.FriendsOnline;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineFriendsFragment extends Fragment {

    private View myMainView;
    ProgressBar progressBar;

    private RecyclerView myFriendsOnlineList;
    private Query myFriendsReference;
    private DatabaseReference students;

    private TextView textViewDefaultText;
    private TextView textViewMyOnlineFrineds;
    String SerieIDCard;

    String senderUserId;
    long countFriendsOnline;

    HashMap<Object, Object> student = new HashMap();
    HashMap<Object, Object> studentQuery;
    NetworkStatus network = new NetworkStatus();

    public OnlineFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_online_friends, container, false);

        textViewDefaultText = myMainView.findViewById(R.id.defaultTextListFriendOnline);
        textViewMyOnlineFrineds= myMainView.findViewById(R.id.textViewMyFriendsOnline);
        progressBar = myMainView.findViewById(R.id.progressBarMyFriendsOnline);
        progressBar.setVisibility(View.VISIBLE);

        myFriendsOnlineList = myMainView.findViewById(R.id.friendsRecyclerViewOnline);
        ProfileStudent profileStudent = new ProfileStudent();
        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(getContext()));

        myFriendsReference =FirebaseDatabase.getInstance().getReference("studentsCollection")
                .child(senderUserId)
                .child("Friends");

     /*   myFriendsReference =FirebaseDatabase.getInstance().getReference("studentsCollection")
                .child(senderUserId)
                .child("Friends");
        myFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                student =  (HashMap) dataSnapshot.getValue();
                studentQuery = student;
                System.out.println("student = " + student);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        students = FirebaseDatabase.getInstance().getReference("students");

        myFriendsOnlineList.setHasFixedSize(true);
        myFriendsOnlineList.setLayoutManager(new LinearLayoutManager(getContext()));


        //  BottomNavigationView bottomNavigationView = (BottomNavigationView) myMainView.findViewById(R.id.bottom_navigation_friends);
        //  bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));

        // Inflate the layout for this fragment
        return myMainView;
    }

    public void setTextViewForEmptyList() {
        if (countFriendsOnline != 0) {
            textViewDefaultText.setText("");
            textViewMyOnlineFrineds.setText(getResources().getText(R.string.MyOnlineFriends) + " " +  countFriendsOnline);
          //  + " " + getResources().getText(R.string.MyOnlineFriends2) );
        } else {
            progressBar.setVisibility(View.GONE);
            textViewDefaultText.setText(R.string.DefaultTextListMyOnlineFriends);
            textViewMyOnlineFrineds.setText("");
        }
    }

   @Override
    public void onStart() {
        super.onStart();

        myFriendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

        students.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                FindNewFriends findNewFriends = new FindNewFriends();
                SerieIDCard = findNewFriends.getStudentSeriesIDCard(AppDatabase.getAppDatabase(getContext()));

                FirebaseRecyclerAdapter<FriendsOnline, FriendsOnlineViewHolder> firebaseRecyclerAdapter
                        = new FirebaseRecyclerAdapter<FriendsOnline, FriendsOnlineViewHolder>
                        (
                                FriendsOnline.class,
                                R.layout.friends_layout,
                                FriendsOnlineViewHolder.class,
                                myFriendsReference

                        ) {


                    @Override
                    protected void populateViewHolder(final FriendsOnlineViewHolder friendsOnlineViewHolder, final FriendsOnline friendsOnline, final int i) {
                        setTextViewForEmptyList();
                        String currentFriend = getRef(i).getKey();
                        students.child(currentFriend).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("online")) {
                                    boolean online;
                                    try {
                                        online = (boolean) dataSnapshot.child("online").getValue();
                                    } catch (Exception e) {
                                        online = false;
                                    }
                                    if (online) {
                                        countFriendsOnline++;
                                        String name = dataSnapshot.child("name").getValue().toString();
                                        String lastName = dataSnapshot.child("lastName").getValue().toString();
                                        String grop = dataSnapshot.child("group").getValue().toString();
                                        final String seriesIDcard = dataSnapshot.child("seriesIDcard").getValue().toString();
                                        String linkFirebaseStorageMainPhoto;
                                        try {
                                            linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                                        } catch (NullPointerException nullPointerException) {
                                            linkFirebaseStorageMainPhoto = "";
                                        }
                                        friendsOnlineViewHolder.setStudentName(name, lastName);
                                        friendsOnlineViewHolder.setStudentGroup(grop);
                                        if (linkFirebaseStorageMainPhoto != "") {
                                            friendsOnlineViewHolder.setStudentImage(getContext(), linkFirebaseStorageMainPhoto);
                                        }
                                        friendsOnlineViewHolder.mView.setOnClickListener(new View.OnClickListener() {
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

                                }
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
                myFriendsOnlineList.setAdapter(firebaseRecyclerAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }

            ;

        });

    }

}

  class FriendsOnlineViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public FriendsOnlineViewHolder(View itemView){
        super(itemView);

        mView= itemView;
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


