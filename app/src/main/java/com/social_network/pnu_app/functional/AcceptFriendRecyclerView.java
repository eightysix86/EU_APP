package com.social_network.pnu_app.functional;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.social_network.pnu_app.localdatabase.AppDatabase;
import com.social_network.pnu_app.network.NetworkStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AcceptFriendRecyclerView  extends AppCompatActivity {

    NetworkStatus network = new NetworkStatus();


    public void AcceptFriendRequst(String VisitedStudentKey, final String senderUserId, final Context myContex) {
        final String ReceiverStudentKey = VisitedStudentKey;
        DateFormat calForDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date currentDate = new Date();
        final String saveCurrentDate = calForDate.format(currentDate);

        final DatabaseReference FriendReferenceAlien = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).
                child("Friends");
        FriendReferenceAlien.keepSynced(true);


        final DatabaseReference FriendReferenceMy = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                child("Friends");
        FriendReferenceMy.keepSynced(true);

        final String finalSenderUserId = senderUserId;
        FriendReferenceAlien.child(senderUserId).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FriendReferenceMy.child(ReceiverStudentKey).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

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

                                                DatabaseReference  FriendRequestsReferenceMySender = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).
                                                        child("FriendRequestSender").child(ReceiverStudentKey).child("requestType");
                                                FriendRequestsReferenceMySender.keepSynced(true);

                                                DatabaseReference  FriendRequestsReferenceAlienReceiver = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).
                                                        child("FriendRequestReceiver").child(senderUserId).child("requestType");
                                                FriendRequestsReferenceAlienReceiver.keepSynced(true);

                                                // Remove My Sender and Alien Receiver (if in receiver happens interrupt internet connection)

                                                FriendRequestsReferenceMySender.getParent().removeValue();
                                                FriendRequestsReferenceAlienReceiver.getParent().removeValue();


                                                // Remove Subscribers and Subscribed if this method calls from alertDialogRespondOnYouSubscribed()

                                                DatabaseReference SubscribersReferenceMy = FirebaseDatabase.getInstance().getReference("studentsCollection").child(senderUserId).child("Subscribers");
                                                SubscribersReferenceMy.keepSynced(true);

                                                SubscribersReferenceMy.child(ReceiverStudentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        DatabaseReference  SubscribedReferenceAlien = FirebaseDatabase.getInstance().getReference("studentsCollection").child(ReceiverStudentKey).child("Subscribed");
                                                        SubscribedReferenceAlien.keepSynced(true);

                                                        SubscribedReferenceAlien.child(finalSenderUserId).removeValue().addOnFailureListener(new OnFailureListener() {
                                                            // Кінець  Можливо тут треба додаткових умов для видалення ? --> Тут видаляємо підписника з групи підписників якщо він є
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


                                                if (myContex != null) {
                                                    Toast.makeText(myContex, "Додано в друзі",
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
                            //   progressBar.setVisibility(View.GONE);
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
                    //   progressBar.setVisibility(View.GONE);
                    Toast.makeText(myContex, " Please Connect to Internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
