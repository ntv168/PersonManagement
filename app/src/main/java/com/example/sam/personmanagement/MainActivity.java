package com.example.sam.personmanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sam.personmanagement.facetracker.FaceTrackerActivity;
import com.example.sam.personmanagement.helper.PersonManagement;
import com.example.sam.personmanagement.helper.StorageHelper;
import com.example.sam.personmanagement.persongroupmanagement.PersonGroupActivity;
import com.example.sam.personmanagement.ui.IdentificationActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void clicktoDetect(View view){
        startActivity(new Intent(this, IdentificationActivity.class));
    }

    public void clicktoManage(View view){
        startActivity(new Intent(this, PersonGroupActivity.class));
    }


    public void clicktoFaceTracker(View view){
        startActivity(new Intent(this, FaceTrackerActivity.class));
    }


}
