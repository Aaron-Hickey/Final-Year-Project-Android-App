package com.example.hal9000.communitywatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    List<Address> addresses = null;
    Boolean isMessageValid = true;
    TextView debugText;
    EditText messageDescriptionET;
    EditText messageNameET;
    EditText messagePhoneNoET;

    String messageDescriptionValue;
    String invalidMessageText;

    private GoogleMap mMap;
    double longitude = 0;
    double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugText = findViewById(R.id.DebugText);
        messageDescriptionET = findViewById(R.id.MessageDescription);
        messageNameET = findViewById(R.id.MessageName);
        messagePhoneNoET = findViewById(R.id.MessagePhoneNo);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);
        }

        //MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        final Button button = findViewById(R.id.MessageSend);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendMessage();
            }
        });
    }

    void SendMessage()
    {
        isMessageValid = true;

        //emulator localhost IP address
      //  String url = "http://10.0.2.2:9000/report";
        String url = "http://192.168.43.203:9000/report";

        // String url = "http://192.168.1.2:9000/report";
        //String url = "http://httpbin.org/post";
        RequestQueue queue = Volley.newRequestQueue(this);
        messageDescriptionValue = messageDescriptionET.getText().toString();
        if(messageDescriptionValue.equals(""))
        {
            isMessageValid = false;
            invalidMessageText = "Please enter a description of what's happening";
        }
        if(longitude==0 && latitude==0)
        {
            isMessageValid = false;
            invalidMessageText = "Can't find location on map";
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                       // debugText.setText(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("description", messageDescriptionValue);
                params.put("longitude", ""+longitude);
                params.put("latitude", "" +latitude );
                params.put("name", messageNameET.getText().toString());
                params.put("phone", messagePhoneNoET.getText().toString());

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if(isMessageValid == true)
        {
            queue.add(postRequest);
        }
        else
        {
            debugText.setText(invalidMessageText);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            Location location = getLastKnownLocation();

            if(location != null)
            {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                LatLng pos = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(pos).draggable(true).title("Incident"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,12.0f));
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.setMyLocationEnabled(true);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {}
                    @Override
                    public void onMarkerDrag(Marker marker) {}
                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        latitude = marker.getPosition().latitude;
                        longitude = marker.getPosition().longitude;
                    }
                });
            }

        } catch (IOException ioException) {
            debugText.setText("IOException");

        } catch (IllegalArgumentException illegalArgumentException) {
            debugText.setText("Illegal");
        }
        catch (SecurityException se)
        {
            debugText.setText("Security Exception");
        }
    }
    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
