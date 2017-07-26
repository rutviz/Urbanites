package com.hackathon.urbanites;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bhavi on 26-07-2017.
 */

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    ArrayList<Bus> bus=new ArrayList<Bus>();
    public BusAdapter(ArrayList<Bus>  bus){
        this.bus=bus;
    }
    @Override
    public BusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_detailed_card,parent,false);
        BusViewHolder busViewHolder=new BusViewHolder(view);
        return busViewHolder;
    }

    @Override
    public void onBindViewHolder(BusViewHolder holder, int position) {
        Bus BUS= bus.get(position);
        holder.busNum.setText(BUS.bus_num);
        holder.Fare.setText(BUS.fare);
        holder.busStop.setText(BUS.bus_stop);
        holder.Distance.setText(BUS.distance);
        holder.arrTime.setText(BUS.arr_time);
        holder.tripTime.setText(BUS.trip_time);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public static class BusViewHolder extends RecyclerView.ViewHolder{
        TextView busNum,Fare,busStop,Distance,arrTime,tripTime;

        public BusViewHolder(View view){
            super(view);
            busNum = (TextView) view.findViewById(R.id.bus_number);
            busStop = (TextView) view.findViewById(R.id.bus_stop);
            Fare = (TextView) view.findViewById(R.id.bus_fare);
            Distance = (TextView) view.findViewById(R.id.distance);
            arrTime = (TextView) view.findViewById(R.id.arriving_time);
            tripTime = (TextView) view.findViewById(R.id.trip_time);


        }

    }
}
