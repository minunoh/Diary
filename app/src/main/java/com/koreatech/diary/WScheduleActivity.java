package com.koreatech.diary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Date;

public class WScheduleActivity extends AppCompatActivity {

    //firebase auth object
    private static FirebaseAuth firebaseAuth;

    //firebase data object
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView TDate;
    private NavigationView navigationView;
    private EditText eventday;
    private EditText event;
    private EditText eventlist;
    Date mDate;
    long mNow;
    SimpleDateFormat today1 = new SimpleDateFormat("yyyy-MM-dd");

    // 날짜 가져오기
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return today1.format(mDate);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wschedule);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        TDate = findViewById(R.id.Date);
        TDate.setText(getTime());  //날짜 입력
        event = findViewById(R.id.et_event);
        eventlist = findViewById(R.id.schdulelist);
        eventday = findViewById(R.id.et_Date);
        // 네비게이션 드로어 + 툴바 설정
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
                Intent intent = null;

                if(mid == R.id.M_diary){ // 다이어리 작성
                    intent=  new Intent(WScheduleActivity.this,WDiaryActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid ==R.id.M_mydiary){ //나의 다이어리 리스트
                    intent =  new Intent(WScheduleActivity.this,MydiaryActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.M_calendar){
                    intent = new Intent(WScheduleActivity.this,Calendar.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.M_home){
                    intent = new Intent(WScheduleActivity.this,MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });

    }
    public void onClick(View view) {
        int ViewId = view.getId();
        if (ViewId == R.id.iv_menu) {  // 햄버거 버튼 클릭시 네비게이션 드로어
            drawerLayout.openDrawer(GravityCompat.START);
        }else if(ViewId == R.id.ib_addtodo){ // 일정추가 버튼
            addSchedule(eventday.getText().toString(),event.getText().toString(),eventlist.getText().toString());
            event.setText("");


            Toast.makeText(getApplicationContext(), "일정 추가", Toast.LENGTH_SHORT).show();
        }else if(ViewId==R.id.et_Date){  // Todolist 날짜설정
            Calendar cal = Calendar.getInstance();
            DatePickerDialog datedialog =  new DatePickerDialog(this,mDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE));
            datedialog.getDatePicker().setMinDate(System.currentTimeMillis());// 최소날짜 오늘
            datedialog.show();

        }
    }
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
            TextView date = findViewById(R.id.et_Date);
            date.setText(String.format("%d-%d-%d",yy,mm+1,dd));
        }
    };

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
    public void addSchedule(String day, String title, String content){
        ScheduleData scheduleData = new ScheduleData(day,title,content);
        mDatabaseReference.child("Schedule").child(user.getUid()).child(day).child(title).setValue(scheduleData);
        finish();
    }
}
