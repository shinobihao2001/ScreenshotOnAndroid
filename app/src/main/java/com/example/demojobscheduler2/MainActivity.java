package com.example.demojobscheduler2;

import androidx.annotation.LongDef;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.widget.Toast;
import android.os.Bundle;

import kotlinx.coroutines.Job;


public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE =11 ;
    private static final int REQUEST_MEDIA_PROJECTION = 10;
    public static MediaProjectionManager mediaProjectionManager;
    public static MediaProjection mediaProjection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"Asking permission",Toast.LENGTH_SHORT).show();
        // Check if the WRITE_EXTERNAL_STORAGE permission is already granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If not granted, request it
            Log.d("Hao","Ask permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission already granted
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }

        // Asking for the MediaProjection
        openActivityForResult();
    }
    public  void openActivityForResult(){
        Log.d("HAO","Create mediaProjection sucess Testing");
        mediaProjectionManager= (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent= mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_MEDIA_PROJECTION,null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                Log.d("HAO","Create mediaProjection sucess");
            }
            else {
                Log.d("HAO","Create media failed");
            }
        }
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void scheduleJob(View v) {

        if (mediaProjection==null){
            Log.d("Hao","MediaProjection null");
        }
        else
            Log.d("Hao","MediaProjection  not null");
        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(123, componentName)
                .setRequiresCharging(false)
                .setMinimumLatency(1000)
                .setPersisted(true);

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(builder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }
    public void cancelJob(View v){
        JobScheduler scheduler= (JobScheduler)  getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel((123));
        Log.d(TAG,"Job cancelled");
    }
}

//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.app.job.JobInfo;
//import android.app.job.JobScheduler;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.projection.MediaProjection;
//import android.media.projection.MediaProjectionManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//
//public class MainActivity extends AppCompatActivity {
//    private static final String TAG = "MainActivity";
//    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 11;
//    private static final int REQUEST_MEDIA_PROJECTION = 12;
//    private MediaProjectionManager mediaProjectionManager;
//    private ExampleJobService exampleJobService;
//    private MediaProjection mediaProjection;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toast.makeText(this, "Asking permission", Toast.LENGTH_SHORT).show();
//        // Check if the WRITE_EXTERNAL_STORAGE permission is already granted or not
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // If not granted, request it
//            Log.d(TAG, "Ask permission");
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_WRITE_EXTERNAL_STORAGE);
//        } else {
//            // Permission already granted
//            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
//        }
//        // Check if the WRITE_EXTERNAL_STORAGE permission is already granted or not
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // If not granted, request it
//            Log.d(TAG, "Ask permission");
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.FOREGROUND_SERVICE},
//                    REQUEST_MEDIA_PROJECTION);
//        } else {
//            // Permission already granted
//            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
//        }
//        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        exampleJobService = new ExampleJobService();
//    }
//
//    // Handle the permission request result
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission denied
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.P)
//    public void scheduleJob(View v) {
//        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
//        JobInfo info = new JobInfo.Builder(123, componentName)
//                .setPersisted(true).build();
//
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = scheduler.schedule(info);
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job scheduled");
//        } else {
//            Log.d(TAG, "Job scheduling failed");
//        }
//    }
//
//    public void cancelJob(View v) {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel((123));
//        Log.d(TAG, "Job cancelled");
//    }
//
//    public void startProjection(View v) {
//        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_MEDIA_PROJECTION) {
//            if (resultCode == RESULT_OK) {
//                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
//                exampleJobService.setMediaProjection(mediaProjection);
//            }
//        }
//    }
//}