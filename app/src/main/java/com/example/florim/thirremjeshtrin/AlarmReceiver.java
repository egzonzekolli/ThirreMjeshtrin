package com.example.florim.thirremjeshtrin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gresa on 08-Dec-16.
 */
     /** When the alarm fires, this BroadcastReceiver receives the broadcast Intent
     * and then checks a certain request's status in the server if there's connectivity, or just sets another alarm to check 30 min later.
     */
    public class AlarmReceiver extends BroadcastReceiver {

        /**
         * Callback method when the alarm is fired
         * @param context the context that has set the alarm
         * @param intent the intent that was sent to the BroadcastReceiver
         */
        @Override
        public void onReceive(Context context, Intent intent) {
        /*
      The classes's ConnectivityManager, which provides information on the connectivity sections
     */
            String ID=intent.getStringExtra("ID");
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //Check if the device has connectivity,if so send request to server
            if(PermissionUtils.connectivityCheck(cm)) {
                Map<String,String> params=new HashMap<>();
                params.put("ReqID",ID);

               ConnectToServer connectToServer=new ConnectToServer();

                connectToServer.sendRequest(ConnectToServer.CHECKREQUEST,params,true);

            }
            else{
                AlarmReceiver alarmReceiver=new AlarmReceiver();
                alarmReceiver.setAlarm(context,ID,30);
            }
        }



    /**
     * Sets an alarm that runs given min after it is created. When the
     * alarm fires, the app broadcasts an Intent to this BroadcastReceiver.
     * @param context The context of the activity that sets the alarm
     */
    public void setAlarm(Context context,String ID, int min) {
        /**
         *The app's AlarmManager, which provides access to the system alarm services.
      */
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ID",ID);

            /*
      The pending intent that is triggered when the alarm fires.
     */
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,Integer.valueOf(ID), intent, 0);

        /*
         * The call below specifies the alarm type, the trigger time, the interval at
         * which the alarm is fired, and the alarm's associated PendingIntent.
         * It uses the alarm type RTC_WAKEUP ("Real Time Clock" wake up), which wakes up
         * the device and triggers the alarm according to the time of the device's clock.
         */



        // Set the alarm to fire after the given minutes, according to the device's
        // clock.
        alarmMgr.set(AlarmManager.RTC,
                System.currentTimeMillis() + min*(60*1000), alarmIntent);


    }

}

