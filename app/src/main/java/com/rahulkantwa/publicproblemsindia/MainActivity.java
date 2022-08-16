package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if(FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
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
                        },1000);

                    }

                    else
                    {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(),LoginShowPageActivitity.class);
                    startActivity(intent);
                    finish();
                }
            }

}
