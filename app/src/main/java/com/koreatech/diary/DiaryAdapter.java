package com.koreatech.diary;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koreatech.diary.R;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {


    ArrayList<DiaryData> items = new ArrayList<DiaryData>();
    private Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.detaillist_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryData item = items.get(position);
        holder.setItem(item);
    }



    @Override
    public int getItemCount() {
        if(items==null){
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

    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener{

        TextView content;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.detailListTitle);
            content = itemView.findViewById(R.id.detailListContext);
            itemView.setOnCreateContextMenuListener(this);
        }

        //롱클릭 삭제 수정 메뉴
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(this.getAdapterPosition(), 1001, 0, "수정");
            MenuItem Delete = menu.add(this.getAdapterPosition(), 1002, 1, "삭제");
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case 1001:  // 수정 항목을 선택시

                        //AlertDialog.Builder builder = new AlertDialog.Builder(mContext);



                        break;

                    case 1002://삭제항목 선택시
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
            title.setText(item.getTitle());

        }

    }





}

