package com.rahulkantwa.publicproblemsindia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

class LoadingViewHolder extends RecyclerView.ViewHolder
{
    public ProgressBar progressBar;
    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar_result_comment_2);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder
{
    public TextView username,time,like,comment,problem;
    public ImageButton likebutton,commentbutton;
    public CircleImageView userimage;
    public ImageView problemimage;
    public RelativeLayout relativeLayout;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
                relativeLayout = itemView.findViewById(R.id.line_1);
                username = itemView.findViewById(R.id.problem_user_name);
                time = itemView.findViewById(R.id.problem_time);
                like = itemView.findViewById(R.id.problem_likes);
                comment = itemView.findViewById(R.id.comments_count);
                problem = itemView.findViewById(R.id.problem);
                likebutton = itemView.findViewById(R.id.thumb_up);
                commentbutton = itemView.findViewById(R.id.problem_comment);
                userimage = itemView.findViewById(R.id.problem_user_profile);
                problemimage = itemView.findViewById(R.id.problem_image);
    }
}

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<ProblemData> items;
    int visibleThresold=2;
    int lastVisibleItem,totalItemCount;
    private ArrayList<String> Ids;
    public static String mainID = "";
    public static ArrayList<String> LikesStatement = new ArrayList<>();
    public static String mainIdType = "al";
    private ValueEventListener valueEventListener = null;
    public static  ArrayList<Drawable> problemimages = new ArrayList<>();
    public static  ArrayList<Drawable> userimages = new ArrayList<>();
    public MyAdapter(RecyclerView recyclerView, final Activity activity, List<ProblemData> items, ArrayList<String> Ids)
    {
        this.activity = activity;
        this.items = items;
        this.Ids = Ids;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThresold))
                {
                    if(loadMore != null)
                    {
                        loadMore.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return  items.get(position) == null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM)
        {
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.problemlayout,parent,false);
            return new ItemViewHolder(view);
        }
        else if(viewType == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.progress_bar,parent,false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            try {
                if (holder instanceof ItemViewHolder)
                {
                    final ProblemData problemData = items.get(position);
                    final ItemViewHolder viewHolder = (ItemViewHolder)holder;
                    if(items.size()-1 == position)
                    {
                        viewHolder.relativeLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        viewHolder.relativeLayout.setVisibility(View.VISIBLE);
                    }
                    if(LikesStatement.get(position).equalsIgnoreCase("null"))
                    {
                        LikesProblems(Ids.get(position), new FirebaseCallback() {
                            @Override
                            public void onCallback(String CNT) {
                                if(position < LikesStatement.size())
                                {
                                    if(CNT.equalsIgnoreCase("yes"))
                                    {
                                        viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                                    }
                                    else
                                    {
                                        viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                    }
                                    if(LikesStatement.get(position).equalsIgnoreCase("null"))
                                    {
                                        LikesStatement.set(position,CNT);
                                        Fun(viewHolder,position,problemData);
                                    }
                                }
                                viewHolder.likebutton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else
                    {
                        if(LikesStatement.get(position).equalsIgnoreCase("yes"))
                        {
                            viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                        }
                        else
                        {
                            viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        }
                        viewHolder.likebutton.setVisibility(View.VISIBLE);
                        Fun(viewHolder,position,problemData);
                    }
                    viewHolder.problemimage.setImageResource(R.drawable.errorimage);
                    if(problemimages.get(position) == null)
                    {
                        Glide.with(activity)
                                .load(Uri.parse(problemData.getImageurl()))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.errorimage)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                        if(position < problemimages.size())
                                        {
                                            if(problemimages.get(position) == null)
                                            {
                                                viewHolder.problemimage.setImageDrawable(resource);
                                                problemimages.remove(position);
                                                problemimages.add(position,resource);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                    }
                    else
                    {
                        viewHolder.problemimage.setImageDrawable(problemimages.get(position));
                    }
                    viewHolder.userimage.setImageResource(R.drawable.errorimage);
                    if(userimages.get(position) == null)
                    {
                        Glide.with(activity)
                                .load(Uri.parse(problemData.getUserurl()))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.errorimage)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        if(position < userimages.size())
                                        {
                                            if(userimages.get(position) == null)
                                            {
                                                viewHolder.userimage.setImageDrawable(resource);
                                                userimages.remove(position);
                                                userimages.add(position,resource);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                    }
                    else
                    {
                        viewHolder.userimage.setImageDrawable(userimages.get(position));
                    }
                    viewHolder.like.setText(problemData.getLikes());
                    viewHolder.comment.setText(problemData.getComments());
                    viewHolder.username.setText(problemData.getUsername());
                    if(mainIdType.equalsIgnoreCase("al"))
                    {
                        viewHolder.problem.setText(problemData.getPlace()+": "+problemData.getProblem());
                    }
                    else
                    {
                        viewHolder.problem.setText(problemData.getProblem());
                    }

                    viewHolder.commentbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity.getApplicationContext(),CommentActivity.class);
                            intent.putExtra("mainId",problemData.getName());
                            intent.putExtra("Id",Ids.get(position));
                            activity.startActivity(intent);
                        }
                    });

                    long time = Long.parseLong(problemData.getTime());
                    long systime = System.currentTimeMillis();
                    long sec = (systime-time)/1000;
                    if(sec < 60)
                    {
                        viewHolder.time.setText("Just now");
                    }
                    else if(sec/(60*60) < 1)
                    {
                        viewHolder.time.setText(sec/60+"m");
                    }
                    else if(sec/(60*60*24) < 1)
                    {
                        viewHolder.time.setText(sec/(60*60)+"h");
                    }
                    else if(sec/(60*60*24*366) < 1)
                    {
                        viewHolder.time.setText(sec/(60*60*24)+"d");
                    }
                    else
                    {
                        viewHolder.time.setText(sec/(60*60*24*366)+"d");
                    }
                }
                else if(holder instanceof LoadingViewHolder)
                {
                    LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
                    loadingViewHolder.progressBar.setIndeterminate(true);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void Fun(final ItemViewHolder viewHolder, final int position, final ProblemData problemData)
    {
        final boolean[] FunFlag = {true};
        viewHolder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    if(FunFlag[0])
                    {
                        if(LikesStatement.get(position).equalsIgnoreCase("yes"))
                        {
                            final boolean[] LikeFlag = {true};
                            FirebaseDatabase.getInstance().getReference("Problems")
                                    .child("allproblems")
                                    .child(Ids.get(position))
                                    .child("likes")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(LikeFlag[0])
                                            {
                                                if(snapshot.getValue() != null)
                                                {
                                                    long l = Long.parseLong(snapshot.getValue(String.class));
                                                    l -= 1;
                                                    if(l < 0)
                                                    {
                                                        l = 0;
                                                    }
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child("allproblems")
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue(l+"");
                                                }
                                                else
                                                {
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child("allproblems")
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue("0");
                                                }
                                                LikeFlag[0] = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            final boolean[] LikeFlag1 = {true};
                            FirebaseDatabase.getInstance().getReference("Problems")
                                    .child(problemData.getName())
                                    .child(Ids.get(position))
                                    .child("likes")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(LikeFlag1[0])
                                            {
                                                if(snapshot.getValue() != null)
                                                {
                                                    long l = Long.parseLong(snapshot.getValue(String.class));
                                                    l -= 1;
                                                    if(l < 0)
                                                    {
                                                        l = 0;
                                                    }
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child(problemData.getName())
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue(l+"");
                                                }
                                                else
                                                {
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child(problemData.getName())
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue("0");
                                                }
                                                LikeFlag1[0] = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                            LikesStatement.set(position,"no");
                            FirebaseDatabase.getInstance().getReference("Likes")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(Ids.get(position))
                                    .removeValue()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            LikesStatement.set(position,"yes");
                                            viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                                        }
                                    });
                        }
                        else
                        {
                            LikesStatement.set(position,"yes");
                            viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_yes_black_24dp);
                            FirebaseDatabase.getInstance().getReference("Likes")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(Ids.get(position))
                                    .setValue("yes")
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            LikesStatement.set(position,"no");
                                            viewHolder.likebutton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                        }
                                    });

                            final boolean[] LikeFlag = {true};
                            FirebaseDatabase.getInstance().getReference("Problems")
                                    .child("allproblems")
                                    .child(Ids.get(position))
                                    .child("likes")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(LikeFlag[0])
                                            {
                                                if(snapshot.getValue() != null)
                                                {
                                                    long l = Long.parseLong(snapshot.getValue(String.class));
                                                    l += 1;
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child("allproblems")
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue(l+"");
                                                }
                                                else
                                                {
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child("allproblems")
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue("1");
                                                }
                                                LikeFlag[0] = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            final boolean[] LikeFlag1 = {true};
                            FirebaseDatabase.getInstance().getReference("Problems")
                                    .child(problemData.getName())
                                    .child(Ids.get(position))
                                    .child("likes")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(LikeFlag1[0])
                                            {
                                                if(snapshot.getValue() != null)
                                                {
                                                    long l = Long.parseLong(snapshot.getValue(String.class));
                                                    l += 1;
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child(problemData.getName())
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue(l+"");
                                                }
                                                else
                                                {
                                                    FirebaseDatabase.getInstance().getReference("Problems")
                                                            .child(problemData.getName())
                                                            .child(Ids.get(position))
                                                            .child("likes")
                                                            .setValue("1");
                                                }
                                                LikeFlag1[0] = false;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        FunFlag[0] = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FunFlag[0] = true;
                            }
                        },1000);
                    }
                }

            }
        });
    }
    private interface FirebaseCallback
    {
        void onCallback(String CNT);
    }

    public void LikesProblems(String id, final FirebaseCallback firebaseCallback)
    {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = "no";
                if(snapshot.getValue() != null)
                {
                    s = "yes";
                }
                firebaseCallback.onCallback(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference("Likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(id)
                .addValueEventListener(valueEventListener);


    }

}