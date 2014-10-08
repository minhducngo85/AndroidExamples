package com.example.ch11compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class MyActivity extends Activity {

    /** acceleration value */
    private float[] aValues = new float[3];

    /** the magnetic values */
    private float[] mValues = new float[3];

    /** The compass view */
    private CompassView compassView;

    /** The sensor manager */
    private SensorManager sensorManger;

    /** The rotation angle */
    private int rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        compassView = (CompassView) findViewById(R.id.compassView);
        sensorManger = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // window manager
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        rotation = display.getRotation();

        updateOrientation(new float[] { 0, 0, 0 });
    }

    /**
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magField = sensorManger.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManger.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManger.registerListener(sensorEventListener, magField, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        sensorManger.unregisterListener(sensorEventListener);
        super.onPause();
    }

    /**
     * to calculate the orientation based on the sensor values
     * 
     * @return {bearing, pitch, roll}
     */
    private float[] calculateOrientation() {
        float[] values = new float[3];

        /** rotation */
        float[] inR = new float[9];
        float[] outR = new float[9];
        SensorManager.getRotationMatrix(inR, null, aValues, mValues);

        // remap the coordinates based on the natural device orientation
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;

        switch (rotation) {
        case (Surface.ROTATION_90):
            x_axis = SensorManager.AXIS_Y;
            y_axis = SensorManager.AXIS_MINUS_X;
            break;
        case (Surface.ROTATION_180):
            y_axis = SensorManager.AXIS_MINUS_Y;
            break;
        case (Surface.ROTATION_270):
            x_axis = SensorManager.AXIS_MINUS_Y;
            y_axis = SensorManager.AXIS_X;
        default:
            break;
        }
        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);

        // obtain the current, corrected value
        SensorManager.getOrientation(outR, values);
        // convert radian to degree
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        return values;
    }

    /**
     * to update compass view
     * 
     * @param values
     *            orientation values
     */
    private void updateOrientation(float[] values) {
        if (compassView != null) {
            compassView.setBearing(values[0]);
            compassView.setPitch(values[1]);
            compassView.setRoll(-values[2]);
            compassView.invalidate();
        }
    }

    /**
     * The sensor event listener
     */
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                aValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mValues = event.values;
            }
            updateOrientation(calculateOrientation());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
