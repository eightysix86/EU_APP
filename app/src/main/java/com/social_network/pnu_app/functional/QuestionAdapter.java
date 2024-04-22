/*package com.social_network.pnu_app.functional;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.social_network.pnu_app.R;
import com.social_network.pnu_app.pages.BuildPollsActivity;

import java.sql.Array;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {


    static int countVariant;
    static  int positionQuestion =1;

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        Button btnAddVariant;

        MaterialEditText editTextQuestions;
       static RecyclerView variantPoll;


        VariantAdapter variantAdapter = new VariantAdapter();
        QuestionAdapter questionAdapter = new QuestionAdapter();


        public QuestionViewHolder(@NonNull final View itemView) {
            super(itemView);
          //  variantPoll= itemView.findViewById(R.id.recyclerViewVariant);
            variantPoll.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            btnAddVariant = itemView.findViewById(R.id.btnAddVariant);

            editTextQuestions = itemView.findViewById(R.id.etQuestionPoll);

        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_layout, parent, false);
        QuestionViewHolder qv = new QuestionViewHolder(v);

        return qv;
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionViewHolder holder,  int position) {
        positionQuestion = ++position;
        final VariantAdapter cont = new VariantAdapter();
        holder.variantPoll.setAdapter(holder.variantAdapter);
        System.out.println("positionQuestion = " + positionQuestion);
        holder.btnAddVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED btnAddVariant ");

                int c = cont.getCountVariant();
                c++;
                cont.setCountVariant(c);
                System.out.println("c++ " + c);

                holder.variantPoll.setAdapter(holder.variantAdapter);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (BuildPollsActivity.countQuestion == 0){
            return 1;
        }
        else {
            return (int) BuildPollsActivity.countQuestion;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public QuestionAdapter(){}
}*/
