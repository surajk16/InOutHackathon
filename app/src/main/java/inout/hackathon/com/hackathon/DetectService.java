package inout.hackathon.com.hackathon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DetectService extends Service {
    public DetectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        super.onCreate();

        Constants.DETECT.setListener(new BooVariable.ChangeListener() {
            @Override
            public void onChange() {
                Log.d("detect","detected");
                if (Constants.DETECT.isBoo()) {
                    stopSelf();
                    setAlarm();
                }
            }
        });


    }

    void setAlarm() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.d("alrm", "alrm manager created");
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        Log.d("alrm", "alrm set");
        intent = new Intent(this.getApplicationContext(), Detect.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
