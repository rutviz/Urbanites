package Modules;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rutviz Vyas on 26-07-2017.
 */

public class MyLocation {
    int id;
    String name;
    double latitude;
    double longitude;

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
