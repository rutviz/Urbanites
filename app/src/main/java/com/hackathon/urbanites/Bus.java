package com.hackathon.urbanites;

/**
 * Created by bhavi on 26-07-2017.
 */

public class Bus {
    String bus_num,fare,bus_stop,distance, arr_time,trip_time;
    public Bus(String bus_num,String fare,String bus_stop,String distance, String arr_time,String trip_time) {
        this.bus_num=bus_num;
        this.fare=fare;
        this.bus_stop=bus_stop;
        this.distance=distance;
        this.arr_time=arr_time;
        this.trip_time=trip_time;
    }

    public String getBus_num() {
        return bus_num;
    }

    public void setBus_num(String bus_num) {
        this.bus_num = bus_num;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getBus_stop() {
        return bus_stop;
    }

    public void setBus_stop(String bus_stop) {
        this.bus_stop = bus_stop;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getArr_time() {
        return arr_time;
    }

    public void setArr_time(String arr_time) {
        this.arr_time = arr_time;
    }

    public String getTrip_time() {
        return trip_time;
    }

    public void setTrip_time(String trip_time) {
        this.trip_time = trip_time;
    }
}
