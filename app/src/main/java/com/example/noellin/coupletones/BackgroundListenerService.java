package com.example.noellin.coupletones;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
}
