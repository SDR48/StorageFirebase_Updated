package com.myapps.sdr.storagefirebase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainActivity extends AppCompatActivity {

    private static final int REQ =1;
    private ProgressDialog progressDialog;
    private Button upfile,selfile,fetch;
    private String filepath = "";
    private EditText editText;
    private FirebaseStorage storage;
    private TextView filename;
    private FirebaseDatabase firebasedb;
    Uri pdfuri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgress(0);
        selfile = findViewById(R.id.selfile);
        editText = findViewById(R.id.editText);
        upfile = findViewById(R.id.upfile);
        fetch = findViewById(R.id.fetch);
        filename = findViewById(R.id.filename);
        firebasedb = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        selfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                {
                    selectfile();
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            }
        });
        upfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pdfuri!=null)
                {

                    uploadfile(pdfuri);

                }
                else
                {
                    Toast.makeText(MainActivity.this,"Select a file",Toast.LENGTH_SHORT).show();
                }
            }
        });


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
            Toast.makeText(MainActivity.this,"Please grant permission",Toast.LENGTH_SHORT).show();
        }
    }
    private void selectfile()
    {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,10);
    }
    private void uploadfile(Uri pdfuri){
        if(editText.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this,"Please assign a name",Toast.LENGTH_SHORT).show();
        }
        else{
        final String filename = editText.getText().toString()+".pdf";
        final String filename2 = editText.getText().toString();
        progressDialog.show();
        final StorageReference store = storage.getReference();
        store.child("My Files").child(filename).putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String url = store.getDownloadUrl().toString();
                DatabaseReference databaseReference = firebasedb.getReference();
                databaseReference.child(filename2).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"File uploaded successfully",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"File upload failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentprog = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentprog);

            }
        });
    }}


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == 10 && resultCode == RESULT_OK && data!=null)
        {
            pdfuri = data.getData();
            filename.setText(pdfuri.getLastPathSegment());
        }
        else
        {
            Toast.makeText(MainActivity.this,"Please select a file",Toast.LENGTH_SHORT).show();
        }

    }


}
