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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {
    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter_Feed adapter;
    final ArrayList<FeedData> mList = new ArrayList<FeedData>();
    final ArrayList<FeedData> mList_get = new ArrayList<FeedData>();
    final ArrayList<String> oldPost_get = new ArrayList<String>();

    private Spinner spinner;

    private boolean isLoading = false;

    private String spinner_theme = "전체";
    private String key_storage = null;
    private String oldestPostId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        spinner = (Spinner)findViewById(R.id.spinner);

        mDatabaseReference =mDatabaseReference.child("Feed");
        mDatabaseReference.limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mList!=null)
                    mList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    key_storage = dataSnapshot.getKey();
                    populateData(dataSnapshot.getValue(FeedData.class));
                }
                oldestPostId = oldPost_get.get(0);
                recyclerView = findViewById(R.id.recyclerView);
                initAdapter();
                initScrollListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals("전체")){
                    spinner_theme = "전체";
                    listRevise_viewall();
                } else if(parent.getItemAtPosition(position).toString().equals("여행")){
                    spinner_theme = "여행";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("요리")){
                    spinner_theme = "요리";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("일상")){
                    spinner_theme = "일상";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("운동")){
                    spinner_theme = "운동";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("공부")){
                    spinner_theme = "공부";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("트러블")){
                    spinner_theme = "트러블";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("비밀")){
                    spinner_theme = "비밀";
                    listRevise();
                } else if(parent.getItemAtPosition(position).toString().equals("예술")){
                    spinner_theme = "예술";
                    listRevise();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateData(FeedData data) {
        //FeedData item = new FeedData();
        if(data.getOpen()){
            if(data.getTheme().equals(spinner_theme)){
                mList.add(0, data);
            }else if(spinner_theme.equals("전체")){
                mList.add(0, data);
                mList_get.add(0, data);
                oldPost_get.add(key_storage);
            }else{
                oldPost_get.add(key_storage);
            }
        }
    }

    private void listRevise(){
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mList!=null) {
                    mList.clear();
                }
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    populateData(dataSnapshot.getValue(FeedData.class));
                }
                recyclerView = findViewById(R.id.recyclerView);
                initAdapter();
                initScrollListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void listRevise_viewall(){
        mDatabaseReference.limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mList!=null) {
                    mList.clear();
                    mList_get.clear();
                    oldPost_get.clear();
                }
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    key_storage = dataSnapshot.getKey();
                    populateData(dataSnapshot.getValue(FeedData.class));
                }
                oldestPostId = oldPost_get.get(0);
                recyclerView = findViewById(R.id.recyclerView);
                initAdapter();
                initScrollListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initAdapter() {
        adapter = new RecyclerViewAdapter_Feed(mList);
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
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == mList.size() - 1) {
                        //리스트 마지막
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        mList.add(null);
        adapter.notifyItemInserted(mList.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.remove(mList.size() - 1);
                int scrollPosition = mList.size();
                adapter.notifyItemRemoved(scrollPosition);

                mDatabaseReference.orderByKey().endAt(oldestPostId).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mList_get.clear(); //임시저장 위치
                        oldPost_get.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getValue(FeedData.class).getTheme().equals(spinner_theme)){
                                mList_get.add(0,dataSnapshot.getValue(FeedData.class));
                                oldPost_get.add(dataSnapshot.getKey());
                            } else if(spinner_theme.equals("전체")){
                                mList_get.add(0,dataSnapshot.getValue(FeedData.class));
                                oldPost_get.add(dataSnapshot.getKey());
                            }
                        }

                        if(mList_get.size() > 1) {//1개라도 있으면 불러옴
                            //마지막 중복되는 부분 삭제
                            mList_get.remove(0);
                            //contents 뒤에 추가
                            mList.addAll(mList_get);
                            oldestPostId = oldPost_get.get(0);
                            //메시지 갱신 위치
                            adapter.notifyDataSetChanged();
                            isLoading = false;
                        } else {
                            isLoading = false;
                            Snackbar.make(getWindow().getDecorView().getRootView(), "마지막 게시물입니다.", Snackbar.LENGTH_SHORT)
                                    .setAction("닫기", new View.OnClickListener() {@Override public void onClick(View view) {}}).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }, 2000);
    }

    public void onClick(View v){
        Intent intent = new Intent(FeedActivity.this, CommentActivity.class);


        if(v.getId() == R.id.comment_button)
            startActivity(intent);


    }
/*
    public void addFeed(String theme, String date, String content, String imagename, String url) {
        FeedData feedData;

        feedData = new FeedData(content, theme, date, time, imagename, url);


        mDatabaseReference.child("Feed").child(date + " " + time + " " + user.getUid()).setValue(feedData);
        finish();

    }*/
}