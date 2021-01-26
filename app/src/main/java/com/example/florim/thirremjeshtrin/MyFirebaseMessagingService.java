package com.example.florim.thirremjeshtrin;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Gresa on 29-Nov-16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        String TAG = "FirebaseMsgService";
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG,"ReqID: "+remoteMessage.getData().get("ID"));
        sendNotification(remoteMessage);

    }
    private void sendNotification(RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        String from = remoteMessage.getData().get("From");
        String ID = remoteMessage.getData().get("ID");
        Intent intent;
        PendingIntent pIntent = null;
        if (type != null) {
            if (type.equals("request")) {
                intent = new Intent(this, Request.class);
                intent.putExtra("from", from);
                intent.putExtra("ID", ID);
                intent.putExtra("Message", remoteMessage.getNotification().getBody());
                pIntent = PendingIntent.getActivity(this, Integer.valueOf(ID), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            } else if (type.equals("message")) {
                newMessageToActivity();
                intent=new Intent(this,UserList.class);
                intent.setAction(getString(R.string.Inbox_action));
                pIntent=PendingIntent.getActivity(this,3,intent,PendingIntent.FLAG_CANCEL_CURRENT);
            } else if (type.equals("answer")) {

                String status = remoteMessage.getData().get("status");
                if (status.equals("ACCEPTED")) {
                    intent = new Intent(this, UserInfo.class);
                    intent.setAction(getString(R.string.Profile_action));
                    String Username = remoteMessage.getData().get("Username");
                    String Email = remoteMessage.getData().get("Email");
                    String UserID = remoteMessage.getData().get("ID");
                    String Phone = remoteMessage.getData().get("Phone");
                    String Lat = remoteMessage.getData().get("Lat");
                    String Lon = remoteMessage.getData().get("Lon");
                    String Radius = remoteMessage.getData().get("Radius");
                    String Category = remoteMessage.getData().get("Category");
                    Log.d("Firebase IM: ",Lat+" "+Lon);
                    intent.putExtra("RepairmanID", UserID);
                    intent.putExtra("Username", Username);
                    intent.putExtra("Lat", Lat);
                    intent.putExtra("Lon", Lon);
                    intent.putExtra("Radius", Radius);
                    intent.putExtra("Phone", Phone);
                    intent.putExtra("Email", Email);
                    intent.putExtra("Category", Category);
                    pIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                }
            }
            // build notification
            // the addAction re-use the same intent to keep the example short
            Notification n = new Notification.Builder(this)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.ic_wrench_png_19773)
                    .setContentIntent(pIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
    }

    private void newMessageToActivity() {
        Intent intent = new Intent("newMessage");
        sendNewMessageBroadcast(intent);
    }

    private void sendNewMessageBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

