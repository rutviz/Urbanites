package com.hackathon.urbanites;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView
        .Adapter<BusAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "BusAdapter";
    private ArrayList<Bus> bus_list;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView busNum, Fare, busStop, Distance, arrTime, tripTime;

        public DataObjectHolder(View view) {
            super(view);
            busNum = (TextView) view.findViewById(R.id.bus_number);
            busStop = (TextView) view.findViewById(R.id.bus_stop);
            Fare = (TextView) view.findViewById(R.id.bus_fare);
            Distance = (TextView) view.findViewById(R.id.bus_distance);
            arrTime = (TextView) view.findViewById(R.id.arriving_time);
            tripTime = (TextView) view.findViewById(R.id.trip_time);
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

    public BusAdapter(ArrayList<Bus> myDataset) {
        bus_list = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_detailed_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.busNum.setText(bus_list.get(position).getBus_num());
        holder.Fare.setText(bus_list.get(position).getFare());
        holder.busStop.setText(bus_list.get(position).getBus_stop());
        holder.Distance.setText(bus_list.get(position).getDistance());
        holder.arrTime.setText(bus_list.get(position).getArr_time());
        holder.tripTime.setText(bus_list.get(position).getTrip_time());
    }

    public void addItem(Bus dataObj, int index) {
        bus_list.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        bus_list.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return bus_list.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}