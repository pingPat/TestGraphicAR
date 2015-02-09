package com.example.testgraphic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class GLLayer extends GLSurfaceView {

	private Context context;
	private PhoneOrientation phoneOri = new PhoneOrientation();
	private float[] mViewMatrix = new float[16];
	private FloatBuffer cubeBuff;
	private FloatBuffer texBuff;

	public GLLayer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;

		phoneOri.start(context);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Position the eye behind the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 4.2f, 0, 0, 0, 0, 1, 0);
		
		float floatMat[]=phoneOri.getMatrix();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadMatrixf(floatMat, 0);
		
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 4.2f, 0, 0, 0, 0, 1, 0);		
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0, 0, 0, 0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		cubeBuff = makeFloatBuffer(camObjCoord);
		texBuff = makeFloatBuffer(camTexCoords);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	
	final static float camObjCoord[] = new float[] {
		// FRONT
		 -2.0f, -1.5f,  2.0f,
		  2.0f, -1.5f,  2.0f,
		 -2.0f,  1.5f,  2.0f,
		  2.0f,  1.5f,  2.0f,
		 // BACK
		 -2.0f, -1.5f, -2.0f,
		 -2.0f,  1.5f, -2.0f,
		  2.0f, -1.5f, -2.0f,
		  2.0f,  1.5f, -2.0f,
		 // LEFT
		 -2.0f, -1.5f,  2.0f,
		 -2.0f,  1.5f,  2.0f,
		 -2.0f, -1.5f, -2.0f,
		 -2.0f,  1.5f, -2.0f,
		 // RIGHT
		  2.0f, -1.5f, -2.0f,
		  2.0f,  1.5f, -2.0f,
		  2.0f, -1.5f,  2.0f,
		  2.0f,  1.5f,  2.0f,
		 // TOP
		 -2.0f,  1.5f,  2.0f,
		  2.0f,  1.5f,  2.0f,
		 -2.0f,  1.5f, -2.0f,
		  2.0f,  1.5f, -2.0f,
		 // BOTTOM
		 -2.0f, -1.5f,  2.0f,
		 -2.0f, -1.5f, -2.0f,
		  2.0f, -1.5f,  2.0f,
		  2.0f, -1.5f, -2.0f,
	};
	final static float camTexCoords[] = new float[] {
		// Camera preview
		 0.0f, 0.0f,
		 0.9375f, 0.0f,
		 0.0f, 0.625f,
		 0.9375f, 0.625f,

		// BACK
		 0.9375f, 0.0f,
		 0.9375f, 0.625f,
		 0.0f, 0.0f,
		 0.0f, 0.625f,
		// LEFT
		 0.9375f, 0.0f,
		 0.9375f, 0.625f,
		 0.0f, 0.0f,
		 0.0f, 0.625f,
		// RIGHT
		 0.9375f, 0.0f,
		 0.9375f, 0.625f,
		 0.0f, 0.0f,
		 0.0f, 0.625f,
		// TOP
		 0.0f, 0.0f,
		 0.9375f, 0.0f,
		 0.0f, 0.625f,
		 0.9375f, 0.625f,
		// BOTTOM
		 0.9375f, 0.0f,
		 0.9375f, 0.625f,
		 0.0f, 0.0f,
		 0.0f, 0.625f		 
	};

}
