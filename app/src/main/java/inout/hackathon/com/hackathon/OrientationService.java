package inout.hackathon.com.hackathon;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class OrientationService extends Service {
    SensorManager mSensorManager;
    SensorEventListener mSensorListener;
    Sensor mSensor;
    ArrayList<Float> x,y,z;
    Double angle[];

    public OrientationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
        angle = new Double[3];

        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x1 = sensorEvent.values[0];
                float y1 = sensorEvent.values[1];
                float z1 = sensorEvent.values[2];

                x.add(x1);
                y.add(y1);
                z.add(z1);

                if (x.size()>=3) {
                    x.remove(0);
                    y.remove(0);
                    z.remove(0);
                    calcAngle();
                }
                else if (x.size()==2)
                    calcAngle();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        mSensorManager.registerListener(mSensorListener,mSensor,2000000);



    }

    public void calcAngle() {
        angle[0] = Math.toDegrees((x.get(1)-x.get(0))*2);
        angle[1] = Math.toDegrees((y.get(1)-y.get(0))*2);
        angle[2] = Math.toDegrees((z.get(1)-z.get(0))*2);


        if ((Math.abs(angle[0])>35)) {
            stopSelf();
            Constants.DETECT.setBoo(true);

            if ((Math.abs(angle[0])>120))
                Constants.SEVERITY = "high";
            else if ((Math.abs(angle[0])>70))
                Constants.SEVERITY = "mild";
            else
                Constants.SEVERITY = "low";
        }

        if ((Math.abs(angle[2])>35)) {
            stopSelf();
            Constants.DETECT.setBoo(true);

            if ((Math.abs(angle[2])>120))
                Constants.SEVERITY = "high";
            else if ((Math.abs(angle[2])>70))
                Constants.SEVERITY = "mild";
            else
                Constants.SEVERITY = "low";
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorListener);
    }

}
