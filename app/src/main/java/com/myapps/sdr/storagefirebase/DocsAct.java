package com.myapps.sdr.storagefirebase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocsAct extends AppCompatActivity {
    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_recycler);
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("My Documents").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String filename = dataSnapshot.getKey();
                String url = dataSnapshot.getValue(String.class);
                ((DocAdapter)recyclerView.getAdapter()).update(filename,url);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView = findViewById(R.id.doc_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(DocsAct.this));
        DocAdapter imgAdapter = new DocAdapter(recyclerView,DocsAct.this,new ArrayList<String>(),new ArrayList<String>());
        recyclerView.setAdapter(imgAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.doc_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.doc_home)
        {
            finish();
            startActivity(new Intent(DocsAct.this,UploadAct.class));
        }
        else if(id == R.id.doc_logout)
        {
            firebaseAuth.signOut();
            UploadAct.getInstance().finish();
            finish();
            startActivity(new Intent(DocsAct.this,LoginAct.class));
        }
        else if(id == R.id.doc_music)
        {
            finish();
            startActivity(new Intent(DocsAct.this,MusicAct.class));
        }
        else if(id == R.id.doc_videos)
        {
            finish();
            startActivity(new Intent(DocsAct.this,VideoAct.class));
        }
        else if(id == R.id.doc_pictures)
        {
            finish();
            startActivity(new Intent(DocsAct.this,ImageAct.class));
        }


        return super.onOptionsItemSelected(item);
    }


}
