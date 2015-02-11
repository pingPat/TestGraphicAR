package com.example.testgraphic;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CameraSurfaceView";
	private SurfaceHolder holder;
	private Camera camera;
	
	public CameraSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		this.holder = this.getHolder();
		this.holder.addCallback(this);
	}

	public CameraSurfaceView(Context context, AttributeSet set) {
		super(context, set);

		// Initiate the Surface Holder properly
		this.holder = this.getHolder();
		this.holder.addCallback(this);
		this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			// Open the Camera in preview mode
			this.camera = Camera.open();

			this.camera.setPreviewDisplay(this.holder);
			// this.camera.setPreviewCallback(mPreviewCallback);

		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
        int preview_index = ImageMaxSize.maxSize(previewSize);
        int picture_index = ImageMaxSize.maxSize(pictureSize);
        params.setPictureSize(previewSize.get(preview_index).width, previewSize.get(preview_index).height);
        params.setPreviewSize(previewSize.get(picture_index).width,previewSize.get(picture_index).height);
        params.setJpegQuality(100);
        camera.setParameters(params);
        
        try {
            camera.setPreviewDisplay(this.getHolder());
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}
	
	public Camera getCamera() {
		return this.camera;
	}

	public void setScreenshot(PictureCallback rawPictureCallback,
			PictureCallback jpegPictureCallback) {
		camera.takePicture(null, rawPictureCallback, jpegPictureCallback);
	}

	Point screenSize = null;

}
