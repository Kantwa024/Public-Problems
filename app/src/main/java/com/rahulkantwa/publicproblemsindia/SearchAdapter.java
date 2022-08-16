package com.rahulkantwa.publicproblemsindia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<String> searchdata;

    public SearchAdapter(Activity activity, ArrayList<String> searchdata){
        this.activity = activity;
        this.searchdata = searchdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.searchresult,parent,false);
        return new SearchAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textView.setText(searchdata.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataActivity.childEventListener != null)
                {
                    FirebaseDatabase.getInstance().getReference("Problems")
                            .child(DataActivity.Id)
                            .removeEventListener(DataActivity.childEventListener[0]);
                }
                if(DataActivity.childEventListenerload != null)
                {
                    FirebaseDatabase.getInstance().getReference("Problems")
                            .child(DataActivity.Id)
                            .removeEventListener(DataActivity.childEventListenerload);
                }
                String Types = "";
                String s = "";
                String[] splits = searchdata.get(position).trim().split(",");
                if (splits.length  == 1)
                {
                    Types = "s";
                }else if(splits.length  == 2)
                {
                    Types = "d";
                }
                else
                {
                    Types = "t";
                }
                for(int i = 0; i < splits.length; i++)
                {
                    String ms = "";
                    String[] splitms = splits[i].trim().split(" ");
                    for(int j = 0;j < splitms.length; j++)
                    {
                        ms += splitms[j].trim().toLowerCase();
                    }
                    s +=  ms;
                }
                Intent intent = new Intent(activity,DataActivity.class);
                intent.putExtra("name",splits[splits.length-1]);
                intent.putExtra("longname",searchdata.get(position).trim());
                intent.putExtra("id",s);
                intent.putExtra("type",Types);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return searchdata.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.search_result_text);
        }
    }


}
