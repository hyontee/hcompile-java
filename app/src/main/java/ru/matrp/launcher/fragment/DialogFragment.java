package ru.matrp.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.bonus.R;

public class DialogFragment extends MainActivity {

    public FrameLayout dialog_layout;
    public ImageView dialog_image;
    public TextView dialog_text;
    public TextView dialog_positive_text;
    public TextView dialog_negative_text;
    public FrameLayout dialog_positive;
    public FrameLayout dialog_negative;

    public DialogFragment() {
        super();
        dialogInit();
    }
    public void dialogInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_dialog, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        dialog_layout = (FrameLayout) viewGroup.findViewById(R.id.dialog_layout);
        dialog_image = (ImageView) viewGroup.findViewById(R.id.dialog_image);
        dialog_text = (TextView) viewGroup.findViewById(R.id.dialog_text);
        dialog_positive_text = (TextView) viewGroup.findViewById(R.id.dialog_positive_text);
        dialog_negative_text = (TextView) viewGroup.findViewById(R.id.dialog_negative_text);
        dialog_positive = (FrameLayout) viewGroup.findViewById(R.id.dialog_positive);
        dialog_negative = (FrameLayout) viewGroup.findViewById(R.id.dialog_negative);
        viewGroup.setVisibility(View.GONE);
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point point = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        dialog_layout.clearAnimation();
        dialog_layout.setTranslationY(0.0f);
        dialog_layout.animate().setDuration(150L).translationY(point.y).start();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        mHandler.postDelayed(new ssetVisibility(), 300L);
        viewGroup.animate().setDuration(300L).alpha(0.0f).start();
    }

    public final void show(int i10, String str, String str2, String str3, View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        dialog_image.setImageResource(i10);
        dialog_text.setText(str);
        dialog_text.setMovementMethod(LinkMovementMethod.getInstance());
        dialog_text.setLinkTextColor(-1);
        dialog_text.setHighlightColor(0);
        dialog_positive_text.setText(Html.fromHtml(str2));
        if (str2 == null || str2.isEmpty()) {
            dialog_positive.setVisibility(View.GONE);
        } else {
            dialog_positive.setVisibility(View.VISIBLE);
            dialog_positive.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), dialog_positive));
            dialog_positive.setOnClickListener(onClickListener);
        }
        if (str3 == null) {
            dialog_negative.setVisibility(View.GONE);
        } else if(str3 != null) {
            dialog_negative_text.setText(Html.fromHtml(str3));
            dialog_negative.setOnClickListener(onClickListener2);
            dialog_negative.setVisibility(View.VISIBLE);
        }
        dialog_negative.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), dialog_negative));
        Point point = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        mHandler.removeCallbacksAndMessages(null);
        dialog_layout.clearAnimation();
        dialog_layout.setTranslationY(-point.y);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.animate().setDuration(300L).alpha(1.0f).start();
        dialog_layout.animate().setDuration(150L).translationY(0.0f).start();
    }

    public static class closeDialog implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MainActivity.getMainActivity().dialogFragment.hide();
        }
    }

    public class ssetVisibility implements Runnable {

        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }

    /*@Override // v6.n0
    public final ViewPropertyAnimator j() {
        Point f10 = a0.b.f(this.f13561a.getWindowManager().getDefaultDisplay());
        this.f13546f.clearAnimation();
        this.f13546f.setTranslationY(0.0f);
        this.f13546f.animate().setDuration(150L).translationY(f10.y).start();
        this.f13562b.setVisibility(0);
        this.f13562b.clearAnimation();
        this.f13562b.setAlpha(1.0f);
        return this.f13562b.animate().setDuration(300L).alpha(0.0f);
    }

    public final void l() {
        if (c()) {
            this.f13551k.setOnClickListener(null);
            this.f13552l.setOnClickListener(null);
            a();
        }
    }

    public final void m(int i10, String str, String str2, String str3, View.OnClickListener onClickListener, View.OnClickListener onClickListener2) {
        if (c()) {
            this.f13553m = new a(i10, str, str2, str3, onClickListener, onClickListener2);
            l();
            return;
        }
        i();
        if (c()) {
            this.f13553m = null;
            this.f13547g.setImageResource(i10);
            this.f13548h.setText(Html.fromHtml(str));
            this.f13548h.setMovementMethod(LinkMovementMethod.getInstance());
            this.f13548h.setLinkTextColor(-1);
            this.f13548h.setHighlightColor(0);
            this.f13549i.setText(Html.fromHtml(str2));
            this.f13550j.setText(Html.fromHtml(str3));
            if (str2 == null || str2.isEmpty()) {
                this.f13551k.setVisibility(8);
            } else {
                this.f13551k.setVisibility(0);
                FrameLayout frameLayout = this.f13551k;
                frameLayout.setOnTouchListener(new h5.a(this.f13561a, frameLayout));
                this.f13551k.setOnClickListener(onClickListener);
            }
            if (str3 == null || str3.isEmpty()) {
                this.f13552l.setVisibility(8);
                return;
            }
            this.f13552l.setVisibility(0);
            FrameLayout frameLayout2 = this.f13552l;
            frameLayout2.setOnTouchListener(new h5.a(this.f13561a, frameLayout2));
            this.f13552l.setOnClickListener(onClickListener2);
        }
    }*/
}
