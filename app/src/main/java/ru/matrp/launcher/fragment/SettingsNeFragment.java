package ru.matrp.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.bonus.R;

public class SettingsNeFragment extends MainActivity {

    public ConstraintLayout settings_layout;
    public ImageView btn_close;
    public TextView account_not_auth_text;
    public FrameLayout account_layout;
    public ImageView account_background;
    public ImageView account_image;
    public TextView account_text;
    public FrameLayout btn_reinstall_client;
    public FrameLayout btn_reinstall_data;
    public FrameLayout btn_reinstall_data_dev;
    public FrameLayout btn_changepass;
    public TextView faq_text;
    public ConstraintLayout footer_layout;
    public FrameLayout btn_logout;
    public FirebaseAuth mAuth;

    public SettingsNeFragment() {
        super();
        settingsInit();
    }

    public void settingsInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_settings, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        mAuth = FirebaseAuth.getInstance();
        settings_layout = (ConstraintLayout) viewGroup.findViewById(R.id.settings_layout);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        account_not_auth_text = (TextView) viewGroup.findViewById(R.id.account_not_auth_text);
        account_layout = (FrameLayout) viewGroup.findViewById(R.id.account_layout);
        account_background = (ImageView) viewGroup.findViewById(R.id.account_background);
        account_image = (ImageView) viewGroup.findViewById(R.id.account_image);
        account_text = (TextView) viewGroup.findViewById(R.id.account_text);
        btn_reinstall_client = (FrameLayout) viewGroup.findViewById(R.id.btn_reinstall_client);
        btn_reinstall_data = (FrameLayout) viewGroup.findViewById(R.id.btn_reinstall_data);
        btn_reinstall_data_dev = (FrameLayout) viewGroup.findViewById(R.id.btn_reinstall_data_dev);
        btn_changepass = (FrameLayout) viewGroup.findViewById(R.id.btn_changepass);
        faq_text = (TextView) viewGroup.findViewById(R.id.faq_text);
        footer_layout = (ConstraintLayout) viewGroup.findViewById(R.id.footer_layout);
        btn_logout = (FrameLayout) viewGroup.findViewById(R.id.btn_logout);
        btn_close.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener(view -> { hide(); });
        SpannableString spannableString = new SpannableString("Проблемы? Мы можем вам помочь!");
        spannableString.setSpan((Typeface.defaultFromStyle(Typeface.NORMAL)), 10, spannableString.length(), 33);
        spannableString.setSpan(new UnderlineSpan(), 10, spannableString.length(), 33);
        faq_text.setText(spannableString);
        faq_text.setOnClickListener(view -> {
            MainActivity.getMainActivity().faqFragment.show();
        });
        btn_logout.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_logout));
        btn_logout.setOnClickListener(v -> {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_exit, "Вы уверены, что хотите выйти из аккаунта?", "Да", "Нет", new AccSignOut(), new DialogFragment.closeDialog());
        });
        upSettings();
        btn_reinstall_data_dev.setVisibility(View.GONE);
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
        settings_layout.clearAnimation();
        settings_layout.setTranslationY(point.y);
        settings_layout.animate().setDuration(300L).translationY(0.0f).start();
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
        settings_layout.clearAnimation();
        settings_layout.setTranslationY(0.0f);
        settings_layout.animate().setDuration(300L).translationY(point.y).start();
    }

    public void upSettings() {
        if (isAuth) {
            FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("google-email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email_g = snapshot.getValue(String.class);
                    email_google = email_g;
                    Log.i("edgar", "Email = " + email_g);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            FirebaseDatabase.getInstance().getReference().child("Users").child("User-info").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("way").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer paramInt = snapshot.getValue(Integer.class);
                    way = paramInt;
                    Log.i("edgar", "way = " + way);
                    UpdateSettings();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            email_google = null;
            way = null;
            UpdateSettings();
        }
    }

    public void UpdateSettings() {
        if (isAuth) {
            footer_layout.setVisibility(View.VISIBLE);
            if (way == 2) {
                btn_changepass.setVisibility(View.GONE);
                account_not_auth_text.setVisibility(View.GONE);
                account_layout.setVisibility(View.VISIBLE);
                account_image.setVisibility(View.VISIBLE);
                account_layout.setOnTouchListener(null);
                account_layout.setOnClickListener(null);
                account_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                account_text.setText(email_google);
                account_background.setImageResource(R.drawable.auth_bg_google);
                account_image.setImageResource(R.drawable.ic_launcher_google);
            }
        } else {
            footer_layout.setVisibility(View.GONE);
            btn_changepass.setVisibility(View.GONE);
            account_not_auth_text.setVisibility(View.VISIBLE);
            account_layout.setVisibility(View.VISIBLE);
            account_image.setVisibility(View.GONE);
            account_layout.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), account_layout));
            account_layout.setOnClickListener(v -> {
                hide();
                MainActivity.getMainActivity().authFragment.show();
            });
            account_background.setImageResource(R.drawable.auth_bg_button);
            account_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            account_text.setText("Авторизоваться");
        }
    }

    public class AccSignOut implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mAuth.signOut();
            MainActivity.isAuth = false;
            MainActivity.getMainActivity().mainFragment.upServerId();// EDGAR 3.0 newLauncher version от 06.01.2024
            MainActivity.getMainActivity().settingsNeFragment.hide();
            MainActivity.getMainActivity().dialogFragment.hide();
            upSettings();
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
}
