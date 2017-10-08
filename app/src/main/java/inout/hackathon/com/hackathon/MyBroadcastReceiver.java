package inout.hackathon.com.hackathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by suraj on 08-10-2017.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Constants.MP = MediaPlayer.create(context, R.raw.alrm);
        Constants.MP.start();
    }
}
