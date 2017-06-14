package com.example.sam.personmanagement;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sam.personmanagement.helper.StorageHelper;
import com.example.sam.personmanagement.persongroupmanagement.PersonActivity;
import com.example.sam.personmanagement.persongroupmanagement.PersonGroupActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DetectPersonManagement extends AppCompatActivity {

    boolean addNewPersonGroup;
    boolean personGroupExists;
    String personGroupId;
    String oldPersonGroupName;
    TextView editTextPersonGroupName;
    TextView persongroupid;
    PersonListAdapter personListAdapter;

    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_person_management);

        personGroupId = StorageHelper.getPersonGroupId("nguoinha", this);

        if (!personGroupId.equals("")) {
            oldPersonGroupName = "nguoinha";
            addNewPersonGroup = false;
            personGroupExists = true;
            editTextPersonGroupName.setText(oldPersonGroupName);
            persongroupid.setText(personGroupId);
        } else {
            personGroupId = UUID.randomUUID().toString();
            StorageHelper.setPersonGroupId(personGroupId,"nguoinha",this);
            addNewPersonGroup = true;
            personGroupExists = false;
            oldPersonGroupName = "nguoinha";
            editTextPersonGroupName.setText(oldPersonGroupName);
            persongroupid.setText(personGroupId);
        }


        progressDialog = new ProgressDialog(this);
    }



    private class PersonListAdapter extends BaseAdapter {

        List<String> personIdList;
        List<Boolean> personChecked;
        boolean longPressed;

        PersonListAdapter() {
            longPressed = false;
            personIdList = new ArrayList<>();
            personChecked = new ArrayList<>();

            Set<String> personIdSet = StorageHelper.getAllPersonIds(personGroupId, DetectPersonManagement.this);
            for (String personId: personIdSet) {
                personIdList.add(personId);
                personChecked.add(false);
            }
        }

        @Override
        public int getCount() {
            return personIdList.size();
        }

        @Override
        public Object getItem(int position) {
            return personIdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // set the item view
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_person, parent, false);
            }
            convertView.setId(position);

            String personId = personIdList.get(position);
            Set<String> faceIdSet = StorageHelper.getAllFaceIds(personId, DetectPersonManagement.this);
            if (!faceIdSet.isEmpty()) {
                Iterator<String> it = faceIdSet.iterator();
                Uri uri = Uri.parse(StorageHelper.getFaceUri(it.next(), DetectPersonManagement.this));
                ((ImageView)convertView.findViewById(R.id.image_person)).setImageURI(uri);
            } else {
                Drawable drawable = getResources().getDrawable(R.drawable.select_image);
                ((ImageView)convertView.findViewById(R.id.image_person)).setImageDrawable(drawable);
            }

            // set the text of the item
            String personName = StorageHelper.getPersonName(personId, personGroupId, DetectPersonManagement.this);
            ((TextView)convertView.findViewById(R.id.text_person)).setText(personName);

            // set the checked status of the item
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_person);
            if (longPressed) {
                checkBox.setVisibility(View.VISIBLE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        personChecked.set(position, isChecked);
                    }
                });
                checkBox.setChecked(personChecked.get(position));
            } else {
                checkBox.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }
}
