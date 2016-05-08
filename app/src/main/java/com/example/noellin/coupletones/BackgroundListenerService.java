package com.example.noellin.coupletones;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class BackgroundListenerService extends Service {
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

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
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
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_collections_cloud)
                .setContentTitle("CoupleTones Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(com.example.noellin.coupletones.Constants.NOTIFICATION_NR, mBuilder.build());
    }
}
