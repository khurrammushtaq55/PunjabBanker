package com.mmushtaq.bank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mmushtaq.bank.R;
import com.mmushtaq.bank.activities.BanksListActivity;
import com.mmushtaq.bank.model.Banks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BanksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Banks> banksList;
    private final LayoutInflater mLayoutInflater;
    private final Context context;


    public BanksAdapter(Context context, List<Banks> banksList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.banksList = banksList;
        this.context = context;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_banks, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {


        final BanksAdapter.MyViewHolder viewHolder = (BanksAdapter.MyViewHolder) holder;
        Banks bank = banksList.get(position);
        viewHolder.bankName.setText(bank.getName());
            viewHolder.btnGetCases.setOnClickListener(view -> ( (BanksListActivity)context).getCases());



    }


    @Override
    public int getItemCount() {
        return null!= banksList ? banksList.size():0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder  {
        Button btnGetCases;
        TextView bankName;

        public MyViewHolder(View itemView) {
            super(itemView);

            btnGetCases = itemView.findViewById(R.id.btnVerify);
            bankName= itemView.findViewById(R.id.bankName);


        }
    }
}
