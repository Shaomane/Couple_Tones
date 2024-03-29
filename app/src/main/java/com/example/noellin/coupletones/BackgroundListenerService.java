package com.example.noellin.coupletones;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import android.os.Vibrator;
import android.util.Log;

import android.support.v4.app.NotificationCompat;

import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


/*
 * Background service that listens for new messages from the Firebase database. Will show a
 * notification and a sound if the user receives a notification.
 */
public class BackgroundListenerService extends Service {

    String rel_id = null;
    String partner_email = null;
    String partner_name = null;

    final static String notifGroup = "group_notif";
    static int id = 0;
    public Thread thread;
    public Firebase ref;
    public ChildEventListener listener;
    long arrivalVibe [] = {0, 200, 800, 200, 800, 200};
    long leavingVibe [] = {0, 800, 200, 800, 200, 800};

    long customVibes[][];
    Uri customTones[] = new Uri[10];

    private LocationController locationController;

    public BackgroundListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * Separate thread to run the Firebase listener.
     */
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
                //Listener that will check if any new messages have been received
                ref = new Firebase("https://dazzling-inferno-7112.firebaseio.com/relationships/"+rel_id+"/notifications");
                listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot notification, String s) {
                        if (notification.child("sender").getValue().equals(partner_email)){
                            //notification came from partner
                            String msg = notification.child("message").getValue().toString();
                            showNotification(msg, startId);
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
                };
                ref.addChildEventListener(listener);

            }
        }
    }

    final class NotificationThread implements Runnable
    {
        int startId;
        Uri uri;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibeTone;
        int locVibeTone;
        int locSoundTone;
        boolean sound;
        boolean vibe;
        String currVibeTone = "";

        public NotificationThread(int startId, long[] vibeTone, Uri uri, String location)
        {
            this.startId = startId;
            this.vibeTone = vibeTone;
            this.uri = uri;
            System.err.println("NOTIFICATION THREAD CONSTRUCTOR");
            String currTone = locationController.getSoundTone(location);

            if (currTone.equals("")) System.err.println("NULL NULL NULL");
            System.err.println(currTone);

            String currToneNumberString = currTone.substring(9);
            locSoundTone = Integer.parseInt(currToneNumberString);

            currVibeTone = locationController.getVibeTone(location);
            String currVibeToneNumberString = currVibeTone.substring(8);
            locVibeTone = Integer.parseInt(currVibeToneNumberString);

            SharedPreferences sharedPreferences = getSharedPreferences("user_settings", MODE_PRIVATE);
            String soundSetting = sharedPreferences.getString("sound", "");
            String vibeSetting = sharedPreferences.getString("vibe", "");

            if (soundSetting.equals("true")) sound = true;
            if (vibeSetting.equals("true")) vibe = true;
        }

        /*
         * Run the 5 second wait
         */
        @Override
        public void run()
        {
            synchronized (this)
            {
                try
                {
                    Ringtone r;
                    if (vibe)
                    {
                        v.vibrate(vibeTone, -1);
                        Log.d("vibration confirmation", "Phone is currently vibrating");
                    }
                    if (sound)
                    {
                        r = RingtoneManager.getRingtone(getApplicationContext(), uri);
                        r.play();
                    }
                    wait(3000);
                    if (vibe)
                    {
                        v.vibrate(customVibes[locVibeTone], -1);
                        Log.d("vibration confirmation", "Phone is currently vibrating with " + currVibeTone);
                    }
                    if (sound)
                    {
                        r = RingtoneManager.getRingtone(getApplicationContext(), customTones[locSoundTone]);
                        r.play();
                    }
                    wait(3000);
                } catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                stopSelf(startId);
            }
        }
    }

    /*
     * Starts a separate thread and toasts the user to let them know that their app is receiving messages
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent == null){
            Log.d("onStartCommand","onStartCommand intent is null");
            return -1;
        }
        setupVibetones();
        setupTones();
        Bundle extras = intent.getExtras();
        if (extras != null){
            rel_id = extras.getString("rel_id");
            partner_email = extras.getString("partner_email");
            partner_name = extras.getString("partner_name");
        }
        locationController = new LocationController(rel_id, partner_name);

        //Toast.makeText(BackgroundListenerService.this, "Able to receive messages", Toast.LENGTH_SHORT).show();
        System.err.println("ABLE TO RECEIVE MESSAGES");
        thread = new Thread(new MyThread(startId));
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     * Lets the user know that the service has been destroyed. Removes the listener and calls super.onDestroy
     */
    @Override
    public void onDestroy()
    {
        //Toast.makeText(BackgroundListenerService.this, "Not able to receive messages", Toast.LENGTH_SHORT).show();
        //thread.interrupt();
        if (ref != null && listener != null) ref.removeEventListener(listener);
        super.onDestroy();
    }

    /*
     * Shows a notification to the user, with sound
     */
    private void showNotification(String msg, int startId) {
        //try{Thread.sleep(2000);}catch(InterruptedException e){}
        Uri uri;
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Thread notifThread;

        String location = "";
        String msgArray[] = msg.split(": ");
        System.err.println(msgArray[1]);
        location = msgArray[1];
        if (location.equals("CSE Building")) System.err.println("SAME STRING");

        // when partner arrives at a location
        if (msg.contains("visited"))
        {
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.arpeggio);

            // TODO add check for vibe mode
            //v.vibrate (arrivalVibe, -1);
            System.err.println ("Arrival vibe tone!!!");

            notifThread = new Thread(new NotificationThread(29, arrivalVibe, uri, location));
            notifThread.start();

        }
        // when partner leaves location
        else
        {
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.droplet);

            //TODO add check for vibrate mode
            //v.vibrate (leavingVibe, -1);
            System.err.println ("Departure vibe tone!!!");

            notifThread = new Thread(new NotificationThread(13, leavingVibe, uri, location));
            notifThread.start();

        }
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
                //.setSound(uri);

        mBuilder.setContentIntent(contentIntent);
        //mNotificationManager.notify(com.example.noellin.coupletones.Constants.NOTIFICATION_NR, mBuilder.build());
        mNotificationManager.notify(id, mBuilder.build());
        id++;
    }

    /*
     * Vibetones are accessed by calling customVibes[5] for vibeTone5 etc
     */
    private void setupVibetones()
    {
        ToneContainer t = new ToneContainer(getApplicationContext());
        customVibes = t.getVibeTones();
    }

    private void setupTones()
    {
        ToneContainer t = new ToneContainer(getApplicationContext());
        customTones = t.getTones();
    }

    /*
     * Tests whether a background service is running. Is useful for determining whether to start
     * the messaging listener
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
