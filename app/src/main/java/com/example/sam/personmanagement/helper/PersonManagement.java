package com.example.sam.personmanagement.helper;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.sam.personmanagement.R;
import com.example.sam.personmanagement.utils.FacedetectUtils;
import com.example.sam.personmanagement.utils.VoiceUtils;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

/**
 * Created by Sam on 6/7/2017.
 */

public class PersonManagement extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();
        VoiceUtils.initializeInstance(context);

        Log.d("App context","initiating .....");
    }

    public static FaceServiceClient getFaceServiceClient() {
        String apiKey = StorageHelper.getApiKey(context);
        sFaceServiceClient = new FaceServiceRestClient(context.getString(R.string.endpoint), apiKey);
        return sFaceServiceClient;
    }

    private static FaceServiceClient sFaceServiceClient;

    public static Context getContext(){
        return context;
    }
}
