package com.hackathon.urbanites;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import Modules.Bus_Traking;
import Modules.DirectionFinder;
import Modules.Distance;
import Modules.MyLocation;
import Modules.DirectionFinderListener;
import Modules.Route;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener {


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_dest = 2;
    private final String TAG = "locationLog";

    GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<MyLocation> BRTS_bus, RMTS_bus;
    private double SourceLat, SourceLong, DestLat, DestLong;
    private DistanceCalc DC = new DistanceCalc();
    EditText Source, Destination;
    TextView RMTS, BRTS, Cycle;
    LatLng NearSource, NearDest;
    android.support.v7.widget.CardView cardView;
    float total_distance;
    TextView bus_stop, bus_distance;
    int flag_walk = 1, flag_bus = 1,Zoom_level=0;
    ImageView my_location;
    private ArrayList<MyLocation> CYCLE_stand;
    public static int TAB = 1;
    Place sPlace,dPlace;
    public Thread bus_track;
    FloatingActionButton fab1,fab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Source = (EditText) findViewById(R.id.source);
        Destination = (EditText) findViewById(R.id.dest);
        RMTS = (TextView) findViewById(R.id.RMTS);
        BRTS = (TextView) findViewById(R.id.BRTS);
        Cycle = (TextView) findViewById(R.id.CYCLE);
        bus_distance = (TextView) findViewById(R.id.bus_distance);
        bus_stop = (TextView) findViewById(R.id.bus_stop);
        my_location = (ImageView) findViewById(R.id.loc_img_source);
        fab1 = (FloatingActionButton) findViewById(R.id.fab22);
        fab2 = (FloatingActionButton) findViewById(R.id.fab32);
        cardView = (android.support.v7.widget.CardView) findViewById(R.id.card_view_ini);


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RMTS.callOnClick();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        convert();
        BRTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAB = 1;
                mMap.clear();
                routefinder(sPlace,1,1);
                routefinder(dPlace,1,2);
                BRTS_bus = loadJSONFromAsset();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 12));
                RMTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                BRTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round_active));
                Cycle.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
            }
        });
        RMTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAB = 2;
                mMap.clear();
                RMTS_bus = loadJSONFromAssetRMTS();
                bus_track.start();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 13));
                RMTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round_active));
                BRTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                Cycle.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                routefinder(sPlace,1,1);
                routefinder(dPlace,2,1);
            }
        });
        Cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAB = 3;
                mMap.clear();
                CYCLE_stand = loadJSONFromAssetCycle();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 13));
                RMTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                BRTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                Cycle.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round_active));
                routefinder(sPlace,1,1);
                routefinder(dPlace,2,1);
            }
        });

        bus_track = new Thread(new Runnable(){

            @Override
            public void run() {
                while(TAB==2) {
                    Log.d("parsing", "updating tab= "+TAB);
                    new Bus_Traking(mMap,TAB).execute();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = getMycurrentloc();
                Source.setText(latLng.latitude + "," + latLng.longitude);
            }
        });

        Source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });


        Destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_dest);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        } else {
            mMap.setMyLocationEnabled(true);

        }

        BRTS_bus = loadJSONFromAsset();
/*
        mMap.setOnCameraMoveListener(new OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d("parsing123", "" + mMap.getCameraPosition().zoom);
                Zoom_level = (int) mMap.getCameraPosition().zoom;
                if (Zoom_level >= 13) {
                    if (TAB == 1) {
                        for (MyLocation i : BRTS_bus) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(i.getLatitude(), i.getLongitude())));
                        }
                    } else if (TAB == 2) {
                        for (MyLocation i : RMTS_bus) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(i.getLatitude(), i.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rmt)));
                        }
                    } else {
                        for (MyLocation i : CYCLE_stand) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(i.getLatitude(), i.getLongitude())));
                        }
                    }

                }
                else if(TAB==2)
                {
                    for (MyLocation i : RMTS_bus) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(i.getLatitude(), i.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rmt)));
                    }
                    mMap.clear();
                }
            }
        });
*/
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 13));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Source.setText(place.getName() + ", " + place.getAddress());
                SourceLat = place.getLatLng().latitude;
                SourceLong = place.getLatLng().longitude;
                sPlace=place;
                routefinder(place,1,0);
                Log.d("test", place.getLatLng().latitude + "");
                Log.d("test", place.getLatLng().longitude + "");
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_dest) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Destination.setText(place.getName() + ", " + place.getAddress());
                DestLat = place.getLatLng().latitude;
                DestLong = place.getLatLng().longitude;
                dPlace=place;
                Log.d("test", place.getLatLng().latitude + "");
                Log.d("test", place.getLatLng().longitude + "");
                routefinder(place,2,0);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    public void onDirectionFinderStart() {
        // progressDialog = ProgressDialog.show(this, "Please wait.","Finding direction..!", true);

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        //progressDialog.dismiss();
        //  Log.d("success123","success");
        Log.d("original", "inside");
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
            Log.d("test", route.distance.text);

            if (flag_bus == 1) {
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.startAddress)
                        .position(route.startLocation)));
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(getResources().getColor(R.color.green_600)).
                        width(15);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));
                Polyline mpolyline = mMap.addPolyline(polylineOptions);

                polylinePaths.add(mpolyline);
            }
            else if (flag_bus == 2) {
                Log.d("original", String.valueOf(flag_bus));
                bus_distance.setText(route.distance.text);
                cardView.setVisibility(View.VISIBLE);
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(getResources().getColor(R.color.blue_600)).
                        width(15);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));
                Polyline mpolyline = mMap.addPolyline(polylineOptions);

                polylinePaths.add(mpolyline);
            }
            else if (flag_bus == 3) {
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.endAddress)
                        .position(route.endLocation)));
                PolylineOptions  polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(getResources().getColor(R.color.green_600)).
                        width(15);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));
                Polyline mpolyline = mMap.addPolyline(polylineOptions);

                polylinePaths.add(mpolyline);

            }
            flag_bus++;
            if (flag_bus > 3) {
                flag_bus = 0;
            }

            /*((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);*/


            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))

;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void convert()
    {
        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.station_time);
        Scanner scanner = new Scanner(is);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        ArrayList<MyLocation> locListRMTS = new ArrayList<>();
        StringBuilder builder1 = new StringBuilder(builder);
        try {

            JSONArray RMTS = new JSONArray(builder1);
            for (int i = 0; i < RMTS.length(); i++) {
                Log.d("parsing123",RMTS.getString(i));
            }
        } catch (JSONException e) {
            Log.d("Station", "error");
            builder.append("name: ");
            e.printStackTrace();
            Log.d("test", e.getMessage());
        }
    }

    private ArrayList<MyLocation> loadJSONFromAsset() {
        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.rajkot_bus_list);
        Scanner scanner = new Scanner(is);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        return parseJson(builder.toString());
    }

    private ArrayList<MyLocation> parseJson(String s) {
        ArrayList<MyLocation> locList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try {
            JSONObject root = new JSONObject(s);

            JSONArray BRTS = root.getJSONArray("BRTS");
            for (int i = 0; i < BRTS.length(); i++) {
                JSONObject val = BRTS.getJSONObject(i);
                MyLocation location = new MyLocation();
                location.setName(val.getString("name"));
                location.setId(val.getInt("id"));
                location.setLongitude(val.getDouble("long"));
                location.setLatitude(val.getDouble("lat"));
                locList.add(location);
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rmt))
                        .title(location.getName())
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));

            }
        } catch (JSONException e) {
            Log.d("Station", "error");
            builder.append("name: ");
            e.printStackTrace();
            Log.d("test", e.getMessage());
        }


        Log.d("test", builder.toString());
        return locList;
    }

    private ArrayList<MyLocation> loadJSONFromAssetCycle() {
        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.rajkot_cycle);
        Scanner scanner = new Scanner(is);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        return parseJsonCycle(builder.toString());
    }

    private ArrayList<MyLocation> parseJsonCycle(String s) {
        ArrayList<MyLocation> locListCYCLE = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try {
            JSONObject root = new JSONObject(s);

            JSONArray CYCLE = root.getJSONArray("CYCLE");
            for (int i = 0; i < CYCLE.length(); i++) {
                JSONObject val = CYCLE.getJSONObject(i);
                MyLocation location = new MyLocation();
                Log.d("original", val.getString("name"));
                location.setName(val.getString("name"));
                location.setId(val.getInt("id"));
                location.setLongitude(val.getDouble("long"));
                location.setLatitude(val.getDouble("lat"));
                locListCYCLE.add(location);
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_cycle))
                        .title(location.getName())
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));

            }
        } catch (JSONException e) {
            Log.d("Station", "error");
            builder.append("name: ");
            e.printStackTrace();
            Log.d("test", e.getMessage());
        }


        Log.d("test", builder.toString());
        return locListCYCLE;
    }

    private ArrayList<MyLocation> loadJSONFromAssetRMTS() {
        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.rmts_bus);
        Scanner scanner = new Scanner(is);
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }
        return parseJsonRMTS(builder.toString());
    }

    private ArrayList<MyLocation> parseJsonRMTS(String s) {
        ArrayList<MyLocation> locListRMTS = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try {
            JSONObject root = new JSONObject(s);

            JSONArray RMTS = root.getJSONArray("RMTS");
            for (int i = 0; i < RMTS.length(); i++) {
                JSONObject val = RMTS.getJSONObject(i);
                MyLocation location = new MyLocation();
                Log.d("original", val.getString("name"));
                location.setName(val.getString("name"));
                location.setId(val.getInt("id"));
                location.setLongitude(val.getDouble("long"));
                location.setLatitude(val.getDouble("lat"));
                locListRMTS.add(location);
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rmt))
                        .title(location.getName())
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));

            }
        } catch (JSONException e) {
            Log.d("Station", "error");
            builder.append("name: ");
            e.printStackTrace();
            Log.d("test", e.getMessage());
        }


        Log.d("test", builder.toString());
        return locListRMTS;
    }

    private void sendRequest() {
        String origin = Source.getText().toString();
        String destination = Destination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
            new DirectionFinder(this, origin, NearSource.latitude + "," + NearSource.longitude).execute();
            Log.d("original", "flag bus");
            new DirectionFinder(this, NearSource.latitude + "," + NearSource.longitude, NearDest.latitude + "," + NearDest.longitude).execute();
            Log.d("original", "flag walk");
            new DirectionFinder(this, NearDest.latitude + "," + NearDest.longitude, destination).execute();
            /*new DirectionFinder(this, "Gondal chowk, samrat Industrial area,rajkot", destination).execute();*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public LatLng getMycurrentloc() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location l = null;
        LatLng latLng = null;

        for (int i = 0; i < providers.size(); i++) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) {
                double latitude = l.getLatitude();
                double longitude = l.getLongitude();
                latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng));
                break;
            }
        }
        NearSource = getNearestBRTS(latLng.latitude, latLng.longitude, 1);
        return latLng;
    }

    private LatLng getNearestBRTS(Double Lat, Double Long, int i) {
        double D1;
        double D2 = 0;
        String near_station = null;
        LatLng latLng = new LatLng(0, 0);

        for (MyLocation location : BRTS_bus) {
            D1 = DC.distance(Lat, Long, location.getLatitude(), location.getLongitude());
            if (D2 == 0) {
                D2 = D1;
                near_station = location.getName();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            if (D1 < D2) {
                D2 = D1;
                near_station = location.getName();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

        }
        Log.d("Stationnear", latLng.toString());
        if (i == 1)
            bus_stop.setText(near_station);
        return latLng;
    }

    private LatLng getNearestRMTS(Double Lat, Double Long, int i) {
        double D1;
        double D2 = 0;
        String near_station = null;
        LatLng latLng = new LatLng(0, 0);

        for (MyLocation location : RMTS_bus) {
            D1 = DC.distance(Lat, Long, location.getLatitude(), location.getLongitude());
            if (D2 == 0) {
                D2 = D1;
                near_station = location.getName();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            if (D1 < D2) {
                D2 = D1;
                near_station = location.getName();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

        }
        Log.d("Stationnear RMTS", near_station);
        if (i == 1)
            bus_stop.setText(near_station);
        return latLng;
    }

    private LatLng getNearestCycle(Double Lat, Double Long, int i) {
        double D1;
        double D2 = 0;
        String near_station = null;
        LatLng latLng = new LatLng(0, 0);

        for (MyLocation location : CYCLE_stand) {
            D1 = DC.distance(Lat, Long, location.getLatitude(), location.getLongitude());
            if (D2 == 0) {
                D2 = D1;
                near_station = location.getName();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            if (D1 < D2) {
                D2 = D1;
                near_station = location.getName();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }

        }
        Log.d("Stationnear", latLng.toString());
        if (i == 1)
            bus_stop.setText(near_station);
        return latLng;
    }

    private void routefinder(Place place, int i,int j) {
        Log.d("testr121",i+""+j+""+TAB);
        if (i == 1) {
            if (TAB == 1) {
                NearSource = getNearestBRTS(SourceLat, SourceLong, 1);
            } else if (TAB == 2) {
                NearSource = getNearestRMTS(SourceLat, SourceLong, 1);
            } else {
                NearSource = getNearestCycle(SourceLat, SourceLong, 1);
            }
            if (!Destination.getText().toString().equals("") && j==0) {
                sendRequest();
            }

        }
        else
        {
            if (TAB == 1) {
                NearDest = getNearestBRTS(DestLat, DestLong, 2);
            } else if (TAB == 2) {
                NearDest = getNearestRMTS(DestLat, DestLong, 2);
            } else {
                NearDest = getNearestCycle(DestLat, DestLong, 2);
            }
            if (!Source.getText().toString().equals("")) {
                sendRequest();
            }
        }

    }

}