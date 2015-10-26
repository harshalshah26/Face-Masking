package com.example.facedetection;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.facedetection.util.CameraUtil;

public class MainActivity extends Activity {
	private String TAG = "FaceDetection";

	Camera camera ;
	CameraPreview mPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		//device has camera or not
		if(CameraUtil.checkCameraHardware(this))
		{
			// Create an instance of Camera
			camera = CameraUtil.getCameraInstance();	


			// Create our Preview view and set it as the content of our activity.
			mPreview = new CameraPreview(this, camera);


			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);


			// Add a listener to the Capture button
			Button captureButton = (Button) findViewById(R.id.button_capture);
			captureButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// get an image from the camera
							camera.takePicture(null, null, mPicture);
						}
					}
					);


			// Add a listener to the open button
			Button openButton = (Button) findViewById(R.id.button_open);
			openButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// get an image from the gallery
							// Allow user to pick an image from Gallery or other
							// registered apps
							Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setType("image/*");
							startActivityForResult(intent,100);
						}
					}
					);

		}
	}

	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			//get proper rotated bitmap
			ImageData image = new ImageData(data);
			new GenerateImagetask().execute(image);
		}

	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			
			//get original path of selected file
			String path = CameraUtil.getRealPathFromURI(uri,this);

			//start Pictuer mode activity
			Intent intent = new Intent(MainActivity.this,PictureActivity.class);
			intent.putExtra("PATH",path);
			startActivity(intent);
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();		
	};

	/***
	 * release camera on sestroy activity
	 */
	private void releaseCamera(){
		if (camera != null){
			camera.release();        // release the camera for other applications
			camera = null;
		}
	}

	/**
	 *  @author harshal
	 *  wrapper class for byte[]  image data to pass in async task
	 */

	private class ImageData
	{
		byte[] data;
		public ImageData(byte[] data) {
			// TODO Auto-generated constructor stub
			this.data = data;
		}
	}


	/***
	 *@author harshal
	 *
	 *Asynctask which will generate proper rotated image and store it on SD Card
	 *
	 */

	public class GenerateImagetask extends AsyncTask<ImageData, Void, File>
	{


		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected File doInBackground(ImageData... params) {
			// TODO Auto-generated method stub
			ImageData image = params[0];

			Bitmap bm = CameraUtil.rotateImage(image.data,MainActivity.this);
			File pictureFile = null;
			if(bm!=null)
			{
				pictureFile = CameraUtil.getOutputFile("sdcard/Facedetection");
				CameraUtil.writeToFile(bm,pictureFile);				               
			}

			return pictureFile;
		}


		@Override
		protected void onPostExecute(File file) {
			// TODO Auto-generated method stub
			super.onPostExecute(file);
			setProgressBarIndeterminateVisibility(false);
			if(file!=null)
			{
				//start Picture Mode
				Intent intent = new Intent(MainActivity.this,PictureActivity.class);
				intent.putExtra("PATH",file.getAbsolutePath());
				startActivity(intent);
			}
			else
			{
				Toast.makeText(MainActivity.this,"File not generated properly",Toast.LENGTH_SHORT).show();
			}

		}

	}

}
