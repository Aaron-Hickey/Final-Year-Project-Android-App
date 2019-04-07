package com.example.hal9000.communitywatch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


//import io.ably.lib.realtime.AblyRealtime;
//import io.ably.lib.realtime.Channel;
//import io.ably.lib.types.AblyException;
//import io.ably.lib.types.ClientOptions;
//import io.ably.lib.types.Message;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
//import com.pusher.client.Pusher;
//import com.pusher.client.PusherOptions;
//import com.pusher.client.channel.Channel;
//import com.pusher.client.channel.SubscriptionEventListener;

import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText messageDescription;
    private ListView messagesView;
    private UserObject uo;
    MessageAdapter messageAdapter = new MessageAdapter(this);
    private Gson gson;
    private FileReaderObject fro;
    private String fileName = "Logged_In_User";
    private GoogleMap mMap;
    private final int Communication_Radius = 10000; //metres
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        displayPromptForEnablingGPS(this);
        //      intent1.putExtra("User",uo);
        Log.i("Test", "Report on create");
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the camera
                        Log.i("Dexter", "PERMISSION GRANTED");
                        Intent intent1 = new Intent(getApplicationContext(), LocationUpdaterService.class);
                        startService(intent1);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        Log.i("Dexter", "PERMISSION NOT GRANTED");

                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        messageDescription = findViewById(R.id.MessageDescription);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        messageAdapter.getStoredData();
        messagesView.setSelection(messagesView.getCount() - 1);


        GetUserFromFile guff = new GetUserFromFile(getApplicationContext());
        uo = guff.getUser();
        if(uo == null)
        {
            return;
        }

        final ImageButton button2 = findViewById(R.id.btnSend);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendMessage();
            }
        });

       // uo = (UserObject) getIntent().getSerializableExtra("User");

   /*     try {

            initAbly();

        } catch (AblyException e) {

            e.printStackTrace();

        }*/

       // initPusher();
        newPusher();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void SendMessage(){
        final String messageDescriptionValue = messageDescription.getText().toString();
        messageDescription.setText("");



        String url = ServerAddressHandler.getInstance().getAddress()+"Report";

 /*       RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response,
                                Toast.LENGTH_LONG).show();

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

                params.put("message", messageDescriptionValue);
                params.put("id", ""+uo.getId());


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);
*/
        RequestQueue rq = Volley.newRequestQueue(this.getApplicationContext());


        JSONObject params = new JSONObject();
        try {
            params.put("message", messageDescriptionValue);
            params.put("id", ""+uo.getId());
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
                        Toast.makeText(getApplicationContext(), response.toString(),
                                Toast.LENGTH_LONG).show();


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

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
        Date timeOfMessage = Calendar.getInstance().getTime();
        String timeOfMessageString = DATE_FORMAT.format(timeOfMessage);

        final Message message = new Message(messageDescriptionValue, uo.getFirstName(), true, timeOfMessageString);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Message> messages;

                gson = new Gson();
                fro = new FileReaderObject();

                UserObject uo = new GetUserFromFile(getApplicationContext()).getUser();
                Type listType = new TypeToken<List<Message>>() {}.getType();

                String messagesInFile = fro.readFromFile(getApplicationContext(),""+uo.getId()+"_Message_File");

                if(!messagesInFile.equals(""))
                {
                    messages = gson.fromJson(messagesInFile, listType);

                }
                else {
                    messages = new ArrayList<Message>() ;

                }

                messages.add(message);

                String json = gson.toJson(messages, listType);
                fro.writeToFile(json, ""+uo.getId()+"_Message_File", getApplicationContext());


                messageAdapter.add(message);
                System.out.println(message.getText() + " "+message.getName());
                // scroll the ListView to the last added element
                messagesView.setSelection(messagesView.getCount() - 1);
            }
        });

    }


  /*  private void initAbly() throws AblyException {

        ClientOptions options = new ClientOptions("ZZshjw.lg8kkg:AIG6phM4gldBrsxv");
        AblyRealtime realtime = new AblyRealtime(options);

        Channel channel = realtime.channels.get("channel1");
        System.out.println("initAbly");

        channel.subscribe(new Channel.MessageListener() {

            @Override

            public void onMessage(Message messages) {


                System.out.println("Messaaage"+ messages.data);

            }

        });


        channel.publish("greeting", "hello!");

    }*/


  /*  public void initPusher()
    {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("bec6b057b12a49314680", options);

        Channel channel = pusher.subscribe(""+uo.getId());

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName,  String data) {
                System.out.println("PUSHER: "+data);

                Gson gson = new Gson();

                //The JSON gets messed up by Pusher, this fixes it
                data = data.substring(1,data.length()-1);
                String finalData ="";
                for(int x = 0; x < data.length(); x++)
                {
                    if(data.charAt(x) == '\\')
                    {
                        //DO NOTHING
                    }
                    else
                    {
                        finalData += data.charAt(x);
                    }
                }

                System.out.println("data substring "+finalData);

                Message messageReceived = gson.fromJson(finalData, Message.class);

                System.out.println("MESSAGE "+messageReceived.getText() + " "+messageReceived.getName());
                final Message message = new Message(messageReceived.getText(), messageReceived.getName(), messageReceived.isBelongsToCurrentUser());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.add(message);
                        System.out.println(message.getText() + " "+message.getName());
                        // scroll the ListView to the last added element
                        messagesView.setSelection(messagesView.getCount() - 1);

                    }
                });
            }
        });

        pusher.connect();
    }*/

   public void newPusher()
   {
       PushNotifications.start(getApplicationContext(), "a1e05091-6b9b-46b6-b848-69b980dca55e");
       PushNotifications.subscribe(""+uo.getId());
   }

    @Override
    protected void onResume() {
        super.onResume();
        messageAdapter.getStoredData();
        PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                String messagePayload = remoteMessage.getData().get("body");
               // String test = remoteMessage.getData().toString();

                Map<String, String> data = remoteMessage.getData();
                Gson gson = new Gson();
                String json = gson.toJson(data);
                System.out.println("THE JSON: "+json);
                String body = data.get("body");
                String title = data.get("title");
             //   String openPrice = data.get("openPrice");
             //   String currencyPair = data.get("currencyPair");

           //     Log.i("MyActivity", test);
                System.out.println("BODY: "+body);
                System.out.println("TITLE: "+title);
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
                Date timeOfMessage = Calendar.getInstance().getTime();
                String timeOfMessageString = DATE_FORMAT.format(timeOfMessage);

                final Message message = new Message(body, title, false, timeOfMessageString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.add(message);
                        System.out.println(message.getText() + " "+message.getName());
                        // scroll the ListView to the last added element
                        messagesView.setSelection(messagesView.getCount() - 1);

                    }
                });
                if (messagePayload == null) {
                    // Message payload was not set for this notification
                    Log.i("MyActivity", "Payload was missing");
                } else {
                    Log.i("MyActivity", messagePayload);
                    // Now update the UI based on your message payload!

                }
            }
        });
    }

    public  void displayPromptForEnablingGPS(
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

                                }
                            })
                    .setNegativeButton("Quit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                    finish();
                                }
                            });
            builder.create().show();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a center in Sydney and move the camera
    //    MapUpdater mu = new MapUpdater(uo,mMap);
     //   mu.start();
        mMap = googleMap;
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                // UI code goes here
            //    while (true) {


               //     while (true)
                //    {


                    //}
             //   }
            }
        });
        LatLng center = new LatLng(uo.getLatitude(), uo.getLongitude());
        circle = mMap.addCircle(new CircleOptions()
                .center(center).strokeWidth(0)
                .radius(Communication_Radius)
                .fillColor(Color.GRAY))
        ;

        // mMap.addMarker(new MarkerOptions().position(center).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mMap.setMaxZoomPreference(10);
        mMap.setMinZoomPreference(10);
        new Thread(new Runnable() {
            public void run(){


                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LatLng newCenter = new LatLng(uo.getLatitude(), uo.getLongitude());
                         //   LatLng newCenter = new LatLng(getRandomNumberInRange(0,90), getRandomNumberInRange(0,180)); //Randomly moves the center for testing
                            circle.setCenter(newCenter);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newCenter));
                            System.out.println("Moving circle center to "+newCenter.latitude+" "+newCenter.longitude);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /*
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
*/

}

