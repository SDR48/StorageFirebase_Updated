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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationAct extends AppCompatActivity {

    EditText Name, Username, Contact, Password;
    Button submit;
    TextView login,passalert;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    DatabaseReference rootreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Name=findViewById(R.id.Names);
        Name.setTextColor(Color.WHITE);
        Username=findViewById(R.id.username);
        Username.setTextColor(Color.WHITE);
        Contact=findViewById(R.id.Contact);
        Contact.setTextColor(Color.WHITE);
        Password=findViewById(R.id.password);
        Password.setTextColor(Color.WHITE);
        passalert = findViewById(R.id.textView3);
        passalert.setVisibility(View.INVISIBLE);
        passalert.setTextColor(Color.RED);
        submit = findViewById(R.id.Reg);
        progressDialog = new ProgressDialog(this);
        login = findViewById(R.id.gotolog);
        login.setTextColor(Color.WHITE);
        firebaseAuth = FirebaseAuth.getInstance();
        rootreference = FirebaseDatabase.getInstance().getReference();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {
                    final String name = Name.getText().toString();
                    final String email = Username.getText().toString();
                    final String pass = Password.getText().toString().trim();
                    final String contact = Contact.getText().toString();
                    Query emailqury = FirebaseDatabase.getInstance().getReference().child("My Info").orderByChild("Email").equalTo(email);
                    emailqury.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0)
                            {
                                Toast.makeText(RegistrationAct.this,"Email already in use",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                progressDialog.setMessage("Please wait....");
                                progressDialog.show();
                                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (pass.length() >= 8) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                sendemail();
                                                String userid = firebaseAuth.getCurrentUser().getUid();
                                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child(userid).child("My-Info");
                                                Toast.makeText(RegistrationAct.this, "Stored", Toast.LENGTH_SHORT).show();
                                                Map newnode = new HashMap();
                                                newnode.put("Name", name);
                                                newnode.put("Email", email);
                                                newnode.put("Phone", contact);
                                                dbref.setValue(newnode);
                                            }
                                            else {
                                                Toast.makeText(RegistrationAct.this, "Not Stored", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(RegistrationAct.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(RegistrationAct.this,"Request Cancelled",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationAct.this, LoginAct.class));
            }
        });
    }

    public boolean validate() {
        Boolean result = false;
        String name = Name.getText().toString();
        String usname = Username.getText().toString();
        String phonenum = Contact.getText().toString();
        String pass = Password.getText().toString();
        if (name.isEmpty() || usname.isEmpty() || phonenum.isEmpty() || pass.isEmpty()) {
            Toast.makeText(RegistrationAct.this, "Please fill all details", Toast.LENGTH_SHORT).show();
        }
        else if(pass.length()<8)
        {
            Toast.makeText(RegistrationAct.this,"Weak password strength",Toast.LENGTH_LONG).show();
        }
        else if(!Password_Validation(pass))
        {
            passalert.setVisibility(View.VISIBLE);
        }
        else {
            result = true;
        }
        return result;
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
    private void sendemail() {
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbuser != null) {
            fbuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationAct.this, "Successfully sent verification", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationAct.this, LoginAct.class));
                    } else {
                        Toast.makeText(RegistrationAct.this, "Mail couldn't be sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

