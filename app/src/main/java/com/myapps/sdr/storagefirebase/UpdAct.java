package com.myapps.sdr.storagefirebase;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdAct extends AppCompatActivity {

    TextView logout;
    Button upd;
    EditText editText;
    private FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upd);
        editText = (EditText)findViewById(R.id.contnum);
        logout = (TextView)findViewById(R.id.logoutupd);
        upd = (Button)findViewById(R.id.button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        final String id = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase.child("Users").child(id).child("Phone").setValue(editText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(editText.getText().toString().equals(""))
                        {
                            Toast.makeText(UpdAct.this,"Please enter the number",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(task.isSuccessful())
                            {
                                Toast.makeText(UpdAct.this,"Successfully updated",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(UpdAct.this,"Failed update",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });


            }
        });
    }
}
