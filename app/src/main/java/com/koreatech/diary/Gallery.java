package com.koreatech.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    private static ArrayList<GalleryData>  arrayList = new ArrayList<>();;


    private static GridViewAdapter galleryadapter = null;
    private static GridView gridView= null;


    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        storage=FirebaseStorage.getInstance();
        galleryadapter =new GridViewAdapter();
        gridView = findViewById(R.id.gallery_gridview);
        // initializing database
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage=FirebaseStorage.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        ImageView imageView2 =findViewById(R.id.test2);
        galleryadapter.items=showGallery();

        if(galleryadapter.items.size()!=0) {
            galleryadapter.notifyDataSetChanged();
            gridView.setAdapter(galleryadapter);
            galleryadapter.notifyDataSetChanged();
        }






    }



    /* 그리드뷰 어댑터 */
    public static class GridViewAdapter extends BaseAdapter {
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

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.gallery_item, viewGroup, false);


                ImageView iv_icon = (ImageView) convertView.findViewById(R.id.gallery_image);

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
                    //Toast.makeText(context, bearItem.getNum()+" 번 - "+bearItem.getName()+" 입니당! ", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }


    public static ArrayList<GalleryData> showGallery(){
        mDatabaseReference =  mFirebaseDatabase.getReference().child("Gallery").child(user.getUid());
        if (mDatabaseReference != null) {
            mFirebaseDatabase.getReference().child("Gallery").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // firebase 데이터베이스의 데이터를 받아오는 곳
                    if (arrayList != null)
                        arrayList.clear();
                    gridView.setAdapter(galleryadapter);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄


                        arrayList.add(snapshot.getValue(GalleryData.class));
                    }

                    gridView.setAdapter(galleryadapter);
                    galleryadapter.notifyDataSetChanged();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 디비를 가져오던 중 에러 발생 시
                    Log.e("MainActivity", String.valueOf(error.toException())); // 에러 출력
                }
            });

            return arrayList;
        }
        else {
            return null;
        }
    }
}