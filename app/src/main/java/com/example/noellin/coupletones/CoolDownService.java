package com.example.noellin.coupletones;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/*
 * Service that keeps the user from sending their partner too many notifications. Cool down
 * is set to five seconds currently for demo purposes, but would normally be about 10 minutes
 */
public class CoolDownService extends Service {
    public CoolDownService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * Starts a separate thread to run a wait, which will indicate to the main activities that
     * notifications should not be sent
     */
    final class MyThread implements Runnable
    {
        int startId;

        public MyThread(int startId)
        {
            this.startId = startId;
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
                    wait(5000);
                } catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                stopSelf(startId);
            }
        }
    }

    /*
     * Toast the user to indicate that the cool down period is beginning. Mainly useful for demoing purposes.
     * Starts the separate thread that begins the timer
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(CoolDownService.this, "Starting cooldown period", Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new MyThread(startId));
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     * Toasts the user that the cool down period has ended, for demoing purposes. call super.onDestroy
     */
    @Override
    public void onDestroy()
    {
        Toast.makeText(CoolDownService.this, "Cooldown period over", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
