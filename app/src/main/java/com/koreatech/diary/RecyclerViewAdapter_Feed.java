package com.koreatech.diary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_Feed extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context context;
    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference mDatabaseReference; // 데이터베이스의 주소를 저장합니다.
    private static FirebaseDatabase mFirebaseDatabase; // 데이터베이스에 접근할 수 있는 진입점 클래스입니다.
    private static FirebaseUser user;
    private int i = 0;

    private ArrayList<FeedData> mList = new ArrayList<FeedData>();

    public RecyclerViewAdapter_Feed(ArrayList<FeedData> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_row, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {

    }

    private void populateItemRows(ItemViewHolder holder, int position) {
        FeedData item = mList.get(position);
        holder.setItem(item);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView theme;
        private final TextView day;
        private final TextView content;
        private final TextView like;
        private final TextView comment;
        private final ImageView image;
        private final ImageView like_btn;
        private final ImageView comment_btn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            theme = itemView.findViewById(R.id.feed_theme);
            day = itemView.findViewById(R.id.feed_uptime);
            content = itemView.findViewById(R.id.feed_content);
            like = itemView.findViewById(R.id.like_num);
            comment = itemView.findViewById(R.id.comment_num);
            image = itemView.findViewById(R.id.feed_image);
            like_btn = itemView.findViewById(R.id.like_button);
            comment_btn = itemView.findViewById(R.id.comment_button);
        }

        public void setItem(FeedData mList) {
            theme.setText(mList.getTheme());
            day.setText(mList.getDay());
            content.setText(mList.getContent());
            like.setText(mList.getLike());
            comment.setText(mList.getComment());
            if(mList.getImageurl() != null){
                Glide.with(context)
                        .load(Uri.parse(mList.getImageurl()))
                        .override(700,700)
                        .into(image);
                image.setVisibility(View.VISIBLE);
            }
            heartOnOff(mList);
            if(i == 0)
                like_btn.setImageResource(R.drawable.heart_1);
            else if(i == 1)
                like_btn.setImageResource(R.drawable.heart_2);

            like_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view)
                {
                    //if(){
                    addFeed(mList);
                    if(i == 0)
                        like_btn.setImageResource(R.drawable.heart_1);
                    else if(i == 1)
                        like_btn.setImageResource(R.drawable.heart_2);
                    //}
                }
            });
            //comment_btn
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public void addFeed(FeedData mList) {
        String userid = mList.getUserid();
        String date = mList.getDay();
        String time = mList.getTime();

        if(i == 1){
            int a = Integer.parseInt(mList.getLike());
            a -=1;
            mDatabaseReference.child("Feed").child(date + " " + time + " " + userid).child("like").setValue(String.valueOf(a));
            mDatabaseReference.child("Feed").child(date + " " + time + " " + userid).child("like_clicker").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getValue().equals(user.getUid())) {
                            mDatabaseReference.child("Feed").child(date + " " + time + " " + userid).child("like_clicker").child(dataSnapshot.getKey()).setValue(null);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            i=0;
        }else if( i == 0){
            int a = Integer.parseInt(mList.getLike());
            a +=1;
            mDatabaseReference.child("Feed").child(date + " " + time + " " + userid).child("like").setValue(String.valueOf(a));
            mDatabaseReference.child("Feed").child(date + " " + time + " " + userid).child("like_clicker").push().setValue(user.getUid());
            i=1;
        }
    }

    public void heartOnOff(FeedData mList){
        String userid = mList.getUserid();
        String date = mList.getDay();
        String time = mList.getTime();


        mDatabaseReference.child("Feed").child(date + " " + time + " " + userid).child("like_clicker").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.getValue().equals(user.getUid())){
                        i = 1;
                        break;
                    } else
                        i = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}