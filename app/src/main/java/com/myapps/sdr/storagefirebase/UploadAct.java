package com.myapps.sdr.storagefirebase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadAct extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static UploadAct fa;
    Uri pdfuri;
    String dwnldurl;
    FirebaseAuth firebaseAuth;
    Button upfile,selfile;
    ProgressBar progressBar;
    private FirebaseStorage storage;
    TextView filenames,header,prog;
    private FirebaseDatabase firebasedb;
    ImageView imageView;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        fa=this;
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        header = headerview.findViewById(R.id.header);
        prog=findViewById(R.id.progress);
        prog.setText("0%");
        prog.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.parseColor("#009688"), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.INVISIBLE);
        imageView = findViewById(R.id.imageView6);
        firebaseAuth = FirebaseAuth.getInstance();;
        selfile = findViewById(R.id.selfile);
        upfile = findViewById(R.id.upfile);
        filenames = findViewById(R.id.filename);
        filenames.setTextColor(Color.WHITE);
        firebasedb = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
firebasedb.getReference().addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String email = dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).child("My-Info").child("Email").getValue(String.class);
        header.setText(email);
    }
    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
        selfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(UploadAct.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectfile();
                }
                else{
                    ActivityCompat.requestPermissions(UploadAct.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            }
        });
        upfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pdfuri!=null)
                {
                    uploadfile();

                }
                else
                {
                    Toast.makeText(UploadAct.this,"Select a file",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(UploadAct.this,DocsAct.class));
        } else if (id == R.id.nav_gallery) {

            startActivity(new Intent(UploadAct.this,MusicAct.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(UploadAct.this,ImageAct.class));
        } else if (id == R.id.nav_tools) {
            startActivity(new Intent(UploadAct.this,VideoAct.class));
        } else if (id == R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(UploadAct.this,LoginAct.class));
        }
        else if(id==R.id.change)
        {
            startActivity(new Intent(UploadAct.this,change.class));
        }
        else if(id == R.id.contact_us)
        {
            startActivity(new Intent(UploadAct.this,ContactUs.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( requestCode == 1 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectfile();
        }
        else
        {
            Toast.makeText(UploadAct.this,"Please grant permission",Toast.LENGTH_SHORT).show();
        }
    }
    private void selectfile()
    {
        Intent i = new Intent();
        i.setType("*/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,10);
    }
    private void uploadfile(){
        if(filenames.getText().toString().equals("No File Selected")) {
            Toast.makeText(UploadAct.this,"Please select a file",Toast.LENGTH_SHORT).show();
        }
        else{
            final String filename = filenames.getText().toString();
            final String filename2 = filename.replace(".","--");
            final String extension = filename.substring(filename.lastIndexOf(".")+1);
            progressBar.setVisibility(View.VISIBLE);
            prog.setVisibility(View.VISIBLE);
            final StorageReference store = storage.getReference();
            if(extension.equals("pdf") || extension.equals("xslx") || extension.equals("docx") || extension.equals("pptx") || extension.equals("txt") || extension.equals("zip")) {
                store.child(firebaseAuth.getCurrentUser().getUid()).child("My Documents").child(filename).putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        store.child(firebaseAuth.getCurrentUser().getUid()).child("My Documents").child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dwnldurl = uri.toString();
                                DatabaseReference databaseReference = firebasedb.getReference();
                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("My Documents").child(filename2).setValue(dwnldurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            prog.setVisibility(View.INVISIBLE);
                                            Toast.makeText(UploadAct.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        prog.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadAct.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int currentprog = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(currentprog);
                        prog.setText(Integer.toString(currentprog)+"%");

                    }
                });
            }
            else if(extension.equals("mp3"))
            {
                store.child(firebaseAuth.getCurrentUser().getUid()).child("My Music").child(filename).putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        store.child(firebaseAuth.getCurrentUser().getUid()).child("My Music").child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dwnldurl = uri.toString();
                                DatabaseReference databaseReference = firebasedb.getReference();
                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("My Music").child(filename2).setValue(dwnldurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            prog.setVisibility(View.INVISIBLE);
                                            filenames.setText("No File Selected");
                                            String uri = "@drawable/cloud_nav.png";
                                            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                                            Drawable res = ContextCompat.getDrawable(UploadAct.this,R.drawable.cloud_nav);
                                            imageView.setImageDrawable(res);
                                            Toast.makeText(UploadAct.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadAct.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int currentprog = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(currentprog);
                        prog.setText(Integer.toString(currentprog)+"%");
                    }
                });
            }
            else if(extension.equals("avi") || extension.equals("mp4") || extension.equals("mkv") || extension.equals("mov") || extension.equals("flv"))
            {
                store.child(firebaseAuth.getCurrentUser().getUid()).child("My Videos").child(filename).putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        store.child(firebaseAuth.getCurrentUser().getUid()).child("My Videos").child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dwnldurl = uri.toString();
                                DatabaseReference databaseReference = firebasedb.getReference();
                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("My Videos").child(filename2).setValue(dwnldurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            prog.setVisibility(View.INVISIBLE);
                                            ;
                                            Toast.makeText(UploadAct.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadAct.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int currentprog = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(currentprog);
                        prog.setText(Integer.toString(currentprog)+"%");
                    }
                });
            }
            else if(extension.equals("jpg") || extension.equals("tif") || extension.equals("gif") || extension.equals("png"))
            {
                store.child(firebaseAuth.getCurrentUser().getUid()).child("My Pictures").child(filename).putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        store.child(firebaseAuth.getCurrentUser().getUid()).child("My Pictures").child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dwnldurl = uri.toString();
                                DatabaseReference databaseReference = firebasedb.getReference();
                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("My Pictures").child(filename2).setValue(dwnldurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            prog.setVisibility(View.INVISIBLE);
                                            Toast.makeText(UploadAct.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        prog.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadAct.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
                ).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int currentprog = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(currentprog);
                        prog.setText(Integer.toString(currentprog)+"%");
                    }
                });
            }
        }}


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10 && resultCode == RESULT_OK && data!=null)
        {
            pdfuri = data.getData();
            String path = getPath(getContentResolver(),pdfuri);
            String filename1 = path.substring(path.lastIndexOf("/")+1);
            filenames.setText(filename1);
            String extension = filename1.substring(filename1.lastIndexOf(".")+1);
            if(extension.equals("mp3"))
            {
                Glide.with(UploadAct.this).asBitmap().load(R.drawable.audioimg).into(imageView);
            }
            else if(extension.equals("jpg") || extension.equals("tif") || extension.equals("gif") || extension.equals("png"))
            {
                Glide.with(UploadAct.this).asBitmap().load(pdfuri).into(imageView);

            }
            else if(extension.equals("avi") || extension.equals("mp4") || extension.equals("mkv") || extension.equals("mov") || extension.equals("flv"))
            {
                Glide.with(UploadAct.this).asBitmap().load(pdfuri).apply(new RequestOptions().frame(5000)).into(imageView);
            }
            else if(extension.equals("pdf"))
            {
                Glide.with(UploadAct.this).asBitmap().load(R.drawable.pdf).into(imageView);
            }
            else if(extension.equals("xslx"))
            {
                Glide.with(UploadAct.this).asBitmap().load(R.drawable.excel).into(imageView);
            }
            else if(extension.equals("pptx"))
            {
                Glide.with(UploadAct.this).asBitmap().load(R.drawable.ppt).into(imageView);
            }
            else if(extension.equals("docx") )
            {
                Glide.with(UploadAct.this).asBitmap().load(R.drawable.word).into(imageView);
            }
            else if(extension.equals("txt"))
            {
                Glide.with(UploadAct.this).asBitmap().load(R.drawable.text).into(imageView);
            }
        }
        else
        {
            Toast.makeText(UploadAct.this,"Please select a file",Toast.LENGTH_SHORT).show();
        }

    }

    private String getPath(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
    public static UploadAct getInstance()
    {
        return fa;
    }
}
