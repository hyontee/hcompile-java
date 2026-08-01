package ru.matrp.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.adapter.ServersAdapter;
import ru.matrp.launcher.model.Servers;
import ru.matrp.launcher.other.Lists;
import ru.matrp.bonus.R;

public class ServerSelectFragment extends MainActivity {

    public ImageView f13713h;
    public TextView f13714i;
    public RecyclerView f13715j;

    ServersAdapter serversAdapter;
    ArrayList<Servers> slist;

    public ServerSelectFragment() {
        super();
        serverSelectInit();
    }

    public void serverSelectInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_server_select, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        f13713h = (ImageView) viewGroup.findViewById(R.id.btn_close);
        f13714i = (TextView) viewGroup.findViewById(R.id.serverlist_caption);
        f13715j = (RecyclerView) viewGroup.findViewById(R.id.serverlist_recycler);
        f13713h.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), f13713h));
        f13713h.setOnClickListener(v -> {
            hide();
        });

        f13715j.setHasFixedSize(true);
        LinearLayoutManager layoutManagerr = new LinearLayoutManager(MainActivity.getMainActivity());
        f13715j.setLayoutManager(layoutManagerr);

        //slist = Lists.slist;


        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(f10);
        f13713h.clearAnimation();
        f13713h.setTranslationY(f10.y);
        f13713h.animate().setDuration(300L).translationY(0.0f).start();
        f13714i.clearAnimation();
        f13714i.setTranslationY(f10.y);
        f13714i.animate().setDuration(300L).translationY(0.0f).start();
        f13715j.clearAnimation();
        f13715j.setTranslationY(f10.y);
        f13715j.animate().setDuration(300L).translationY(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
        serversAdapter = new ServersAdapter(MainActivity.getMainActivity(), Lists.slist);
        f13715j.setAdapter(serversAdapter);
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(f10);
        f13713h.clearAnimation();
        f13713h.setTranslationY(0.0f);
        f13713h.animate().setDuration(300L).translationY(f10.y).start();
        f13714i.clearAnimation();
        f13714i.setTranslationY(0.0f);
        f13714i.animate().setDuration(300L).translationY(f10.y).start();
        f13715j.clearAnimation();
        f13715j.setTranslationY(0.0f);
        f13715j.animate().setDuration(300L).translationY(f10.y).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new ssetVisibility(), 300L);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
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

