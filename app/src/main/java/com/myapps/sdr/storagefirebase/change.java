package com.myapps.sdr.storagefirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class change extends AppCompatActivity {
    TextView textView;
    Button button;
    EditText old,upd;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        firebaseAuth=FirebaseAuth.getInstance();
        textView=findViewById(R.id.textView);
        textView.setTextColor(Color.RED);
        textView.setVisibility(View.INVISIBLE);
        old=findViewById(R.id.editText);
        upd=findViewById(R.id.editText2);
        button = findViewById(R.id.button2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        user = FirebaseAuth.getInstance().getCurrentUser();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldp,newp;
                oldp=old.getText().toString();
                newp=upd.getText().toString();
                if(oldp.isEmpty() || newp.isEmpty())
                {
                    Toast.makeText(change.this,"Please fill all details",Toast.LENGTH_SHORT).show();
                }
                else if(newp.length()<8)
                {
                    Toast.makeText(change.this,"Weak Password Strength",Toast.LENGTH_SHORT).show();
                }
                else if(!Password_Validation(newp))
                {
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    progressDialog.show();
                    AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), old.getText().toString());
                    user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(upd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(change.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(change.this, UploadAct.class));
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(change.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(change.this, "Some error occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public static boolean Password_Validation(String password)
    {

        if(password.length()>=8)
        {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            //Pattern eight = Pattern.compile (".{8}");


            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            return hasLetter.find() && hasDigit.find() && hasSpecial.find();

        }
        else
            return false;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ch_menu,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.ch_home)
        {
            finish();
            startActivity(new Intent(change.this,UploadAct.class));
        }
        else if(id == R.id.ch_logout)
        {
            firebaseAuth.signOut();
            UploadAct.getInstance().finish();
            finish();
            startActivity(new Intent(change.this,LoginAct.class));
        }
        else if(id == R.id.ch_music)
        {
            finish();
            startActivity(new Intent(change.this,MusicAct.class));
        }
        else if(id == R.id.ch_pictures)
        {
            finish();
            startActivity(new Intent(change.this,ImageAct.class));
        }
        else if(id == R.id.ch_doc)
        {
            finish();
            startActivity(new Intent(change.this,DocsAct.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
