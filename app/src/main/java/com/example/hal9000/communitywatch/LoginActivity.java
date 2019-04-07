package com.example.hal9000.communitywatch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private FileOutputStream outputStream;

    private final String fileName = "Logged_In_User";

    private Gson gson;

    private FileReaderObject fro;

    private UserObject uo;

    boolean gpsNotifyResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        fro = new FileReaderObject();
        gpsNotifyResult = displayPromptForEnablingGPS(LoginActivity.this);

            GetUserFromFile guff = new GetUserFromFile(getApplicationContext());
            uo = guff.getUser();
            if(uo != null)
            {
                Toast.makeText(getApplicationContext(), "Welcome Back "+uo.getFirstName(),
                        Toast.LENGTH_LONG).show();
                continueToNextActivity();
            }







        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        final Button button1 = findViewById(R.id.btnLogin);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login();
            }
        });

        final Button button2 = findViewById(R.id.btnLinkToRegisterScreen);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void Login(){

        /*
        final String emailValue = email.getText().toString();
        final String passwordValue = password.getText().toString();

        String url = ServerAddressHandler.getInstance().getAddress()+"loginUser";

        RequestQueue queue = Volley.newRequestQueue(this);
        Toast.makeText(getApplicationContext(), url,
                Toast.LENGTH_LONG).show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {


                        if(response.charAt(0) == '!')
                        {
                            System.out.println("An error occured");

                            Toast.makeText(getApplicationContext(), response,
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            uo = gson.fromJson(response, UserObject.class);
                            System.out.println(uo.getFirstName());
                            Toast.makeText(getApplicationContext(), "Welcome "+uo.getFirstName(),
                                    Toast.LENGTH_LONG).show();

                            //Save login details to file
                            fro.writeToFile(response,fileName,getApplicationContext());

                           continueToNextActivity();
                          finish();

                        }
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

                params.put("email", emailValue);
                params.put("password", passwordValue);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(postRequest);

            */

        String url = ServerAddressHandler.getInstance().getAddress()+"Login";

        //final ProgressDialog pDialog = new ProgressDialog(this.getActivity());
        //pDialog.setMessage("Loading...");
        //pDialog.show();
        RequestQueue rq = Volley.newRequestQueue(this.getApplicationContext());
        final String emailValue = email.getText().toString();
        final String passwordValue = password.getText().toString();

        JSONObject params = new JSONObject();
        try {
            params.put("email", emailValue);
            params.put("password", passwordValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), url,
                Toast.LENGTH_LONG).show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, params, //Not null.
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d(TAG, response.toString());
                        // pDialog.hide();
                            String responseString = response.toString();
                            uo = gson.fromJson(responseString, UserObject.class);
                            System.out.println(uo.getFirstName());
                            Toast.makeText(getApplicationContext(), "Welcome "+uo.getFirstName(),
                                    Toast.LENGTH_LONG).show();

                            //Save login details to file
                            fro.writeToFile(responseString,fileName,getApplicationContext());

                            continueToNextActivity();
                            finish();


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // VolleyLog.d(TAG, "Error: " + error.getMessage());
                //pDialog.hide();
            }
        });

// Adding request to request queue
        rq.add(jsonObjReq);

    }

    public  boolean displayPromptForEnablingGPS(
            final Activity activity)
    {
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(GpsStatus == false)
        {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(activity);
            final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            final String message = "This app requires the use of your location. Please enable it in your settings.";

            builder.setMessage(message)
                    .setPositiveButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    activity.startActivity(new Intent(action));
                                    d.dismiss();
                                    gpsNotifyResult = true;

                                }
                            })
                    .setNegativeButton("Quit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                    finish();
                                    gpsNotifyResult = false;
                                }
                            });
            builder.create().show();
        }
        return gpsNotifyResult;
    }

    public void continueToNextActivity()
    {



        Intent intent2 = new Intent(getApplicationContext(), ReportActivity.class);
        //     intent2.putExtra("User", uo);

        startActivity(intent2);
    }
}
