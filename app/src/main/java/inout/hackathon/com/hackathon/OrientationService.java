package inout.hackathon.com.hackathon;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class OrientationService extends Service {
    SensorManager mSensorManager;
    SensorEventListener mSensorListener;
    float azimuth,roll,pitch;

    public OrientationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mSensorListener = new SensorEventListener() {
            float[] mGravity;
            float[] mGeomagnetic;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    mGravity = event.values;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    mGeomagnetic = event.values;
                if (mGravity != null && mGeomagnetic != null) {
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                    if (success) {
                        float orientationData[] = new float[3];
                        SensorManager.getOrientation(R, orientationData);

                        azimuth = (float) Math.toDegrees(orientationData[0]);
                        pitch = (float) Math.toDegrees(orientationData[1]);
                        roll = (float) Math.toDegrees(orientationData[2]);

                        //Log.d("angle","azi:"+azimuth+" pitch:"+pitch+" roll:"+roll);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };
        mSensorManager.registerListener(mSensorListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
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
