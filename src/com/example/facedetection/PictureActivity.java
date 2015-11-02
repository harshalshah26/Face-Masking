package com.example.facedetection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PictureActivity extends Activity{
	
	Button addMask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picturemode);
		ImageView imageView = (ImageView)findViewById(R.id.image);
		final String filePath = getIntent().getStringExtra("PATH");
		Bitmap bmp = BitmapFactory.decodeFile(filePath);
		imageView.setImageBitmap(bmp);
		
		addMask = (Button)findViewById(R.id.addMask);
		addMask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(PictureActivity.this,FaceDetectionActivity.class);
				intent.putExtra("PATH",filePath);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				
			}
		});
		
	}

}
