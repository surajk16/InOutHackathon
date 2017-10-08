package inout.hackathon.com.hackathon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by suraj on 07-10-2017.
 */

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    ArrayList<Double> time;
    ArrayList<Location> loc;
    Double v1,v2;

    public GPS_Service () {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        loc = new ArrayList<>();
        time = new ArrayList<>();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc.add(location);
                time.add((double) (System.currentTimeMillis()/1000));

                if (loc.size()>=4) {
                    loc.remove(0);
                    time.remove(0);
                    calculateVelocity();
                }
                else if (loc.size()==3)
                    calculateVelocity();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,listener);
    }

    private void calculateVelocity() {
        float d1 = loc.get(0).distanceTo(loc.get(1));
        float d2 = loc.get(1).distanceTo(loc.get(2));

        v1 = d1/(time.get(1)-time.get(0));
        v2 = d2/(time.get(2)-time.get(1));

        Log.d("velocity",""+(v2-v1));

        if ((v2-v1)<-5) {
            stopSelf();
            Constants.DETECT.setBoo(true);
            Constants.LOC = loc.get(loc.size()-1);
            if ((v2-v2)<-14)
                Constants.SEVERITY = "high";
            else if ((v2-v1)<-9)
                Constants.SEVERITY = "mild";
            else Constants.SEVERITY = "low";
            Log.d("vel","Yes");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}