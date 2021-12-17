package com.koreatech.diary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WDiaryActivity extends AppCompatActivity {

    private Intent intent;
    private String inurl;
    private String inimagename;
    private boolean inopen;
    private String incontent;
    private String intheme;
    private String inday;
    private String intime;
    private boolean editstate = false;

    static int count = 0;
    //firebase auth object
    private static FirebaseAuth firebaseAuth;

    //갤러리
    private final int GALLERY_CODE = 10;
    private FirebaseStorage storage;
    private Uri file;
    Boolean existFile = false;
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
    boolean openck = true;
    long mNow;
    Date mDate;
    private String time = "";
    SimpleDateFormat today1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat today2 = new SimpleDateFormat("hh:mm:ss");

    // 날짜 가져오기
    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return today1.format(mDate);
    }

    //시간 가져오기
    private String getTime2() {
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
        storage = FirebaseStorage.getInstance();
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        ivMenu = findViewById(R.id.iv_menu);


        diary_content = (EditText) findViewById(R.id.writepage);// write diary content
        B_Theme = (Button) findViewById(R.id.b_theme);
        setSupportActionBar(toolbar);

        //수정하기를 눌렀을 경우 값 받아오기
        intent = getIntent();
        incontent = intent.getStringExtra("content");
        inimagename = intent.getStringExtra("imagename");
        inopen = intent.getBooleanExtra("open", true);
        inday = intent.getStringExtra("day");
        intheme = intent.getStringExtra("theme");
        inurl = intent.getStringExtra("url");
        intime = intent.getStringExtra("time");

        if (incontent != null) {
            editstate = true;
            B_Theme.setText(intheme);
            diary_content.setText(incontent);
            TDate.setText(inday);
            openck = inopen;
            if (!inurl.equals("")) {
                file = Uri.parse(inurl);
            }

        }


        // initializing database
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        // 네비게이션 바 클릭 이벤트 설정
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                int mid = item.getItemId();
                Intent intent2 = null;
                if (mid == R.id.M_tdlist) { // 일정 작성
                    intent2 = new Intent(WDiaryActivity.this, WScheduleActivity.class);
                    startActivity(intent2);
                    return true;
                } else if (mid == R.id.M_mydiary) {
                    intent2 = new Intent(WDiaryActivity.this, MydiaryActivity.class);
                    startActivity(intent2);
                    return true;
                } else if (mid == R.id.M_calendar) {
                    Intent intent = new Intent(WDiaryActivity.this, Calendar.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.M_home){
                    Intent intent = new Intent(WDiaryActivity.this,MainActivity.class);
                    startActivity(intent);
                    return true;
                }else if(mid == R.id.tomembership){  // 개인정보
                    Intent intent = new Intent(WDiaryActivity.this,MemberActivity.class);
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
        } else if (ViewId == R.id.lock) { // 공개/비공개 토글
            view.setActivated(!view.isActivated());
            if (tog == 1) {
                Toast.makeText(getApplicationContext(), "다이어리 비공개", Toast.LENGTH_SHORT).show();
                openck = false;
            } else {
                Toast.makeText(getApplicationContext(), "다이어리 공개", Toast.LENGTH_SHORT).show();
                openck = true;
            }
            tog++;
            tog %= 2;
        } else if (ViewId == R.id.iv_save) {
            if (diary_content.getText().toString().equals("")) return;
            time = getTime2();


            //테마를 선택했을 경우
            if (!B_Theme.getText().toString().equals("테마 설정")) {
                //수정하는 경우, 이전 데이터는 삭제
                if (editstate) {
                    FirebaseDatabase mDatabase;
                    DatabaseReference dataRef;
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    mDatabase = FirebaseDatabase.getInstance();
                    dataRef = mDatabase.getReference();
                    //다이어리를 작성할 때 이미지를 넣었다면, 이미지에 대한 정보도 삭제
                    if (inimagename.length() > 0) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference riversRef = storageRef.child(user.getUid()).child("photo").child(inimagename);
                        riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }


                    if (inopen) {
                        dataRef.child("Feed").child(inday + " " + intime + " " + user.getUid()).setValue(null);
                    }
                    dataRef.child("Gallery").child(user.getUid()).child(intime).setValue(null);
                    dataRef.child("Diary").child(user.getUid()).child(inday).child(intime).removeValue()
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

                Log.d("test", String.valueOf(file));
                //이미지 저장 (파이어 스토어에)
                //사진이 존재한다면
                if (file != null) {

                    Log.d("test", "test3");
                    StorageReference storageRef = storage.getReference();
                    StorageReference riversRef = storageRef.child(user.getUid()).child("photo").child(time + ".png");

                    riversRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override

                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                        @Override

                                        public void onSuccess(Uri uri) {
                                            String imgurl = uri.toString();
                                            GalleryData galleryData = new GalleryData(imgurl, time + ".png", time);
                                            mDatabaseReference.child("Gallery").child(user.getUid()).child(time).setValue(galleryData);
                                            if (openck == true) {
                                                addFeed(openck, B_Theme.getText().toString(), TDate.getText().toString(), diary_content.getText().toString(), time + ".png", imgurl); // diary 데이터 푸쉬
                                            }
                                            addDiary(openck, B_Theme.getText().toString(), TDate.getText().toString(), diary_content.getText().toString(), time + ".png", imgurl); // diary 데이터 푸쉬

                                        }

                                    });

                                }

                            });

                } else {
                    Log.d("test", "test2");
                    if (openck == true) {
                        addFeed(openck, B_Theme.getText().toString(), TDate.getText().toString(), diary_content.getText().toString(), "", ""); // diary 데이터 푸쉬
                    }
                    Log.d("test", "test5");
                    addDiary(openck, B_Theme.getText().toString(), TDate.getText().toString(), diary_content.getText().toString(), "", ""); // diary 데이터 푸쉬
                    Log.d("test", "test4");

                }


                Intent intent = new Intent(WDiaryActivity.this, MydiaryActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), "테마를 선택해주세요", Toast.LENGTH_SHORT).show();
            }

        } else if (ViewId == R.id.iv_clock) {  // 시간 가져오기
            diary_content.setText(getTime2());

        } else if (ViewId == R.id.iv_cam) {  // 시간 가져오기
            loadAlbum();

        } else if (ViewId == R.id.b_theme) {
            Toast.makeText(getApplicationContext(), "테마 선택 창", Toast.LENGTH_SHORT).show();
            popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.t_menu, popupMenu.getMenu());
            popupMenu.getMenu().add(0, 4, 3, "테마 선택");
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

    public void addDiary(boolean open, String theme, String date, String content, String imagename, String uri) {
        DiaryData diaryData;

        diaryData = new DiaryData(content, theme, date, time, open, imagename, uri);


        mDatabaseReference.child("Diary").child(user.getUid()).child(date).child(time).setValue(diaryData);
        finish();

    }

    public void addFeed(boolean open, String theme, String date, String content, String imagename, String uri) {
        DiaryData diaryData;

        diaryData = new DiaryData(content, theme, date, time, open, imagename, uri);


        mDatabaseReference.child("Feed").child(date + " " + time + " " + user.getUid()).setValue(diaryData);
        finish();

    }


    private void loadAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();


            return;
        }

        if (requestCode == GALLERY_CODE) {
            file = data.getData();


            /*try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                photo.setImageBitmap(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
        }

    }
}

