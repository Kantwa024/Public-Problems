package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegionAvtivity extends AppCompatActivity {
    private Spinner state,district,tehsil;
    private Button button;
    private RelativeLayout relativeLayout,regonrelativelayout;
    private ProgressBar progressBar;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_avtivity);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textView = (TextView)findViewById(R.id.tehsil) ;
        button = (Button)findViewById(R.id.login_button);
        state = (Spinner)findViewById(R.id.state_spinner);
        district = (Spinner)findViewById(R.id.district_spinner);
        tehsil = (Spinner)findViewById(R.id.tehsil_spinner);
        relativeLayout = (RelativeLayout)findViewById(R.id.progress_bar_layout_region) ;
        regonrelativelayout = (RelativeLayout) findViewById(R.id.layout_region);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_result_comment_1) ;
        StateData();

    }

    public void StateData()
    {
        FirebaseDatabase.getInstance().getReference("Login")
                .child("India")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String[] st = {""};
                        final String[] d = {""};
                        if (snapshot.getValue() != null)
                        {
                            final String[] s = snapshot.getValue(String.class).split("-");
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegionAvtivity.this
                                    ,R.layout.spinneritem,s);
                            state.setAdapter(arrayAdapter);
                            regonrelativelayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    relativeLayout.setVisibility(View.GONE);
                                    regonrelativelayout.setVisibility(View.VISIBLE);
                                }
                            },950);
                            state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    st[0] = s[position];
                                    FirebaseDatabase.getInstance().getReference("Login")
                                            .child(s[position])
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.getValue() != null)
                                                    {
                                                        final String[] s1 = snapshot.getValue(String.class).split("-");
                                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegionAvtivity.this
                                                                ,R.layout.spinneritem,s1);
                                                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                        district.setAdapter(arrayAdapter);

                                                        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                d[0] = s1[position];
                                                                FirebaseDatabase.getInstance().getReference("Login")
                                                                        .child(s1[position])
                                                                        .addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.getValue() != null)
                                                                                {
                                                                                    textView.setVisibility(View.VISIBLE);
                                                                                    tehsil.setVisibility(View.VISIBLE);
                                                                                    final String[] s2 = snapshot.getValue(String.class).split("-");
                                                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RegionAvtivity.this
                                                                                            ,R.layout.spinneritem,s2);
                                                                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                                    tehsil.setAdapter(arrayAdapter);

                                                                                    tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                        @Override
                                                                                        public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                                                                                            button.setOnClickListener(new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View v) {
                                                                                                    button.setEnabled(false);
                                                                                                    progressBar.setVisibility(View.VISIBLE);
                                                                                                    RegionData regionData = new RegionData();
                                                                                                    regionData.setState(st[0]);
                                                                                                    regionData.setDistrict(d[0]);
                                                                                                    regionData.setTehsil(s2[position]);

                                                                                                    FirebaseDatabase.getInstance().getReference("Region")
                                                                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                            .setValue(regionData)
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    FirebaseMessaging.getInstance().subscribeToTopic("india");
                                                                                                                    FirebaseMessaging.getInstance().subscribeToTopic(st[0].trim().toLowerCase());
                                                                                                                    String sss = "";
                                                                                                                    String s = st[0]+d[0];
                                                                                                                    String[] ss = s.split(" ");
                                                                                                                    for(int i = 0; i < ss.length; i++)
                                                                                                                    {
                                                                                                                        sss += ss[i].trim().toLowerCase();
                                                                                                                    }
                                                                                                                    FirebaseMessaging.getInstance().subscribeToTopic(sss);
                                                                                                                    sss = "";
                                                                                                                    s = st[0]+d[0]+s2[position];
                                                                                                                    String[] ss1 = s.split(" ");
                                                                                                                    for(int i = 0; i < ss1.length; i++)
                                                                                                                    {
                                                                                                                        sss += ss1[i].trim().toLowerCase();
                                                                                                                    }
                                                                                                                    FirebaseDatabase.getInstance().getReference("Token")
                                                                                                                            .child(sss)
                                                                                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                                            .setValue(FirebaseInstanceId.getInstance().getToken());
                                                                                                                    SharedPreferences sharedPreferences = getSharedPreferences("Region", Context.MODE_PRIVATE);
                                                                                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                                                    editor.putString("enter","yes");
                                                                                                                    editor.commit();
                                                                                                                    Intent intent = new Intent(getApplicationContext(), DataActivity.class);
                                                                                                                    startActivity(intent);
                                                                                                                    finish();
                                                                                                                }
                                                                                                            })
                                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                                @Override
                                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                                                                                    button.setEnabled(true);
                                                                                                                    progressBar.setVisibility(View.GONE);
                                                                                                                    relativeLayout.setVisibility(View.GONE);
                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            });
                                                                                        }

                                                                                        @Override
                                                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                                                        }
                                                                                    });
                                                                                }
                                                                                else
                                                                                {
                                                                                    tehsil.setVisibility(View.GONE);
                                                                                    textView.setVisibility(View.GONE);
                                                                                    button.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            button.setEnabled(false);
                                                                                            RegionData regionData = new RegionData();
                                                                                            regionData.setState(st[0]);
                                                                                            regionData.setDistrict(d[0]);
                                                                                            regionData.setTehsil("null");

                                                                                            FirebaseDatabase.getInstance().getReference("Region")
                                                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                    .setValue(regionData)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            FirebaseMessaging.getInstance().subscribeToTopic("india");
                                                                                                            FirebaseMessaging.getInstance().subscribeToTopic(st[0].trim().toLowerCase());
                                                                                                            String sss = "";
                                                                                                            String s = st[0]+d[0];
                                                                                                            String[] ss = s.split(" ");
                                                                                                            for(int i = 0; i < ss.length; i++)
                                                                                                            {
                                                                                                                sss += ss[i].trim().toLowerCase();
                                                                                                            }
                                                                                                            FirebaseMessaging.getInstance().subscribeToTopic(sss);
                                                                                                            SharedPreferences sharedPreferences = getSharedPreferences("Region", Context.MODE_PRIVATE);
                                                                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                                            editor.putString("enter","yes");
                                                                                                            editor.commit();
                                                                                                            Intent intent = new Intent(getApplicationContext(), DataActivity.class);
                                                                                                            startActivity(intent);
                                                                                                            finish();
                                                                                                        }
                                                                                                    })
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                                                                            button.setEnabled(true);
                                                                                                            progressBar.setVisibility(View.GONE);
                                                                                                            relativeLayout.setVisibility(View.GONE);
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });

                                                                                }

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                                relativeLayout.setVisibility(View.GONE);
                                                                                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> parent) {

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    relativeLayout.setVisibility(View.VISIBLE);
                                                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        relativeLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}
