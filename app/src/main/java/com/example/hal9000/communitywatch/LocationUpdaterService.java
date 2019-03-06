package com.example.hal9000.communitywatch;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class LocationUpdaterService extends IntentService {
    private static final int NOTIF_ID = 1;

    private Location location = null;

    private UserObject uo;
    private Gson gson;
    private FileReaderObject fro;
    private final String fileName = "Logged_In_User";

    /**
     * A constructor is required, and must call the super <code><a href="/reference/android/app/IntentService.html#IntentService(java.lang.String)">IntentService(String)</a></code>
     * constructor with a name for the worker thread.
     */
    public LocationUpdaterService() {
        super("HelloIntentService");

    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
  /*  @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
       // requestLocationPermission();
        GetUserFromFile guff = new GetUserFromFile(getApplicationContext());
        uo = guff.getUser();
        if(uo == null)
        {
            System.out.println("USER WAS NULL");
            return;
        }
        startForeground();

        try {

            LocationManager locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {

                    System.out.println("The location changed!!" + location.getLatitude() + " "+location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                while (true) {

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        System.out.println("Last known location " + location.getLongitude() + " " + location.getLatitude());

                        updateServer();
                    }

                    Thread.sleep(5000);

                }


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread catch");
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

    }

    private void updateServer() {


        String url = ServerAddressHandler.getInstance().getAddress()+"updateUserPosition";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

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

                params.put("id",""+uo.getId());
                params.put("longitude", ""+location.getLongitude());
                params.put("latitude", ""+location.getLatitude());


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);


    }

    private void startForeground() {

        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                channel) // don't forget create a notification channel first
                .setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .build());
        System.out.println("START FOREGROUND");

    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
            System.out.println("CHANEL CREATED");
        } else {
            stopSelf();
        }
        return "snap map channel";
    }
}

