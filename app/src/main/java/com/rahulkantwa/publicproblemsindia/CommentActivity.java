package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private String mainId = "",Id = "";
    private ImageView offimage,onimage;
    private EditText editText;
    private RecyclerView recyclerView;
    private List<CommentData> items = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private int totalcomments;
    private ArrayList<String> Ids = new ArrayList<>();
    private String Name,Url;
    private RelativeLayout relativeLayout,recyclerlayout;
    private String ids = "";
    private CountDownTimer countDownTimer;
    private int countnewproblem = 0;
    private String lastid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recyclerView = (RecyclerView)findViewById(R.id.comment_problem);
        offimage = (ImageView)findViewById(R.id.image_off);
        onimage = (ImageView)findViewById(R.id.image_on);
        editText = (EditText)findViewById(R.id.text_password);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        relativeLayout = (RelativeLayout)findViewById(R.id.progress_bar_layout_comment) ;
        recyclerlayout = (RelativeLayout)findViewById(R.id.recycler_layout) ;


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() != 0)
                {
                    offimage.setVisibility(View.GONE);
                    onimage.setVisibility(View.VISIBLE);
                }
                else
                {
                    offimage.setVisibility(View.VISIBLE);
                    onimage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        try {
            Intent intent = getIntent();
            mainId = intent.getStringExtra("mainId");
            Id = intent.getStringExtra("Id");
            ids = mainId;
            if(!mainId.equalsIgnoreCase(Id))
            {
                mainId = "allproblems";
            }
            LoadData();
            commentsAdapter = new CommentsAdapter(recyclerView,this,items);
            recyclerView.setAdapter(commentsAdapter);
            onimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final long tame = Long.parseLong("9999999999999") - System.currentTimeMillis();
                    CommentData commentData = new CommentData();
                    commentData.setComment(editText.getText().toString().trim());
                    editText.setText("");
                    commentData.setUrl(Url);
                    commentData.setName(Name);
                    FirebaseDatabase.getInstance().getReference("ProblemsComments")
                            .child(mainId)
                            .child(Id)
                            .child(tame + FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(commentData);
                    if(mainId.equalsIgnoreCase(Id))
                    {
                        final boolean[] Flag = {true};
                        final DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("LeaderCount")
                                .child(Id)
                                .child("Comment");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (Flag[0])
                                {
                                    if(snapshot.getValue() == null)
                                    {
                                        databaseReference.setValue("1");
                                    }
                                    else
                                    {
                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                        l += 1;
                                        databaseReference.setValue(l+"");
                                    }
                                    Flag[0] = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        final boolean[] Flag = {true};
                        final DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("Problems")
                                .child(mainId)
                                .child(Id)
                                .child("comments");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (Flag[0])
                                {
                                    if(snapshot.getValue() == null)
                                    {
                                        databaseReference.setValue("1");
                                    }
                                    else
                                    {
                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                        l += 1;
                                        databaseReference.setValue(l+"");
                                    }
                                    Flag[0] = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        final boolean[] Flag1 = {true};
                        final DatabaseReference databaseReference1 =  FirebaseDatabase.getInstance().getReference("Problems")
                                .child(ids)
                                .child(Id)
                                .child("comments");
                        databaseReference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (Flag1[0])
                                {
                                    if(snapshot.getValue() == null)
                                    {
                                        databaseReference1.setValue("1");
                                    }
                                    else
                                    {
                                        long l = Long.parseLong(snapshot.getValue(String.class));
                                        l += 1;
                                        databaseReference1.setValue(l+"");
                                    }
                                    Flag1[0] = false;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });

            commentsAdapter.setLoadMore(new ILoadMore() {
                @Override
                public void onLoadMore() {
                    if(items.size() < totalcomments)
                    {
                        final int[] c = {0};
                        items.add(null);
                        commentsAdapter.notifyItemInserted(items.size()-1);
                        countDownTimer = new CountDownTimer(60000, 100) {
                            public void onTick(long millisUntilFinished) {
                                if(!CommentsAdapter.userimages.contains(null))
                                {
                                    if (Ids.size() > 0)
                                    {
                                        FirebaseDatabase.getInstance().getReference("ProblemsComments")
                                                .child(mainId)
                                                .child(Id)
                                                .orderByKey()
                                                .startAt(lastid)
                                                .limitToFirst(6)
                                                .addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                        countnewproblem = 0;
                                                        if(c[0] == 0)
                                                        {
                                                            items.remove(items.size()-1);
                                                            commentsAdapter.notifyItemRemoved(items.size());
                                                            c[0] = 1;
                                                        }
                                                        if (!Ids.contains(snapshot.getKey()))
                                                        {
                                                            CommentsAdapter.userimages.add(null);
                                                            items.add(snapshot.getValue(CommentData.class));
                                                            if(snapshot.getKey().compareTo(lastid) > 0)
                                                            {
                                                                lastid = snapshot.getKey();
                                                            }
                                                            Ids.add(snapshot.getKey());
                                                            commentsAdapter.notifyDataSetChanged();
                                                            commentsAdapter.setLoaded();
                                                        }
                                                    }

                                                    @Override
                                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                                                });
                                    }
                                    countDownTimer.cancel();
                                }
                            }
                            public void onFinish() {
                                commentsAdapter.setLoaded();
                                items.remove(items.size()-1);
                                commentsAdapter.notifyItemRemoved(items.size());
                            }

                        };
                        countDownTimer.start();

                            }
                }
            });
        }catch (Exception e)
        {
            finish();
        }
    }

    public void LoadData()
    {
        CommentsAdapter.userimages.clear();
        final boolean[] Flag = {true};
        CountProblems(new FirebaseCallback() {
            @Override
            public void onCallback(String CNT) {
                totalcomments = Integer.parseInt(CNT);
                if(Flag[0])
                {
                    if(totalcomments == 0)
                    {
                        relativeLayout.setVisibility(View.GONE);
                        recyclerlayout.setVisibility(View.VISIBLE);
                    }
                        FirebaseDatabase.getInstance().getReference("ProblemsComments")
                                .child(mainId)
                                .child(Id)
                                .orderByKey()
                                .limitToFirst(5)
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {

                                        if(Ids.size() > 0)
                                        {
                                            if(Ids.get(0).compareTo(snapshot.getKey()) < 0)
                                            {
                                                CommentsAdapter.userimages.add(null);
                                                items.add(snapshot.getValue(CommentData.class));
                                                Ids.add(snapshot.getKey());
                                            }
                                            else
                                            {
                                                CommentsAdapter.userimages.add(null);
                                                    Ids.add(snapshot.getKey());
                                                    int position = items.size()-1;
                                                    if(items.get(position) != null)
                                                    {
                                                        items.add(snapshot.getValue(CommentData.class));
                                                    }
                                                    else
                                                    {
                                                        items.add(position,snapshot.getValue(CommentData.class));
                                                    }
                                            }
                                        }
                                        else
                                        {
                                            CommentsAdapter.userimages.add(null);
                                            items.add(snapshot.getValue(CommentData.class));
                                            Ids.add(snapshot.getKey());
                                            lastid = snapshot.getKey();
                                        }
                                        if(snapshot.getKey().compareTo(lastid) > 0)
                                        {
                                            lastid = snapshot.getKey();
                                        }
                                        commentsAdapter.notifyDataSetChanged();
                                        relativeLayout.setVisibility(View.GONE);
                                        recyclerlayout.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                                });
                    }
                    Flag[0] = false;
                }
        });

    }

    private interface FirebaseCallback
    {
        void onCallback(String CNT);
    }

    private interface FirebaseCallback1
    {
        void onCallback(String name,String url);
    }

    public void NameImage(final FirebaseCallback1 firebaseCallback1)
    {
        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileData profileData = snapshot.getValue(ProfileData.class);
                        firebaseCallback1.onCallback(profileData.getName(),profileData.getProfileurl());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void CountProblems(final FirebaseCallback firebaseCallback)
    {
        if(!mainId.equalsIgnoreCase(Id))
        {
            FirebaseDatabase.getInstance().getReference("Problems")
                    .child(mainId)
                    .child(Id)
                    .child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null)
                            {
                                firebaseCallback.onCallback(snapshot.getValue(String.class));
                            }
                            else {
                                firebaseCallback.onCallback("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            firebaseCallback.onCallback("0");
                        }
                    });
        }
        else
        {
            FirebaseDatabase.getInstance().getReference("LeaderCount")
                    .child(Id)
                    .child("Comment")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null)
                            {
                                firebaseCallback.onCallback(snapshot.getValue(String.class));
                            }
                            else
                            {
                                firebaseCallback.onCallback("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            firebaseCallback.onCallback("0");
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        NameImage(new FirebaseCallback1() {
            @Override
            public void onCallback(String name, String url) {
                Name = name;
                Url = url;
            }
        });
    }
}
