package com.hackathon.urbanites;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private int temp;
    private ArrayList<MyLocation> RMTS_bus;
    private double SourceLat;
    private double SourceLong;
    private double DestLat;
    private double DestLong;
    private DistanceCalc DC = new DistanceCalc();



    EditText Source, Destination;

    LatLng NearSource, NearDest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Source = (EditText) findViewById(R.id.source);
        Destination = (EditText) findViewById(R.id.dest);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RMTS_bus = loadJSONFromAsset();

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

    private ArrayList<MyLocation> loadJSONFromAsset() {
        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.rajkot_bus_list);
        Scanner scanner = new Scanner(is);
        StringBuilder builder = new StringBuilder();
        while(scanner.hasNextLine())
        {
            builder.append(scanner.nextLine());
        }
        return parseJson(builder.toString());
    }

    private ArrayList<MyLocation> parseJson(String s) {
        ArrayList<MyLocation> locList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        try {
            JSONObject root = new JSONObject(s);

            JSONArray RMTS = root.getJSONArray("RMTS");
            for(int i=0;i<RMTS.length();i++)
            {
                Log.d("Station",RMTS.length()+"");
                JSONObject routes = RMTS.getJSONObject(i);
                JSONArray route = routes.getJSONArray("Route"+(i+1));
                for (int j=0;j<route.length();j++)
                {
                    Log.d("Station",route.length()+"");
                    JSONObject val = route.getJSONObject(j);
                    MyLocation location = new MyLocation();
                    location.setName(val.getString("name"));
                    location.setId(val.getInt("id"));
                    location.setLongitude(val.getDouble("long"));
                    location.setLatitude(val.getDouble("lat"));
                    locList.add(location);
                }
            }

        } catch (JSONException e) {
            Log.d("Station","error");
            builder.append("name: ");
            e.printStackTrace();
            Log.d("test",e.getMessage());
        }

        Log.d("test",builder.toString());
        return locList;
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
            temp = 0;
            new DirectionFinder(this, origin, destination).execute();
            /*new DirectionFinder(this, "Gondal chowk, samrat Industrial area,rajkot", destination).execute();*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        else
        {
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "Map Loaded");
        }
    }

    public void setLocationEnable() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Source.setText(place.getName() + ", " + place.getAddress());
                SourceLat = place.getLatLng().latitude;
                SourceLong = place.getLatLng().longitude;
                Log.d("test",place.getLatLng().latitude+"");
                Log.d("test",place.getLatLng().longitude+"");
                /*if (!Destination.getText().toString().equals("")){
                    Log.d(TAG,"Finding");
                    sendRequest();
                }*/
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_dest) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Destination.setText(place.getName()+", "+ place.getAddress());
                DestLat = place.getLatLng().latitude;
                DestLong = place.getLatLng().longitude;

                Log.d("test",place.getLatLng().latitude+"");
                Log.d("test",place.getLatLng().longitude+"");
                if (!Source.getText().toString().equals("")){
                    Log.d(TAG,"Finding");
                    sendRequest();
                    double dist = getNearestBRTS();
                    Log.d("distance", String.valueOf(dist));


                }
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }



    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.","Finding direction..!", true);
        /*if (originMarkers != null) {
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
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }*/
    }

    private double getNearestBRTS() {
        double D1;
        double D2 = 0;
        String near_station = null;

        for (MyLocation location : RMTS_bus)
        {
            D1 =  DC.distance(SourceLat,SourceLong,location.getLatitude(),location.getLongitude());
          //  Log.d("Send123","Source-"+SourceLat+" "+SourceLong+" "+location.getLatitude()+" "+location.getLongitude());
            Log.d("Distance123",location.getName()+"--"+D1);
            Log.d("Distance123",near_station+"--"+D2);
            if(D2 == 0)
            {
                D2 = D1;
                near_station = location.getName();
            }

            if(D1 < D2)
            {
                Log.d("Distance123","changed");
                D2 = D1;
                near_station = location.getName();
            }

        }
        Log.d("Station",near_station);
        return D2;
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        Log.d("success123","success");
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
            Log.d("test",route.distance.text);
            /*((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);*/

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);
            if (temp==1) {
                polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.RED).
                        width(10);
            }


            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            Polyline mpolyline = mMap.addPolyline(polylineOptions);

            polylinePaths.add(mpolyline);
            temp =1;
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

   /* public ArrayList<MyLocation> loadJSONFromAsset() {
        ArrayList<MyLocation> locList = new ArrayList<>();
        String json = null;
        try {
            Log.d("outputJson","1 done");
            InputStream is = getAssets().open("rajkot_bus_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;

        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray m_jArry = obj.getJSONArray("RMTS").getJSONArray(1);
            Log.d("outputJson","1 done");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                MyLocation location = new MyLocation();
                location.setId(jo_inside.getInt("id"));
                location.setName(jo_inside.getString("name"));
                location.setLatitude((float) jo_inside.getDouble("lat"));
                location.setLongitude((float) jo_inside.getDouble("long"));
                Log.d("outputJson",jo_inside.getString("name"));

                //Add your values in your `ArrayList` as below:
                locList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locList;

    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}