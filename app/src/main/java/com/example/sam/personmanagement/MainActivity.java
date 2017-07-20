package com.example.sam.personmanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.personmanagement.facetracker.FaceTrackerActivity;
import com.example.sam.personmanagement.helper.PersonManagement;
import com.example.sam.personmanagement.helper.StorageHelper;
import com.example.sam.personmanagement.http.WebServer;
import com.example.sam.personmanagement.persongroupmanagement.PersonGroupActivity;
import com.example.sam.personmanagement.ui.IdentificationActivity;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    LinearLayout groupFunction, groupEdittext;
    EditText subKey, groupId;
    TextView key;
    Button btnOk;
    private WebServer mWebServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupFunction = (LinearLayout) findViewById(R.id.group_function);
        groupFunction.setVisibility(View.INVISIBLE);
        groupEdittext = (LinearLayout) findViewById(R.id.group_edittext);
        groupEdittext.setVisibility(View.INVISIBLE);

        subKey = (EditText) findViewById(R.id.sub_key);
        subKey.setText("ede0317678184a29b0a62f3e0f65dc65");

        groupId = (EditText) findViewById(R.id.groupId);
        groupId.setText("29f1ccf6-16a3-4e09-95c7-24e5e31a2acf");

        key = (TextView) findViewById(R.id.apikey);

        final String apikey = StorageHelper.getApiKey(MainActivity.this);
        if (!apikey.equals("")) {
            groupFunction.setVisibility(View.VISIBLE);
            key.setText(StorageHelper.getApiKey(MainActivity.this));
        } else {
            groupEdittext.setVisibility(View.VISIBLE);
//            String Id = StorageHelper.getPersonGroupId("nguoinha",MainActivity.this);
//            if (Id.equals("")){
//                Id = UUID.randomUUID().toString();
//            }
//            groupId.setText(Id);

        }

        btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupFunction.setVisibility(View.VISIBLE);
                groupEdittext.setVisibility(View.INVISIBLE);

                StorageHelper.setApiKey(subKey.getText().toString(), MainActivity.this);
                StorageHelper.setPersonGroupId(groupId.getText().toString(), "nguoinha", MainActivity.this);




                key.setText(StorageHelper.getApiKey(MainActivity.this));

            }
        });

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

    public void testrequest(View view) {
        startActivity(new Intent(this, com.example.sam.personmanagement.testRequest.IdentificationActivity.class));
    }

    public void startServer(View view) {

        final int port = 8080;
        mWebServer = new WebServer(port);

        (new Thread(mWebServer)).start();
        Toast.makeText(this, "Server Start", Toast.LENGTH_SHORT).show();
    }

    public void stopServer(View view) {

        mWebServer.stop();
        Toast.makeText(this, "Server Stop", Toast.LENGTH_SHORT).show();
    }

    public void checkList(View view) {
        String Id = StorageHelper.getPersonGroupId("nguoinha",MainActivity.this);
        if (!StorageHelper.getAllPersonIds(Id, MainActivity.this).isEmpty()) {
            StorageHelper.clearPersonIds(Id,MainActivity.this);
        }
        new GetPersonIdsTask().execute(Id);
    }

    class GetPersonIdsTask extends AsyncTask<String, String, Person[]> {

        String groupid = "";

        @Override
        protected Person[] doInBackground(String... params) {


            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = PersonManagement.getFaceServiceClient();
            try{
                publishProgress("Training person group...");
                groupid = params[0];

                return faceServiceClient.listPersons(params[0]);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Person[] result) {
            String message = "";

            for (Person person : result) {
                try {
                    String name = URLDecoder.decode(person.name, "UTF-8");
                    StorageHelper.setPersonName(person.personId.toString(),name, groupid, MainActivity.this);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
            Toast.makeText(MainActivity.this, message + "", Toast.LENGTH_SHORT).show();
        }
    }


}
