package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private EditText email,pass;
    private TextView counttext;
    private Button register;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        email = (EditText)findViewById(R.id.text_emailId);
        pass = (EditText)findViewById(R.id.text_password);
        counttext = (TextView)findViewById(R.id.maxlen_register_pass);
        register = (Button)findViewById(R.id.login_button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_result_comment) ;

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counttext.setText(20-s.toString().trim().length()+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                            .createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseAuth.getInstance().getCurrentUser()
                                            .sendEmailVerification();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    intent.putExtra("email",email.getText().toString());
                                    intent.putExtra("pass",pass.getText().toString().trim());
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(),"We have sent you a mail, verify it.",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    register.setEnabled(true);
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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
