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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.MySubscribers;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribedsProfileFragment extends Fragment {

    private View myMainView;
    ProgressBar progressBar;

    private RecyclerView SubscribedsProfileList;
    private DatabaseReference SubscribedsProfileReference;
    private DatabaseReference students;

    private TextView textViewDefaultText;
    private TextView textViewSubscribedsProfile;
    String SerieIDCard;

    String senderUserId;
    long countSubscribedsProfile;
    NetworkStatus network = new NetworkStatus();
    static Context myContex;


    String ReceiverStudentKey;


    public SubscribedsProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myContex = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ProfileStudent profileStudent = new ProfileStudent();
        ReceiverStudentKey = profileStudent.ReceiverStudentKey;


        myMainView = inflater.inflate(R.layout.fragment_subscribeds_profile, container, false);

        textViewDefaultText = myMainView.findViewById(R.id.defaultTextListSubscribedsProfile);

        textViewSubscribedsProfile = myMainView.findViewById(R.id.textViewSubscribedsProfile);

        progressBar = myMainView.findViewById(R.id.progressBarSubscribedsProfile);
        progressBar.setVisibility(View.VISIBLE);

        SubscribedsProfileList= myMainView.findViewById(R.id.SubscribedsProfileRecyclerView);
        ProfileStudent profileKey = new ProfileStudent();
        senderUserId = profileKey.getKeyCurrentStudend(AppDatabase.getAppDatabase(myContex));

        SubscribedsProfileReference = FirebaseDatabase.getInstance().getReference("studentsCollection")
                .child(ReceiverStudentKey)
                .child("Subscribed");

        students = FirebaseDatabase.getInstance().getReference("students");

        SubscribedsProfileList.setHasFixedSize(true);
        SubscribedsProfileList.setLayoutManager(new LinearLayoutManager(myContex));

        // Inflate the layout for this fragment
        return myMainView;
    }

    public void setTextViewForEmptyList() {
        if (countSubscribedsProfile != 0) {
            textViewDefaultText.setText("");
            textViewSubscribedsProfile.setText(getResources().getText(R.string.SubscribedsProfile) + " " +  countSubscribedsProfile);
        } else {
            progressBar.setVisibility(View.GONE);
            textViewDefaultText.setText(R.string.DefaultTextListSubscribedsProfile);
            textViewSubscribedsProfile.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SubscribedsProfileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countSubscribedsProfile = dataSnapshot.getChildrenCount();
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

                progressBar.setVisibility(View.GONE);
                FindNewFriends findNewFriends = new FindNewFriends();
                SerieIDCard = findNewFriends.getStudentSeriesIDCard(AppDatabase.getAppDatabase(myContex));
                FirebaseRecyclerAdapter<MySubscribers, SubscribedsProfileViewHolder> firebaseRecyclerAdapter
                        = new FirebaseRecyclerAdapter<MySubscribers, SubscribedsProfileViewHolder>
                        (
                                MySubscribers.class,
                                R.layout.subscribers_profile_layout,
                                SubscribedsProfileViewHolder.class,
                                SubscribedsProfileReference

                        ) {
                    @Override
                    protected void populateViewHolder(final SubscribedsProfileViewHolder subscribedsProfileViewHolder, final MySubscribers SubscribersProfile, final int i) {
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
                                    subscribedsProfileViewHolder.setOnlineImage();
                                }
                                String linkFirebaseStorageMainPhoto;
                                try {
                                    linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                                }
                                catch (NullPointerException nullPointerException){
                                    linkFirebaseStorageMainPhoto ="";
                                }
                                subscribedsProfileViewHolder.setStudentName(name, lastName);
                                subscribedsProfileViewHolder.setStudentGroup(grop);
                                if (linkFirebaseStorageMainPhoto != "" && myContex != null) {
                                    subscribedsProfileViewHolder.setStudentImage(myContex, linkFirebaseStorageMainPhoto);
                                }
                                subscribedsProfileViewHolder.mView.setOnClickListener(new View.OnClickListener() {
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
                SubscribedsProfileList.setAdapter(firebaseRecyclerAdapter);


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

}


  class SubscribedsProfileViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public SubscribedsProfileViewHolder(View itemView){
        super(itemView);
        mView= itemView;
    }

    public void setOnlineImage(){
        ImageView imageOnline = mView.findViewById(R.id.img_online_subscribersProfile);
        imageOnline.setVisibility(View.VISIBLE);
    }

    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.SubscribersProfile_username);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }
    public void setStudentGroup(String studentGroup){
        TextView group = mView.findViewById(R.id.SubscribersProfile_status);
        group.setText(studentGroup);
    }

    public void setStudentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.SubscribersProfile_profile_image);
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

