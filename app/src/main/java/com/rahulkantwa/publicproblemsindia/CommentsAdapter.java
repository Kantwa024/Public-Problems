package com.rahulkantwa.publicproblemsindia;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

class LoadingViewHolder1 extends RecyclerView.ViewHolder
{
    public ProgressBar progressBar;
    public LoadingViewHolder1(@NonNull View itemView) {
        super(itemView);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar_result_comment_2);
    }
}

class ItemViewHolder1 extends RecyclerView.ViewHolder
{
    public TextView username,comment;
    public CircleImageView userimage;
    public ItemViewHolder1(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.userName);
        comment = itemView.findViewById(R.id.comment_comment);
        userimage = itemView.findViewById(R.id.comment_user_profile);
    }
}

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<CommentData> items;
    int visibleThresold=5;
    int lastVisibleItem,totalItemCount;
    public static  ArrayList<Drawable> userimages = new ArrayList<>();


    public CommentsAdapter(RecyclerView recyclerView, Activity activity, List<CommentData> items)
    {
        this.activity = activity;
        this.items = items;

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
                    .inflate(R.layout.commentlayout,parent,false);
            return new ItemViewHolder1(view);
        }
        else if(viewType == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.progress_bar,parent,false);
            return new LoadingViewHolder1(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder1)
        {
            final ItemViewHolder1 viewHolder = (ItemViewHolder1)holder;
            CommentData commentData = items.get(position);
            viewHolder.comment.setText(commentData.getComment());
            viewHolder.username.setText(commentData.getName());
            viewHolder.userimage.setImageResource(R.drawable.errorimage);
            if(userimages.get(position) == null)
            {
                Glide.with(activity)
                        .load(Uri.parse(commentData.getUrl()))
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
            viewHolder.userimage.setEnabled(false);
        }
        else if(holder instanceof LoadingViewHolder1)
        {
            LoadingViewHolder1 loadingViewHolder = (LoadingViewHolder1)holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

}