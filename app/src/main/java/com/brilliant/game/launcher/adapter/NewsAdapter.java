package com.brilliant.game.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brilliant.game.R;
import com.brilliant.game.launcher.model.News;
import com.brilliant.game.launcher.util.SimpleImageLoader;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context;
    private final ArrayList<News> nlist;

    public NewsAdapter(Context context, ArrayList<News> nlist) {
        this.context = context;
        this.nlist = nlist;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = nlist.get(position);
        holder.title.setText(news.getTitle());
        SimpleImageLoader.load(news.getImageUrl(), holder.image);
    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final ImageView image;

        public NewsViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvNewsText);
            image = itemView.findViewById(R.id.ivNewsImage);
        }
    }
}
