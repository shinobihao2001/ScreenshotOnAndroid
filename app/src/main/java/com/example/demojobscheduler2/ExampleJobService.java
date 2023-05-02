package com.example.demojobscheduler2;
import android.graphics.Bitmap;

//import android.app.job.JobParameters;
//import android.app.job.JobService;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.hardware.display.VirtualDisplay;
//import android.media.ImageReader;
//import android.media.MediaScannerConnection;
//import android.media.projection.MediaProjection;
//import android.media.projection.MediaProjectionManager;
//import android.os.Environment;
//import android.util.Log;
//import android.view.Display;
//import android.view.Surface;
//import android.view.View;
//import android.view.WindowManager;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.Calendar;
//import java.util.Date;
//
//public class ExampleJobService extends JobService {
//    private static final String TAG = "ExampleJobService";
//    private  boolean  jobCancelled=false;
//    @Override
//    public boolean onStartJob(JobParameters params) {
//        Log.d(TAG,"Job started");
//        doBackgroundWork(params);
//        return true;
//    }
//    private void takeScreenshot() {
//        try {
//            WindowManager window = (WindowManager) getSystemService(WINDOW_SERVICE);
//            Display display = window.getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size); //gets the screen dimensions
//            Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
//            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Screenshots";
//            File dir = new File(path);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            String fileName = Calendar.getInstance().getTimeInMillis() + ".png";
//            File file = new File(dir, fileName);
//
//// Check if the parent directory exists
//            if (!file.getParentFile().exists()) {
//                boolean success = file.getParentFile().mkdirs();
//                if (!success) {
//                    Log.d(TAG,"Cannot create the folder");
//                }
//            }
//
//            FileOutputStream outputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            outputStream.flush();
//            outputStream.close();
//            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
//            Log.d(TAG, "Screenshot saved to " + file.getAbsolutePath());
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to take screenshot! " + e.getMessage());
//        }
//        //Log.i("MyTag", "Hello World");
//    }
//
//    private  void doBackgroundWork(JobParameters params){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i=0;i<10;i++){
//                    if (jobCancelled){
//                        return;
//                    }
//                    Log.d(TAG,"run: "+i);
//                    takeScreenshot();
//                    try {
//                        Thread.sleep(1000*60);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                Log.d(TAG,"Job finished");
//                jobFinished(params,false);
//            }
//        }).start();
//    }
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        Log.d(TAG,"Job cancelled before completion");
//        jobCancelled=true;
//        return true;
//    }
//}


//package com.example.demojobscheduler2;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;

//    public void setMediaProjection(MediaProjection mediaProjection) {
//        this.mediaProjection = mediaProjection;
//    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        mediaProjection=MainActivity.mediaProjection;
        doBackgroundWork(params);
        return true;
    }

    private void takeScreenshot() {
        try {
            WindowManager window = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size); //gets the screen dimensions

            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }

            //imageReader = ImageReader.newInstance(size.x, size.y, ImageFormat.RGB_565, 1);
            imageReader = ImageReader.newInstance(size.x, size.y, ImageFormat.JPEG, 1);
            //imageReader = ImageReader.newInstance(size.x, size.y, ImageFormat.YUV_420_888, 1);

            mediaProjection.createVirtualDisplay("screenshot", size.x, size.y, getResources().getDisplayMetrics().densityDpi,
                    Display.FLAG_SECURE, imageReader.getSurface(), null, null);

            // Wait for the image to be available
            SystemClock.sleep(5000);

            Image image = imageReader.acquireLatestImage();
            if (image != null) {
                Log.d(TAG,"The 1");
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                Log.d(TAG,"The 2");
                int pixelStride = image.getPlanes()[0].getPixelStride();
                Log.d(TAG,"The 3");
                int rowStride = image.getPlanes()[0].getRowStride();
                int rowPadding = rowStride - pixelStride * size.x;
                Bitmap bitmap = Bitmap.createBitmap(size.x + rowPadding / pixelStride, size.y, Bitmap.Config.ARGB_8888);
                //Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
                Log.d(TAG,"The 4");
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, size.x, size.y);

                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Screenshots";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Screenshot_" + timeStamp + ".png";
                File file = new File(dir, fileName);

                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                // Refresh the media gallery with the new file
                MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);

                Log.d(TAG, "Screenshot saved to " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to take screenshot! " + e.getMessage());
        } finally {
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }
        }
    }

    private void doBackgroundWork(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (jobCancelled) {
                        return;
                    }
                    Log.d(TAG, "run: " + i);
                    takeScreenshot();
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}