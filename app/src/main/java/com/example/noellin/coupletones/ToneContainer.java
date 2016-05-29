package com.example.noellin.coupletones;

import android.content.Context;
import android.net.Uri;

/**
 * Created by Andrew on 5/28/2016.
 */
public class ToneContainer {

    Context context;

    public ToneContainer(Context context)
    {
        this.context = context;
    }

    public long[][] getVibeTones()
    {
        long customVibes[][] = new long[10][];
        long[] currVibe = {0, 1000};
        customVibes[0] = currVibe;
        currVibe = new long[]{0, 1000, 500, 1000};
        customVibes[1] = currVibe;
        currVibe = new long[]{0, 500, 200, 500, 200, 500};
        customVibes[2] = currVibe;
        currVibe = new long[]{0, 200, 200, 500};
        customVibes[3] = currVibe;
        currVibe = new long[]{0, 500, 500, 500, 500, 500 ,500, 500};
        customVibes[4] = currVibe;
        currVibe = new long[]{0, 500, 500, 500, 500, 500 ,500, 500, 500, 500, 500, 500};
        customVibes[5] = currVibe;
        currVibe = new long[]{0, 3000};
        customVibes[6] = currVibe;
        currVibe = new long[]{0, 500};
        customVibes[7] = currVibe;
        currVibe = new long[]{0, 300, 500, 1000, 500, 300};
        customVibes[8] = currVibe;
        currVibe = new long[]{0, 300, 300, 300, 300, 1000, 300, 300, 300, 300};
        customVibes[9] = currVibe;

        return customVibes;
    }

    public Uri[] getTones()
    {
        Uri customTones[] = new Uri[10];
        Uri currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep1);
        customTones[0] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep10);
        customTones[1] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep19);
        customTones[2] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep21);
        customTones[3] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep50);
        customTones[4] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep39);
        customTones[5] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep40);
        customTones[6] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep41);
        customTones[7] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep46);
        customTones[8] = currUri;
        currUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep49);
        customTones[9] = currUri;

        return customTones;
    }
}
