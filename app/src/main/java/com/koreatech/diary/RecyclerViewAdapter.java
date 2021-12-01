package com.koreatech.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private  Context mContext;
    ArrayList<DiaryData> mList = new ArrayList<DiaryData>();



    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView t_date;
        TextView t_theme;
        TextView t_content;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            t_date =(TextView) itemView.findViewById(R.id.t_MyDiaryDate);
            t_theme= (TextView) itemView.findViewById(R.id.t_MyDiaryTheme);
            t_content= (TextView) itemView.findViewById(R.id.t_MyDiaryContent);
        }
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

        View view = inflater.inflate(R.layout.mydiary_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryData item = mList.get(position);

       // holder.imgView_item.setImageResource(R.drawable.ic_launcher_background);   // 사진 없어서 기본 파일로 이미지 띄움
        holder.t_date.setText(item.getDay());
        holder.t_theme.setText(item.getTheme());
        holder.t_content.setText(item.getContent());


    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}

