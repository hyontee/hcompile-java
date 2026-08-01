package ru.matrp.launcher.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.adapter.SliderStoriesAdapter;
import ru.matrp.launcher.other.Lists;
import ru.matrp.bonus.R;

public class StoryFragment extends MainActivity {

    public ImageView btn_close;
    public ImageView story_picture_back;
    public ImageView story_picture_front;
    public TextView story_caption_top;
    public TextView story_caption_bottom;
    public View story_prev;
    public View story_next;

    public float scalleX;
    public float scalleY;
    public int translationX;
    public int translationY;

    private SliderStoriesAdapter sliderStoriesAdapter;
    private SliderView sliderView;

    public StoryFragment() {
        super();
        storyInit();
    }

    public void storyInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_story, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        story_picture_back = (ImageView) viewGroup.findViewById(R.id.story_picture_back);
        story_picture_front = (ImageView) viewGroup.findViewById(R.id.story_picture_front);
        story_caption_top = (TextView) viewGroup.findViewById(R.id.story_caption_top);
        story_caption_bottom = (TextView) viewGroup.findViewById(R.id.story_caption_bottom);
        story_prev = viewGroup.findViewById(R.id.story_prev);
        story_next = viewGroup.findViewById(R.id.story_next);
        sliderView = (SliderView) viewGroup.findViewById(R.id.sliderView);
        btn_close.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        viewGroup.setVisibility(View.GONE);
    }

    public void setSlideSelection(int pos) {
        sliderStoriesAdapter = new SliderStoriesAdapter(MainActivity.getMainActivity());
        sliderView.setSliderAdapter(sliderStoriesAdapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderStoriesAdapter.addItems(Lists.nlist);
        sliderView.getSliderPager().setCurrentItem(pos, false);
        sliderView.getPagerIndicator().setSelection(pos);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        sliderView.clearAnimation();
        sliderView.setAlpha(0.0f);
        sliderView.setTranslationY(sliderView.getHeight());
        sliderView.animate().setDuration(300L).translationY(0.0f).alpha(1.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setScaleX(scalleX);
        viewGroup.setScaleY(scalleY);
        viewGroup.setPivotX(0.0f);
        viewGroup.setPivotY(0.0f);
        viewGroup.setTranslationX(translationX);
        viewGroup.setTranslationY(translationY);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).translationX(0.0f).translationY(0.0f).setDuration(300L);
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        sliderView.clearAnimation();
        sliderView.setAlpha(1.0f);
        sliderView.setTranslationY(0.0f);
        sliderView.animate().setDuration(300L).translationY(sliderView.getHeight()).alpha(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setScaleX(1.0f);
        viewGroup.setScaleY(1.0f);
        viewGroup.setPivotX(0.0f);
        viewGroup.setPivotY(0.0f);
        viewGroup.setTranslationX(0.0f);
        viewGroup.setTranslationY(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new ssetVisibility(), 300L);
        viewGroup.animate().alpha(0.0f).scaleX(scalleX).scaleY(scalleY).translationX(translationX).translationY(translationY).setDuration(300L);
    }

    public class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }
}
