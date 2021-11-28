package com.koreatech.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MydiaryActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private ArrayList<RecyclerViewItem> mList;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiary);
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView =(NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                int mid = item.getItemId();
                if(mid == R.id.M_diary){ // 다이어리 작성
                    Intent intent=  new Intent(MydiaryActivity.this,WDiaryActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid ==R.id.M_tdlist){ // 일정 작성
                    Intent intent=  new Intent(MydiaryActivity.this,WScheduleActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.M_calendar){
                    Intent intent = new Intent(MydiaryActivity.this,Calendar.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });

        firstInit();// 리스트에 있는거

        for(int i=0;i<5;i++){   // 아이템 추가(child 수로 변경해야할듯)
            addItem("xxxx-xx-xx"+i,"운동","나는~~");
        }

        mRecyclerViewAdapter = new RecyclerViewAdapter(mList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void firstInit(){  // 있던거  설정
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mList = new ArrayList<>();
    }

    public void addItem(String date, String theme, String content){
        RecyclerViewItem item = new RecyclerViewItem();

        item.setDate(date);
        item.setTheme(theme);
        item.setContent(content);

        mList.add(item);
    }
    public void onClick(View view) {
        int ViewId = view.getId();
        if (ViewId == R.id.iv_menu) {  // 햄버거 버튼 클릭시 네비게이션 드로어
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };
}
