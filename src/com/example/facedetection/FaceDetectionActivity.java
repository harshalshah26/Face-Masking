package com.example.facedetection;

import java.io.File;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.facedetection.util.CameraUtil;

public class FaceDetectionActivity extends Activity {

	private int imageWidth, imageHeight;
	private int numberOfFace = 10;
	private FaceDetector myFaceDetect;
	private FaceDetector.Face[] myFace;
	float myEyesDistance;
	int numberOfFaceDetected;
	Bitmap myBitmap;
	Button save,back;
	ImageView imageView;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facedetection);
		String path = getIntent().getStringExtra("PATH");
		imageView = (ImageView)findViewById(R.id.imageView);
		//setContentView(new MyView(this,path));
		
		createMask(path, imageView);

		save = (Button)findViewById(R.id.saveButton);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Bitmap bm = ((BitmapDrawable)imageView.getDrawable()).getBitmap();				
				File pictureFile = CameraUtil.getOutputFile("sdcard/Facedetection/Mask Image");
				CameraUtil.writeToFile(bm,pictureFile);
				Toast.makeText(FaceDetectionActivity.this,"Image successfully stored...", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		back = (Button)findViewById(R.id.backBotton);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FaceDetectionActivity.this,MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);				
			}
		});
	}

	

	public void createMask(String path,ImageView imageView)
	{
		BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
		bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		//myBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.jennifer_lopez, bitmapFatoryOptions);
		myBitmap = BitmapFactory.decodeFile(path, bitmapFatoryOptions);		
		imageWidth = myBitmap.getWidth();
		imageHeight = myBitmap.getHeight();
		myFace = new FaceDetector.Face[numberOfFace];
		myFaceDetect = new FaceDetector(imageWidth, imageHeight,numberOfFace);
		numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);
		Bitmap mutableBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);

		
		


		for (int i = 0; i < numberOfFaceDetected; i++) {
			Face face = myFace[i];
			PointF myMidPoint = new PointF();
			face.getMidPoint(myMidPoint);
			Log.d("midpoint"+i, ""+myMidPoint.x+"  "+myMidPoint.y);
			myEyesDistance = face.eyesDistance();

			float left = myMidPoint.x - myEyesDistance * 1.5f;
			float top = myMidPoint.y - myEyesDistance * 1.5f;
			float right = myMidPoint.x + myEyesDistance * 1.5f;
			float bottom = myMidPoint.y + myEyesDistance * 1.5f;
			Log.d("left",""+left);
			Log.d("top",""+top);
			Log.d("right",""+right);
			Log.d("bottom",""+bottom);
			
			int color1 = generateRandomColor();
			int color2;
			do
			{
				color2 = generateRandomColor();
			}while(color2==color1);
			
			//paint for eye and lips
			Paint myPaint = new Paint();
			myPaint.setColor(color1);
			myPaint.setStyle(Paint.Style.STROKE);
			myPaint.setStrokeWidth(8);

			//paint for mask and cap
			Paint myPaint1 = new Paint();
			myPaint1.setColor(color2);
			myPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
			myPaint1.setStrokeWidth(8);

			//draw mouth circle
			float radius = myEyesDistance*1.6f;
			
			//canvas.drawRect((int)left,(int)top,(int)right,(int)bottom, myPaint);
			//center is center of rectangle
			PointF center = new PointF(left+((right-left)/2), top+((bottom-top)/2));

			//draw triangle
			
			
			//draw line1
			float startX = center.x - radius;
			float startY= center.y;
			float stopX= center.x;
			float stopY = center.y - (radius*4f);
			canvas.drawLine(startX, startY, stopX, stopY, myPaint);

			//draw line2

			startX = center.x + radius;
			startY= center.y;
			canvas.drawLine(startX, startY, stopX, stopY, myPaint);
			
			
			//draw cap circle
			float cx = stopX;
			float cy = stopY -(myEyesDistance /2);
			canvas.drawCircle(cx, cy, myEyesDistance /2, myPaint1);


			//draw mouth circle
			canvas.drawCircle(myMidPoint.x, myMidPoint.y, radius, myPaint1);
			
			

			//draw eyes
			cx = center.x - (myEyesDistance /2);
			cy = center.y;
			Log.d("center"+i, ""+cx+"  "+cy);
			canvas.drawCircle(cx, cy,myEyesDistance /4 , myPaint);
			cx = center.x + (myEyesDistance /2);
			cy = center.y;
			canvas.drawCircle(cx, cy,myEyesDistance /4 , myPaint);

			//draw lips
			startX = center.x - myEyesDistance/2;
			startY= center.y + myEyesDistance*0.8f;
			stopX= center.x + myEyesDistance/2;
			stopY = center.y + myEyesDistance*0.8f;
			canvas.drawLine(startX, startY, stopX, stopY, myPaint);
			
		}
			imageView.setImageBitmap(mutableBitmap);

	}
	
	int generateRandomColor()
	{
		int i =new Random().nextInt(6);
		switch(i)
		{
			case 0:
				return Color.RED;
			case 1:
				return Color.GREEN;
			case 2:
				return Color.BLUE;
			case 3:
				return Color.YELLOW;
			case 4:
				return Color.MAGENTA;
			case 5:
				return Color.CYAN;
			
		}		
		return 0;
	}

}
