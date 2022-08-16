package com.rahulkantwa.publicproblemsindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView listView;
    private ProgressBar searchprogressbar;
    private EditText searchlocation;
    private String Types;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (RecyclerView) findViewById(R.id.search_list_view) ;
        searchlocation = (EditText)findViewById(R.id.text_search) ;
        searchprogressbar = (ProgressBar)findViewById(R.id.progress_bar_search);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));

        searchlocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    InputKey(v);
                    listView.setVisibility(View.GONE);
                    searchprogressbar.setVisibility(View.VISIBLE);
                    String s = "";
                    String[] ss = searchlocation.getText().toString().trim().split(",");
                    for(int i = 0; i < ss.length; i++)
                    {
                        s += ss[i].trim().toLowerCase()+"-";
                    }
                    FirebaseFirestore.getInstance().collection("SerachData")
                            .whereArrayContains("data",s.substring(0,s.length()-1))
                            .limit(15)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    listView.setVisibility(View.VISIBLE);
                                    searchprogressbar.setVisibility(View.GONE);
                                    final ArrayList<String> arrayList = new ArrayList<>();
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            ArrayList<String> arrayList1 = (ArrayList<String>)document.get("data") ;
                                            String[] splitarraylist = arrayList1.get(0).split("-");
                                            String splitsadd = "";
                                            for(int j = 0; j < splitarraylist.length; j++)
                                            {
                                                String[] splitsplitarraylist = splitarraylist[j].trim().split(" ");
                                                for(int i = 0; i < splitsplitarraylist.length; i++)
                                                {
                                                    splitsadd += splitsplitarraylist[i].substring(0,1).toUpperCase().trim()+splitsplitarraylist[i].substring(1).toLowerCase().trim()+" ";
                                                }
                                                splitsadd = splitsadd.trim() + ", ";
                                            }
                                            arrayList.add(splitsadd.trim().substring(0,splitsadd.length()-2));
                                        }
                                        if (arrayList.size() == 0)
                                        {
                                            Toast.makeText(getApplicationContext(),"No result found",Toast.LENGTH_LONG).show();
                                        }
                                        SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this,arrayList);
                                        listView.setAdapter(searchAdapter);
                                    } else {
                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
                return false;
            }
        });


    }
    public void InputKey(View v)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),10);
    }
}
