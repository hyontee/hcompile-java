package ru.matrp.launcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.matrp.launcher.model.News;
import ru.matrp.bonus.R;

public class SliderStoriesAdapter extends SliderViewAdapter<SliderStoriesAdapter.Holder> {

    private final Context context;
    private List<News> stories = new ArrayList();

    public SliderStoriesAdapter(Context context) {
        this.context = context;
    }

    public void addItems(List<News> list) {
        this.stories = list;
        notifyDataSetChanged();
    }

    public void deleteItem(int i) {
        this.stories.remove(i);
        notifyDataSetChanged();
    }

    public void addItem(News story) {
        this.stories.add(story);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.story_pager_item, (ViewGroup) null));
    }

    public void onBindViewHolder(Holder holder, int i) {
        News story = this.stories.get(i);
        holder.story_caption_top.setText(story.getTitle());
        holder.story_caption_bottom.setText(story.getTitleBig());
        Glide.with(this.context).load(story.getImageFullUrl()).into(holder.story_picture_back);
        holder.story_details.setVisibility(story.getUrl().isEmpty() ? View.GONE : View.VISIBLE);
        holder.story_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(holder.hcontext, R.anim.button_click));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.hcontext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(story.getUrl())));
                    }
                }, 200);
            }
        });
    }

    @Override
    public int getCount() {
        return this.stories.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder {
        ImageView story_picture_back;
        View itemView;
        CardView story_details;
        TextView story_caption_top, story_caption_bottom;
        Context hcontext;

        public Holder(View view) {
            super(view);
            this.story_caption_top = (TextView) view.findViewById(R.id.story_caption_top);
            this.story_caption_bottom = (TextView) view.findViewById(R.id.story_caption_bottom);
            this.story_picture_back = (ImageView) view.findViewById(R.id.story_picture_back);
            this.story_details = (CardView) view.findViewById(R.id.story_details);
            this.hcontext = context;
            this.itemView = view;
        }
    }
}
