/*
package com.social_network.pnu_app.functional;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.social_network.pnu_app.R;

import static com.social_network.pnu_app.functional.QuestionAdapter.QuestionViewHolder.variantPoll;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    public int getCountVariant() {
        return countVariant;
    }


    public static void setCountVariant(int countVariant) {
        VariantAdapter.countVariant = countVariant;
    }

    static int countVariant = 3;

    public static class VariantViewHolder extends RecyclerView.ViewHolder {

        MaterialEditText editTextVariant;
        Button deleteVariant;
        RecyclerView variantPoll;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
         //   variantPoll = itemView.findViewById(R.id.recyclerViewVariant);
            editTextVariant = itemView.findViewById(R.id.etQuestionVariantPoll1);
            deleteVariant = itemView.findViewById(R.id.deleteVariant);
        }
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.variant_layout, parent, false);
        VariantViewHolder qv = new VariantViewHolder(v);
        return qv;
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
    */
/*    countVariant = 2+position;*//*

        System.out.println("onBindViewHolder() countVariant = " + ++position);
        holder.editTextVariant.setHint("Варіант " + position);

        final int finalPosition = ++position;
        holder.deleteVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                variantPoll.removeItemDecorationAt(finalPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (countVariant <= 3){
            countVariant = 3;
        }
        System.out.println("getItemCount() countVariant = " + countVariant);
        return countVariant;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public VariantAdapter(){}
}
*/
