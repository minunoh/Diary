package com.koreatech.diary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private  Context mcontext;
    ArrayList<DiaryData> mList = new ArrayList<DiaryData>();



    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView t_date;
        TextView t_theme;
        TextView t_content;
        ImageView t_image;



        public ViewHolder(@NonNull View itemView){
            super(itemView);
            // mydiary_item.xml의 view들을 연결
            t_date =(TextView) itemView.findViewById(R.id.t_MyDiaryDate);
            t_theme= (TextView) itemView.findViewById(R.id.t_MyDiaryTheme);
            t_content= (TextView) itemView.findViewById(R.id.t_MyDiaryContent);
            t_image = (ImageView) itemView.findViewById(R.id.t_MyDiaryImage);
            itemView.setOnCreateContextMenuListener(this);
        }

        //수정, 삭제 팝업 메뉴
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(this.getAdapterPosition(), 1001, 0, "수정");
            MenuItem Delete = menu.add(this.getAdapterPosition(), 1002, 1, "삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);
            Edit.setOnMenuItemClickListener(onEditMenu);
        }

        //팝업 메뉴 선택 이벤트 처리
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseDatabase mDatabase;
                DatabaseReference dataRef;
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mDatabase = FirebaseDatabase.getInstance();
                dataRef = mDatabase.getReference();

                switch (item.getItemId()) {
                    case 1001:  // 수정 항목을 선택시

                        //기존의 다이어리 데이터를 WDiaryActivity에 전달하고 이동
                        Intent intent = new Intent(mcontext, WDiaryActivity.class);
                        intent.putExtra("day",mList.get(getAdapterPosition()).getDay());
                        intent.putExtra("content",mList.get(getAdapterPosition()).getContent());
                        intent.putExtra("url",mList.get(getAdapterPosition()).getImageurl());
                        intent.putExtra("theme",mList.get(getAdapterPosition()).getTheme());
                        intent.putExtra("open",mList.get(getAdapterPosition()).getOpen());
                        intent.putExtra("imagename",mList.get(getAdapterPosition()).getImagename());
                        intent.putExtra("time", mList.get(getAdapterPosition()).getTime());
                        mcontext.startActivity(intent);


                        break;

                    case 1002://삭제항목 선택시


                        //다이어리를 작성할 때 이미지를 넣었다면, 이미지에 대한 정보도 삭제
                        if (mList.get(getAdapterPosition()).getImageurl().length()>0 ) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference riversRef = storageRef.child(user.getUid()).child("photo").child(mList.get(getAdapterPosition()).getImagename());
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


                        //공개글 이었다면
                        if(mList.get(getAdapterPosition()).getOpen()) {
                            //DB의 Feed에도 존재하므로 삭제
                            dataRef.child("Feed").child(mList.get(getAdapterPosition()).getDay() + " " + mList.get(getAdapterPosition()).getTime()+" "+user.getUid()).setValue(null);
                        }
                        //DB의 Gallery에서 해당 데이터를 삭제
                        dataRef.child("Gallery").child(user.getUid()).child(mList.get(getAdapterPosition()).getTime()).setValue(null);
                        //DB의 Diary에서 해당 데이터를 삭제
                        dataRef.child("Diary").child(user.getUid()).child(mList.get(getAdapterPosition()).getDay()).child(mList.get(getAdapterPosition()).getTime()).removeValue()
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
                        //선택된 해당 아이템을 삭제하고 반영
                        mList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mList.size());
                        break;

                }
                return true;
            }
        };
    }




    public DiaryData getItem(int position) {
        return mList.get(position);
    }
    public RecyclerViewAdapter(ArrayList<DiaryData> mList) {
        this.mList = mList;
    }
    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mcontext =parent.getContext();
        View view = inflater.inflate(R.layout.mydiary_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryData item = mList.get(position);


        //날짜, 테마, 내용 표시
        holder.t_date.setText(item.getDay());
        holder.t_theme.setText(item.getTheme());
        holder.t_content.setText(item.getContent());

        //다이어리의 이미지 삽입
        Glide.with(mcontext)
                .load(Uri.parse(item.getImageurl()))
                .override(700,500)
                .into(holder.t_image);


    }
    @Override
    public int getItemCount() {
        return mList.size();
    }



}

