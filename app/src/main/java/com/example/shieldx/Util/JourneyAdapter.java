package com.example.shieldx.Util;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shieldx.R;

import java.util.ArrayList;

public class JourneyAdapter  extends RecyclerView.Adapter<JourneyAdapter.ViewHolder> {
    //Initialize variable
    Activity activity;
    ArrayList<JourneyModel> arrayList = new ArrayList<JourneyModel>();

    //create constructor
    public JourneyAdapter(Activity activity, ArrayList<JourneyModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public JourneyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journey, parent, false);
        //Return view
        return new JourneyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JourneyAdapter.ViewHolder holder, int position) {
        //Initialize Contact Model
        JourneyModel model = arrayList.get(position);

        if (model.getFollowers() != null)

            holder.followers.setText(model.getFollowers());

        if (model.getSourceName() != null)

            holder.source.setText(Html.fromHtml( "<b>" + "Source:" + "</b> " + model.getSourceName()));

        if (model.getDestinationName() != null)

            holder.destination.setText(Html.fromHtml( "<b>" + "Destination:" + "</b> " + model.getDestinationName()));

        if (model.getDuration() != null)

            holder.duration.setText(Html.fromHtml( "<b>" + "Duration:" + "</b> " + model.getDuration()));

        if (model.getActivityDate() != null)
            holder.date.setText(Html.fromHtml( "<b>" + "Activity Date:" + "</b> " + model.getActivityDate()));

        if (model.getAborted()) {
            holder.aborted.setImageResource(R.drawable.abort_background);
        }
        else{
            holder.aborted.setImageResource(R.drawable.trending_background);
        }

        if (model.getModeOfTransport().equalsIgnoreCase("walking")) {
            holder.modeOfTransport.setBackgroundResource(R.drawable.ic_walking);
        } else if (model.getModeOfTransport().equalsIgnoreCase("driving")) {
            holder.modeOfTransport.setBackgroundResource(R.drawable.ic_driving);
        } else {
            holder.modeOfTransport.setBackgroundResource(R.drawable.ic_bicycle);
        }

    }

    @Override
    public int getItemCount() {
        //Return array list size
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView source, destination, duration, followers, date;
        ImageView aborted, modeOfTransport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //assign variable
            source = itemView.findViewById(R.id.source);
            destination = itemView.findViewById(R.id.destination);
            duration = itemView.findViewById(R.id.duration);
            modeOfTransport = itemView.findViewById(R.id.modeOfTransport);
            followers = itemView.findViewById(R.id.followers);
            aborted = itemView.findViewById(R.id.abort);
            date = itemView.findViewById(R.id.date);
        }
    }
}
