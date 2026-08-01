package ru.matrp.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.adapter.FaqAdapter;
import ru.matrp.launcher.other.Lists;
import ru.matrp.bonus.R;

public class FaqFragment extends MainActivity {

    public ImageView btn_close;
    public RecyclerView faq_recycler;
    public FaqAdapter faqAdapter;

    public FaqFragment() {
        super();
        faqInit();
    }

    public void faqInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_faq, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        faq_recycler = (RecyclerView) viewGroup.findViewById(R.id.faq_recycler);

        faq_recycler.setHasFixedSize(true);
        //faq_recycler.setHasTransientState(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.getMainActivity(), LinearLayoutManager.VERTICAL, false);
        faq_recycler.setLayoutManager(layoutManager);

        faqAdapter = new FaqAdapter(MainActivity.getMainActivity(), Lists.faqlist);
        faq_recycler.setAdapter(faqAdapter);
        faq_recycler.setItemAnimator(faq_recycler.getItemAnimator());

        btn_close.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener( view -> { hide(); });

        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point point = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
        btn_close.clearAnimation();
        btn_close.setTranslationY(point.y);
        btn_close.animate().setDuration(300L).translationY(0.0f).start();
        faq_recycler.clearAnimation();
        faq_recycler.setTranslationY(point.y);
        faq_recycler.animate().setDuration(300L).translationY(0.0f).start();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point point = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
        mHandler.postDelayed(new ssetVisibility(), 300L);
        btn_close.clearAnimation();
        btn_close.setTranslationY(0.0f);
        btn_close.animate().setDuration(300L).translationY(point.y).start();
        faq_recycler.clearAnimation();
        faq_recycler.setTranslationY(0.0f);
        faq_recycler.animate().setDuration(300L).translationY(point.y).start();

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
