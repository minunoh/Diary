package com.koreatech.diary;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {


    //ScheduleData 데이터 형식의 어레이리스트
    ArrayList<ScheduleData> items = new ArrayList<ScheduleData>();
    Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.schedule_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleData item = items.get(position);
        holder.setItem(item);
    }


    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void addItem(ScheduleData item) {
        items.add(item);
    }

    public void setItems(ArrayList<ScheduleData> items) {
        this.items = items;
    }

    public ScheduleData getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView content;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            content = itemView.findViewById(R.id.detailListContext);
            itemView.setOnCreateContextMenuListener(this);
        }

        //롱클릭 삭제 or 수정 팝업 메뉴
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(this.getAdapterPosition(), 1001, 0, "수정");
            MenuItem Delete = menu.add(this.getAdapterPosition(), 1002, 1, "삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);
            Edit.setOnMenuItemClickListener(onEditMenu);
        }

        //팝업 메뉴 클릭시 이벤트
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

                        //해당 아이템의 일정 정보를 전달하며, 일정 작성 창으로 이동한다.
                        Intent intent = new Intent(context, WScheduleActivity.class);
                        intent.putExtra("day", items.get(getAdapterPosition()).getDay());
                        intent.putExtra("content", items.get(getAdapterPosition()).getContent());

                        context.startActivity(intent);


                        break;

                    case 1002://삭제항목 선택시

                        //Schedule DB의 해당 데이터를 삭제
                        dataRef.child("Schedule").child(user.getUid()).child(items.get(getAdapterPosition()).getDay()).child(items.get(getAdapterPosition()).getContent()).removeValue()
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
                        //해당 아이템 삭제 및 반영
                        items.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), items.size());

                        break;

                }
                return true;
            }
        };


        public void setItem(ScheduleData item) {
            content.setText(item.getContent());

        }

    }


}

