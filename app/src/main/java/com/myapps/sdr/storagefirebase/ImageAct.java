package com.myapps.sdr.storagefirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ImageAct extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_recycler);
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("My Pictures").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String filename = dataSnapshot.getKey();
                String url = dataSnapshot.getValue(String.class);
                ((ImgAdapter)recyclerView.getAdapter()).update(filename,url);
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
         recyclerView = findViewById(R.id.img_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImageAct.this));
        ImgAdapter imgAdapter = new ImgAdapter(recyclerView,ImageAct.this,new ArrayList<String>(),new ArrayList<String>());
        recyclerView.setAdapter(imgAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pic_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.pic_home)
        {
            finish();
            startActivity(new Intent(ImageAct.this,UploadAct.class));
        }
        else if(id == R.id.pic_logout)
        {
            firebaseAuth.signOut();
            UploadAct.getInstance().finish();
            finish();
            startActivity(new Intent(ImageAct.this,LoginAct.class));
        }
        else if(id == R.id.pic_music)
        {
            finish();
            startActivity(new Intent(ImageAct.this,MusicAct.class));
        }
        else if(id == R.id.pic_videos)
        {
            finish();
            startActivity(new Intent(ImageAct.this,VideoAct.class));
        }
        else if(id == R.id.pic_doc)
        {
            finish();
            startActivity(new Intent(ImageAct.this,DocsAct.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
