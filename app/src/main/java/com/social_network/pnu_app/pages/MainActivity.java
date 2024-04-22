package com.social_network.pnu_app.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.firebase.QueriesFirebase;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.localdatabase.DatabaseInitializer;


public class MainActivity extends AppCompatActivity {

    private final static int SIGN_IN_CODE = 1;

    private Button btnMainSignIn;
    private Button btnRegistration;
    private static int buttonCounter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentStudent;
    DatabaseReference studentsReference;


    private RelativeLayout rlActivityMain;

    private static final String TAG = MainActivity.class.getName();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {

            Intent intentFromMainActivity;
            Snackbar.make(rlActivityMain, "Ви авторизовані", Snackbar.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, " Ви авторизовані",
                    Toast.LENGTH_LONG).show();
            intentFromMainActivity = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
           // startActivity(intentFromMainActivity);
        } else {
            Snackbar.make(rlActivityMain, "Ви не авторизовані", Snackbar.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, " Ви не авторизовані",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlActivityMain = findViewById(R.id.rlActivityMain);
        mAuth = FirebaseAuth.getInstance();
        currentStudent= mAuth.getCurrentUser();

        System.out.println("currentStudentonCreate = " + currentStudent);


        // QueriesFirebase queriesFirebase = new QueriesFirebase();
        // queriesFirebase.addStudent(); // TODO THIS METHOD ADD USER


     //  if (currentStudent != null  &&
          // checkNullCurrentStudent(AppDatabase.getAppDatabase(MainActivity.this)) == false) {
//

        if(checkNullCurrentStudent(AppDatabase.getAppDatabase(MainActivity.this)) == false){ // TODO change on codeLine above if
            rlActivityMain = findViewById(R.id.rlActivityMain);
            Intent intentFromMainActivity;
            intentFromMainActivity = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
            startActivity(intentFromMainActivity);
        }
        else {
          //  Snackbar.make(rlActivityMain , "Ви не автризовані", Snackbar.LENGTH_LONG).show();

            //    mess_current_user.setText("current_user(displayName) = " + FirebaseAuth.getInstance().getCurrentUser().getUid() );
        }


      //  Intent intentFromSignIn = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
      //  startActivity(intentFromSignIn);

        DatabaseInitializer.populateAsync(AppDatabase.getAppDatabase(MainActivity.this));
        listenerOnButton();


    }



    public boolean checkNullCurrentStudent(final AppDatabase db){
        boolean nullCurrentStudent;
        return nullCurrentStudent =(db.studentDao().getCurrentStudent() ) == null ? true : false;
    }

  public void setNullCurrentStudent(final AppDatabase db){
      if(db.studentDao().getCurrentStudent() != null) {
          MainStudentPage.linkStorageFromFireBase = null;
          db.studentDao().updateCurrentStudent(Integer.parseInt(db.studentDao().getCurrentStudent()));
      }

  }

    public static void plusCounter(){
        buttonCounter++;
    }


    public void listenerOnButton(){
        btnMainSignIn = findViewById(R.id.btnMainSignIn);
        btnRegistration = findViewById(R.id.btnRegister);


        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent;
                switch(view.getId()){
                    case R.id.btnMainSignIn:
                        setNullCurrentStudent(AppDatabase.getAppDatabase(MainActivity.this));
                        intent = new Intent( "com.social_network.pnu_app.signin.SignIn");
                        startActivity(intent);
                        break;
                    case R.id.btnRegister:
                        setNullCurrentStudent(AppDatabase.getAppDatabase(MainActivity.this));
                        if (buttonCounter == 0){
                            plusCounter();
                     //    MigrationToSQLITE.addDatatoSqliteFromFirebase(AppDatabase.getAppDatabase(MainActivity.this));
                        }
                        intent = new Intent( "com.social_network.pnu_app.registration.Registration");
                        startActivity(intent);
                        break;
                }
            }


        };


        btnMainSignIn.setOnClickListener(listener);
        btnRegistration.setOnClickListener(listener);



    }


}
