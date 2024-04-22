package com.social_network.pnu_app.pages;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.Student;
import com.social_network.pnu_app.firebase.QueriesFirebase;
import com.social_network.pnu_app.functional.PostHolder;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;


public class MainStudentPage extends AppCompatActivity {

    TextView tvPIBvalue;
    TextView tvFacultyValue;
    TextView tvGroupValue;
    TextView tvDateOfEntryValue;
    TextView tvFormStudyingValue;
    TextView tvTextWall;

    Button btnLoadPhotoStudent;
    Button btnlistFriends;
    Button btnlistSubscribers;
    Button btnlistPolls;
    Button btnWallNotes;

    ImageView btnAddPostl;
    EmojiconEditText editTextPost;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static HashMap<Object, Object> studentData = new HashMap();

    public CircleImageView imStudentMainPhoto;
    public ImageView imSendPhotoWall;
    private Button btnAddPicture;
    public static RecyclerView recyclerViewPost;

    private File mTempPhoto;

    private String mImageUri = "";
    public String mRereference = "";

    private EmojiconEditText emojiconEditText;

    public static String SeriesIDCard;
    public String nameFileFirebase = "MainStudentPhoto";
    public String dirImages = "images/";
    public String pathToFirebaseStorage;
    public String castomPathToFirebaseStorage;
    public String urlMainStudentPhoto;

    private static final int REQUEST_CODE_PERMISSION_RECEIVE_CAMERA = 102;
    private static final int REQUEST_CODE_TAKE_PHOTO = 103;

    public static Uri finalLocalFile;
    Bitmap thumb_bitmap = null;
    static long countMyFriends;
    static long countMySubscribers;
    static long countMyPosts;

    NetworkStatus network = new NetworkStatus();

    String name;
    String lastName;
    String group;
    String dateOfEntry;
    String formStudying;
    String faculty;
    String linkFirebaseStorageMainPhoto;

   static String btnNamePress;

    static String linkStorageFromFireBase;

    DatabaseReference referenceMyFriends;
    DatabaseReference referenceMySubsribers;
    static String senderUserId;
    DatabaseReference studentsReference;
    Query myPostsReference;

    ImageView btnSendWallPhoto;

    private FirebaseAuth mAuth;
    private FirebaseUser currentStudent;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student_page);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        btnLoadPhotoStudent = findViewById(R.id.btnLoadPhotoStudent);
        btnlistFriends = findViewById(R.id.btnListFriends);
        btnlistSubscribers = findViewById(R.id.btnListSubscribers);
        btnlistPolls = findViewById(R.id.btnListPoll);


        mAuth = FirebaseAuth.getInstance();
        currentStudent = mAuth.getCurrentUser();

        btnAddPostl = findViewById(R.id.btnSendWall);
        btnAddPostl.setOnClickListener(btnlistener);
        editTextPost = findViewById(R.id.editTextWall);

        tvPIBvalue = findViewById(R.id.tvPIBvalue);
        tvFacultyValue = findViewById(R.id.tvFacultyValue);
        tvGroupValue = findViewById(R.id.tvGroupValue);
        tvDateOfEntryValue = findViewById(R.id.tvDateOfEntryValue);
        tvFormStudyingValue = findViewById(R.id.tvFormStudyingValue);
        btnLoadPhotoStudent.setOnClickListener(btnlistener);
        btnlistFriends.setOnClickListener(btnlistener);
        btnlistSubscribers.setOnClickListener(btnlistener);
        btnlistPolls.setOnClickListener(btnlistener);

        btnSendWallPhoto = findViewById(R.id.btnSendWallPhoto);
        btnSendWallPhoto.setOnClickListener(btnlistener);

        btnWallNotes = findViewById(R.id.btnWallNotes);
        tvTextWall = findViewById(R.id.tvTextWall);
        recyclerViewPost = findViewById(R.id.recyclerViewMainStudentPage);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));

        ProfileStudent profileStudent = new ProfileStudent();

        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(MainStudentPage.this));

        studentsReference = FirebaseDatabase.getInstance().getReference("students").child(senderUserId);
        myPostsReference = FirebaseDatabase.getInstance().getReference("students").child(senderUserId).child("Posts")
                .orderByPriority();

        referenceMyFriends = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("Friends");
        referenceMyFriends.keepSynced(true);

        referenceMySubsribers = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("Subscribers");
        referenceMySubsribers.keepSynced(true);

        //    emojiconEditText = findViewById(R.id.editTextWall);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_profile);
        bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));
        menuChanges(bottomNavigationView);
        BuildStudentPage(AppDatabase.getAppDatabase(MainStudentPage.this));
        imStudentMainPhoto = (CircleImageView) findViewById(R.id.imStudentMainPhoto);
        imSendPhotoWall = (ImageView) findViewById(R.id.imSendPhotoWall);
        btnAddPicture = (Button) findViewById(R.id.btnLoadPhotoStudent);

        loadPhoto();

        PostHolder posts = new PostHolder();
        posts.holderPost(senderUserId ,tvTextWall, recyclerViewPost, senderUserId);

    }


    public void updateLinkMainStudentPage(final AppDatabase db) {
        String keyStudent = db.studentDao().getKeyStudent();

        Student linkFirebaseStorageMainPhoto = new Student(urlMainStudentPhoto, null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students");
        reference.child(keyStudent).updateChildren(linkFirebaseStorageMainPhoto.toMapUpdatelinkFirebaseStorageMainPhoto());
    }

    public String getStudentSeriesIDCard(final AppDatabase db){
        int currentStudent = Integer.parseInt(db.studentDao().getCurrentStudent());
        String SeriesIDCard = db.studentDao().getSeriesBYId(currentStudent);
        return SeriesIDCard;
    }

    public void loadPhoto() {
        File localFile = null;
        try {
            localFile = createTempImageFile(getExternalCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }


        finalLocalFile = Uri.fromFile(localFile);


        SeriesIDCard = getStudentSeriesIDCard(AppDatabase.getAppDatabase(MainStudentPage.this));
        pathToFirebaseStorage = dirImages + SeriesIDCard + "/";
        castomPathToFirebaseStorage = pathToFirebaseStorage.replace("/", "%2F");
        nameFileFirebase += new Date().getTime();
        nameFileFirebase += "thumb_images";
        urlMainStudentPhoto = "https://firebasestorage.googleapis.com/v0/b/pnu-app.appspot.com/o/".concat(castomPathToFirebaseStorage).concat(nameFileFirebase).concat("?alt=media&");

        System.out.println("CURRENT THREAD FROM LOADPHOTO = " + Thread.currentThread().getName());

        mRereference = getIntent().getStringExtra("Reference");

        if (linkStorageFromFireBase != null) {

            Picasso.with(getBaseContext())
                    .load(linkStorageFromFireBase)
                    .placeholder(R.drawable.logo_pnu)
                    .error(R.drawable.com_facebook_close)
                    .centerCrop()
                    .fit()
                    //.resize(1920,2560)
                    .into(imSendPhotoWall);

          /*  Toast.makeText(MainStudentPage.this, "Succes get photo from FirebaseStorage onCreate " + finalLocalFile,
                    Toast.LENGTH_LONG).show();*/

            Picasso.with(getBaseContext())
                    .load(linkStorageFromFireBase)
                    .placeholder(R.drawable.logo_pnu)
                    .error(R.drawable.com_facebook_close)
                    .centerCrop()
                    .fit()
                    // .resize(1920,2560)
                    .into(imStudentMainPhoto);
        } else {
            loadPhotoFromInternet(AppDatabase.getAppDatabase(MainStudentPage.this));
        }
    }


    public void loadPhotoFromInternet(final AppDatabase db) {

        String keyStudent = db.studentDao().getKeyStudent();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students");
        reference.child(keyStudent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("linkFirebaseStorageMainPhoto")) {
                    linkStorageFromFireBase = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();

                    Picasso.with(getBaseContext())
                            .load(linkStorageFromFireBase)
                            .placeholder(R.drawable.logo_pnu)
                            .error(R.drawable.com_facebook_close)
                            .centerCrop()
                            .fit()
                            //.resize(1920,2560)
                            .into(imSendPhotoWall);

         /*       Toast.makeText(MainStudentPage.this, "Succes get photo from FirebaseStorage loadPhotoFromInternet " + finalLocalFile,
                        Toast.LENGTH_LONG).show();*/

                    Picasso.with(getBaseContext())
                            .load(linkStorageFromFireBase)
                            .placeholder(R.drawable.logo_pnu)
                            .error(R.drawable.com_facebook_close)
                            .centerCrop()
                            .fit()
                            // .resize(1920,2560)
                            .into(imStudentMainPhoto);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                NetworkStatus network = new NetworkStatus();
                if (!network.isOnline()) {
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void BuildStudentPage(final AppDatabase db) {

        String keyStudent = db.studentDao().getKeyStudent();

        Query queryByKey = FirebaseDatabase.getInstance().getReference("students").child(keyStudent);

        queryByKey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                name = dataSnapshot.child("name").getValue().toString();
                lastName = dataSnapshot.child("lastName").getValue().toString();
                group = dataSnapshot.child("group").getValue().toString();
                dateOfEntry = dataSnapshot.child("dateOfEntry").getValue().toString();
                formStudying = dataSnapshot.child("formStudying").getValue().toString();
                faculty = dataSnapshot.child("faculty").getValue().toString();
                try {
                    linkFirebaseStorageMainPhoto = dataSnapshot.child("linkFirebaseStorageMainPhoto").getValue().toString();
                } catch (NullPointerException nullLinkPhoto) {
                    linkFirebaseStorageMainPhoto = "";
                }

                referenceMyFriends.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        countMyFriends = dataSnapshot.getChildrenCount();
                        btnlistFriends.setText(countMyFriends + " " + "Друзі");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        NetworkStatus network = new NetworkStatus();
                        if (!network.isOnline()) {
                            // progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                });

                referenceMySubsribers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        countMySubscribers = dataSnapshot.getChildrenCount();
                        btnlistSubscribers.setText(countMySubscribers + " " + "Підписники");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        NetworkStatus network = new NetworkStatus();
                        if (!network.isOnline()) {
                            // progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                });

                myPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        countMyPosts = dataSnapshot.getChildrenCount();
                        btnWallNotes.setText("Записи " + countMyPosts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        NetworkStatus network = new NetworkStatus();
                        if (!network.isOnline()) {
                            // progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                });

                int currentStudent;
                if (db.studentDao().getCurrentStudent() != null) {
                    currentStudent = Integer.parseInt(db.studentDao().getCurrentStudent());
                    SeriesIDCard = db.studentDao().getSeriesBYId(currentStudent);
                    Bundle bundle = new Bundle();
                    //  bundle.putString(FirebaseAnalytics.Param.ITEM_ID,  studentData.get("id"));
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, (String) db.studentDao().getFirstNameById(currentStudent));
                    // bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
                studentData = Student.student;

                // Local DB

              /*  tvPIBvalue.setText(db.studentDao().getLastNameById(currentStudent) + " " +
                        db.studentDao().getFirstNameById(currentStudent));

                tvFacultyValue.setText(" " + db.studentDao().getFacultyById(currentStudent));
                tvGroupValue.setText(" " + db.studentDao().getGroupById(currentStudent));
                tvDateOfEntryValue.setText(" " + db.studentDao().getDateOfEntryById(currentStudent));
                tvFormStudyingValue.setText(" " + db.studentDao().getFormStudyingById(currentStudent));*/


                tvPIBvalue.setText(lastName + " " + name);
                tvFacultyValue.setText(" " + faculty);
                tvGroupValue.setText(" " + group);
                tvDateOfEntryValue.setText(" " + dateOfEntry);
                tvFormStudyingValue.setText(" " + formStudying);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!network.isOnline()) {
                    //   progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void menuChanges(BottomNavigationView bottomNavigationView) {

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intentMenu;
                        switch (item.getItemId()) {
                            case R.id.action_search:
                                intentMenu = new Intent("com.social_network.pnu_app.pages.Search");
                                startActivity(intentMenu);

                                break;
                            case R.id.action_message:
                                intentMenu = new Intent("com.social_network.pnu_app.pages.Messenger");
                                startActivity(intentMenu);


                                break;
                            case R.id.action_main_student_page:
                                intentMenu = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
                                startActivity(intentMenu);

                                break;
                            case R.id.action_schedule:
                                intentMenu = new Intent("com.social_network.pnu_app.pages.Schedule");
                                startActivity(intentMenu);

                                break;
                            case R.id.action_settings:
                                intentMenu = new Intent("com.social_network.pnu_app.pages.Settings");
                                startActivity(intentMenu);

                                break;
                        }
                        return false;
                    }
                });
    }

    //Метод для добавления фото
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

                        if (btnNamePress.equals("btnLoadPhotoStudent")) {

                            Picasso.with(getBaseContext())
                                    .load(data.getData())
                                    .placeholder(R.drawable.logo_pnu)
                                    .error(R.drawable.com_facebook_close)
                                    .centerInside()
                                    .fit()
                                    //  .resize(100,100)
                                    .into(imSendPhotoWall);

                            Picasso.with(getBaseContext())
                                    .load(data.getData())
                                    .placeholder(R.drawable.logo_pnu)
                                    .error(R.drawable.com_facebook_close)
                                    .centerInside()
                                    .fit()
                                    //     .resize(1920,2560)
                                    .into(imStudentMainPhoto);

                            uploadFileInFireBaseStorage(thumb_byte);
                        }
                        else if (btnNamePress.equals("btnSendWallPhoto")){


                            String post = editTextPost.getText().toString();
                            System.out.println("post = " + post);
                          //  if (!post.equals("")) {
                                PostHolder posts = new PostHolder();
                                posts.addPostToDatabase(post, senderUserId, data.getData(),thumb_byte, pathToFirebaseStorage, senderUserId);
                                editTextPost.setText("");
                        /*    } else {
                                Toast.makeText(MainStudentPage.this, "Введіть запис!",
                                        Toast.LENGTH_SHORT).show();
                            }*/

                            }

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

    public void uploadFileInFireBaseStorage(final byte[] thumb_byte) {
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // mStorageRef.child(pathToFirebaseStorage + nameFileFirebase).putFile(uri)
        mStorageRef.child(pathToFirebaseStorage + nameFileFirebase).putBytes(thumb_byte).addOnCompleteListener(
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        updateLinkMainStudentPage(AppDatabase.getAppDatabase(MainStudentPage.this));
                        String thumb_downloadUri = task.getResult().getUploadSessionUri().toString();
                        System.out.println("15+ " + thumb_downloadUri);
                    }
                });
  /*      uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred());
                Log.i("Load","Upload is " + progress + "% done");
            }
        });*/

    }

    View.OnClickListener btnlistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnLoadPhotoStudent:
                    btnNamePress = "btnLoadPhotoStudent";

                    if (!network.isOnline()) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                                Toast.LENGTH_LONG).show();
                    } else {
                        addPhoto();
                    }
                    break;
                case R.id.btnListFriends:

                    Intent intentlistMyFriends;
                    intentlistMyFriends = new Intent("com.social_network.pnu_app.pages.FriendsActivity");
                    startActivity(intentlistMyFriends);
                    break;

                case R.id.btnListSubscribers:

                    Intent intentlistMySubscribers;
                    intentlistMySubscribers = new Intent("com.social_network.pnu_app.pages.MySubscribersActivity");
                    startActivity(intentlistMySubscribers);
                    break;

                case R.id.btnListPoll:
                    Intent intentlistPolls;
                    intentlistPolls = new Intent("com.social_network.pnu_app.pages.PollsActivity");
                    startActivity(intentlistPolls);
                    break;

                case R.id.btnSendWall:
               /*     if (!network.isOnline()) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                                Toast.LENGTH_LONG).show();
                    } else {*/

                        String post = editTextPost.getText().toString();
                        System.out.println("post = " + post);
                        if (!post.equals("")) {
                            PostHolder posts = new PostHolder();
                            posts.addPostToDatabase(post, senderUserId, null,null, "", senderUserId);
                            editTextPost.setText("");
                        } else {
                            Toast.makeText(MainStudentPage.this, "Введіть запис!",
                                    Toast.LENGTH_SHORT).show();
                   //     }

                    }
                    break;

                case R.id.btnSendWallPhoto:
                    btnNamePress = "btnSendWallPhoto";


                    if (!network.isOnline()) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainStudentPage.this, " Please Connect to Internet",
                                Toast.LENGTH_LONG).show();
                    } else {
                        addPhoto();
                    }

                    break;

            }
        }
    };


    private void lastSeen() {
        studentsReference.child("lastSeen").setValue(ServerValue.TIMESTAMP);
    }

    private void onlineStatus(final boolean online) {
        studentsReference.child("online").setValue(online);
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





