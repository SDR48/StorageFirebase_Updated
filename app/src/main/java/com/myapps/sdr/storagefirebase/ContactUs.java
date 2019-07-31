package com.myapps.sdr.storagefirebase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ContactUs extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        listView = findViewById(R.id.cont);
        arrayList.add("Send a Mail");
        arrayList.add("Give a Call");
        firebaseAuth=FirebaseAuth.getInstance();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ContactUs.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    String[] to = {"saishrege24@gmail.com"};
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL,to);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent,"Choose an email client"));
                }
                else if(i==1)
                {
                    if (ContextCompat.checkSelfPermission(ContactUs.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ContactUs.this,
                                new String[]{Manifest.permission.CALL_PHONE}, 1);
                    } else {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:7875023468")));
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.con_home)
        {
            finish();
            startActivity(new Intent(ContactUs.this,UploadAct.class));
        }
        else if(id==R.id.con_doc)
        {
            finish();
            startActivity(new Intent(ContactUs.this,DocsAct.class));
        }
        else if(id==R.id.con_music)
        {
            finish();
            startActivity(new Intent(ContactUs.this,MusicAct.class));
        }
        else if(id==R.id.con_logout)
        {
            firebaseAuth.signOut();
            UploadAct uploadAct = new UploadAct();
            uploadAct.finish();
            finish();
            startActivity(new Intent(ContactUs.this,LoginAct.class));
        }
        else if(id==R.id.con_pictures)
        {
            finish();
            startActivity(new Intent(ContactUs.this,ImageAct.class));
        }
        else if(id==R.id.con_videos)
        {
            finish();
            startActivity(new Intent(ContactUs.this,VideoAct.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
