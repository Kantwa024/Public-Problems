package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private EditText email;
    private Button register;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        email = (EditText)findViewById(R.id.text_emailId);
        register = (Button)findViewById(R.id.login_button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_result_comment) ;

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().length() != 0)
                {
                    register.setEnabled(true);
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"We have sent you a mail to reset the password.",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    register.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter your email id.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
