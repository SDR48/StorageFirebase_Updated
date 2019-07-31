package com.myapps.sdr.storagefirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class changepass extends AppCompatActivity {
    EditText emailpass;
    Button btnres;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        emailpass = (EditText)findViewById(R.id.emailtext);
        btnres = (Button)findViewById(R.id.reset);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btnres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useremail = emailpass.getText().toString().trim();
                if(useremail.isEmpty())
                {
                    Toast.makeText(changepass.this,"Please enter the email",Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.setMessage("Please wait....");
                            progressDialog.show();
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(changepass.this,"Password reset email sent",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(changepass.this,LoginAct.class));
                            }
                            else {
                                Toast.makeText(changepass.this,"Password reset email couldn't be sent",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
