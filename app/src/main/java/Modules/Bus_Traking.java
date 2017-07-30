package Modules;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hackathon.urbanites.MainActivity;
import com.hackathon.urbanites.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rutviz Vyas on 29-07-2017.
 */

public class Bus_Traking extends AsyncTask<String, Void, String>{

    GoogleMap mMap;
    int TAB=1;
    public static ArrayList<Marker> almarker = new ArrayList<Marker>();
    public Bus_Traking(GoogleMap mMa,int i)
    {
        mMap=mMa;
        TAB=i;
    }
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://rrlapi.hitecpoint.in/api/Rajkot/LiveData?Custkey=0EE46AAB-3E71-47AD-A56A-2650DD296A6A");
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                if (almarker != null) {
                    Log.d("parsing", String.valueOf(almarker.size()));
                    for (Marker marker : almarker) {
                        marker.remove();
                    }
                    almarker.clear();
                }
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseJSon(String res) throws JSONException {
            Log.d("parsing",res);
            StringBuilder builder = new StringBuilder();
            try {
                JSONArray RMTS = new JSONArray(res);
                Log.d("parsing123", String.valueOf(RMTS.length()));
                for (int i = 0; i < RMTS.length(); i++) {
                    JSONObject val = RMTS.getJSONObject(i);
                    Log.d("parsing",val.getString("VehicleStatus"));

                    if((val.getString("VehicleStatus").equals("Moving") || val.getString("VehicleStatus").equals("IgnitionOn"))&& MainActivity.TAB==2)
                    {
                        if(!val.getString("VehName").contains("BRTS")) {
                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .title(val.getString("VehName"))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                                    .position(new LatLng(Double.parseDouble(val.getString("Latitude")), Double.parseDouble(val.getString("Longitude")))));
                            almarker.add(m);
                        }
                    }
                    /*mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rmt))
                            .title(location.getName())
                            .position(new LatLng(location.getLatitude(), location.getLongitude())));
*/
                }
            } catch (JSONException e) {
                Log.d("Station", "error");
                builder.append("name: ");
                e.printStackTrace();
                Log.d("test", e.getMessage());
            }


            Log.d("test", builder.toString());
        }
}
