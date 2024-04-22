package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.functional.LastSeenTime;
import com.social_network.pnu_app.functional.PostHolder;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;


public class ProfileStudent extends AppCompatActivity {


    TextView tvPIBvalue;
    TextView tvFacultyValue;
    TextView tvGroupValue;
    TextView tvDateOfEntryValue;
    TextView tvFormStudyingValue;
    TextView tvOnlineProfile;
    TextView tvTextWallProfile;
    RecyclerView recyclerViewPostProfile;

    Button btnAddToFriends;
    Button btnlistFriends;
    Button btnListSubscribersProfile;
    Button btnSendMessage;
    ImageView btnSendWallProfile;
    ImageView btnSendWallPhotoProfile;
    EmojiconEditText editTextWallProfile;

    private String CurrentStateFriend;

    private DatabaseReference FriendRequestsReferenceAlienReceiver;  //  Отримувач
    private DatabaseReference FriendRequestsReferenceMySender;  //  Відправник

    private DatabaseReference FriendReferenceAlien; //  Отримувач
    private DatabaseReference FriendReferenceMy;  //  Відправник;


    DatabaseReference CheckFriendReferenceRequestMyReceiver;  // Check
    DatabaseReference CheckFriendReferenceRequestMySender;     // Check
    DatabaseReference CheckFriendReferenceRequestAlienSender;  // Check

    DatabaseReference SubscribersReferenceMy;                 // Subscribers My
    DatabaseReference SubscribersReferenceAlien;              // Subscribers Alien
    DatabaseReference SubscribedReferenceMy;                  // Subscribed My
    DatabaseReference SubscribedReferenceAlien;               // Subscribed Alien
    DatabaseReference studentsReference;                  // Notification

    String senderUserId;

    static String ReceiverStudentKey;

    String SendeRequestType;
    String ReceiverRequestType;

    public CircleImageView imStudentMainPhoto;
    public ImageView imSendPhotoWall;

    String name;
    String lastName;
    String group;
    String dateOfEntry;
    String formStudying;
    String faculty;
    String linkFirebaseStorageMainPhoto;
    boolean online;
    long countFriends;
    long countSubsrcribers;
    long lastSenn;
    ValueEventListener valueEventListener;
    NetworkStatus network = new NetworkStatus();


    int activeColorButtonAddToFriends;
    int activeColorTextButtonAddToFriends;

    int disactivateColorButtonAddToFriends;
    int disactivateColorTextButtonAddToFriends;
    private static final int REQUEST_CODE_PERMISSION_RECEIVE_CAMERA = 102;
    private static final int REQUEST_CODE_TAKE_PHOTO = 103;

    public static Uri finalLocalFile;
    Bitmap thumb_bitmap = null;

    private File mTempPhoto;
    private String mImageUri = "";
    public static String SeriesIDCard;
    public String dirImages = "images/";
    static long countMyPosts;

    public String pathToFirebaseStorage;
    Query AlienPostsReference;
    Button btnWallNotesProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ReceiverStudentKey = getIntent().getExtras().get("VisitedStudentKey").toString();
        senderUserId = getKeyCurrentStudend(AppDatabase.getAppDatabase(ProfileStudent.this));


        // SEND AND CANCEL REQUESTS
        FriendRequestsReferenceAlienReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).
                child("FriendRequestReceiver").child(senderUserId).child("requestType");
        FriendRequestsReferenceAlienReceiver.keepSynced(true);

        FriendRequestsReferenceMySender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("FriendRequestSender").child(ReceiverStudentKey).child("requestType");
        FriendRequestsReferenceMySender.keepSynced(true);

        // CHECK MY AND ALIEN REFERENCE

        CheckFriendReferenceRequestMyReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
        child("FriendRequestReceiver");
        CheckFriendReferenceRequestMyReceiver.keepSynced(true);

        CheckFriendReferenceRequestMySender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId)
        .child("FriendRequestSender");
        CheckFriendReferenceRequestMySender.keepSynced(true);

        // -> DELETE ALIEN SENDER WHEN ACCEPT FRIEND
        CheckFriendReferenceRequestAlienSender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey)
                .child("FriendRequestSender");
        CheckFriendReferenceRequestAlienSender.keepSynced(true);


        // FRIENDS

        FriendReferenceAlien = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).
                child("Friends");

        FriendReferenceMy = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("Friends");
        FriendReferenceMy.keepSynced(true);

       // Subscribers

        SubscribersReferenceMy = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).child("Subscribers");
        SubscribersReferenceMy.keepSynced(true);

        SubscribersReferenceAlien = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).child("Subscribers");

        // Subscribed

        SubscribedReferenceAlien = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).child("Subscribed");
        SubscribedReferenceAlien.keepSynced(true);

        SubscribedReferenceMy = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).child("Subscribed");
        SubscribedReferenceMy.keepSynced(true);

        // Notification
        studentsReference = FirebaseDatabase.getInstance().getReference("students");
        studentsReference.keepSynced(true);


        // Colors
        activeColorButtonAddToFriends = getResources().getColor(R.color.lines);
        activeColorTextButtonAddToFriends = getResources().getColor(R.color.btn_sign_in);

        disactivateColorButtonAddToFriends = getResources().getColor(R.color.btn_sign_in);
        disactivateColorTextButtonAddToFriends = getResources().getColor(R.color.colorAccentWhite);

        // Views

        btnAddToFriends = findViewById(R.id.btnAddToFriends);
        btnlistFriends = findViewById(R.id.btnListFriendsProfile);
        btnListSubscribersProfile = findViewById(R.id.btnListSubscribersProfile);
        btnSendMessage = findViewById(R.id.btnSendMessageProfile);


        tvPIBvalue = findViewById(R.id.tvPIBvalueProfile);
        tvFacultyValue = findViewById(R.id.tvFacultyValueProfile);
        tvGroupValue = findViewById(R.id.tvGroupValueProfile);
        tvDateOfEntryValue = findViewById(R.id.tvDateOfEntryValueProfile);
        tvFormStudyingValue = findViewById(R.id.tvFormStudyingValueProfile);
        tvOnlineProfile = findViewById(R.id.tvOnlineProfile);


        btnAddToFriends.setOnClickListener(btnlistener);
        btnlistFriends.setOnClickListener(btnlistener);
        btnListSubscribersProfile.setOnClickListener(btnlistener);
        btnSendMessage.setOnClickListener(btnlistener);

        btnSendWallProfile = findViewById(R.id.btnSendWallProfile);
        btnSendWallProfile.setOnClickListener(btnlistener);

        btnSendWallPhotoProfile = findViewById(R.id.btnSendWallPhotoProfile);
        btnSendWallPhotoProfile.setOnClickListener(btnlistener);

        btnWallNotesProfile = findViewById(R.id.btnWallNotesProfile);

        recyclerViewPostProfile = findViewById(R.id.recyclerViewPostProfile);
        recyclerViewPostProfile.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_profileProfile);
        bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));

        AlienPostsReference = FirebaseDatabase.getInstance().getReference("students").child(ReceiverStudentKey).child("Posts")
                .orderByPriority();

        editTextWallProfile = findViewById(R.id.editTextWallProfile);
        tvTextWallProfile = findViewById(R.id.tvTextWallProfile);

        SeriesIDCard = getStudentSeriesIDCard(AppDatabase.getAppDatabase(ProfileStudent.this));
        CurrentStateFriend = "notFriend";


        pathToFirebaseStorage = dirImages + SeriesIDCard + "/";
        menuChanges(bottomNavigationView);

        imStudentMainPhoto = findViewById(R.id.imStudentMainPhotoProfile);
        imSendPhotoWall = findViewById(R.id.imSendPhotoWallProfile);

        PostHolder posts = new PostHolder();
        posts.holderPost(senderUserId ,tvTextWallProfile, recyclerViewPostProfile, ReceiverStudentKey);
        BuildStudentPage();


    }

      public String getKeyCurrentStudend(final AppDatabase db) {
        String keyStudent = db.studentDao().getKeyStudent();
        return keyStudent;
    }

    public String getStudentSeriesIDCard(final AppDatabase db){
        int currentStudent = Integer.parseInt(db.studentDao().getCurrentStudent());
        String SeriesIDCard = db.studentDao().getSeriesBYId(currentStudent);
        return SeriesIDCard;
    }


    public void BuildStudentPage(){
        Query queryByKey = FirebaseDatabase.getInstance().getReference("students").child(ReceiverStudentKey);

  /*      if(!network.isOnline()){
            Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                    Toast.LENGTH_LONG).show();*/
      //  }
     //   else{
        queryByKey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                System.out.println("dataSnapshot =" + dataSnapshot);

                name = dataSnapshot.child("name").getValue().toString();
                lastName = dataSnapshot.child("lastName").getValue().toString();
                group = dataSnapshot.child("group").getValue().toString();
                dateOfEntry = dataSnapshot.child("dateOfEntry").getValue().toString();
                formStudying = dataSnapshot.child("formStudying").getValue().toString();
                faculty = dataSnapshot.child("faculty").getValue().toString();
                try {
                    online = (boolean) dataSnapshot.child("online").getValue();
                } catch (Exception e) {
                    online = false;
                }
                try {
                    lastSenn = (long) dataSnapshot.child("lastSeen").getValue();
                } catch (Exception e) {
                    lastSenn = 0;
                }

                try {
                    linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                }
                catch (NullPointerException nullLinkPhoto){
                    linkFirebaseStorageMainPhoto = "";
                }

                FriendReferenceAlien.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countFriends = dataSnapshot.getChildrenCount();

                        btnlistFriends.setText(countFriends + " " + "Друзі");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (!network.isOnline()) {
                            //   progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


                SubscribersReferenceAlien.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countSubsrcribers = dataSnapshot.getChildrenCount();

                        btnListSubscribersProfile.setText(countSubsrcribers + " " + "Підписники");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (!network.isOnline()) {
                            //   progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


                AlienPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        countMyPosts = dataSnapshot.getChildrenCount();
                        btnWallNotesProfile.setText("Записи " + countMyPosts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        NetworkStatus network = new NetworkStatus();
                        if (!network.isOnline()) {
                            // progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                });

                tvPIBvalue.setText(lastName + " " + name);
                tvFacultyValue.setText(" " + faculty);
                tvGroupValue.setText(" " + group);
                tvDateOfEntryValue.setText(" " + dateOfEntry);
                tvFormStudyingValue.setText(" " + formStudying);

                if (online) {
                    tvOnlineProfile.setText(getString(R.string.Online));
                }
                else{
                    if (lastSenn != 0) {
                        LastSeenTime getTime = new LastSeenTime();
                        long lastSeenTime = Long.parseLong(String.valueOf(lastSenn));
                        String lastSeenDisplayTime = getTime.getTimeAgo(lastSeenTime);
                        tvOnlineProfile.setText(lastSeenDisplayTime);
                    }
                    else {
                        tvOnlineProfile.setText(getString(R.string.NotOnline));
                    }
                }

                FirebaseDatabase.getInstance().getReference("students").child(senderUserId)
                        .child("linkFirebaseStorageMainPhoto").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getValue().toString();

                            if (!dataSnapshot.getValue().toString().isEmpty()) {

                                Picasso.with(ProfileStudent.this)
                                        .load(dataSnapshot.getValue().toString())
                                        .placeholder(R.drawable.logo_pnu)
                                        .error(R.drawable.com_facebook_close)
                                        .centerCrop()
                                        .fit()
                                        // .resize(1920,2560)
                                        .into(imSendPhotoWall);
                            } else {
                                Picasso.with(ProfileStudent.this)
                                        .load(R.drawable.com_facebook_profile_picture_blank_square)
                                        .placeholder(R.drawable.logo_pnu)
                                        .error(R.drawable.com_facebook_close)
                                        .centerCrop()
                                        .fit()
                                        // .resize(1920,2560)
                                        .into(imSendPhotoWall);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if (!linkFirebaseStorageMainPhoto.isEmpty()){
                Picasso.with(ProfileStudent.this)
                        .load(linkFirebaseStorageMainPhoto)
                        .placeholder(R.drawable.logo_pnu)
                        .error(R.drawable.com_facebook_close)
                        .centerCrop()
                        .fit()
                        // .resize(1920,2560)
                        .into(imStudentMainPhoto);

                }
                else {
                    Picasso.with(ProfileStudent.this)
                            .load(R.drawable.com_facebook_profile_picture_blank_square)
                            .placeholder(R.drawable.logo_pnu)
                            .error(R.drawable.com_facebook_close)
                            .centerCrop()
                            .fit()
                            // .resize(1920,2560)
                            .into(imStudentMainPhoto);


                }

               // CHECK ON MY RECEIVED
                checkOnMyReceivedRequest();

                // CHECK ON MY SENT
                checkOnMySentRequest();

                // CHECK ON MY FRIEND
                checkOnMyFriend();

                // CHECK ON YOU SUBSCRIBED
                checkOnYouSubscribed();

                // CHECK YOU ARE SUBSCRIBED
                checkAreYouSubscribed();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                 //   progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        }

    // CHECK ON MY RECEIVED
    private void checkOnMyReceivedRequest() {
        CheckFriendReferenceRequestMyReceiver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(ReceiverStudentKey)) {
                        ReceiverRequestType = dataSnapshot.child(ReceiverStudentKey).child("requestType").getValue().toString();
                        if (ReceiverRequestType != null) {
                            if (ReceiverRequestType.equals("received")) {
                                CurrentStateFriend = "requestReceived";
                                btnAddToFriends.setText("Відповісти на заявку в друзі");
                                btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                                btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                        }
                    }
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
                if (!network.isOnline()) {
                    //      progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    // CHECK ON MY SENT
    private void checkOnMySentRequest() {
        CheckFriendReferenceRequestMySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(ReceiverStudentKey)) {
                        SendeRequestType = dataSnapshot.child(ReceiverStudentKey).child("requestType").getValue().toString();

                        if (SendeRequestType != null) {
                            if (SendeRequestType.equals("sent")) {
                                CurrentStateFriend = "requestSent";
                                btnAddToFriends.setText("Скасувати заявку");
                                btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                                btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                        }
                    }
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
                if (!network.isOnline()) {
                    //    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    // CHECK ON MY FRIEND
    private void checkOnMyFriend() {
        FriendReferenceMy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(ReceiverStudentKey)) {
                        CurrentStateFriend = "friends";
                        btnAddToFriends.setText("Видалити з друзів");
                        btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                        btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // CHECK ON YOU SUBSCRIBED
    private void checkOnYouSubscribed(){
        SubscribersReferenceMy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(ReceiverStudentKey)) {
                        CurrentStateFriend ="onYouSubscribed";
                        btnAddToFriends.setText("Ваш підписник");
                        btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                        btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // CHECK YOU ARE SUBSCRIBED
    private void checkAreYouSubscribed(){

        SubscribersReferenceAlien.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(senderUserId)) {
                        CurrentStateFriend ="youAreSubscribed";
                        btnAddToFriends.setText("Ви підписані");
                        btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                        btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public void addPhoto() {
        //Проверяем разрешение на работу с камеройMainStudentPage
        boolean isCameraPermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        //Проверяем разрешение на работу с внешнем хранилещем телефона
        boolean isWritePermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        //Если разрешения != true
        if (!isCameraPermissionGranted || !isWritePermissionGranted) {

            String[] permissions;//Разрешения которые хотим запросить у пользователя

            if (!isCameraPermissionGranted && !isWritePermissionGranted) {
                permissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            } else if (!isCameraPermissionGranted) {
                permissions = new String[]{android.Manifest.permission.CAMERA};
            } else {
                permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }
            //Запрашиваем разрешения у пользователя
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION_RECEIVE_CAMERA);
        } else {
            //Если все разрешения получены
            try {
                mTempPhoto = createTempImageFile(getExternalCacheDir());
                mImageUri = mTempPhoto.getAbsolutePath();

                //Создаём лист с интентами для работы с изображениями
                List<Intent> intentList = new ArrayList<>();
                Intent chooserIntent = null;


                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                takePhotoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempPhoto));

                intentList = addIntentsToList(this, intentList, pickIntent);
                intentList = addIntentsToList(this, intentList, takePhotoIntent);

                if (!intentList.isEmpty()) {
                    chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), "Choose your image source");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
                }

                /*После того как пользователь закончит работу с приложеним(которое работает с изображениями)
                 будет вызван метод onActivityResult
                */
                startActivityForResult(chooserIntent, REQUEST_CODE_TAKE_PHOTO);
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
    }


    //Абсолютний шлях файлу із Uri
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int columnIndex = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }
    /*
      File storageDir -  шлях до кешу
     */
    public static File createTempImageFile(File storageDir) throws IOException {

        // Генерируем имя файла
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());//получаем время
        String imageFileName = "photo_" + timeStamp;//состовляем имя файла

        //Создаём файл
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /*
    Метод для добавления интента в список інтентів
    */
    public static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        mImageUri = getRealPathFromURI(data.getData());

                        finalLocalFile = Uri.parse(getRealPathFromURI(data.getData()));
                        Uri resultUri = finalLocalFile;
                        File thumb_filePathUri = new File(resultUri.getPath());

                        try {
                            thumb_bitmap = new Compressor(this)
                                    .setMaxWidth(500)
                                    .setMaxHeight(500)
                                    .setQuality(75)
                                    .compressToBitmap(thumb_filePathUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                        final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                        /*
                        Toast.makeText(MainStudentPage.this, "onActivityResult " + finalLocalFile,
                                Toast.LENGTH_LONG).show();*/


                            String post = editTextWallProfile.getText().toString();
                            System.out.println("post = " + post);
                            //  if (!post.equals("")) {
                            PostHolder posts = new PostHolder();
                            posts.addPostToDatabase(post, senderUserId, data.getData(),thumb_byte, pathToFirebaseStorage, ReceiverStudentKey);
                        editTextWallProfile.setText("");
                        /*    } else {
                                Toast.makeText(MainStudentPage.this, "Введіть запис!",
                                        Toast.LENGTH_SHORT).show();
                            }*/



                    }/* else if (mImageUri != null) {
                        mImageUri = Uri.fromFile(mTempPhoto).toString();
                        Toast.makeText(MainStudentPage.this, "onActivityResult 2" + finalLocalFile,
                                Toast.LENGTH_LONG).show();
                        uploadFileInFireBaseStorage(Uri.fromFile((mTempPhoto)));
                        Picasso.with(this)
                                .load(mImageUri)
                                .placeholder(R.drawable.logo_pnu)
                                .error(R.drawable.com_facebook_tooltip_black_xout)
                                .centerInside()
                                .fit()
                           //     .resize(100,100)
                                .into(imSendPhotoWall);

                        Picasso.with(getBaseContext())
                                .load(data.getData())
                                .error(R.drawable.com_facebook_tooltip_black_xout)
                                .placeholder(R.drawable.logo_pnu)
                                .centerInside()
                                .fit()
                             //   .resize(100,100)
                                .into(imStudentMainPhoto);


                    }*/
                }
                break;
        }
    }

    View.OnClickListener btnlistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btnAddToFriends:

                    if(CurrentStateFriend.equals("notFriend")) {
                        btnAddToFriends.setEnabled(false);
                        btnAddToFriends.setBackgroundColor(disactivateColorButtonAddToFriends);
                        btnAddToFriends.setTextColor(disactivateColorTextButtonAddToFriends);
                        SendRequestOnAFriendship();
                    }
                    if(CurrentStateFriend.equals("requestSent")) {
                        btnAddToFriends.setEnabled(false);
                        CancelFriendRequest();
                    }
                    if(CurrentStateFriend.equals("requestReceived")){
                        // add or subscribe friend
                         alertDialogRespondToAFrinedRequest();

                    }
                    if(CurrentStateFriend.equals("friends")){
                        btnAddToFriends.setEnabled(false);
                        DeleteFriend();
                    }
                    if(CurrentStateFriend.equals("onYouSubscribed")) {
                        // add  friend
                        alertDialogRespondOnYouSubscribed();
                    }
                    if(CurrentStateFriend.equals("youAreSubscribed")) {
                        // Un subscribe
                        alertDialogRespondUnSubscribed();
                    }
                    break;
                case R.id.btnListFriendsProfile:
                    Intent intentFriendsProfileActivity = new Intent(ProfileStudent.this, FriendsProfileActivity.class);
                    intentFriendsProfileActivity.putExtra("VisitedStudentKey", ReceiverStudentKey);
                    startActivity(intentFriendsProfileActivity);

                    break;

                case R.id.btnListSubscribersProfile:
                    Intent intentSubscribersProfileActivity = new Intent(ProfileStudent.this, SubscribersProfileActivity.class);
                    intentSubscribersProfileActivity.putExtra("VisitedStudentKey", ReceiverStudentKey);
                    startActivity(intentSubscribersProfileActivity);
                    break;

                case R.id.btnSendMessageProfile:
                    Intent intentSendMessageProfileActivity = new Intent(ProfileStudent.this, Message.class);
                    intentSendMessageProfileActivity.putExtra("VisitedStudentKey", ReceiverStudentKey);
                    startActivity(intentSendMessageProfileActivity);
                    break;

                case R.id.btnSendWallProfile:
                 /*   if (!network.isOnline()) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                Toast.LENGTH_LONG).show();
                    } else {
*/
                        String post = editTextWallProfile.getText().toString();
                        System.out.println("post = " + post);
                        if (!post.equals("")) {
                            PostHolder posts = new PostHolder();
                            posts.addPostToDatabase(post, senderUserId, null,null, "", ReceiverStudentKey);
                            editTextWallProfile.setText("");
                        } else {
                            Toast.makeText(ProfileStudent.this, "Введіть запис!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // }
                    break;
                case R.id.btnSendWallPhotoProfile:

                    if (!network.isOnline()) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                Toast.LENGTH_LONG).show();
                    } else {
                        addPhoto();
                    }
                    break;
            }
        }
    };

    private void alertDialogRespondUnSubscribed() {
        String mass[] = new String[] {"Відписатися"};
        AlertDialog.Builder a_builder = new AlertDialog.Builder(ProfileStudent.this);

        a_builder.setTitle("Ви підписник")
                .setItems( mass, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            // Unsubscribe
                            UnSubscribed();
                        }
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.show();
    }

    private void alertDialogRespondOnYouSubscribed() {
        String mass[] = new String[] {"Добавити в друзі"};
        AlertDialog.Builder a_builder = new AlertDialog.Builder(ProfileStudent.this);

        a_builder.setTitle("Добавити підписника в друзі")
                .setItems( mass, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                           // Add Subscriber On a FriendsActivity
                            AcceptFriendRequst();
                        }
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.show();
    }

    public void alertDialogRespondToAFrinedRequest(){
        String mass[] = new String[] {"Добавити в друзі", "Відхилити (добавити в підписники)"};
        AlertDialog.Builder a_builder = new AlertDialog.Builder(ProfileStudent.this);

        a_builder.setTitle("Відповісти на заявку в друзі")
                .setItems( mass, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            // Accept frined
                            AcceptFriendRequst();
                        }
                        else if (which == 1){
                            // Subscribe
                            SubscribeFriendRequest();
                        }
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.show();
    }

    private void UnSubscribed() {
        btnAddToFriends.setEnabled(false);
    SubscribedReferenceMy.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            SubscribersReferenceAlien.child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    btnAddToFriends.setEnabled(true);
                    CurrentStateFriend = "notFriend";
                    btnAddToFriends.setText("Додати в друзі");
                    btnAddToFriends.setBackgroundColor(disactivateColorButtonAddToFriends);
                    btnAddToFriends.setTextColor(disactivateColorTextButtonAddToFriends);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (!network.isOnline()) {
                        //        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (!network.isOnline()) {
                //        progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                        Toast.LENGTH_LONG).show();
            }
        }
    });
    }

    public void SubscribeFriendRequest() {
        btnAddToFriends.setEnabled(false);
        DateFormat calForDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentDate = new Date();
        final String saveCurrentDate = calForDate.format(currentDate);

        SubscribersReferenceMy.child(ReceiverStudentKey).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SubscribedReferenceAlien.child(senderUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CheckFriendReferenceRequestMyReceiver.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    CheckFriendReferenceRequestAlienSender.child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                btnAddToFriends.setEnabled(true);
                                                CurrentStateFriend ="onYouSubscribed";
                                                btnAddToFriends.setText("Ваш підписник");
                                                btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                                                btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                                                // Remove My Sender and Alien Receiver (if in receiver happens interrupt internet connection)

                                                FriendRequestsReferenceMySender.getParent().removeValue();
                                                FriendRequestsReferenceAlienReceiver.getParent().removeValue();


                                                Toast.makeText(ProfileStudent.this, "Додано в підписники",
                                                        Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (!network.isOnline()) {
                                                //        progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
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
                                    //     progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!network.isOnline()) {
                            //        progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            //////////
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (!network.isOnline()) {
                    //        progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //////////


    }

    private void DeleteFriend() {
        FriendReferenceMy.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendReferenceAlien.child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SubscribeFriendRequest();

                         /*   btnAddToFriends.setEnabled(true);
                            CurrentStateFriend = "notFriend";
                            btnAddToFriends.setText("Додати в друзі");
                            btnAddToFriends.setBackgroundColor(disactivateColorButtonAddToFriends);
                            btnAddToFriends.setTextColor(disactivateColorTextButtonAddToFriends);*/
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (!network.isOnline()) {
                                //     progressBar.setVisibility(View.GONE);
                                Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
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
                    //     progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void AcceptFriendRequst() {
        DateFormat calForDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentDate = new Date();
        final String saveCurrentDate = calForDate.format(currentDate);

        FriendReferenceAlien.child(senderUserId).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FriendReferenceAlien.child(senderUserId).child("online").setValue(false);
           FriendReferenceMy.child(ReceiverStudentKey).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   FriendReferenceMy.child(ReceiverStudentKey).child("online").setValue(false);

                   CheckFriendReferenceRequestMyReceiver.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               CheckFriendReferenceRequestAlienSender.child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){

                                           // Remove My Sender and Alien Receiver (if in receiver happens interrupt internet connection)

                                           FriendRequestsReferenceMySender.getParent().removeValue();
                                           FriendRequestsReferenceAlienReceiver.getParent().removeValue();


                                           // Remove Subscribers and Subscribed if this method calls from alertDialogRespondOnYouSubscribed()


                                           SubscribersReferenceMy.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   SubscribedReferenceAlien.child(senderUserId).removeValue().addOnFailureListener(new OnFailureListener() {
                                                       // Кінець  Можливо тут треба додаткових умов для видалення ? --> Тут видаляємо підписника з групи підписників якщо він є
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           if (!network.isOnline()) {
                                                               //        progressBar.setVisibility(View.GONE);
                                                               Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                                                       Toast.LENGTH_LONG).show();
                                                           }
                                                       }
                                                   });
                                               }
                                           }).addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   if (!network.isOnline()) {
                                                       //        progressBar.setVisibility(View.GONE);
                                                       Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                                               Toast.LENGTH_LONG).show();
                                                   }
                                               }
                                           });


                                           btnAddToFriends.setEnabled(true);
                                           CurrentStateFriend ="friends";
                                           btnAddToFriends.setText("Видалити з друзів");
                                           btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                                           btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);

                                           Toast.makeText(ProfileStudent.this, "Додано в друзі",
                                                   Toast.LENGTH_LONG).show();
                                       }

                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       if (!network.isOnline()) {
                                   //        progressBar.setVisibility(View.GONE);
                                           Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
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
                          //     progressBar.setVisibility(View.GONE);
                               Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                                       Toast.LENGTH_LONG).show();
                           }
                       }
                   });

               }

           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   if (!network.isOnline()) {
                    //   progressBar.setVisibility(View.GONE);
                       Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                               Toast.LENGTH_LONG).show();
                   }
               }
           });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (!network.isOnline()) {
                 //   progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void CancelFriendRequest() {
        FriendRequestsReferenceAlienReceiver.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FriendRequestsReferenceMySender.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                btnAddToFriends.setEnabled(true);
                                CurrentStateFriend ="notFriend";
                                btnAddToFriends.setText("Додати в друзі");
                                btnAddToFriends.setBackgroundColor(disactivateColorButtonAddToFriends);
                                btnAddToFriends.setTextColor(disactivateColorTextButtonAddToFriends);

                                CheckFriendReferenceRequestMyReceiver.child(ReceiverStudentKey).removeValue();
                                CheckFriendReferenceRequestAlienSender.child(senderUserId).removeValue();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (!network.isOnline()) {
                         //       progressBar.setVisibility(View.GONE);
                                Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
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
                 //   progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SendRequestOnAFriendship() {
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

                                                    btnAddToFriends.setEnabled(true);
                                                    CurrentStateFriend = "requestSent";
                                                    btnAddToFriends.setText("Скасувати заявку");
                                                    btnAddToFriends.setBackgroundColor(activeColorButtonAddToFriends);
                                                    btnAddToFriends.setTextColor(activeColorTextButtonAddToFriends);
                                                }

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (!network.isOnline()) {
                                            //            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
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
                    //            progressBar.setVisibility(View.GONE);
                                Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
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
                //    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileStudent.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
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




