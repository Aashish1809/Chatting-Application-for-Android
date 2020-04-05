package com.example.chat;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    MaterialEditText email,password;
    Button btn_login;
    FirebaseAuth auth;
    TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        forgot = findViewById(R.id.forgot);
        password=findViewById(R.id.password);
        btn_login=findViewById(R.id.btn_login);
        forgot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String txt_email = Objects.requireNonNull(email.getText()).toString();
                if(txt_email.length()==0)
                    Toast.makeText(getApplicationContext(),"Enter Email ID!",Toast.LENGTH_SHORT).show();
                else{
                    final ProgressDialog p;
                    p = new ProgressDialog(LoginActivity.this);
                    p.setMessage("Verifying. Please wait...");
                    p.setIndeterminate(false);
                    p.setCancelable(false);
                    p.show();

                    FirebaseAuth.getInstance().sendPasswordResetEmail(txt_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        p.dismiss();
                                        Toast.makeText(getApplicationContext(),"Email Sent.",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        p.dismiss();
                                        String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                                        Toast.makeText(getApplicationContext(), errorCode, Toast.LENGTH_SHORT).show();
                                    }}
                            });
                }}
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if((TextUtils.isEmpty(txt_email))||(TextUtils.isEmpty(txt_password))){
                    Toast.makeText(getApplicationContext(),"Empty!",Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Failed!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}

