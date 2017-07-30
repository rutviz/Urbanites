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

public class BusSmallAdapter extends RecyclerView.Adapter<BusSmallAdapter.DataObjectHolder> {
    private static String LOG_TAG = "BusSmallAdapter";
    private ArrayList<SmallBus> bus_small_list;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView busNum, Fare, Distance, arrTime;

        public DataObjectHolder(View view) {
            super(view);
            busNum = (TextView) view.findViewById(R.id.bus_number);
            Fare = (TextView) view.findViewById(R.id.bus_fare);
            Distance = (TextView) view.findViewById(R.id.bus_distance);
            arrTime = (TextView) view.findViewById(R.id.arriving_time);
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

    public BusSmallAdapter(ArrayList<SmallBus> myDataset) {
        bus_small_list = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.busNum.setText(bus_small_list.get(position).getBus_num());
        holder.Fare.setText(bus_small_list.get(position).getFare());
        holder.Distance.setText(bus_small_list.get(position).getDistance());
        holder.arrTime.setText(bus_small_list.get(position).getArr_time());
    }

    public void addItem(SmallBus dataObj, int index) {
        bus_small_list.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        bus_small_list.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return bus_small_list.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
