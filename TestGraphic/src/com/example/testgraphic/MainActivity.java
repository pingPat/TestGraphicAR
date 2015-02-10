package com.example.testgraphic;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		SensorEventListener , SurfaceHolder.Callback{

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_main); }
	 */

	private static final String TAG = "Compass";
	private static boolean DEBUG = false;
	Float azimut; // View to draw a compass
	Camera mCamera;
    SurfaceView mPreview;
    private DrawSurfaceView mDrawView;

    LocationClient mLocationClient;
    //TextView textView1;
    //double lat,lng;
	private SensorManager mSensorManager;
	Sensor accelerometer;
	Sensor magnetometer;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main); // Register the sensor listeners
		
		//textView1 = (TextView) findViewById(R.id.textView1);
		mDrawView = (DrawSurfaceView)findViewById(R.id.drawSurfaceView);

		mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		boolean result = isServicesAvailable();        
        if(result) {
            // �����ͧ�� Google Play Services
        	mLocationClient = new LocationClient(this, mCallback, mListener);
        } else {
            // �����ͧ����� Google Play Services �Դ�������
        	finish();
        }

	}

	protected void onStart(){
    	super.onStart();
    	mLocationClient.connect();
    }
    
    protected void onStop(){
    	super.onStop();
    	mLocationClient.disconnect();
    }
    
    private ConnectionCallbacks mCallback = new ConnectionCallbacks() {
        public void onConnected(Bundle bundle) {
            // �������͡Ѻ Google Play Services ��
        	Toast.makeText(MainActivity.this, "Services connected", Toast.LENGTH_SHORT).show();

            LocationRequest mRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000).setFastestInterval(1000);

            mLocationClient.requestLocationUpdates(mRequest, locationListener);
        }
        public void onDisconnected() {
            // ��ش�������͡Ѻ Google Play Services
        	Toast.makeText(MainActivity.this, "Services disconnected", Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnConnectionFailedListener mListener = new OnConnectionFailedListener() {
        public void onConnectionFailed(ConnectionResult result) {
            // ������Դ�ѭ���������͡Ѻ Google Play Services �����
        	Toast.makeText(MainActivity.this, "Services connection failed", Toast.LENGTH_SHORT).show();
        }
    };
    
    private boolean isServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return (resultCode == ConnectionResult.SUCCESS);
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
		/*if (mGravity != null && mGeomagnetic != null) {
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
		}*/
		if (DEBUG)
			Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + ")");
		
		if (mDrawView != null) {
            mDrawView.setOffset(event.values[0]);
            mDrawView.invalidate();
        }
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        	LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());
        	
        	Log.d(TAG, "Location Changed");
			mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
			mDrawView.invalidate();
        	
            /*textView1.setText("Provider : " + location.getProvider() + "\n" 
                    + "Latitude : " + location.getLatitude() + "\n" 
                    + "Longitude : " + location.getLongitude() + "\n" 
                    + "Accuracy : " + location.getAccuracy() + "m\n" 
                    + "Speed : " + location.getSpeed() + "m/s\n"
                    + "Bearing : " + location.getBearing() + "\n");
                    //+ "Satellite : " + location.getExtras().getInt("satellites"));*/
        }
    };
}
