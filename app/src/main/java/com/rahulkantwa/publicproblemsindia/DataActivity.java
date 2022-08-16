package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Region;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RelativeLayout problemslayout,progressbarlayout,bootomlayout,noproblemlayout;
    private RecyclerView problemsrecyclerview;
    private ImageView menu,add;
    private TextView name;
    public static String Id = "allproblems";
    private List<ProblemData> problemDataList = new ArrayList<>();
    private MyAdapter myAdapter;
    private ArrayList<String> IDS = new ArrayList<>();
    private int totalproblems = 0;
    private String Types = "al";
    public static String State,District,Tehsil;
    private TextView yes,no,comment;
    private ImageButton yesbutton,nobutton,commentbutton;
    public static  final ChildEventListener[] childEventListener = {null};
    public static  ChildEventListener childEventListenerload = null;
    private String longname = "";
    private CountDownTimer countDownTimer;
    private int countnewproblem = 0;
    private String lastid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        noproblemlayout = (RelativeLayout)findViewById(R.id.noproblem_layout) ;
        navigationView = (NavigationView)findViewById(R.id.naviview);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        problemslayout = (RelativeLayout)findViewById(R.id.problem_layout);
        progressbarlayout = (RelativeLayout) findViewById(R.id.progress_bar_layout_comment);
        problemsrecyclerview = (RecyclerView)findViewById(R.id.problems_recycler_view);
        menu = (ImageView)findViewById(R.id.menu);
        add = (ImageView)findViewById(R.id.add_problem);
        name = (TextView)findViewById(R.id.name_place);
        bootomlayout = (RelativeLayout)findViewById(R.id.first_line) ;
        yes = (TextView)findViewById(R.id.yes_text) ;
        no = (TextView)findViewById(R.id.no_text) ;
        comment = (TextView)findViewById(R.id.comment_text) ;
        yesbutton = (ImageButton) findViewById(R.id.thumb_up);
        nobutton = (ImageButton)findViewById(R.id.thumb_down);
        commentbutton = (ImageButton)findViewById(R.id.comment);
        final View header = navigationView.getHeaderView(0);
        final TextView headername = header.findViewById(R.id.header_username);
        TextView headermail = header.findViewById(R.id.header_mailid);
        final CircleImageView headerimage = header.findViewById(R.id.profile_header);
        try {
            headermail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        try {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("id"))
            {
                Id = intent.getStringExtra("id");
                Types = intent.getStringExtra("type");
                name.setText(intent.getStringExtra("name"));
                longname = intent.getStringExtra("longname");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileData profileData = snapshot.getValue(ProfileData.class);
                        Glide.with(header)
                                .load(Uri.parse(profileData.profileurl))
                                .into(headerimage);
                        headername.setText(profileData.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        LoadData(Id);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        problemsrecyclerview.setLayoutManager(linearLayoutManager);
        MyAdapter.mainID = Id;
        MyAdapter.mainIdType = "al";
        myAdapter = new MyAdapter(problemsrecyclerview, this,problemDataList,IDS);
        problemsrecyclerview.setAdapter(myAdapter);
        Leader();


                Region(new FirebaseCallback1() {
            @Override
            public void onCallback(String state, String district, String tehsil) {
                State = state;
                District = district;
                Tehsil = tehsil;
                String sss = "";
                String s = State+District;
                if(!Tehsil.equalsIgnoreCase("null"))
                {
                    s += Tehsil;
                }
                String[] ss = s.split(" ");
                for(int i = 0; i < ss.length; i++)
                {
                    sss += ss[i].trim().toLowerCase();
                }

                FirebaseDatabase.getInstance().getReference("Token")
                        .child(sss)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(FirebaseInstanceId.getInstance().getToken());
            }
        });



        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddProblem.class);
                intent.putExtra("id",Id);
                intent.putExtra("type",Types);
                intent.putExtra("name",longname);
                startActivity(intent);
            }
        });

        try {
            myAdapter.setLoadMore(new ILoadMore() {
                @Override
                public void onLoadMore() {
                    if(problemDataList.size() < totalproblems && problemDataList.size() != 0 && IDS.size() != 0)
                    {
                        final int[] c = {0};
                        problemDataList.add(null);
                        myAdapter.notifyItemInserted(problemDataList.size()-1);
                        childEventListenerload = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                                if(snapshot.getKey().compareTo(lastid) > 0)
                                {
                                    lastid = snapshot.getKey();
                                }
                                else
                                {
//                                    lastid = lastid
                                }
                                if(c[0] == 0)
                                {
                                    problemDataList.remove(problemDataList.size()-1);
                                    myAdapter.notifyItemRemoved(problemDataList.size());
                                    c[0] = 1;
                                }
                                else
                                {
                                    if(!IDS.contains(snapshot.getKey()))
                                    {
                                        MyAdapter.problemimages.add(null);
                                        MyAdapter.userimages.add(null);
                                        MyAdapter.LikesStatement.add("null");
                                        problemDataList.add(snapshot.getValue(ProblemData.class));
                                        IDS.add(snapshot.getKey());
                                        myAdapter.notifyDataSetChanged();
                                        myAdapter.setLoaded();
                                    }

                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                                final int lnth = IDS.indexOf(snapshot.getKey());
                                problemDataList.set(lnth,snapshot.getValue(ProblemData.class));
                                myAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };

                         countDownTimer = new CountDownTimer(60000, 100) {
                            public void onTick(long millisUntilFinished) {
                                if(!MyAdapter.problemimages.contains(null) && !MyAdapter.userimages.contains(null))
                                {
                                    if (IDS.size() > 0)
                                    {
                                        FirebaseDatabase.getInstance().getReference("Problems")
                                                .child(Id)
                                                .orderByKey()
                                                .startAt(lastid)
                                                .limitToFirst(2)
                                                .addChildEventListener(childEventListenerload);
                                        countnewproblem = 0;
                                    }
                                    countDownTimer.cancel();
                                }
                            }
                            public void onFinish() {
                                myAdapter.setLoaded();
                                problemDataList.remove(problemDataList.size()-1);
                                myAdapter.notifyItemRemoved(problemDataList.size());
                            }

                        };
                         countDownTimer.start();
                    }
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (item.getItemId() == R.id.search_location_menu)
                {
                    Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.about)
                {
                    Intent intent = new Intent(getApplicationContext(),AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.india_menu)
                {
                    longname = "";
                    if(childEventListener != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListener[0]);
                    }
                    if(childEventListenerload != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListenerload);
                    }
                    Id = "india";
                    Types = "c";
                    LoadData("india");
                    name.setText("India");
                    add.setVisibility(View.VISIBLE);
                    Leader();
                    MyAdapter.mainID = Id;

                }
                if (item.getItemId() == R.id.State_location_menu)
                {
                    longname = "";
                    if(childEventListener != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListener[0]);
                    }
                    if(childEventListenerload != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListenerload);
                    }
                    Id = State.trim().toLowerCase();
                    Types = "s";
                    LoadData(State.trim().toLowerCase());
                    name.setText(State);
                    add.setVisibility(View.VISIBLE);
                    Leader();
                    MyAdapter.mainID = Id;

                }
                if (item.getItemId() == R.id.District_location_menu)
                {
                    longname = "";
                    if(childEventListener != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListener[0]);
                    }
                    if(childEventListenerload != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListenerload);
                    }
                    String sss = "";
                    String s = State+District;
                    String[] ss = s.split(" ");
                    for(int i = 0; i < ss.length; i++)
                    {
                        sss += ss[i].trim().toLowerCase();
                    }
                    Id = sss;
                    Types = "d";
                    LoadData(sss);
                    name.setText(District);
                    add.setVisibility(View.VISIBLE);
                    Leader();
                    MyAdapter.mainID = Id;

                }
                if (item.getItemId() == R.id.Tehsil_location_menu)
                {
                    if(!Tehsil.equalsIgnoreCase("null"))
                    {
                        longname = "";
                        if(childEventListener != null)
                        {
                            FirebaseDatabase.getInstance().getReference("Problems")
                                    .child(Id)
                                    .removeEventListener(childEventListener[0]);
                        }
                        if(childEventListenerload != null)
                        {
                            FirebaseDatabase.getInstance().getReference("Problems")
                                    .child(Id)
                                    .removeEventListener(childEventListenerload);
                        }
                        String sss = "";
                        String s = State+District+Tehsil;
                        String[] ss = s.split(" ");
                        for(int i = 0; i < ss.length; i++)
                        {
                            sss += ss[i].trim().toLowerCase();
                        }
                        Id = sss;
                        Types = "t";
                        LoadData(sss);
                        name.setText(Tehsil);
                        add.setVisibility(View.VISIBLE);
                        Leader();
                        MyAdapter.mainID = Id;

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No result found",Toast.LENGTH_LONG).show();
                    }
                }
                if (item.getItemId() == R.id.all_location_menu)
                {
                    if(childEventListener != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListener[0]);
                    }
                    if(childEventListenerload != null)
                    {
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(Id)
                                .removeEventListener(childEventListenerload);
                    }
                    Types = "al";
                    Id = "allproblems";
                    LoadData(Id);
                    name.setText("All Problems");
                    Leader();
                    MyAdapter.mainID = Id;

                }
                return true;
            }
        });
    }

    public void Leader()
    {
        final boolean[] yesFlag = {true};
        final boolean[] noFlag = {true};
        final int[] y = {0};
        final int[] n = {0};
        yesbutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        nobutton.setImageResource(R.drawable.ic_thumb_down_black_24dp);

        FirebaseDatabase.getInstance().getReference("Yes")
                .child(Id)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null)
                        {
                            yesbutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                            y[0] = 1;
                        }
                        else
                        {
                            yesbutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                            y[0] = 0;
                        }
                        yesbutton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("No")
                .child(Id)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null)
                        {
                            nobutton.setImageResource(R.drawable.ic_thumb_down_yes_black_24dp);
                            n[0] = 1;
                        }
                        else
                        {
                            nobutton.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                            n[0] = 0;
                        }
                        nobutton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LeaderCount")
                .child(Id);
        reference.child("Up")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null)
                        {
                            yes.setText(snapshot.getValue(String.class)+" Yes");
                        }
                        else
                        {
                            yes.setText("0 Yes");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        reference.child("Down")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null)
                        {
                            no.setText(snapshot.getValue(String.class)+" No");
                        }
                        else
                        {
                            no.setText("0 No");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        reference.child("Comment")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null)
                        {
                            long l = Long.parseLong(snapshot.getValue(String.class));
                            if(l > 1)
                            {
                                comment.setText(snapshot.getValue(String.class)+" Comments");
                            }
                            else
                            {
                                comment.setText(snapshot.getValue(String.class)+" Comment");
                            }
                        }
                        else
                        {
                            comment.setText("0 Comment");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesFlag[0])
                {
                    if (y[0] == 0)
                    {
                        if(n[0] == 1)
                        {
                            final boolean[] Flag = {true};
                            reference.child("Down").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(Flag[0])
                                    {
                                        if(snapshot.getValue() != null)
                                        {
                                            long l = Long.parseLong(snapshot.getValue(String.class));
                                            reference.child("Down").setValue(l-1+"");
                                        }
                                        else
                                        {
                                            reference.child("Down")
                                                    .setValue("0");
                                        }
                                        Flag[0] = false;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        yesbutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                        nobutton.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                        FirebaseDatabase.getInstance().getReference("Yes")
                                .child(Id)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("yes")
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        yesbutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                        nobutton.setImageResource(R.drawable.ic_thumb_down_yes_black_24dp);
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference("No")
                                                .child(Id)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .removeValue();
                                        final boolean[] Flag = {true};
                                        reference.child("Up").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(Flag[0])
                                                {
                                                    if(snapshot.getValue() != null)
                                                    {
                                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                                        reference.child("Up").setValue(l+1+"");
                                                    }
                                                    else
                                                    {
                                                        reference.child("Up")
                                                                .setValue("1");
                                                    }
                                                    Flag[0] = false;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference("Yes")
                                .child(Id)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();
                        final boolean[] Flag = {true};
                        reference.child("Up").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(Flag[0])
                                {
                                    if(snapshot.getValue() != null)
                                    {
                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                        reference.child("Up").setValue(l-1+"");
                                    }
                                    else
                                    {
                                        reference.child("Up")
                                                .setValue("0");
                                    }
                                    Flag[0] = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    yesFlag[0] = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            yesFlag[0] = true;
                        }
                    },1000);
                }
            }
        });

        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noFlag[0])
                {
                    if (n[0] == 0)
                    {
                        if(y[0] == 1)
                        {
                            final boolean[] Flag = {true};
                            reference.child("Up").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(Flag[0])
                                    {
                                        if(snapshot.getValue() != null)
                                        {
                                            long l = Long.parseLong(snapshot.getValue(String.class));
                                            reference.child("Up").setValue(l-1+"");
                                        }
                                        else
                                        {
                                            reference.child("Up")
                                                    .setValue("0");
                                        }
                                        Flag[0] = false;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        nobutton.setImageResource(R.drawable.ic_thumb_down_yes_black_24dp);
                        yesbutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        FirebaseDatabase.getInstance().getReference("No")
                                .child(Id)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("no")
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        nobutton.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                                        yesbutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference("Yes")
                                                .child(Id)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .removeValue();
                                        final boolean[] Flag = {true};
                                        reference.child("Down").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(Flag[0])
                                                {
                                                    if(snapshot.getValue() != null)
                                                    {
                                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                                        reference.child("Down").setValue(l+1+"");
                                                    }
                                                    else
                                                    {
                                                        reference.child("Down")
                                                                .setValue("1");
                                                    }
                                                    Flag[0] = false;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference("No")
                                .child(Id)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();

                        final boolean[] Flag = {true};
                        reference.child("Down").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(Flag[0])
                                {
                                    if(snapshot.getValue() != null)
                                    {
                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                        reference.child("Down").setValue(l-1+"");
                                    }
                                    else
                                    {
                                        reference.child("Down")
                                                .setValue("0");
                                    }
                                    Flag[0] = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    noFlag[0] = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            noFlag[0] = true;
                        }
                    },1000);
                }
            }
        });

        commentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CommentActivity.class);
                intent.putExtra("mainId",Id);
                intent.putExtra("Id",Id);
                startActivity(intent);
            }
        });

        if(name.getText().toString().equalsIgnoreCase("All Problems"))
        {
            add.setVisibility(View.GONE);
            bootomlayout.setVisibility(View.GONE);
        }
        else
        {
            add.setVisibility(View.VISIBLE);
            bootomlayout.setVisibility(View.VISIBLE);
        }

    }



    public void LoadData(final String ids)
    {
        try {
            if(myAdapter != null)
            {
                myAdapter.setLoaded();
            }
            lastid = "";
            progressbarlayout.setVisibility(View.VISIBLE);
            problemslayout.setVisibility(View.GONE);
            final boolean[] LoadDataFlag = {true};
            CountProblems(ids, new FirebaseCallback() {
                @Override
                public void onCallback(String CNT) {
                    totalproblems = Integer.parseInt(CNT);
                    if(totalproblems == 0)
                    {
                        if(Id.equalsIgnoreCase("allproblems"))
                        {
                            noproblemlayout.setVisibility(View.GONE);
                        }
                        else
                        {
                            noproblemlayout.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        noproblemlayout.setVisibility(View.GONE);
                    }
                    if (LoadDataFlag[0])
                    {
                        countnewproblem = 0;
                        MyAdapter.LikesStatement.clear();
                        MyAdapter.mainIdType = Types;
                        MyAdapter.problemimages.clear();
                        MyAdapter.userimages.clear();
                        problemDataList.clear();
                        IDS.clear();
                        problemsrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        childEventListener[0] = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                                String key = snapshot.getKey();
                                if(key.compareTo(lastid) > 0)
                                {
                                    lastid = key;
                                }
                                if(IDS.size() > 0)
                                {
                                    if(IDS.get(0).compareTo(key) < 0)
                                    {
                                        MyAdapter.problemimages.add(null);
                                        MyAdapter.userimages.add(null);
                                        MyAdapter.LikesStatement.add("null");
                                        problemDataList.add(snapshot.getValue(ProblemData.class));
                                        IDS.add(key);
                                    }
                                    else
                                    {
                                        if(key.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        {
                                            MyAdapter.problemimages.add(null);
                                            MyAdapter.userimages.add(null);
                                            MyAdapter.LikesStatement.add("null");
                                            IDS.add(key);
                                            if(problemDataList.get(problemDataList.size()-1) != null)
                                            {
                                                problemDataList.add(snapshot.getValue(ProblemData.class));
                                            }
                                            else
                                            {
                                                int position = problemDataList.size()-1;
                                                problemDataList.add(position,snapshot.getValue(ProblemData.class));
                                            }

                                        }
                                    }
                                }
                                else
                                {
                                    MyAdapter.problemimages.add(null);
                                    MyAdapter.userimages.add(null);
                                    MyAdapter.LikesStatement.add("null");
                                    problemDataList.add(snapshot.getValue(ProblemData.class));
                                    IDS.add(key);
                                }
                                myAdapter.notifyDataSetChanged();
                                progressbarlayout.setVisibility(View.GONE);
                                problemslayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onChildChanged(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                                final int lnth = IDS.indexOf(snapshot.getKey());
                                problemDataList.set(lnth,snapshot.getValue(ProblemData.class));
                                myAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        FirebaseDatabase.getInstance().getReference("Problems")
                                .child(ids)
                                .orderByKey()
                                .limitToFirst(1)
                                .addChildEventListener(childEventListener[0]);
                        if(totalproblems == 0)
                        {
                            progressbarlayout.setVisibility(View.GONE);
                            problemslayout.setVisibility(View.VISIBLE);
                        }
                        LoadDataFlag[0] = false;
                    }
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public void CountProblems(String id, final FirebaseCallback firebaseCallback)
    {
        FirebaseDatabase.getInstance().getReference("Count")
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String COUNT = "0";
                        if(snapshot.getValue() != null)
                        {
                            COUNT = snapshot.getValue(String.class);
                        }
                        firebaseCallback.onCallback(COUNT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        firebaseCallback.onCallback("0");
                    }
                });
    }


    private interface FirebaseCallback
    {
        void onCallback(String CNT);
    }

    private interface FirebaseCallback1
    {
        void onCallback(String state,String district,String tehsil);
    }


    public void Region(final FirebaseCallback1 firebaseCallback1)
    {
        FirebaseDatabase.getInstance().getReference("Region")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null)
                        {
                            RegionData regionData = snapshot.getValue(RegionData.class);
                            firebaseCallback1.onCallback(regionData.getState(),regionData.getDistrict(),regionData.getTehsil());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
