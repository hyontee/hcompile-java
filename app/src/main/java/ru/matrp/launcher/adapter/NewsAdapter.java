package ru.matrp.launcher.adapter;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.model.News;
import ru.matrp.bonus.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
	Context context;
	
	ArrayList<News> nlist;
	
	public NewsAdapter(Context context, ArrayList<News> nlist){
		 this.context = context;
		 this.nlist = nlist; 
	}
	
	@NonNull
	@Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
		return new NewsViewHolder(v); 
    }
  
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = nlist.get(position);
		//holder.title.setText(news.getTitle());
        holder.image.setAlpha(0.0f);
		Glide.with(context).load(news.getImageUrl()).into(holder.image);
        holder.image.animate().alpha(1.0f).setDuration(500L);
        holder.container.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), holder.container));
		holder.container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startStorySlider(position, holder.view);
                        if (MainActivity.isAuth) {
                            HashMap<String, Boolean> storiesInfo = new HashMap<>();
                            storiesInfo.put("stories_" + position, true);
                            FirebaseDatabase.getInstance().getReference().child("Users").child("User-stories").child("Stories_" + position).child(FirebaseAuth.getInstance().getUid()).setValue(storiesInfo);
                        }
                    }
                }, 200);
            }
        });
        if (MainActivity.isAuth) {
            FirebaseDatabase.getInstance().getReference().child("Users").child("User-stories").child("Stories_" + position).child(FirebaseAuth.getInstance().getUid()).child("stories_" + position).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(Boolean.class) == null) {
                        holder.story_item_stroke.setVisibility(View.VISIBLE);
                    } else {
                        holder.story_item_stroke.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.story_item_stroke.setVisibility(View.VISIBLE);
        }
    }

    public void startStorySlider(int pos, View view) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        int i10 = iArr[0];
        int i11 = iArr[1];
        int width = view.getWidth();
        int height = view.getHeight();
        Point f11 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        Rect rect = new Rect();
        MainActivity.getMainActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int i12 = rect.top;
        MainActivity.getMainActivity().storyFragment.translationX = i10;
        MainActivity.getMainActivity().storyFragment.translationY = i11 - i12;
        MainActivity.getMainActivity().storyFragment.scalleX = width / f11.x;
        MainActivity.getMainActivity().storyFragment.scalleY = height / f11.y;
        MainActivity.getMainActivity().storyFragment.setSlideSelection(pos);
        MainActivity.getMainActivity().storyFragment.show();
    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        
		TextView title;
		ImageView image, story_item_stroke;
		FrameLayout container;
        View view;

        public NewsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            story_item_stroke = itemView.findViewById(R.id.story_item_stroke);
		    title = itemView.findViewById(R.id.title);
			image = itemView.findViewById(R.id.story_item_image);
			container = itemView.findViewById(R.id.container);
        }
    }
}
