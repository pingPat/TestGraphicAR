package com.example.testgraphic;

import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements
		SensorEventListener , SurfaceHolder.Callback{

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_main); }
	 */

	Float azimut; // View to draw a compass
	Camera mCamera;
    SurfaceView mPreview;

	private SensorManager mSensorManager;
	Sensor accelerometer;
	Sensor magnetometer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main); // Register the sensor listeners

		mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, magnetometer,
				SensorManager.SENSOR_DELAY_UI);
		Log.d("System","onResume");
		mCamera = Camera.open();
	}

	protected void onPause() {		
		super.onPause();
		mSensorManager.unregisterListener(this);
		Log.d("System","onPause");
		mCamera.release();
	}

	public void surfaceChanged(SurfaceHolder arg0
            , int arg1, int arg2, int arg3) {
        Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
        int preview_index = ImageMaxSize.maxSize(previewSize);
        int picture_index = ImageMaxSize.maxSize(pictureSize);
        params.setPictureSize(previewSize.get(preview_index).width, previewSize.get(preview_index).height);
        params.setPreviewSize(previewSize.get(picture_index).width,previewSize.get(picture_index).height);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d("CameraSystem","surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) { }
	
	float[] mGravity;
	float[] mGeomagnetic;

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
					mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut = orientation[0]; // orientation contains: azimut, pitch
											// and roll
				Log.e("azi", "" + azimut * 360 / (2 * 3.14159f));

				float azimuthInDegress = (float) Math.toDegrees(azimut);
				if (azimuthInDegress < 0.0f) {
					azimuthInDegress += 360.0f;
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
