package com.example.shieldx.Util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    //Initialize variable
    Activity activity;
    ArrayList<ContactModel> arrayList = new ArrayList<>();

    //create constructor
    public MainAdapter(Activity activity, ArrayList<ContactModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        //Return view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        //Initialize Contact Model
        ContactModel model = arrayList.get(position);
        //set name
        holder.tvName.setText(model.getName());
        //set number
        holder.tvNumber.setText(model.getNumber());
    }

    @Override
    public int getItemCount() {
        //Return array list size
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //assign variable
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
        }
    }
}
