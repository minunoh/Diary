package com.koreatech.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivMenu;//메뉴버튼
    private DrawerLayout drawerLayout;// 드로어되는 창
    private Toolbar toolbar;// 액션바(툴바)
    private NavigationView navigationView;

    //firebase auth object
    private static FirebaseAuth firebaseAuth;

    //firebase data object
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;
    private FirebaseAuth mAuth;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*btnLogout = (Button)findViewById(R.id.M_log);*/
       /* btnLogout.setOnClickListener(this);*/

       navigationView =(NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                int mid = item.getItemId();
                if(mid == R.id.M_diary){ // 다이어리 작성
                    Intent intent=  new Intent(MainActivity.this,WDiaryActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid ==R.id.M_tdlist){ // 일정 작성
                    Intent intent=  new Intent(MainActivity.this,WScheduleActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.M_calendar){
                    Intent intent = new Intent(MainActivity.this,Calendar.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.M_home){
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid ==R.id.M_mydiary){
                    Intent intent =  new Intent(MainActivity.this,MydiaryActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid ==R.id.M_setting){
                    Intent intent =  new Intent(MainActivity.this,MemberActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });


    }
        private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

        public void onClick(View view){
            int ViewId = view.getId();
            Intent intent = null;
            if (ViewId == R.id.iv_menu) {  // 햄버거 버튼 클릭시 네비게이션 드로어
                drawerLayout.openDrawer(GravityCompat.START);
            }else if(ViewId == R.id.todiary){  // 다이어리 작성
                intent = new Intent(MainActivity.this,WDiaryActivity.class);
                startActivity(intent);
            }else if(ViewId == R.id.tomembership){  // 개인정보
                intent = new Intent(MainActivity.this,MemberActivity.class);
                startActivity(intent);
            }else if(ViewId == R.id.tocommunity){  // 다이어리 작성
                intent = new Intent(MainActivity.this,FeedActivity.class);
                startActivity(intent);
            }/*else if(ViewId == R.id.M_log){  // 로그아웃
                signOut();
                finishAffinity();

            }*/
        }

   /* DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
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
    };*/

}