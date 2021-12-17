package com.koreatech.diary;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {


    ArrayList<DiaryData> items = new ArrayList<DiaryData>();
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.diary_item, parent, false);
        context =parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryData item = items.get(position);
        holder.setItem(item);
    }


    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void addItem(DiaryData item) {
        items.add(item);
    }

    public void setItems(ArrayList<DiaryData> items) {
        this.items = items;
    }

    public DiaryData getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView content;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            content = itemView.findViewById(R.id.detailListContext);
            itemView.setOnCreateContextMenuListener(this);
        }

        //롱클릭 삭제 수정 메뉴
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(this.getAdapterPosition(), 1001, 0, "수정");
            MenuItem Delete = menu.add(this.getAdapterPosition(), 1002, 1, "삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);
            Edit.setOnMenuItemClickListener(onEditMenu);

        }

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


                        Intent intent = new Intent(context, WDiaryActivity.class);
                        intent.putExtra("day",items.get(getAdapterPosition()).getDay());
                        intent.putExtra("content",items.get(getAdapterPosition()).getContent());
                        intent.putExtra("url",items.get(getAdapterPosition()).getImageurl());
                        intent.putExtra("theme",items.get(getAdapterPosition()).getTheme());
                        intent.putExtra("open",items.get(getAdapterPosition()).getOpen());
                        intent.putExtra("imagename",items.get(getAdapterPosition()).getImagename());
                        intent.putExtra("time", items.get(getAdapterPosition()).getTime());
                        context.startActivity(intent);






                        break;

                    case 1002://삭제항목 선택시


                        //다이어리를 작성할 때 이미지를 넣었다면, 이미지에 대한 정보도 삭제
                        if (items.get(getAdapterPosition()).getImagename().length()>0 ) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference riversRef = storageRef.child(user.getUid()).child("photo").child(items.get(getAdapterPosition()).getImagename());
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


                        if(items.get(getAdapterPosition()).getOpen()) {
                            dataRef.child("Feed").child(items.get(getAdapterPosition()).getDay() + " " + items.get(getAdapterPosition()).getTime()+" "+user.getUid()).setValue(null);
                        }
                        dataRef.child("Gallery").child(user.getUid()).child(items.get(getAdapterPosition()).getTime()).setValue(null);
                        dataRef.child("Diary").child(user.getUid()).child(items.get(getAdapterPosition()).getDay()).child(items.get(getAdapterPosition()).getTime()).removeValue()
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
                        items.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), items.size());
                        break;

                }
                return true;
            }
        };


        public void setItem(DiaryData item) {
            content.setText(item.getContent());


        }


    }


}

