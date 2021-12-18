package com.koreatech.diary;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_Feed extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context context;

    private ArrayList<FeedData> mList = new ArrayList<FeedData>();

    public RecyclerViewAdapter_Feed(ArrayList<FeedData> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            theme = itemView.findViewById(R.id.feed_theme);
            day = itemView.findViewById(R.id.feed_uptime);
            content = itemView.findViewById(R.id.feed_content);
            like = itemView.findViewById(R.id.like_num);
            comment = itemView.findViewById(R.id.comment_num);
            image = itemView.findViewById(R.id.feed_image);
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
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}