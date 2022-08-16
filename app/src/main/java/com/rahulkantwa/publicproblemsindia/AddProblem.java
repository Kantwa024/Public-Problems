package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class AddProblem extends AppCompatActivity {
    private Uri imageurl = null;
    private ImageView problemimage;
    private Button uploadproblem;
    private EditText editproblem;
    private String Id,Types,name;
    private ProgressBar progressBar;
    private TextView length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_problem);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("id"))
            {
                Id = intent.getStringExtra("id");
                Types = intent.getStringExtra("type");
                name = intent.getStringExtra("name");
                if (name.equalsIgnoreCase(""))
                {
                    if(Types.equalsIgnoreCase("c"))
                    {
                        name = "India";
                    }else if(Types.equalsIgnoreCase("s"))
                    {
                        name = DataActivity.State;
                    }
                    else if(Types.equalsIgnoreCase("d"))
                    {
                        name = DataActivity.State+", "+DataActivity.District;
                    }
                    else
                    {
                        name = DataActivity.State+", "+DataActivity.District+", "+DataActivity.Tehsil;
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            finish();
        }

        length = (TextView)findViewById(R.id.maxlen_problem) ;
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_result_comment_1) ;
        uploadproblem = (Button)findViewById(R.id.upload_button);
        problemimage = (ImageView)findViewById(R.id.problem_image) ;
        editproblem = (EditText)findViewById(R.id.problem_text);

        editproblem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                length.setText(300-s.toString().trim().length()+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        problemimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(AddProblem.this);
            }
        });

        uploadproblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageurl != null && editproblem.getText().toString().trim().length() >0 && editproblem.getText().toString().trim().length() <= 300)
                {
                    UploadproblemImage();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Maximum length of problem text is 300 and minimum is 1, Also upload image for your problem",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void UploadproblemImage()
    {
        finish();
        InputKey(getCurrentFocus());
        progressBar.setVisibility(View.VISIBLE);
        final String timestr = System.currentTimeMillis()+"";
        final long tame = Long.parseLong("9999999999999") - System.currentTimeMillis();
        File ImageFile = new File(SiliCompressor.with(AddProblem.this).
                compress(FileUtils.getPath(AddProblem.this,imageurl),new File(AddProblem.this.getCacheDir(),"temp1")));

        Uri uri = Uri.fromFile(ImageFile);

        StorageReference ref = FirebaseStorage.getInstance().getReference("Problems").
                child(Id).
                child(tame+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirebaseStorage.getInstance().getReference("Problems")
                                .child(Id)
                                .child(tame+FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        FirebaseDatabase.getInstance().getReference("User")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        ProblemData problemData = new ProblemData();
                                                        ProfileData profileData = snapshot.getValue(ProfileData.class);
                                                        problemData.setUsername(profileData.getName());
                                                        problemData.setUserurl(profileData.getProfileurl());
                                                        problemData.setTime(timestr);
                                                        problemData.setLikes("0");
                                                        problemData.setComments("0");
                                                        problemData.setImageurl(uri.toString());
                                                        problemData.setName(Id);
                                                        problemData.setProblem(editproblem.getText().toString().trim());
                                                        problemData.setType(Types);
                                                        problemData.setPlace(name);
                                                        FirebaseDatabase.getInstance().getReference("Problems")
                                                                .child(Id)
                                                                .child(tame+FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .setValue(problemData);
                                                        problemData.setType("al");
                                                        FirebaseDatabase.getInstance().getReference("Problems")
                                                                .child("allproblems")
                                                                .child(tame+FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .setValue(problemData);
                                                        final boolean[] CountFlag = {true};
                                                                FirebaseDatabase.getInstance().getReference("Count")
                                                                .child(Id)
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if(CountFlag[0])
                                                                        {
                                                                            if (snapshot.getValue() == null)
                                                                            {
                                                                                FirebaseDatabase.getInstance().getReference("Count")
                                                                                        .child(Id)
                                                                                        .setValue("1");
                                                                            }
                                                                            else
                                                                            {
                                                                                long l = Long.parseLong(snapshot.getValue(String.class));
                                                                                l += 1;
                                                                                FirebaseDatabase.getInstance().getReference("Count")
                                                                                        .child(Id)
                                                                                        .setValue(l+"");
                                                                            }
                                                                            CountFlag[0] = false;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                        final boolean[] CountFlag2 = {true};
                                                        FirebaseDatabase.getInstance().getReference("Count")
                                                                .child("allproblems")
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if(CountFlag2[0])
                                                                        {
                                                                            if (snapshot.getValue() == null)
                                                                            {
                                                                                FirebaseDatabase.getInstance().getReference("Count")
                                                                                        .child("allproblems")
                                                                                        .setValue("1");
                                                                            }
                                                                            else
                                                                            {
                                                                                long l = Long.parseLong(snapshot.getValue(String.class));
                                                                                l += 1;
                                                                                FirebaseDatabase.getInstance().getReference("Count")
                                                                                        .child("allproblems")
                                                                                        .setValue(l+"");
                                                                            }
                                                                            CountFlag2[0] = false;
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image. try again", Toast.LENGTH_SHORT).show();
                    }
                });
        ImageFile.delete();
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
                imageurl = result.getUri();
                problemimage.setImageURI(imageurl);
            }
        }
    }
    public void Crop(Uri PrevUri)
    {
        try {
            CropImage.activity(PrevUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .start(AddProblem.this);
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    public void InputKey(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),10);
    }
}
