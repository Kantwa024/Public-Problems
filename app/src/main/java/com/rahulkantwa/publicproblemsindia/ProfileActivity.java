package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private EditText editText;
    private TextView counttext;
    private Button button;
    private Uri imageuri = null;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        circleImageView = (CircleImageView)findViewById(R.id.take_profile);
        editText = (EditText)findViewById(R.id.text_password);
        counttext = (TextView)findViewById(R.id.maxlen_register_pass);
        button = (Button)findViewById(R.id.login_button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_result_comment_1) ;


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                counttext.setText(60-s.toString().trim().length()+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(ProfileActivity.this);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageuri != null && editText.getText().toString().trim().length() != 0)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    button.setEnabled(false);
                    InputKey(v);
                    UploadProfile();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter your name and select your profile picture.",Toast.LENGTH_LONG).show();
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            TakePermissions();
        }
        else
        {
            button.setVisibility(View.VISIBLE);
        }
    }
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
                    button.setVisibility(View.GONE);
                }
                else
                {
                    button.setVisibility(View.VISIBLE);
                }

                return;
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == RESULT_OK)
        {
            Crop(CropImage.getPickImageResultUri(this,data));
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageuri = result.getUri();
                circleImageView.setImageURI(imageuri);
            }
        }
    }

    public void AfterFirstPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("If you provide this permission than we can provide you a good user experience.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfileActivity.this,
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
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed!")
                        .setMessage("You have to provide this permission because here you have to select your profile picture.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ProfileActivity.this,
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
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},100);
        }
    }

    public void Crop(Uri PrevUri)
    {
        try {
            CropImage.activity(PrevUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .start(ProfileActivity.this);
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public void UploadProfile()
    {
        File ImageFile = new File(SiliCompressor.with(ProfileActivity.this).
                compress(FileUtils.getPath(ProfileActivity.this,imageuri),new File(ProfileActivity.this.getCacheDir(),"temp1")));

        Uri uri = Uri.fromFile(ImageFile);

        StorageReference ref = FirebaseStorage.getInstance().getReference("Profiles").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirebaseStorage.getInstance().getReference("Profiles")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final ProfileData profileData = new ProfileData();
                                        profileData.setProfileurl(uri.toString());
                                        profileData.setName(editText.getText().toString().trim());
                                        FirebaseDatabase.getInstance().getReference("User")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(profileData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        SharedPreferences sharedPreferences = getSharedPreferences("Profile", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("enter","yes");
                                                        editor.commit();
                                                        Intent intent = new Intent(getApplicationContext(), RegionAvtivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressBar.setVisibility(View.GONE);
                                                        button.setEnabled(true);
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        button.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Failed to upload image. try again", Toast.LENGTH_SHORT).show();
                    }
                });
        ImageFile.delete();
    }
    public void InputKey(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),10);
    }
}
