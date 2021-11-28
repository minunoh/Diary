package com.koreatech.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WDiaryActivity extends AppCompatActivity {

    //firebase auth object
    private static FirebaseAuth firebaseAuth;

    //firebase data object
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;
    private EditText diary_content;
    private ImageView ivMenu;//메뉴버튼
    private DrawerLayout drawerLayout;// 드로어되는 창
    private Toolbar toolbar;// 액션바(툴바)
    private TextView TDate;
    private NavigationView navigationView;// 네비게이션 바
    private PopupMenu popupMenu;
    private Button B_Theme; //
    int tog = 1;
    long mNow;
    Date mDate;
    SimpleDateFormat today1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat today2 = new SimpleDateFormat("hh:mm:ss");

    // 날짜 가져오기
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return today1.format(mDate);
    }
    //시간 가져오기
    private String getTime2(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return today2.format(mDate);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wdiary);
        TDate = findViewById(R.id.Date);
        TDate.setText(getTime());  //날짜 입력

        drawerLayout=findViewById(R.id.drawer);
        toolbar =findViewById(R.id.toolbar);
        ivMenu=findViewById(R.id.iv_menu);


        diary_content = (EditText)findViewById(R.id.writepage);// write diary content
        B_Theme = (Button)findViewById(R.id.b_theme);
        setSupportActionBar(toolbar);


        // initializing database
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // 네비게이션 바 클릭 이벤트 설정
        navigationView =(NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                int mid = item.getItemId();
                Intent intent2 = null;
                if(mid ==R.id.M_tdlist){ // 일정 작성
                    intent2=  new Intent(WDiaryActivity.this,WScheduleActivity.class);
                    startActivity(intent2);
                    return true;
                }else if(mid ==R.id.M_mydiary){
                    intent2 =  new Intent(WDiaryActivity.this,MydiaryActivity.class);
                    startActivity(intent2);
                    return true;
                }else if(mid == R.id.M_calendar){
                    Intent intent = new Intent(WDiaryActivity.this,Calendar.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });
    }
    public void onClick(View view) {

        int ViewId= view.getId();
        if(ViewId == R.id.iv_menu){  // 햄버거 버튼 클릭시 네비게이션 드로어
            drawerLayout.openDrawer(GravityCompat.START);
        }else if(ViewId==R.id.lock){ // 공개/비공개 토글
            view.setActivated(!view.isActivated());
            if(tog ==1){
                Toast.makeText(getApplicationContext(), "다이어리 비공개", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "다이어리 공개", Toast.LENGTH_SHORT).show();
            }
            tog++;
            tog%=2;
        }else if(ViewId == R.id.iv_save){
            if(diary_content.getText().toString().equals(""))return;
            // 저장
            Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();

            RecyclerViewItem item = new RecyclerViewItem(user.getUid(),TDate.getText().toString()
                    ,B_Theme.getText().toString(),diary_content.getText().toString());
            mDatabaseReference.child("Diary").child(B_Theme.getText().toString()).push().setValue(item); // 데이터 푸쉬

            Intent intent = new Intent(WDiaryActivity.this,MydiaryActivity.class);
            startActivity(intent);
        }else if(ViewId == R.id.iv_clock){  // 시간 가져오기
            diary_content.setText(getTime2());
        }else if(ViewId==R.id.b_theme){
            Toast.makeText(getApplicationContext(), "테마 선택 창", Toast.LENGTH_SHORT).show();
            popupMenu =  new PopupMenu(this,view);
            popupMenu.getMenuInflater().inflate(R.menu.t_menu,popupMenu.getMenu());
            popupMenu.getMenu().add(0,4,3,"테마 선택");
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    B_Theme.setText(menuItem.getTitle().toString());
                    return false;
                }
            });
            popupMenu.show();
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
