package com.example.noellin.coupletones;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import android.support.v4.app.NotificationCompat;

import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class BackgroundListenerService extends Service {

    String rel_id = null;
    String partner_email = null;

    final static String notifGroup = "group_notif";
    static int id = 0;

    public BackgroundListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    final class MyThread implements Runnable
    {
        int startId;

        public MyThread(int startId)
        {
            this.startId = startId;
        }

        @Override
        public void run()
        {
            synchronized (this)
            {
                //TODO: write the listener in here
                Firebase ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id+"/notifications");
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot notification, String s) {
                        if (notification.child("sender").getValue().equals(partner_email)){
                            //notification came from partner
                            String msg = notification.child("message").getValue().toString();
                            showNotification(msg);
                            notification.getRef().setValue(null);

                        }
                    }
                    //Unused
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.d("Read failed", "Read failed in addChildEventListener");
                    }
                });

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Bundle extras = intent.getExtras();
        if (extras != null){
            rel_id = extras.getString("rel_id");
            partner_email = extras.getString("partner_email");
        }

        Toast.makeText(BackgroundListenerService.this, "Able to receive messages", Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new MyThread(startId));
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        //Toast.makeText(BackgroundListenerService.this, "Cooldown period over", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void showNotification(String msg) {
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        notificationIntent.setAction(com.example.noellin.coupletones.Constants.NOTIFICATION_ACTION);
        notificationIntent.putExtra(com.example.noellin.coupletones.Constants.KEY_MESSAGE_TXT, msg);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_collections_cloud)
                .setContentTitle("CoupleTones Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setGroup(notifGroup)
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        //mNotificationManager.notify(com.example.noellin.coupletones.Constants.NOTIFICATION_NR, mBuilder.build());
        mNotificationManager.notify(id, mBuilder.build());
        id++;

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
