package com.myapps.sdr.storagefirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginAct extends AppCompatActivity {

    Button log;
    EditText emails,passwords;
    TextView register,chpass,emailalert,passalert;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emails = (EditText)findViewById(R.id.logname);
        emails.setTextColor(Color.WHITE);
        passwords = (EditText)findViewById(R.id.logpass);
        passwords.setTextColor(Color.WHITE);
        log = (Button)findViewById(R.id.loginbut);
        emailalert = findViewById(R.id.email_alert);
        emailalert.setTextColor(Color.RED);
        emailalert.setVisibility(View.INVISIBLE);
        passalert = findViewById(R.id.pass_alert);
        passalert.setTextColor(Color.RED);
        passalert.setVisibility(View.INVISIBLE);
        register = (TextView)findViewById(R.id.gotoreg);
        register.setTextColor(Color.WHITE);
        progressDialog = new ProgressDialog(this);
        chpass=(TextView)findViewById(R.id.fgtpw);
        chpass.setTextColor(Color.WHITE);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            this.finish();
            startActivity(new Intent(LoginAct.this,UploadAct.class));
        }

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = emails.getText().toString();
                String pw = passwords.getText().toString();
                if(em.equals("") && pw.equals(""))
                {
                    Toast.makeText(LoginAct.this,"Please fill all the details",Toast.LENGTH_LONG).show();
                }
                else if(em.equals(""))
                {
                    passalert.setVisibility(View.INVISIBLE);
                    emailalert.setVisibility(View.VISIBLE);
                }
                else if(pw.equals(""))
                {
                    emailalert.setVisibility(View.INVISIBLE);
                    passalert.setVisibility(View.VISIBLE);
                }
                else{
                validate(emails.getText().toString(),passwords.getText().toString());
            }}
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginAct.this,RegistrationAct.class));
            }
        });
        chpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginAct.this,changepass.class));
            }
        });
    }
    private void validate(String email, String password)
    {
        progressDialog.setMessage("Please wait....");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    checkemail();
                }
                else{
                    progressDialog.dismiss();
                    emails.setText("");
                    passwords.setText("");
                    Toast.makeText(LoginAct.this,"Failed",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void checkemail()
    {
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();

        if(fbuser.isEmailVerified())
        {
            finish();
            startActivity(new Intent(LoginAct.this,UploadAct.class));
        }
        else
        {
            Toast.makeText(LoginAct.this,"Please open your mail and verify account",Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }

}
