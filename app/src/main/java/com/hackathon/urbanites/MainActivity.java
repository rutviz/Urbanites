package com.hackathon.urbanites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.MyLocation;
import Modules.Route;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener {


    private RecyclerView bRecyclerView;
    private RecyclerView.Adapter bAdapter;
    private RecyclerView.LayoutManager bLayoutManager;
    private RecyclerView cRecyclerView;
    private RecyclerView.Adapter cAdapter;
    private RecyclerView.LayoutManager cLayoutManager;
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
    float total_distance;
    TextView bus_stop, bus_distance;
    int flag_walk = 1, flag_bus = 0;
    ImageView my_location;
    private ArrayList<MyLocation> CYCLE_stand;
    int TAB = 1;
    Place sPlace,dPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        bRecyclerView.setHasFixedSize(true);
        bLayoutManager = new LinearLayoutManager(getApplicationContext());
        bRecyclerView.setLayoutManager(bLayoutManager);
        bAdapter = new BusSmallAdapter(getDataSetBus());
        cRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        cRecyclerView.setHasFixedSize(true);
        cLayoutManager = new LinearLayoutManager(getApplicationContext());
        cRecyclerView.setLayoutManager(cLayoutManager);
        cAdapter = new CycleAdapter(getDataSetCycle());
        Source = (EditText) findViewById(R.id.source);
        Destination = (EditText) findViewById(R.id.dest);
        RMTS = (TextView) findViewById(R.id.RMTS);
        BRTS = (TextView) findViewById(R.id.BRTS);
        Cycle = (TextView) findViewById(R.id.CYCLE);
        bus_distance = (TextView) findViewById(R.id.bus_distance);
        bus_stop = (TextView) findViewById(R.id.bus_stop);
        my_location = (ImageView) findViewById(R.id.loc_img_source);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                //card
                    bRecyclerView.setAdapter(bAdapter);
            }
        });
        RMTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAB = 2;
                mMap.clear();
                RMTS_bus = loadJSONFromAssetRMTS();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 12));
                RMTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round_active));
                BRTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                Cycle.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                routefinder(sPlace,1,1);
                routefinder(dPlace,2,1);
                //card
               bRecyclerView.setAdapter(bAdapter);
            }
        });
        Cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAB = 3;
                mMap.clear();
                CYCLE_stand = loadJSONFromAssetCycle();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 12));
                RMTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                BRTS.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round));
                Cycle.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.round_active));
                routefinder(sPlace,1,1);
                routefinder(dPlace,2,1);
               //card
                cRecyclerView.setAdapter(cAdapter);
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


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMycurrentloc(), 12));

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
            }
            if (flag_bus == 2) {
                Log.d("original", String.valueOf(flag_bus));
                bus_distance.setText(route.distance.text);
            }
            if (flag_bus == 3) {
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.endAddress)
                        .position(route.endLocation)));

            }
            flag_bus++;
            if (flag_bus > 3) {
                flag_bus = 0;
            }

            /*((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);*/


            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(getResources().getColor(R.color.blue_600)).
                    width(15);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            Polyline mpolyline = mMap.addPolyline(polylineOptions);

            polylinePaths.add(mpolyline);
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
            flag_walk = 0;
            flag_bus = 1;
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
                mMap.addMarker(new MarkerOptions().position(latLng = new LatLng(latitude, longitude)));
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

    @Override
    protected void onResume() {
        super.onResume();
        ((BusSmallAdapter) bAdapter).setOnItemClickListener(new BusSmallAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
            }
        });
    }

    private ArrayList<SmallBus> getDataSetBus() {
        ArrayList results = new ArrayList<SmallBus>();
        for (int index = 0; index < 20; index++) {
            SmallBus obj = new SmallBus("2","4","2.1","2");
            results.add(index, obj);
        }
        return results;
    }
    private ArrayList<Cycle> getDataSetCycle() {
        ArrayList results = new ArrayList<Cycle>();
        for (int index = 0; index < 20; index++) {
            Cycle obj = new Cycle("station1","20","0.3","AVAILABLE");
            results.add(index, obj);
        }
        return results;
    }
    public static Context getAppContext() {
        return MainActivity.getAppContext();
    }
}