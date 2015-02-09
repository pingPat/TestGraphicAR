package com.example.testgraphic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class PhoneOrientation {
	
	private SensorManager sensorMan;
	private Sensor sensorAcce;
	private Sensor sensorMagn;
	private SensorEventListener listener;
	private float matrix[]=new float[16];

	
	public PhoneOrientation() {
	}
	
	public void start(Context context) {
        listener = new SensorEventListener() {
        	private float orientation[]=new float[3];
        	private float acceleration[]=new float[3];

        	public void onAccuracyChanged(Sensor arg0, int arg1){}

        	public void onSensorChanged(SensorEvent evt) {
        		int type=evt.sensor.getType();
        		
        		//Smoothing the sensor data a bit seems like a good idea.
        		if (type == Sensor.TYPE_MAGNETIC_FIELD) {
        			orientation[0]=(orientation[0]*1+evt.values[0])*0.5f;
        			orientation[1]=(orientation[1]*1+evt.values[1])*0.5f;
        			orientation[2]=(orientation[2]*1+evt.values[2])*0.5f;
        		} else if (type == Sensor.TYPE_ACCELEROMETER) {
       				acceleration[0]=(acceleration[0]*2+evt.values[0])*0.33334f;
       				acceleration[1]=(acceleration[1]*2+evt.values[1])*0.33334f;
       				acceleration[2]=(acceleration[2]*2+evt.values[2])*0.33334f;
        		}
        		Log.e("SensorLayer", "Sensor Change");
        	}
        };

        sensorMan = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensorAcce = sensorMan.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		sensorMagn = sensorMan.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
		
		sensorMan.registerListener(listener, sensorAcce, SensorManager.SENSOR_DELAY_FASTEST);
		sensorMan.registerListener(listener, sensorMagn, SensorManager.SENSOR_DELAY_FASTEST);		
	}
	
	public float[] getMatrix() {
		return matrix;
	}

	public void finish() {
		sensorMan.unregisterListener(listener);
	}


}
