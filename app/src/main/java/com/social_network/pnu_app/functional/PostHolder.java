package com.social_network.pnu_app.functional;



import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social_network.pnu_app.R;

import com.social_network.pnu_app.entity.Comment;
import com.social_network.pnu_app.entity.Post;

import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.social_network.pnu_app.pages.ActivityListLikesPost;
import com.social_network.pnu_app.pages.FindNewFriends;
import com.social_network.pnu_app.pages.MainStudentPage;
import com.social_network.pnu_app.pages.ProfileStudent;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class PostHolder extends AppCompatActivity {

    DatabaseReference referenceMyPost;
    DatabaseReference referenceAlienPost;
    long idTime;
    long countPost;
    String currentPostedUser;
    String namePost;
    String lastNamePost;
    long time;
    String timePost;
    String linkFirebaseStoragePostPhoto;
    String keyPost;

    public static String SeriesIDCard;
    public String dirImages = "images/";
    public String pathToFirebaseStorage;



    public void SharePost(final Post post, String senderUserId, final String ReceiverStudentKey, final String keyPost){
        idTime = new Date().getTime();
        long minusIdTime = -1 * idTime;

     /*   if (ReceiverStudentKey == ""){
            ReceiverStudentKey = senderUserId;
        }*/

        referenceMyPost = FirebaseDatabase.getInstance().getReference("students").child(senderUserId).child("Posts").push();

        referenceMyPost.setValue(post, minusIdTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                referenceMyPost.child("countShare").setValue(0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                referenceAlienPost = FirebaseDatabase.getInstance().getReference("students")
                        .child(ReceiverStudentKey).child("Posts").child(keyPost).child("countShare");

                referenceAlienPost.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            referenceAlienPost.setValue(1);
                        }
                        else{
                            long countShare = (long) dataSnapshot.getValue();
                            countShare++;
                            referenceAlienPost.setValue(countShare);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                            }
                        });
            }
        });
    }


    public void addPostToDatabase(String post, String senderUserId, final Uri photo, final byte[] thumb_byte, final String pathToFirebaseStorage, String ReceiverStudentKey) {
        idTime = new Date().getTime();
        long minusIdTime = -1 * idTime;

        if (ReceiverStudentKey == ""){
            ReceiverStudentKey = senderUserId;
        }

        referenceMyPost = FirebaseDatabase.getInstance().getReference("students").child(ReceiverStudentKey).child("Posts").push();

        referenceMyPost.setValue(new Post(senderUserId, "text", post, idTime, 0), minusIdTime).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                referenceMyPost.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        keyPost = dataSnapshot.getKey();
                        if (photo != null){
                            StorageReference mStorageRef;
                            mStorageRef = FirebaseStorage.getInstance().getReference();



                            mStorageRef.child(pathToFirebaseStorage + keyPost).putBytes(thumb_byte).addOnCompleteListener(
                                    new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            String castomPathToFirebaseStorage = pathToFirebaseStorage.replace("/", "%2F");
                                            linkFirebaseStoragePostPhoto = "https://firebasestorage.googleapis.com/v0/b/pnu-app.appspot.com/o/"
                                                    .concat(castomPathToFirebaseStorage).concat(keyPost).concat("?alt=media&");

                                            referenceMyPost.child("linkFirebaseStoragePostPhoto").setValue(linkFirebaseStoragePostPhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    public void deletePostToDatabase(String keyPost, String senderUserId, String ReceiverStudentKey) {

        referenceMyPost = FirebaseDatabase.getInstance().getReference("students").child(ReceiverStudentKey).child("Posts").child(keyPost);

        referenceMyPost.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    public void holderPost(final String senderUserId, final TextView tvTextWall, final RecyclerView recyclerViewPost, final String ReceiverStudentKey) {
       Query myPostsReference = FirebaseDatabase.getInstance().getReference("students").child(ReceiverStudentKey).child("Posts").orderByPriority();
       final DatabaseReference allStudentsReference = FirebaseDatabase.getInstance().getReference("students");
        final CommentHolder commentHolder = new CommentHolder();
        FirebaseDatabase.getInstance().getReference("students").child(ReceiverStudentKey).child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countPost = dataSnapshot.getChildrenCount();
                if (countPost > 0){
                    tvTextWall.setVisibility(View.GONE);
                    //   holderPost();
                }
                else {
                    tvTextWall.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Post, PostViewHolder>
                (
                        Post.class,
                        R.layout.post_layout,
                        PostViewHolder.class,
                        myPostsReference

                ) {
            @Override
            protected void populateViewHolder(final PostViewHolder postViewHolder, final Post post, final int i) {

                final String currentKeyPost = getRef(i).getKey();
                /////
            //    recyclerViewPost.scrollToPosition(i);

                if (!post.getKeySender().equals(senderUserId)){
                    postViewHolder.disableBtnSettingPost();
                }
                else {
                    postViewHolder.setVisivbleBtnSettingPost();
                }

                if (post.getLinkFirebaseStoragePostPhoto() != null) {
                    postViewHolder.setStudentPostImage(postViewHolder.mView.getContext(), post.getLinkFirebaseStoragePostPhoto());
                }
                else { postViewHolder.disablePostImage();}

                if (post.getText().equals("")){
                    postViewHolder.disablePostText();
                }

                FirebaseDatabase.getInstance().getReference("students").child(senderUserId)
                        .child("linkFirebaseStorageMainPhoto").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getValue().toString();

                            postViewHolder.setStudentComentImage(postViewHolder.mView.getContext(), dataSnapshot.getValue().toString());

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                allStudentsReference.child(post.getKeySender()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        namePost = dataSnapshot.child("name").getValue().toString();
                        lastNamePost = dataSnapshot.child("lastName").getValue().toString();

                       commentHolder.holderComment(ReceiverStudentKey, postViewHolder.getRecyclerViewComentPost(), currentKeyPost, senderUserId);


                        String linkFirebaseStorageMainPhoto;
                        try {
                            linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                        }
                        catch (NullPointerException nullPointerException){
                            linkFirebaseStorageMainPhoto ="";
                        }


                        postViewHolder.setStudentName(namePost, lastNamePost);
                        postViewHolder.actionButton(senderUserId, currentKeyPost, post.getKeySender(), ReceiverStudentKey, post);


                        final DatabaseReference referenceMyPostLikes =FirebaseDatabase.getInstance().getReference("students")
                                .child(ReceiverStudentKey).child("Posts").child(currentKeyPost).child("likes");

                        referenceMyPostLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.hasChild(senderUserId)) {
                                            postViewHolder.setLikeOn();
                                        } else {
                                            postViewHolder.setLikeOff();
                                        }
                                    }
                                }
                                catch (NullPointerException e){
                                    postViewHolder.setLikeOff();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        // Count Likes
                        FirebaseDatabase.getInstance().getReference("students")
                                .child(ReceiverStudentKey).child("Posts").child(currentKeyPost).child("likes")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            dataSnapshot.getChildrenCount();
                                            postViewHolder.setCountLike(String.valueOf(dataSnapshot.getChildrenCount()));
                                        }
                                        else{
                                            postViewHolder.setCountEmptyLike();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        // Count Comment

                        FirebaseDatabase.getInstance().getReference("students")
                                .child(ReceiverStudentKey).child("Posts").child(currentKeyPost).child("Comments")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            dataSnapshot.getChildrenCount();
                                            postViewHolder.setCountComment(String.valueOf(dataSnapshot.getChildrenCount()));
                                        }
                                        else{
                                            postViewHolder.setCountEmptyComment();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        if (post.getCountShare() != 0){
                            postViewHolder.setPostShareCount(post.getCountShare());
                        }
                        else {
                            postViewHolder.disablePostShareCount();
                        }

                        if (linkFirebaseStorageMainPhoto != "" && postViewHolder.mView.getContext() != null) {
                            postViewHolder.setStudentImage(postViewHolder.mView.getContext(), linkFirebaseStorageMainPhoto);

                        }

                        time = post.getTime();
                        LastSeenTime objTimePost = new LastSeenTime();
                        timePost= objTimePost.getTimePost(time);

                        postViewHolder.setTimePost(timePost);

                        postViewHolder.setTextPost(post.getText());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        };
        recyclerViewPost.setAdapter(firebaseRecyclerAdapter);
    }

    public void alertDialogSettingsPost(final String keyPost, Context contex, final String senderUserId, final String ReceiverStudentKey){
        String mass[] = new String[] {"Видалити пост"};
        final AlertDialog.Builder a_builder = new AlertDialog.Builder(contex);

        a_builder.setTitle("Налаштування запису")
                .setItems( mass, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            // Delete post
                            deletePostToDatabase(keyPost, senderUserId, ReceiverStudentKey);
                        }

                    }
                });
        AlertDialog alert = a_builder.create();
        alert.show();
    }
}


class PostViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public PostViewHolder(View itemView){
        super(itemView);

        mView= itemView;
    }


    public void setOnlineImage(){
        ImageView imageOnline = mView.findViewById(R.id.img_online_post);
        imageOnline.setVisibility(View.VISIBLE);
    }

    public void setStudentName(String studentName, String studentLastName){
        TextView nameAndLastName = mView.findViewById(R.id.post_username);
        nameAndLastName.setText(studentName + " " + studentLastName);
    }

    public void setTimePost(String time){
        TextView timeLastMessage = mView.findViewById(R.id.post_time);
        timeLastMessage.setText(time);
    }
    public void setTextPost(String textPost){
        TextView timeLastMessage = mView.findViewById(R.id.tvPostContent);
        timeLastMessage.setText(textPost);
    }

    Button btnSettingsPost;
    ImageView btnLikeMyPost;
    ImageView sendComentOnPost;
    RecyclerView recyclerViewComentPost;
    EmojiconEditText editTextCommentPost;
    TextView tvCountLikePost;
    ImageView btnShareMyPost;
    public TextView tvCountComentPost;

    ImageView emoji_button_comment;
    private EmojIconActions emojIconActions;
    RelativeLayout rlCommentPost;


    public RecyclerView getRecyclerViewComentPost(){
        recyclerViewComentPost= mView.findViewById(R.id.recyclerViewComentPost);
        recyclerViewComentPost.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        return recyclerViewComentPost;
    }

    public void setLikeOn(){
        btnLikeMyPost.setBackground(mView.getContext().getResources().getDrawable(R.drawable.btn_like_press));
    }

    public void setLikeOff(){
        btnLikeMyPost.setBackground(mView.getContext().getResources().getDrawable(R.drawable.btn_like_unpress));
    }



    public void setCountLike(String countLike){
        tvCountLikePost = mView.findViewById(R.id.tvCountLikePost);
        tvCountLikePost.setVisibility(View.VISIBLE);
        tvCountLikePost.setText(countLike);
    }

    public void setCountEmptyLike(){
        tvCountLikePost = mView.findViewById(R.id.tvCountLikePost);
        tvCountLikePost.setVisibility(View.VISIBLE);
        tvCountLikePost.setText("");
    }

    public void setCountComment(String countComment){
        tvCountComentPost = mView.findViewById(R.id.tvCountComentPost);
        tvCountComentPost.setVisibility(View.VISIBLE);
        tvCountComentPost.setText(countComment);
    }

    public void setCountEmptyComment(){
        tvCountComentPost = mView.findViewById(R.id.tvCountComentPost);
        tvCountComentPost.setVisibility(View.VISIBLE);
        tvCountComentPost.setText("");
    }

    public void disableBtnSettingPost(){
        btnSettingsPost = mView.findViewById(R.id.btnSettingPost);
        btnSettingsPost.setVisibility(View.GONE);
    }

    public void setVisivbleBtnSettingPost(){
        btnSettingsPost = mView.findViewById(R.id.btnSettingPost);
        btnSettingsPost.setVisibility(View.VISIBLE);
    }


    public void actionButton(final String senderUserId, final String keyPost, final String keySetterLike, final String ReceiverStudentKey, final Post post) {
        final DatabaseReference referenceMyPostLikes =FirebaseDatabase.getInstance().getReference("students")
                .child(ReceiverStudentKey).child("Posts").child(keyPost).child("likes").child(senderUserId).child("date");

        btnSettingsPost = mView.findViewById(R.id.btnSettingPost);
        btnLikeMyPost =mView.findViewById(R.id.btnLikeMyPost);
        sendComentOnPost = mView.findViewById(R.id.sendComentOnPost);
        tvCountComentPost = mView.findViewById(R.id.tvCountComentPost);
        tvCountLikePost =  mView.findViewById(R.id.tvCountLikePost);

        btnSettingsPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnSettingsPost.setEnabled(false);
                PostHolder posts = new PostHolder();
                posts.alertDialogSettingsPost(keyPost, mView.getContext(), senderUserId, ReceiverStudentKey);
                btnSettingsPost.setEnabled(true);
            }
        });

        btnLikeMyPost.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                referenceMyPostLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.exists()) {
                                referenceMyPostLikes.removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                         setLikeOn();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            } else {
                                referenceMyPostLikes.setValue(System.currentTimeMillis())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                       setLikeOff();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }
                        catch (NullPointerException e){}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        editTextCommentPost = mView.findViewById(R.id.editTextCommentPost);
        emoji_button_comment = mView.findViewById(R.id.emoji_button_comment);
        rlCommentPost = mView.findViewById(R.id.rlCommentPost);
        emojIconActions = new EmojIconActions(mView.getContext(), rlCommentPost, editTextCommentPost, emoji_button_comment);
        emojIconActions.ShowEmojIcon();
        sendComentOnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           /*     NetworkStatus network = new NetworkStatus();
                if (!network.isOnline()) {
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(mView.getContext(), " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                } else {*/

                    String comment = editTextCommentPost.getText().toString();
                    System.out.println("comment = " + comment);
                    if (!comment.equals("")) {
                        CommentHolder commentHolder= new CommentHolder();
                        commentHolder.addCommentToDatabase(keyPost, comment, senderUserId, ReceiverStudentKey,null,null,null);
                        editTextCommentPost.setText("");
                    } else {
                        Toast.makeText(mView.getContext(), "Введіть запис!",
                                Toast.LENGTH_SHORT).show();
                    }

             //   }
            }
        });

        tvCountLikePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keySender = ReceiverStudentKey;

                final List<String> postKeys = new ArrayList<String>();

                postKeys.add(0, keySender);
                postKeys.add(1,keyPost);
                Intent intentListLikesPost = new Intent(mView.getContext(), ActivityListLikesPost.class);
                intentListLikesPost.putStringArrayListExtra("keysPost", (ArrayList<String>) postKeys);
                mView.getContext().startActivity(intentListLikesPost);
            }
        });

        final ImageView image = mView.findViewById(R.id.btnSendWallPhotoComment);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileStudent profileStudent = new ProfileStudent();
                profileStudent.addPhoto();
            NetworkStatus network = new NetworkStatus();
                if (!network.isOnline()) {
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(mView.getContext(), " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                } else {
                    profileStudent.addPhoto();
                }
            }
        });


        btnShareMyPost = mView.findViewById(R.id.btnShareMyPost);
        btnShareMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostHolder postHolder = new PostHolder();
                postHolder.SharePost(post, senderUserId, ReceiverStudentKey, keyPost);
            }
        });

    }

    public void setPostShareCount(int countSharePost){
        TextView tvCountSharePost = mView.findViewById(R.id.tvCountSharePost);
        tvCountSharePost.setText(String.valueOf(countSharePost));
    }


    public void disablePostShareCount(){
        TextView tvCountSharePost = mView.findViewById(R.id.tvCountSharePost);
        tvCountSharePost.setVisibility(View.GONE);
    }

    public void disablePostImage(){
         ImageView image = mView.findViewById(R.id.ImagePost);
        image.setVisibility(View.GONE);
    }


    public void disablePostText(){
        TextView timeLastMessage = mView.findViewById(R.id.tvPostContent);
        timeLastMessage.setVisibility(View.GONE);
    }


    public void setStudentPostImage(final Context context, final String studentImage) {
        final ImageView image = mView.findViewById(R.id.ImagePost);
        image.setMinimumHeight(500);
        image.setMinimumWidth(600);
        Picasso.with(context)
                .load(studentImage)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.com_facebook_auth_dialog_background)
                .error(R.drawable.com_facebook_close)
                .resize(800,700)
                .centerInside()
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
                                        .resize(800,700)
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

    public void setStudentComentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.post_profile_image_coment);

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

    public void setStudentImage(final Context context, final String studentImage) {
        final CircleImageView image = mView.findViewById(R.id.post_profile_image);

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



