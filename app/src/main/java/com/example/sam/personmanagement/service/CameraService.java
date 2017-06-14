package com.example.sam.personmanagement.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.sam.personmanagement.ui.IdentificationActivity;
import com.example.sam.personmanagement.utils.FacedetectUtils;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by Thuans on 4/18/2017.
 */

public class CameraService extends Service {
    private static final String TAG = "Camera service: ";
    private static Camera camera= null;
    private static Camera.PictureCallback mCallBack;

    static final public String FACE_DETECT = "face_detect";
    static final public String FACE_BITMAP = "FACE_BITMAP";
    static final public String FACE_URI = "FACE_URI";
    private static LocalBroadcastManager broadcaster;

    public static void sendFaceDetects(int faceCount, byte[] data, String nameFile) {
        Intent intent = new Intent(IdentificationActivity.MAIN_FILTER_RECEIVER);
        if(faceCount > 0){
            intent.putExtra(IdentificationActivity.MESSAGE_BROADCAST_SOURCE,FACE_DETECT);
            intent.putExtra(FACE_DETECT, faceCount);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream);
//            byte[] byteArray = stream.toByteArray();
//            showMessage("send byte :::::::"+data.length);



            Bitmap tmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bmp = Bitmap.createScaledBitmap(tmp,150,210,true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            showMessage("compress byte :::::::"+stream.toByteArray().length);
            intent.putExtra(FACE_BITMAP,stream.toByteArray());
            intent.putExtra(FACE_URI, nameFile);
            broadcaster.sendBroadcast(intent);
        }
    }
    private static Timer delayShootTime;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        int cameraId = findFrontFacingCamera();
        camera = Camera.open(cameraId);
        showMessage("Opened camera");
        mCallBack =new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
//                showMessage("camera data length: "+ data.length);
                Bitmap tmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Bitmap bmp = Bitmap.createScaledBitmap(tmp,200,200,true);
//                showMessage("byte count: "+ bmp.getByteCount());
                FacedetectUtils singleFace = FacedetectUtils.getInstance(null);
                Frame frame = new Frame.Builder().setBitmap(tmp).build();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                data = stream.toByteArray();

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/saved_images");
                myDir.mkdirs();
                String nameFile = "testSelf.jpg";
                File file = new File(myDir, nameFile);
                if (file.exists ()) file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    tmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.write(data);
                    out.close();
                    showMessage("Saved picture......."+file.getCanonicalPath());
                } catch (IOException e){
                    Log.d("CAMERA file", e.getMessage());
                }

                showMessage("detecting.... face");
                SparseArray<Face> faces = singleFace.getSafeDetector().detect(frame);
                singleFace.setImageBitmap(tmp);
                if (faces.size()>0){
                    sendFaceDetects(faces.size(),data, nameFile);
                }
            }
        };

        takePhoto(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("deprecation")
    private static void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();



        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created....");
                Camera.Parameters parameters = camera.getParameters();
                parameters.set("orientation", "portrait");
                parameters.setRotation(90);
                camera.setParameters(parameters);
                try {
                    camera.setPreviewDisplay(holder);
                    delayShootTime = new Timer();
                    delayShootTime.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            camera.startPreview();
                            camera.takePicture(null, null, mCallBack);
                        }
                    }, 0, 4000);
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    e.printStackTrace();
                }
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}


        });



        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);
        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);

    }
    private static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        showMessage("nmber camera : "+numberOfCameras);
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                showMessage("Camera found"+i);
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    @Override
    public void onDestroy(){
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        delayShootTime.cancel();
        if (camera != null){
            camera.release();
            showMessage("release CAMERA FOR PHONE");
        }
        stopSelf();
    }
    private static void showMessage(String message) {
        Log.i(TAG, message);
    }

}
