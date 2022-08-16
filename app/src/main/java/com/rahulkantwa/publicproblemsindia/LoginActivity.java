package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText email,pass;
    private TextView forgottext;
    private Button register;
    private ProgressBar progressBar;
    private String mailid = "",password = "";
    private RelativeLayout linelayout;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if(grantResults.length > 0
                        && (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED
                ))
                {
                    AfterFirstPermission();
                    register.setVisibility(View.GONE);
                    linelayout.setVisibility(View.GONE);
                }
                else
                {
                    register.setVisibility(View.VISIBLE);
                    linelayout.setVisibility(View.VISIBLE);
                }
                return;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            Intent intent = getIntent();
            if(intent != null && intent.hasExtra("email"))
            {
                mailid = intent.getStringExtra("email");
                password = intent.getStringExtra("pass");
            }
        }catch (Exception e)
        {

        }
        linelayout = (RelativeLayout)findViewById(R.id.layout_line) ;
        email = (EditText)findViewById(R.id.text_emailId);
        pass = (EditText)findViewById(R.id.text_password);
        forgottext = (TextView)findViewById(R.id.forgot_text);
        register = (Button)findViewById(R.id.login_button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_result_comment) ;
        email.setText(mailid);
        pass.setText(password);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            TakePermissions();
        }
        else
        {
            linelayout.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
        }

        forgottext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgotActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check().trim().length() == 0)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    register.setEnabled(false);
                    FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email.getText().toString().trim()
                                    ,pass.getText().toString().trim())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    register.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                    {
                                        SharedPreferences shared = getSharedPreferences("Profile", Context.MODE_PRIVATE);
                                        String dark = shared.getString("enter", "no");
                                        if(dark.equalsIgnoreCase("yes"))
                                        {
                                            SharedPreferences shared1 = getSharedPreferences("Region", Context.MODE_PRIVATE);
                                            String dark1 = shared1.getString("enter", "no");
                                            if(dark1.equalsIgnoreCase("yes"))
                                            {
                                                Intent intent = new Intent(getApplicationContext(),DataActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                Intent intent = new Intent(getApplicationContext(),RegionAvtivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        else
                                        {
                                            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        register.setEnabled(true);
                                        Toast.makeText(getApplicationContext(),"Please verify you email id.",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),Check().trim(),Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void AfterFirstPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("If you provide this permission than we can provide you a good user experience.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    public void TakePermissions()
    {
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed!")
                        .setMessage("You have to provide this permission because here you have to select your profile picture.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            } else
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);
        }
    }

    public String Check(){
        String s = "";
        if(email.getText().toString().trim().length() == 0)
        {
            s += "Please enter the email id.\n";
        }
        if(pass.getText().toString().trim().length() == 0)
        {
            s += "Please enter the password.\n";
        }
        if(pass.getText().toString().trim().length() < 6 || pass.getText().toString().trim().length() > 20)
        {
            s += "Minimum length of password is 6 and maximum is 20.\n";
        }
        return s;
    }
}
