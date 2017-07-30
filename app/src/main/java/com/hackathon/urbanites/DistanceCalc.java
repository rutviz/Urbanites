package com.hackathon.urbanites;

import android.util.Log;

public class DistanceCalc {

    public double distance(Double sourcelat, Double sourcelong, Double destlat, Double destlong)
    {
        Log.d("dist12","Slati"+sourcelat);
        Log.d("dist12","Slong"+sourcelong);
        Log.d("dist12","Dlati"+destlat);
        Log.d("dist12","Dlon"+destlong);
        double temp1 =  (destlat-sourcelat);
        double temp2 =  (destlong-sourcelong);
        Log.d("dist12",temp1+" temp1");
        Log.d("dist12",temp2+" temp2");
        temp1*=temp1;
        temp2*=temp2;
        Log.d("dist12",temp1+" temp1");
        Log.d("dist12",temp2+" temp2");
        Log.d("dist12",Math.sqrt(temp1+temp2)+"");
        return Math.sqrt(temp1+temp2);

    }
/*
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    public long distance(double userLat, double userLng,
                         double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }*/
}
