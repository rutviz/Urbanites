package com.hackathon.urbanites;
public class Cycle {
    String cycle_stop,cycle_rate,stand_distance,cycle_status;
    Cycle (String cycle_stop,String cycle_rate,String stand_distance,String cycle_status){
        this.cycle_stop=cycle_stop;
        this.cycle_rate=cycle_rate;
        this.stand_distance=stand_distance;
        this.cycle_status=cycle_status;
    }
    public String getCycle_stop() {
        return cycle_stop;
    }

    public void setCycle_stop(String cycle_stop) {
        this.cycle_stop = cycle_stop;
    }

    public String getCycle_rate() {
        return cycle_rate;
    }

    public void setCycle_rate(String cycle_rate) {
        this.cycle_rate = cycle_rate;
    }

    public String getStand_distance() {
        return stand_distance;
    }

    public void setStand_distance(String stand_cycle_status) {
        this.stand_distance = stand_cycle_status;
    }

    public String getCycle_status() {
        return cycle_status;
    }

    public void setCycle_status(String cycle_status) {
        this.cycle_status = cycle_status;
    }

}
