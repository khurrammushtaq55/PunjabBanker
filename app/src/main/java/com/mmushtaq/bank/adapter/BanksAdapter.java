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
import com.mmushtaq.bank.model.Case;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BanksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context context;
    private final List<String> keys;
    private final Map<String, List<Case>> banksList;


    public BanksAdapter(Context context, Map<String, List<Case>> banksList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.banksList = banksList;
        this.keys = new ArrayList<>(banksList.keySet());

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
        viewHolder.bankName.setText(keys.get(position));
        viewHolder.btnBankSchemes.setOnClickListener(view -> ((BanksListActivity) context).goToSchemas(banksList.getOrDefault(keys.get(position), null)));


    }


    @Override
    public int getItemCount() {
        return null != keys ? keys.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class MyViewHolder extends RecyclerView.ViewHolder {
        Button btnBankSchemes;
        TextView bankName;

        public MyViewHolder(View itemView) {
            super(itemView);

            btnBankSchemes = itemView.findViewById(R.id.btnBankSchemes);
            bankName = itemView.findViewById(R.id.bankName);


        }
    }
}
