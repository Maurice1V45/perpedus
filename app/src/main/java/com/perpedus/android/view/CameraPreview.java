package com.perpedus.android.view;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Surface view that displays the camera
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder	surfaceHolder;
	private Camera			camera;


	public CameraPreview(Context context, Camera camera) {
		super(context);
		this.camera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the 
		// underlying surface is created and destroyed. 
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0 
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here. 
		// Make sure to stop the preview before resizing or reformatting it. 

		if (surfaceHolder.getSurface() == null) {
			// preview surface does not exist 
			return;
		}

		// stop preview before making changes 
		try {
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview 
		}

		// set preview size and make any resize, rotate or 
		// reformatting changes here 

		// start preview with new settings 
		try {
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();

		} catch (Exception e) {
			Log.d("Camera", "Error starting camera preview: " + e.getMessage());
		}
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the preview. 
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			Log.d("Camera", "Error setting camera preview: " + e.getMessage());
		}

	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity. 
	}
}