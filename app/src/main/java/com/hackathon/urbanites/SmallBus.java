package com.hackathon.urbanites;
public class SmallBus {
    String bus_num,fare,distance, arr_time;
    SmallBus (String bus_num,String fare,String distance, String arr_time){
        this.bus_num=bus_num;
        this.fare=fare;
        this.distance=distance;
        this.arr_time=arr_time;
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

}
