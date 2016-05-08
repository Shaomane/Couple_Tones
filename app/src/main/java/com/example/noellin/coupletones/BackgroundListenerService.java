package com.example.noellin.coupletones;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class BackgroundListenerService extends Service {

    String rel_id = null;
    String partner_email = null;

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
}
