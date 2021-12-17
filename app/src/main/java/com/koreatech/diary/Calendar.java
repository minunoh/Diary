package com.koreatech.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Calendar extends AppCompatActivity {
    private DrawerLayout drawerLayout;// 드로어되는 창
    NavigationView navigationView;
    private Toolbar toolbar;// 액션바(툴바)
    static RecyclerViewEmptySupport schedulerecyclerView;
    static RecyclerViewEmptySupport diaryrecyclerView;
    static ArrayList<ScheduleData> scheduleData = new ArrayList<ScheduleData>();
    static ArrayList<DiaryData> diaryData = new ArrayList<DiaryData>();
    private static ScheduleAdapter scheduleAdapter = new ScheduleAdapter();
    private static DiaryAdapter diaryAdapter = new DiaryAdapter();
    BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentDiary fragmentDiary;
    private FragmentSchedule fragmentSchedule;
    private FragmentTransaction transaction;
    private ImageView ivMenu;//메뉴버튼
    private PopupMenu popupMenu;
    private int writetype = 0;
    //firebase auth object
    private static FirebaseAuth firebaseAuth;

    //firebase data object
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;


    // 선택한 날짜
    private static int checkYear;
    private static int checkMonth;
    private static int checkDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        navigationView = findViewById(R.id.navigation);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ivMenu = findViewById(R.id.iv_menu);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();
        fragmentDiary = new FragmentDiary();
        fragmentSchedule = new FragmentSchedule();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentSchedule, "aa").commitAllowingStateLoss();


        //네비게이션뷰 선택시 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                int mid = item.getItemId();
                Intent intent = null;

                if (mid == R.id.M_diary) { // 다이어리 작성
                    intent = new Intent(Calendar.this, WDiaryActivity.class);
                    startActivity(intent);
                    return true;

                } else if (mid == R.id.M_tdlist) { // 일정 작성
                    intent = new Intent(Calendar.this, WScheduleActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_mydiary) { //나의 다이어리 리스트
                    intent = new Intent(Calendar.this, MydiaryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_picture) {//갤러리
                    intent = new Intent(Calendar.this, Gallery.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_home) { // 홈
                    intent = new Intent(Calendar.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.tomembership) {  // 개인정보
                    intent = new Intent(Calendar.this, MemberActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_setting) {//설정
                    intent = new Intent(Calendar.this, MemberActivity.class);
                    startActivity(intent);
                    return true;
                }
                return true;
            }
        });

        Button button = (Button) findViewById(R.id.plus_button);
        //일정or다이어리 작성 창 이동
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writetype == 0) {
                    //일정 작성페이지로 이동
                    Intent intent = new Intent(Calendar.this, WScheduleActivity.class);
                    startActivity(intent);
                } else if (writetype == 1) {
                    //다이어리 작성페이지로 이동
                    Intent intent = new Intent(Calendar.this, WDiaryActivity.class);
                    startActivity(intent);
                }
            }
        });


        // initializing database
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //뷰에 있는 위젯들 리턴받기기

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        List<EventDay> events = new ArrayList<>();

        // 오늘 날짜 받게하기
        java.util.Calendar today = java.util.Calendar.getInstance();
        int todayYear = today.get(java.util.Calendar.YEAR);
        int todayMonth = today.get(java.util.Calendar.MONTH);
        int todayDay = today.get(java.util.Calendar.DAY_OF_MONTH);

        // 현재 선택한 날짜
        checkYear = todayYear;
        checkMonth = todayMonth;
        checkDay = todayDay;

        //처음은 오늘 날짜로 선택하여, 일정, 다이어리 리스트를 업데이트 한다.
        fragmentSchedule.update(checkYear, checkMonth, checkDay);
        fragmentDiary.update(checkYear, checkMonth, checkDay);

        //일정에 변화가 있다면, 반영하여 달력에 '새싹' 모양 표시
        mFirebaseDatabase.getReference().child("Schedule").child(user.getUid()).addValueEventListener(new ValueEventListener() {

            //일정데이터가 변경될 때 onDataChange함수 발생
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //먼저 이벤트 목록이 차있다면, 초기화
                if (events != null) {
                    events.clear();
                }
                //일정 데이터를 뽑아옴
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    //연, 월, 일을 분리하여
                    int[] date = splitDate(key);
                    java.util.Calendar event_calendar = java.util.Calendar.getInstance();
                    //event_calendar에 저장
                    event_calendar.set(date[0], date[1] - 1, date[2]);
                    //해당 날짜에 event가 있다면 '새싹' 표시
                    EventDay event = new EventDay(event_calendar, R.drawable.ic_sprout);
                    events.add(event);
                }
                calendarView.setEvents(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        // 선택 날짜가 변경될 때 호출되는 리스너
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                java.util.Calendar clickedDayCalendar = eventDay.getCalendar();

                //체크한 날짜 변경
                checkYear = clickedDayCalendar.get(java.util.Calendar.YEAR);
                checkMonth = clickedDayCalendar.get(java.util.Calendar.MONTH);
                checkDay = clickedDayCalendar.get(java.util.Calendar.DATE);


                fragmentSchedule.update(checkYear, checkMonth, checkDay);
                fragmentDiary.update(checkYear, checkMonth, checkDay);


            }
        });

        //select bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.page_diary:
                        writetype = 1;
                        fragmentDiary.update(checkYear, checkMonth, checkDay);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragmentDiary).commit();

                        break;


                    case R.id.page_schedule:
                        writetype = 0;
                        fragmentSchedule.update(checkYear, checkMonth, checkDay);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragmentSchedule).commit();

                        break;

                }
                return true;
            }
        });


    }


    //다이어리 프래그먼트

    public static class FragmentSchedule extends Fragment {

        private static TextView list_empty;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_schedule, container, false);


            initUI(rootView);

            return rootView;
        }


        //초기화 함수
        private void initUI(ViewGroup rootView) {

            schedulerecyclerView = rootView.findViewById(R.id.scheduleRecyclerView);
            schedulerecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), 1));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            schedulerecyclerView.setLayoutManager(layoutManager);
            schedulerecyclerView.setHasFixedSize(true);
            schedulerecyclerView.setAdapter(scheduleAdapter);
            list_empty = rootView.findViewById(R.id.list_empty);
            schedulerecyclerView.setEmptyView(list_empty);
        }


        //일정 리스트 업데이트 함수
        static void update(int year, int month, int day) {
            showDB(year, month, day, "Schedule");


        }


    }


    //일정 프래그먼트
    public static class FragmentDiary extends Fragment {


        private static TextView list_empty;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_diary, container, false);

            initUI(rootView);

            return rootView;
        }

        //초기화 함수
        private void initUI(ViewGroup rootView) {

            diaryrecyclerView = rootView.findViewById(R.id.diaryRecyclerView);
            diaryrecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), 1));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            diaryrecyclerView.setLayoutManager(layoutManager);
            diaryrecyclerView.setHasFixedSize(true);
            diaryrecyclerView.setAdapter(diaryAdapter);
            list_empty = rootView.findViewById(R.id.list_empty2);
            diaryrecyclerView.setEmptyView(list_empty);


        }

        //Diary 리스트 업데이트 함수
        static void update(int year, int month, int day) {
            showDB(year, month, day, "Diary");


        }
    }


    //선택된 날짜의 일정이나 다이어리 리스트를 보여주는 함수
    public static void showDB(int year, int month, int day, String type) {
        String stringday = "";
        if (day < 10) {
            stringday = String.valueOf("0" + day);
        } else {
            stringday = String.valueOf(day);
        }

        //해당 날짜의 데이터 진입
        mDatabaseReference = mFirebaseDatabase.getReference().child(type).child(user.getUid()).child(year + "-" + (month + 1) + "-" + stringday);
        //만약 '일정' 데이터라면
        if (type.equals("Schedule")) {

            //DB가 비어 있지 않으면,
            if (mDatabaseReference != null) {
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // firebase 데이터베이스의 데이터를 받아온다.
                        //scheduleData 초기화
                        if (scheduleData != null)
                            scheduleData.clear();

                        // 반복문으로 데이터 List를 추출해냄
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            //ScheduleData형식으로 데이터를 추출
                            scheduleData.add(snapshot.getValue(ScheduleData.class));
                        }

                        //일정 리사이클러뷰에 반영
                        scheduleAdapter.items = scheduleData;
                        scheduleAdapter.notifyDataSetChanged();

                    }

                    // 실패 할 시
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 디비를 가져오던 중 에러 발생 시
                        Log.e("MainActivity", String.valueOf(error.toException())); // 에러 출력
                    }
                });


            }

            //만약 '다이어리' 데이터라면
        } else if (type.equals("Diary")) {
            if (mDatabaseReference != null) {
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // firebase 데이터베이스의 데이터를 받아오는 곳
                        //diaryData 초기화
                        if (diaryData != null)
                            diaryData.clear();

                        // 반복문으로 데이터 List를 추출해냄
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            //DiaryData 데이터 형식으로 데이터를 추출
                            diaryData.add(snapshot.getValue(DiaryData.class));
                        }

                        //다이어리 리사이클러뷰에 반영
                        diaryAdapter.items = diaryData;
                        diaryAdapter.notifyDataSetChanged();

                    }

                    //실패 할 시
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 디비를 가져오던 중 에러 발생 시
                        Log.e("MainActivity", String.valueOf(error.toException())); // 에러 출력
                    }
                });
            }
        }
    }


    //"-"를 기준으로 데이터를 분리해주는 함수 (ex 2021-22-22 =>  '2021', '22', '22')
    private int[] splitDate(String date) {
        String[] splitText = date.split("-");
        int[] result_date = {Integer.parseInt(splitText[0]), Integer.parseInt(splitText[1]), Integer.parseInt(splitText[2])};
        return result_date;
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

    //클릭시 드로우레이아웃을 연다
    public void onClick(View view) {
        int ViewId = view.getId();
        if (ViewId == R.id.iv_menu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }
}