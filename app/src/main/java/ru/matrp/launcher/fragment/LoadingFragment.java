package ru.matrp.launcher.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.bonus.R;

public class LoadingFragment extends MainActivity {

    public LoadingFragment() {
        super();
        loadingInit();
    }

    public void loadingInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_loading, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
        mHandler.postDelayed(new ssetVisibility(), 300L);
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
