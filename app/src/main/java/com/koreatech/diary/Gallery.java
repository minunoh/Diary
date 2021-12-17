package com.koreatech.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser user;
    private static FirebaseStorage storage;
    private static ArrayList<GalleryData> arrayList = new ArrayList<>();
    ;
    private DrawerLayout drawerLayout;// 드로어되는 창
    NavigationView navigationView;
    private Toolbar toolbar;// 액션바(툴바)
    private static GridViewAdapter galleryadapter = null;
    private static GridView gridView = null;


    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        navigationView = findViewById(R.id.navigation);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        storage = FirebaseStorage.getInstance();
        galleryadapter = new GridViewAdapter();
        gridView = findViewById(R.id.gallery_gridview);

        // initializing database
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        //url 어레이리스트를 어뎁터에 삽입
        galleryadapter.items = showGallery();


        //어뎁터가 비어있지 않다면,
        if (galleryadapter.items.size() != 0) {

            //갤러리에 그리드뷰 형태로 출력
            galleryadapter.notifyDataSetChanged();
            gridView.setAdapter(galleryadapter);
            galleryadapter.notifyDataSetChanged();
        }

        //네비게이션뷰 선택시 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                int mid = item.getItemId();
                Intent intent = null;

                if (mid == R.id.M_diary) { // 다이어리 작성
                    intent = new Intent(Gallery.this, WDiaryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_mydiary) { //나의 다이어리 리스트
                    intent = new Intent(Gallery.this, MydiaryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_calendar) {//캘린더
                    intent = new Intent(Gallery.this, Calendar.class);
                    startActivity(intent);
                } else if (mid == R.id.M_picture) {//갤러리
                    intent = new Intent(Gallery.this, Gallery.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_home) { // 홈
                    intent = new Intent(Gallery.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                else if (mid == R.id.tomembership) {  // 개인정보
                    intent = new Intent(Gallery.this, MemberActivity.class);
                    startActivity(intent);
                    return true;
                } else if (mid == R.id.M_setting) {//설정
                    intent = new Intent(Gallery.this, MemberActivity.class);
                    startActivity(intent);
                    return true;
                }
                else if (mid == R.id.M_tdlist) { // 일정 작성
                    intent = new Intent(Gallery.this, WScheduleActivity.class);
                    startActivity(intent);
                    return true;
                }

                return true;
            }
        });


    }


    //그리드 뷰 어댑터
    public static class GridViewAdapter extends BaseAdapter {


        AlertDialog dialog;


        //GalleryData 형식의 어레이 리스트
        ArrayList<GalleryData> items = new ArrayList<GalleryData>();


        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(GalleryData item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final GalleryData Item = items.get(position);


            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.gallery_item, viewGroup, false);


                ImageView iv_icon = (ImageView) convertView.findViewById(R.id.gallery_image);

                //iv_icon imageView에 Glide를 사용하여 사진을 출력
                Glide.with(context)
                        .load(Uri.parse(Item.getImaguri()))
                        .into(iv_icon);


            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ImageView image = new ImageView(context);
                    image.setImageResource(R.drawable.ic_sprout);

                    //갤러리에서 사진을 클릭시 700 * 700 크기의 사진으로 확대하여 출력
                    Glide.with(context)
                            .load(Uri.parse(Item.getImaguri()))
                            .override(700, 700)
                            .into(image);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("\n" + "\n");
                    builder.setView(image);

                    builder.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }


    //Gallery DB에서 url을 뽑아오는 함수
    public static ArrayList<GalleryData> showGallery() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Gallery").child(user.getUid());

        // DB가 비어있지 않다면
        if (mDatabaseReference != null) {
            mFirebaseDatabase.getReference().child("Gallery").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // firebase 데이터베이스의 데이터를 받아오는 곳
                    if (arrayList != null)
                        arrayList.clear();
                    gridView.setAdapter(galleryadapter);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄


                        //GalleryData 형식으로 데이터를 추출
                        arrayList.add(snapshot.getValue(GalleryData.class));
                    }

//


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 디비를 가져오던 중 에러 발생 시
                    Log.e("MainActivity", String.valueOf(error.toException())); // 에러 출력
                }
            });

            return arrayList;
        } else {
            return null;
        }
    }

    public void onClick(View view) {
        int ViewId = view.getId();
        if (ViewId == R.id.iv_menu) {
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