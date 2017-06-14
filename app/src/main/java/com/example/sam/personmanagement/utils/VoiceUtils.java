package com.example.sam.personmanagement.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Sam on 4/20/2017.
 */

public class VoiceUtils {
    private static VoiceUtils singleton;
    private String content;
    private TextToSpeech tts;
    public static synchronized void initializeInstance(final Context context) {
        if (singleton == null) {
            singleton = new VoiceUtils();
            singleton.tts = new TextToSpeech(context,
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status == TextToSpeech.SUCCESS) {
                                singleton.tts.setLanguage(new Locale("vi","VN"));
                            }else {
                                Log.d("VOICEUTILSSSS","Khong noi duoc roi");
                            }
                        }
                    });
        }
    }
    public static VoiceUtils getInstance() {
        if (singleton == null){
            throw new IllegalStateException("voice util is not initialized, call initializeInstance(..) method first.");
        }
        return singleton;
    }

    private VoiceUtils() {
    }
    public static  void speak(String sentence){
         singleton.tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static void stopSpeakApi() {
        if (singleton.tts != null){
            singleton.tts.stop();
            singleton.tts.shutdown();
        }
    }
}
