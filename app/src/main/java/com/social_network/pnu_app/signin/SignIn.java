package com.social_network.pnu_app.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.social_network.pnu_app.entity.Student;
import com.social_network.pnu_app.firebase.QueriesFirebase;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.localdatabase.StudentSqlLite;
import com.social_network.pnu_app.network.NetworkStatus;
import com.social_network.pnu_app.registration.PhoneAuthentication;
import com.social_network.pnu_app.registration.Registration;
import com.social_network.pnu_app.R;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class SignIn extends AppCompatActivity {

    Button btnSignIn;
    MaterialEditText IDcardField;
    MaterialEditText passField;
    String valueIDcardField;

    private FirebaseAuth mAuth;

    static String valuePassField="";
    public String ErrorText=null;

    boolean error = true;


    static ValueEventListener valueEventListener;
    private static final String TAG ="TAG";

    public boolean FBverify;
    public static String FBidSerie ="";
    public String FBpassword = "1";
    public String FBemail = "";
    public String FBphone = "";

    public String FBpatronym;
    public String FBfaculty;
    public String FBgroup;
    public String FBdateOfEntry;
    public String FBformStudying;
    public String FBlinkMainStudentPage;

    public static int FBid = 0;
    public String FBName;
    public String FBLastName;

    DatabaseReference studentsReference;
    String KeyStudent ="default";
    QueriesFirebase qf = new QueriesFirebase();
   static HashMap<Object, Object> student = new HashMap();
   StudentSqlLite studentSQLite = new StudentSqlLite();
    private ProgressBar progressBar;
    String codeSent;

    Intent intentFromSignIn;

    NetworkStatus network = new NetworkStatus();
    private String yourName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        progressBar = findViewById(R.id.progressbarSignIn);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        studentsReference = FirebaseDatabase.getInstance().getReference("students");
        verifycationStudentIn();

    }

    public void alertErrorSign(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(SignIn.this);
        a_builder.setMessage(ErrorText)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = a_builder.create();
        alert.setTitle("PERFORMANCE");
        alert.show();
    }

    public void initFieldInput(){

        IDcardField = findViewById(R.id.IDcardField);
        valueIDcardField = String.valueOf(IDcardField.getText()).trim();

        passField = findViewById(R.id.passFieldSignIn);
        valuePassField = String.valueOf(passField.getText()).trim();

    }

    public void queryFB(){
        initFieldInput();
        Query querySeriesIDcard = FirebaseDatabase.getInstance().getReference("students")
                .orderByChild("seriesIDcard")
                .equalTo(valueIDcardField);

        querySeriesIDcard.addListenerForSingleValueEvent(valueEventListener);

        if(!network.isOnline()){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SignIn.this, " Please Connect to Internet",
                    Toast.LENGTH_LONG).show();
        }
    }



    public HashMap<Object, Object> getStudentFB(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //       if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    student = (HashMap) snapshot.getValue();
                    KeyStudent = snapshot.getKey();


                }

                FBName = (String) student.get("name");
                FBLastName = (String) student.get("lastName");
                FBidSerie = (String) student.get("seriesIDcard");
                FBpassword = (String) student.get("password");
                FBid = Integer.parseInt(student.get("id").toString());
                FBphone = (String) student.get("phone");
                Registration encrypt = new Registration();
                valuePassField = encrypt.encryptionPassword(valuePassField);

               FBpatronym = (String) student.get("patronym");
               FBfaculty = (String) student.get("faculty");
               FBgroup = (String) student.get("group");
               FBdateOfEntry = (String) student.get("dateOfEntry");
               FBformStudying = (String) student.get("formStudying");
               FBlinkMainStudentPage = (String) student.get("linkMainStudentPage");

                try {
                    FBverify = (boolean) student.get("verify");
                } catch (Exception castToBool) {
                    FBverify = false;
                }
                if ((FBidSerie != null && FBpassword != null)) {

                 if (!FBidSerie.equals(valueIDcardField)) {
                     progressBar.setVisibility(View.GONE);
                    ErrorText = "StudentSqlLite with the such series id does not exist";
                    alertErrorSign();
                } else if (FBidSerie.equals(valueIDcardField) && (FBverify != true)) {
                     progressBar.setVisibility(View.GONE);
                    ErrorText = "IDPassword = " + valuePassField +
                            "IDSeriesIDCard = " + valueIDcardField;
                    ErrorText = "StudentSqlLite with the such series id does not registered";
                    alertErrorSign();
                }  else if (!(FBpassword.equals(valuePassField)) && FBverify == true) { // TODO
                     progressBar.setVisibility(View.GONE);
                    ErrorText = "Wrong password";
                    alertErrorSign();
                }


                    if (FBverify == true && FBidSerie.equals(valueIDcardField) && FBpassword.equals(valuePassField)) {

                   // TODO акоментувати до sendCodeVerification();
                         FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String deviceToken = instanceIdResult.getToken();

                                studentsReference.child(KeyStudent).child("deviceToken").setValue(deviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Student.student = student;
                                                Student studentSQLite = new Student(valueIDcardField, FBName, FBLastName,FBid , FBemail, FBpassword,
                                                        FBphone, KeyStudent, FBverify, FBpatronym, FBfaculty, FBgroup, FBdateOfEntry, FBformStudying, "");
                                                studentSQLite.synchronizationSQLiteSignIn(AppDatabase.getAppDatabase(SignIn.this));
                                                /////////////////

                                                intentFromSignIn = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
                                                startActivityForResult(intentFromSignIn,1);
                                                progressBar.setVisibility(View.GONE);

                                            }
                                        });
                                progressBar.setVisibility(View.GONE);

                                // Do whatever you want with your token now
                                // i.e. store it on SharedPreferences or DB
                                // or directly send it to server
                            }
                        });

                      //  sendCodeVerification();  // TODO розкоментувати рядок

                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    ErrorText = "StudentSqlLite with such id series does not exist 2";
                    alertErrorSign();
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(!network.isOnline()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignIn.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        };
        return student;
    }


    public void verifycationStudentIn(){

        btnSignIn = findViewById(R.id.btnSignIn);


        View.OnClickListener listenerSignIn = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                initFieldInput();
                getStudentFB();
                if (view.getId() == R.id.btnSignIn) {

                    if (valueIDcardField.isEmpty()) { // TODO
                        progressBar.setVisibility(View.GONE);
                        ErrorText = "Enter Series ID";
                        alertErrorSign();
                    } else if (valuePassField.isEmpty()) { // TODO
                        progressBar.setVisibility(View.GONE);
                        ErrorText = "Enter password";
                        alertErrorSign();
                    } else {
                        if (verifycationSeriesIDcard() == false &&
                                verifycationPassword() == false) {
                            queryFB();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            alertErrorSign();
                        }
                    }


                }

            }

        };

        btnSignIn.setOnClickListener(listenerSignIn);

            }


    public void sendCodeVerification(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                FBphone,                              // Phone number to verify
                60,                               // Timeout duration
                TimeUnit.SECONDS,                     // Unit of timeout
                this,                          // Activity (for callback binding)
                mCallbacks);                          // OnVerificationStateChangedCallbacks
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
            signInWithPhoneAuthCredential(phoneAuthCredential);

            // mAuth.signInAnonymously();

        // THIS METHOD IS AN AUTO SIGN IN , HE CALLS WHEN USER ALREADY GET CODE VERIFY BUT NOT CONFIRM HIS VERIFY CODE
    }


        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            Log.w(TAG, "onVerificationFailed", e.fillInStackTrace());
            ErrorText = "On Verification Sending SMS Failed! You are often do btn_drawable_requests due to unusual activity" +
                    " and was blocked. Try Later! After 4 hours" ; // TODO change text
            alertErrorSign();
        }


        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success");

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String deviceToken = instanceIdResult.getToken();

                        studentsReference.child(KeyStudent).child("deviceToken").setValue(deviceToken)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignIn.this, " Verification automatically completed! SignIn Success",
                                                Toast.LENGTH_LONG).show();
                                        Student.student = student;
                                        Student studentSQLite = new Student(valueIDcardField, FBName, FBLastName,FBid , FBemail, FBpassword,
                                                FBphone, KeyStudent, FBverify, FBpatronym, FBfaculty, FBgroup, FBdateOfEntry, FBformStudying, "");
                                        studentSQLite.synchronizationSQLiteSignIn(AppDatabase.getAppDatabase(SignIn.this));
                                        /////////////////

                                        intentFromSignIn = new Intent("com.social_network.pnu_app.pages.MainStudentPage");
                                        startActivityForResult(intentFromSignIn,1);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                        progressBar.setVisibility(View.GONE);

                        // Do whatever you want with your token now
                        // i.e. store it on SharedPreferences or DB
                        // or directly send it to server
                    }
                });

            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                // If sign in fails, display a message to the user and write logs in outputs.
                Log.w(TAG, "signInWithCredential:failure", e.fillInStackTrace());
                if (!network.isOnline()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignIn.this, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    ErrorText = "Registration failure incorrect verification code or problem with sending response. " +
                            "To solve this problem verify that the verification code, which you get in SMS,  inputted correct." +
                            " If code verification, inputted correct you need restart your phone and repeat registration."; // TODO change text
                    alertErrorSign();
                }
            }

        });
    }

    public boolean verifycationSeriesIDcard(){

        boolean resultSeriesIDcard = valueIDcardField.matches("^[A-Z]{2}([0-9]){8}$");

        if (resultSeriesIDcard) {
            error = false;
        }
        else {
            error=true;
            ErrorText = "Enter the correct Series and Number ID card(example-ВА12345678): should be the first two upper letters and 8 digits";
        }

        return error;
    }

    public boolean verifycationPassword(){

        boolean resultPass = valuePassField.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^.;:&+=])(?=\\S+$).{8,32}$");

        if (resultPass) {
            error = false;
        }
        else {
            error=true;
            ErrorText = "Enter the correct password: The password must be at least 8 characters long and contain 1 digit " +
                    "and 1 letter can also contain uppercase letters or such characters - (@ # $% ^ _-.;: & + =)";
        }
        return error;

    }



    }


