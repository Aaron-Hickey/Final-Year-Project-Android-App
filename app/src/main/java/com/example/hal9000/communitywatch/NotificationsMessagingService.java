package com.example.hal9000.communitywatch;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pusher.pushnotifications.fcm.MessagingService;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificationsMessagingService extends MessagingService {

    private File file;
    private Gson gson;
    private FileReaderObject fro;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("NotificationsService", "Got a remote message 🎉");
        Map<String, String> data = remoteMessage.getData();
        String body = data.get("body");
        String title = data.get("title");

        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        Intent intent = new Intent(this, ReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_send_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(2, builder.build());


        gson = new Gson();
        fro = new FileReaderObject();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
        Date timeOfMessage = Calendar.getInstance().getTime();
        String timeOfMessageString = DATE_FORMAT.format(timeOfMessage);

        Message message = new Message(body,title,false, timeOfMessageString);
        UserObject uo = new GetUserFromFile(getApplicationContext()).getUser();
        Type listType = new TypeToken<List<Message>>() {}.getType();

        String messagesInFile = fro.readFromFile(getApplicationContext(),""+uo.getId()+"_Message_File");
        System.out.println("MESSAGES IN FILE:" +messagesInFile);

        List<Message> messagesInFileList;
        if(!messagesInFile.equals(""))
        {
            messagesInFileList = gson.fromJson(messagesInFile, listType);

        }
        else {
            messagesInFileList = new ArrayList<Message>() ;
        }

        messagesInFileList.add(message);
        String json = gson.toJson(messagesInFileList, listType);
        fro.writeToFile(json, ""+uo.getId()+"_Message_File", getApplicationContext());
        String contentsOfMessageFile = fro.readFromFile(getApplicationContext(),""+uo.getId()+"_Message_File");
        System.out.println(contentsOfMessageFile);

    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        String name = "Message Notification Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel mChannel = new NotificationChannel("Message Notification", name, importance);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {0,500,250,500,250};
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mChannel.setVibrationPattern(v);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        mChannel.setSound(soundUri, att);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "Message Notification";
    }

}