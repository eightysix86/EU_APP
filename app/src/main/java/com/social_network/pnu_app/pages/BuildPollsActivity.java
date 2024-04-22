package com.social_network.pnu_app.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.entity.Znu;
//import com.social_network.pnu_app.functional.QuestionAdapter;
import com.social_network.pnu_app.localdatabase.AppDatabase;

import java.util.HashMap;

public class BuildPollsActivity extends AppCompatActivity {

    Button btnCreateQuestion;
    Button btnBackCreatedPolls;

    Button btnAddQuestion;
    Button btnDeleteQuestion;
    Button btnFinishCreatePoll;
    DatabaseReference refPollCreated;

    DatabaseReference refQuestion;
    MaterialEditText etNamePoll;

    TextView tvPollTextName;
    TextView tvPollName;

    RecyclerView recyclerViewCreatedPoll;

    String namePoll = "";
    String descriptionName;
   public static RecyclerView questionPoll;

   String senderUserId;

    HashMap<Object, Object> pollMap = new HashMap();

  public static String countQuestion;
    String keyPoll = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_polls);

        btnCreateQuestion = findViewById(R.id.btnCreateQuestion);
        btnBackCreatedPolls =findViewById(R.id.btnBackCreatedPolls);

        questionPoll = findViewById(R.id.recyclerViewCreatedPoll);
       //  questionPoll.setHasFixedSize(true);
        questionPoll.setLayoutManager(new LinearLayoutManager(this));


        btnCreateQuestion.setOnClickListener(btnlistener);
        btnBackCreatedPolls.setOnClickListener(btnlistener);


        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnAddQuestion.setOnClickListener(btnlistener);

        btnDeleteQuestion = findViewById(R.id.btnDeleteQuestion);
        btnDeleteQuestion.setOnClickListener(btnlistener);

        btnFinishCreatePoll = findViewById(R.id.btnFinishCreatePoll);
        btnFinishCreatePoll.setOnClickListener(btnlistener);

        etNamePoll = findViewById(R.id.etNamePoll);

        tvPollTextName = findViewById(R.id.tvPollTextName);
        tvPollName = findViewById(R.id.tvPollName);

        recyclerViewCreatedPoll = findViewById(R.id.recyclerViewCreatedPoll);

        ProfileStudent profileStudent= new ProfileStudent();
        senderUserId = profileStudent.getKeyCurrentStudend(AppDatabase.getAppDatabase(BuildPollsActivity.this));

        refPollCreated = FirebaseDatabase.getInstance().getReference("students").child(senderUserId)
                .child("Polls").child("Created");

        refQuestion = FirebaseDatabase.getInstance().getReference("students").child(senderUserId)
                .child("Polls").child("Created");

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_createdPoll);
        bottomNavigationView.setSelectedItemId((R.id.action_main_student_page));
        menuChanges(bottomNavigationView);
    }

    public void holderQuestion(){


/*        refQuestion.child(namePoll).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("fack = " + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        FirebaseRecyclerAdapter<Znu, PollViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Znu, PollViewHolder>
                (
                        Znu.class,
                        R.layout.question_layout,
                        PollViewHolder.class,
                        refQuestion.child(namePoll)

                ) {
            @Override
            protected void populateViewHolder(final PollViewHolder pollViewHolder, final Znu znu, final int i) {

                final String currentKeyPost = getRef(i).getKey();
                /////
                //    recyclerViewPost.scrollToPosition(i);


            }
        };
        recyclerViewCreatedPoll.setAdapter(firebaseRecyclerAdapter);
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

    View.OnClickListener btnlistener = new View.OnClickListener() {
  //      QuestionAdapter adapter = new QuestionAdapter();
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnBackCreatedPolls:
                    Intent intentBackPolls;
                    intentBackPolls = new Intent( "com.social_network.pnu_app.pages.PollsActivity");
                    startActivity(intentBackPolls);
                    break;

                case R.id.btnCreateQuestion:

                    namePoll = etNamePoll.getText().toString();

                    tvPollName.setText(namePoll);

                    refPollCreated.orderByKey().equalTo(namePoll).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                System.out.println("dataSnapshot.getValue2() = " + dataSnapshot.getValue());
                                System.out.println("dataSnapshot23= " + dataSnapshot);
                                System.out.println("dataSnapshotKEY= " + dataSnapshot.getKey());
                                System.out.println("dataSnapshot.getChildren() = " + dataSnapshot.getChildren());
                                Toast.makeText(BuildPollsActivity.this, "Назва з таким опитуванням вже існує",
                                        Toast.LENGTH_LONG).show();


                            } else {

                                if (!etNamePoll.getText().toString().equals("")) {
                                    refPollCreated.child(namePoll).push().child("question").setValue(String.valueOf(countQuestion));


                                    refPollCreated.child(namePoll)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshotPoll) {

                                                    countQuestion = String.valueOf(dataSnapshotPoll.getChildrenCount());
                                                    System.out.println("dataSnapshot24= " + dataSnapshotPoll);
                                                    System.out.println("countQuestion = " + countQuestion);

                                                    btnCreateQuestion.setVisibility(View.GONE);
                                                    btnAddQuestion.setVisibility(View.VISIBLE);
                                                    btnFinishCreatePoll.setVisibility(View.VISIBLE);


                                                    etNamePoll.setVisibility(View.GONE);
                                                    tvPollTextName.setVisibility(View.VISIBLE);
                                                    tvPollName.setVisibility(View.VISIBLE);

                                                        holderQuestion();

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                } else {
                                    Toast.makeText(BuildPollsActivity.this, "Введіть назву опитування",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




                    break;
                case R.id.btnAddQuestion:


                    refPollCreated.child(namePoll).push().child("question").setValue(countQuestion);
                     //   BuildPollsActivity.questionPoll.setAdapter(new QuestionAdapter());
                       // btnFinishCreatePoll.setVisibility(View.GONE);
                      //  btnAddQuestion.setVisibility(View.GONE);
                     //   btnDeleteQuestion.setVisibility(View.GONE);

                    break;

            case R.id.btnDeleteQuestion:

                        System.out.println("CLICKED btnAddQuestion");
                        btnFinishCreatePoll.setVisibility(View.VISIBLE);
                        btnAddQuestion.setVisibility(View.VISIBLE);
                        btnDeleteQuestion.setVisibility(View.VISIBLE);

                            //  holder.btnCreateQuestion.setVisibility(View.VISIBLE);

                        break;

                case R.id.btnFinishCreatePoll:

                    FirebaseRecyclerAdapter<Znu, PollViewHolder> firebaseRecyclerAdapter
                            = new FirebaseRecyclerAdapter<Znu, PollViewHolder>
                            (
                                    Znu.class,
                                    R.layout.question_layout,
                                    PollViewHolder.class,
                                    refQuestion.child(namePoll)

                            ) {
                        @Override
                        protected void populateViewHolder(final PollViewHolder pollViewHolder, final Znu znu, final int i) {

                            final String currentKeyPost = getRef(i).getKey();


                            System.out.println("xxx = " + pollViewHolder.getTextQuestion());

                           final MaterialEditText materialEditText = pollViewHolder.mView.findViewById(R.id.etQuestionPoll);
                           materialEditText.addTextChangedListener(new TextWatcher() {
                               @Override
                               public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                String textQ = materialEditText.getText().toString();
                                   System.out.println( "textQB = " + textQ);
                               }

                               @Override
                               public void onTextChanged(CharSequence s, int start, int before, int count) {
                                   String textQ = materialEditText.getText().toString();
                                   System.out.println( "textQC = " + textQ);
                               }

                               @Override
                               public void afterTextChanged(Editable s) {
                                   String textQ = materialEditText.getText().toString();
                                   System.out.println( "textQA = " + textQ);
                               }
                           });



                           FirebaseDatabase.getInstance().getReference("students").child(senderUserId)
                                    .child("Polls").child("Created").child(namePoll)
                                    .child(currentKeyPost).child("question").setValue(pollViewHolder.getTextQuestion());

                            System.out.println( "currentKeyPost = " + currentKeyPost);

                            System.out.println( "i = " + i);
                            //    recyclerViewPost.scrollToPosition(i);


                        }
                    };
                    recyclerViewCreatedPoll.setAdapter(firebaseRecyclerAdapter);


                        break;

            }
        }
    };
}



class PollViewHolder extends RecyclerView.ViewHolder {
    View mView;

    MaterialEditText etQuestionPoll;

    public PollViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
    }

    String q;
  public String getTextQuestion(){

      etQuestionPoll = mView.findViewById(R.id.etQuestionPoll);

      etQuestionPoll.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              q = etQuestionPoll.getText().toString();
              System.out.println("qq1 = " + q);
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

             q = etQuestionPoll.getText().toString();
              System.out.println("qq2 = " + q);
          }

          @Override
          public void afterTextChanged(Editable s) {
              q = etQuestionPoll.getText().toString();
              System.out.println("qq3 = " + q);
          }
      });

      return q;

    }


}
