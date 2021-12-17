package com.koreatech.diary;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView eventday;
    private EditText event;
    //수정 데이터
    Intent intent;
    String day;
    String content;

    Date mDate;
    long mNow;
    SimpleDateFormat today1 = new SimpleDateFormat("yyyy-MM-dd");
    Boolean editState = false;

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
        eventday = findViewById(R.id.et_Date);
        // 네비게이션 드로어 + 툴바 설정
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        intent = getIntent();
        day = intent.getStringExtra("day");
        content = intent.getStringExtra("content");
        //수정 요청이 왔다면,
        if(day!=null&&content!=null){
            event.setText(content);
            eventday.setText(day);
            editState =true;
        }

        //현재의 날짜를 받아온다.
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        Button button = findViewById(R.id.datepick);

        //날짜 선택 버튼 클릭시
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            //선택한 날짜를 저장
            @Override

            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String day ="";
                String smonth ="";

                //일자 두자리수 맞추기
                if(dayOfMonth > 0 && dayOfMonth < 10){
                    day = "0" + String.valueOf(dayOfMonth);
                }else{
                    day=  String.valueOf(dayOfMonth);
                }

                //달 두자리수 맞추기
                if(month+1> 0 && month+1 < 10){
                    smonth= "0" + String.valueOf(month+1);
                }else{
                    smonth= String.valueOf(month+1);
                }


                eventday.setText(year + "-" + smonth + "-" + day);

            }

        }, mYear, mMonth, mDay);


        //오늘 이전 날짜는 선택불가
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        button.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                datePickerDialog.show();

            }

        });


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
                }else if(mid == R.id.tomembership){  // 개인정보
                    intent = new Intent(WScheduleActivity.this,MemberActivity.class);
                    startActivity(intent);
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
            //수정하기일 경우, 이전 데이터 삭제
            if(editState){
                FirebaseDatabase mDatabase;
                DatabaseReference dataRef;
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mDatabase = FirebaseDatabase.getInstance();
                dataRef = mDatabase.getReference();
                dataRef.child("Schedule").child(user.getUid()).child(day).child(content).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() { // DB에서 Fail날경우는 거의 없음..
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // fail ui
                    }
                });
            }
            addSchedule(eventday.getText().toString(),event.getText().toString());


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
    public void addSchedule(String day, String content){
        ScheduleData scheduleData = new ScheduleData(day,content);
        mDatabaseReference.child("Schedule").child(user.getUid()).child(day).child(content).setValue(scheduleData);
        finish();
    }



}
