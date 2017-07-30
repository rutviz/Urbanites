package com.hackathon.urbanites;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bhavi on 30-07-2017.
 */

public class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.DataObjectHolder> {
    private static String LOG_TAG = "CycleAdapter";
    private ArrayList<Cycle> cycle_list;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView cycle_stop, cycle_rate, stop_distance, cycle_status;

        public DataObjectHolder(View view) {
            super(view);
            cycle_stop = (TextView) view.findViewById(R.id.cycle_stop);
            cycle_rate = (TextView) view.findViewById(R.id.cycle_rate);
            stop_distance = (TextView) view.findViewById(R.id.stand_distance);
            cycle_status = (TextView) view.findViewById(R.id.cycle_avability_status);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CycleAdapter(ArrayList<Cycle> myDataset) {
        cycle_list = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cycle_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.cycle_stop.setText(cycle_list.get(position).getCycle_stop());
        holder.cycle_rate.setText(cycle_list.get(position).getCycle_rate());
        holder.stop_distance.setText(cycle_list.get(position).getStand_distance());
        holder.cycle_status.setText(cycle_list.get(position).getCycle_status());
    }

    public void addItem(Cycle dataObj, int index) {
        cycle_list.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        cycle_list.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return cycle_list.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
