package com.example.facedetection.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class CameraUtil {
	
	/**
	 * 
	 *  Check if this device has a camera 
	 *  
	 */
	public static  boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	        c.setDisplayOrientation(90);
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	/***
	 * 
	 * getOutputFile
	 * @return output file in jpeg format
	 */
	public static File getOutputFile(String directory)
	{
	   File dir= new File(directory);
	   if(!dir.exists())
	   {
		   boolean flag = dir.mkdir();
	   }
	   
  	   File f = new File(dir.getAbsolutePath(),"IMG" + System.currentTimeMillis()
  			   + ".jpeg");
  	 try {
		   f.createNewFile();
	   } catch (IOException e) {
		   // TODO Auto-generated catch block
		  Log.d("Error in creating file",e.toString());
	   }
  	   return f;
	}
	
	
	public static void writeToFile(Bitmap bm,File outupt)
	{


		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outupt);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
			// PNG is a lossless format, the compression factor (100) is ignored
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

	
	
	
	/**
	 * 
	 * This methos will rotate image and return bitmap to store it in a proper rotated format
	 * @param data byte array to convert in bitmap
	 * @return properly rotated bitmap object or null 
	 */
	public static Bitmap rotateImage(byte[] data,Context context)
	{
        if (data != null) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Notice that width and height are reversed
                Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                int w = scaled.getWidth();
                int h = scaled.getHeight();
                // Setting post rotate to 90
                Matrix mtx = new Matrix();
                mtx.postRotate(90);
                // Rotating Bitmap
                bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            }else{// LANDSCAPE MODE
                //No need to reverse width and height
                Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth,screenHeight , true);
                bm=scaled;
            }
           return bm;            
        }
        return null;
	}
	
	/***
	 * 
	 * @param contentURI
	 * @param context
	 * @return absolute path for given URI
	 */
	public static  String getRealPathFromURI(Uri contentURI,Context context) {
		 String result;
		 Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
		 if (cursor == null) { // Source is Dropbox or other similar local file path
			 result = contentURI.getPath();
		 } else { 
			 cursor.moveToFirst(); 
			 int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			 result = cursor.getString(idx);
			 cursor.close();
		 }
		 return result;
	 }
}
