package com.example.saurabh.map_final;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "test";
    private GoogleMap mMap;
    private Button button;
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("Bus");


    private List<Polyline> polylinePaths = new ArrayList<>();





    static InputStream is = null;
    static JSONObject json = null;
    static String output = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ArrayList<LatLng> stops = new ArrayList<LatLng>();
        stops.add(new LatLng(18.607074, 73.873445));
        stops.add(new LatLng(18.601163, 73.872753));
        stops.add(new LatLng(18.590967, 73.874425));
        stops.add(new LatLng(18.574144, 73.877497));
        stops.add(new LatLng(18.573500, 73.884581));
        stops.add(new LatLng(18.574272, 73.891017));

        for (int i = 0; i < stops.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(stops.get(i)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stops.get(i), 13));

            //myRef.setValue("Hello");
        }

        for (int i = 0; i < stops.size() - 1; i++) {

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

            }

            String origin = stops.get(0).latitude + "," + stops.get(0).longitude;
            String destination = stops.get(5).latitude + "," + stops.get(5).longitude;
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination + "&key=AIzaSyBC5mZ8i142fKpuGMGTHvs95MkpZ3kBPic\n";

            URL Url = null;
            HttpURLConnection urlConnection = null;


            try {
                Url = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                urlConnection = (HttpURLConnection) Url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder(is.available());
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line).append('\n');
                }
                output = total.toString();
            } catch (IOException e) {
                Log.e("JSON Parser", "IO error " + e.toString());

            } finally {
                urlConnection.disconnect();
            }

            try {
                json = new JSONObject(output);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }


            List<Route> routes = new ArrayList<>();
            JSONObject jsonData = json;
            JSONArray jsonRoutes = null;


            try {
                jsonRoutes = jsonData.getJSONArray("routes");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < jsonRoutes.length(); j++) {

                JSONObject jsonRoute = null;
                try {
                    jsonRoute = jsonRoutes.getJSONObject(j);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Route route = new Route();


                JSONObject overview_polylineJson = null;
                try {
                    overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    route.points = decodePolyLine(overview_polylineJson.getString("points"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                routes.add(route);
            }

            plot(routes);
            getLocation();


        }


    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng((((double) lat / 1e5)),
                    (((double) lng / 1e5))));
        }

        return decoded;
    }

    public void plot(List<Route> routes) {


        for (Route route : routes) {

            PolylineOptions polylineOptions = new PolylineOptions().
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    protected void getLocation() {


        button = (Button) findViewById(R.id.b);
        textView = (TextView) findViewById(R.id.t);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                textView.setText(" " + latitude + " " + longitude);
                //myRef.setValue("Hello, World!");
               // myRef.setValue(latitude + " " + longitude);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        ACCESS_FINE_LOCATION,
                        permission.ACCESS_COARSE_LOCATION,
                        permission.INTERNET
                }, 10);
                return;
            }
        } else {
            configureButton();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates("network", 5000, 0, locationListener);
            }
        });
    }


}
