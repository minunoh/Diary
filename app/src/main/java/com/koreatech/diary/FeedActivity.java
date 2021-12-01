package com.koreatech.diary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {
    private Spinner spinner;
    private RecyclerView recyclerView;
    private com.koreatech.diary.RecyclerViewAdapter_Feed adapter;
    private ArrayList<String> items = new ArrayList<>();
    int like = 1;
    ImageView Imgv = null;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //테마 선택시 발생하는 이벤트
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //아무것도 선택 안할 시
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        populateData();
        initAdapter();
        initScrollListener();
    }

    private void populateData() {
        for (int i=0; i<10; i++) {
            items.add("Item " + i);
        }
    }

    private void initAdapter() {
        adapter = new com.koreatech.diary.RecyclerViewAdapter_Feed(items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initScrollListener() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == items.size() - 1) {
                        //리스트 마지막
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        items.add(null);
        adapter.notifyItemInserted(items.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                items.remove(items.size() - 1);
                int scrollPosition = items.size();
                adapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    items.add("Item " + currentSize);
                    currentSize++;
                }

                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    public void onClick(View v){
        Intent intent = new Intent(FeedActivity.this, CommentActivity.class);
        Imgv = (ImageView)findViewById(R.id.like_button);

        if(v.getId() == R.id.comment_button)
            startActivity(intent);
        else if(v.getId() == R.id.like_button)
        {
            if(like == 1){
                Imgv.setImageResource(R.drawable.heart_2);
                like *= -1;
            }
            else if(like == -1){
                Imgv.setImageResource(R.drawable.heart_1);
                like *= -1;
            }
        }
    }
}
