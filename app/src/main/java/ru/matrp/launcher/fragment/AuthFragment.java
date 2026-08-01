package ru.matrp.launcher.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.other.Interface;
import ru.matrp.launcher.other.Lists;
import ru.matrp.bonus.R;

public class AuthFragment extends MainActivity {

    public boolean isFreeEmail = true;
    public boolean isSendCode = false;
    public Point point1 = new Point();
    public int regStatus = 1;
    private FirebaseAuth mAuth;
    public String codeMail;
    public int secs = 60;

    public ImageView btn_close;
    public ImageView btn_faq;
    public LinearLayout auth_bottom;
    public TextView faq_text;
    public LinearLayout auth_layout_main;
    public FrameLayout main_btn_email;
    public FrameLayout main_btn_google;
    public FrameLayout main_btn_vk;
    public TextView policy_text;
    public LinearLayout auth_layout_email;
    public TextView email_caption_1;
    public TextView email_caption_2;
    public TextView email_info;
    public ViewGroup email_layout_email;
    public ImageView email_layout_email_bg;
    public ImageView email_layout_email_image;
    public EditText email_layout_email_input;
    public ViewGroup email_layout_email_error;
    public TextView email_layout_email_error_text;
    public ViewGroup email_layout_pass;
    public ImageView email_layout_pass_bg;
    public ImageView email_layout_pass_image;
    public EditText email_layout_pass_input;
    public ViewGroup email_layout_pass_error;
    public TextView email_layout_pass_error_text;
    public TextView email_layout_pass_recover;
    public ViewGroup email_layout_repeat_pass;
    public ImageView email_layout_repeat_pass_bg;
    public ImageView email_layout_repeat_pass_image;
    public EditText email_layout_repeat_pass_input;
    public ViewGroup email_layout_repeat_pass_error;
    public TextView email_layout_repeat_pass_error_text;
    public ViewGroup email_layout_code;
    public ImageView email_layout_code_bg;
    public ImageView email_layout_code_image;
    public EditText email_layout_code_input;
    public ViewGroup email_layout_code_error;
    public TextView email_layout_code_error_text;
    public TextView email_layout_code_send;
    public ViewGroup email_layout_continue;
    public int f13290a0;
    public int X;
    public int Y;

    public AuthFragment() {
        super();
        InitAuth();
    }

    public void InitAuth(){
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_auth, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point1);
        mAuth = FirebaseAuth.getInstance();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        btn_faq = (ImageView) viewGroup.findViewById(R.id.btn_faq);
        auth_bottom = (LinearLayout) viewGroup.findViewById(R.id.auth_bottom);
        faq_text = (TextView) viewGroup.findViewById(R.id.faq_text);
        auth_layout_main = (LinearLayout) viewGroup.findViewById(R.id.auth_layout_main);
        main_btn_email = (FrameLayout) viewGroup.findViewById(R.id.main_btn_email);
        main_btn_google = (FrameLayout) viewGroup.findViewById(R.id.main_btn_google);
        main_btn_vk = (FrameLayout) viewGroup.findViewById(R.id.main_btn_vk);
        policy_text = (TextView) viewGroup.findViewById(R.id.policy_text);
        auth_layout_email = (LinearLayout) viewGroup.findViewById(R.id.auth_layout_email);
        email_caption_1 = (TextView) viewGroup.findViewById(R.id.email_caption_1);
        email_caption_2 = (TextView) viewGroup.findViewById(R.id.email_caption_2);
        email_info = (TextView) viewGroup.findViewById(R.id.email_info);
        email_layout_email = (ViewGroup) viewGroup.findViewById(R.id.email_layout_email);
        email_layout_email_bg = (ImageView) viewGroup.findViewById(R.id.email_layout_email_bg);
        email_layout_email_image = (ImageView) viewGroup.findViewById(R.id.email_layout_email_image);
        email_layout_email_input = (EditText) viewGroup.findViewById(R.id.email_layout_email_input);
        email_layout_email_error = (ViewGroup) viewGroup.findViewById(R.id.email_layout_email_error);
        email_layout_email_error_text = (TextView) viewGroup.findViewById(R.id.email_layout_email_error_text);
        email_layout_pass = (ViewGroup) viewGroup.findViewById(R.id.email_layout_pass);
        email_layout_pass_bg = (ImageView) viewGroup.findViewById(R.id.email_layout_pass_bg);
        email_layout_pass_image = (ImageView) viewGroup.findViewById(R.id.email_layout_pass_image);
        email_layout_pass_input = (EditText) viewGroup.findViewById(R.id.email_layout_pass_input);
        email_layout_pass_error = (ViewGroup) viewGroup.findViewById(R.id.email_layout_pass_error);
        email_layout_pass_error_text = (TextView) viewGroup.findViewById(R.id.email_layout_pass_error_text);
        email_layout_pass_recover = (TextView) viewGroup.findViewById(R.id.email_layout_pass_recover);
        email_layout_repeat_pass = (ViewGroup) viewGroup.findViewById(R.id.email_layout_repeat_pass);
        email_layout_repeat_pass_bg = (ImageView) viewGroup.findViewById(R.id.email_layout_repeat_pass_bg);
        email_layout_repeat_pass_image = (ImageView) viewGroup.findViewById(R.id.email_layout_repeat_pass_image);
        email_layout_repeat_pass_input = (EditText) viewGroup.findViewById(R.id.email_layout_repeat_pass_input);
        email_layout_repeat_pass_error = (ViewGroup) viewGroup.findViewById(R.id.email_layout_repeat_pass_error);
        email_layout_repeat_pass_error_text = (TextView) viewGroup.findViewById(R.id.email_layout_repeat_pass_error_text);
        email_layout_code = (ViewGroup) viewGroup.findViewById(R.id.email_layout_code);
        email_layout_code_bg = (ImageView) viewGroup.findViewById(R.id.email_layout_code_bg);
        email_layout_code_image = (ImageView) viewGroup.findViewById(R.id.email_layout_code_image);
        email_layout_code_input = (EditText) viewGroup.findViewById(R.id.email_layout_code_input);
        email_layout_code_error = (ViewGroup) viewGroup.findViewById(R.id.email_layout_code_error);
        email_layout_code_error_text = (TextView) viewGroup.findViewById(R.id.email_layout_code_error_text);
        email_layout_code_send = (TextView) viewGroup.findViewById(R.id.email_layout_code_send);
        email_layout_continue = (ViewGroup) viewGroup.findViewById(R.id.email_layout_continue);
        btn_close.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener(v -> { hide(); });
        btn_faq.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_faq));
        btn_faq.setOnClickListener(v -> { MainActivity.getMainActivity().faqFragment.show();});
        SpannableString spannableString = new SpannableString("Проблемы? Мы можем вам помочь!");
        spannableString.setSpan((Typeface.defaultFromStyle(Typeface.BOLD)), 10, spannableString.length(), 33);
        spannableString.setSpan(new UnderlineSpan(), 10, spannableString.length(), 33);
        faq_text.setText(spannableString);
        faq_text.setOnClickListener(v -> { MainActivity.getMainActivity().faqFragment.show();});
        SpannableString spannableString2 = new SpannableString("Забыли пароль?");
        spannableString2.setSpan(new UnderlineSpan(), 0, spannableString2.length(), 33);
        email_layout_pass_recover.setText(spannableString2);
        email_layout_pass_recover.setOnClickListener(view -> {
            MainActivity.getMainActivity().loadingFragment.show();
            email_caption_1.clearAnimation();
            email_caption_1.setAlpha(1.0f);
            email_caption_1.setVisibility(View.VISIBLE);
            email_caption_1.setTranslationX(0.0f);
            email_caption_1.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

            email_caption_2.clearAnimation();
            email_caption_2.setAlpha(1.0f);
            email_caption_2.setVisibility(View.VISIBLE);
            email_caption_2.setTranslationX(0.0f);
            email_caption_2.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

            email_info.clearAnimation();
            email_info.setAlpha(1.0f);
            email_info.setVisibility(View.VISIBLE);
            email_info.setTranslationX(0.0f);
            email_info.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

            email_layout_pass.clearAnimation();
            email_layout_pass.setVisibility(View.GONE);
            email_layout_pass_recover.setVisibility(View.GONE);

            email_layout_email_input.setEnabled(true);
            email_layout_email_input.setFocusable(true);
            email_layout_email_input.setFocusableInTouchMode(true);
            email_layout_email_input.setTextColor(-1);

            email_layout_email_image.animate().alpha(1.0f).setDuration(150L).start();

            InputMethodManager immm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            immm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

            regStatus = 6;

            MainActivity.getMainActivity().loadingFragment.hide();

            mHandler.postDelayed(new setTextRegistr("Восстановление", "пароля", "Введите адрес электронной почты Вашего аккаунта."), 150L);
        });
        SpannableString spannableString3 = new SpannableString("Не пришёл код? Отправить повторно");
        spannableString3.setSpan(new UnderlineSpan(), 15, spannableString3.length(), 33);
        email_layout_code_send.setText(spannableString3);
        email_layout_code_send.setOnClickListener(view -> {
            if(!isSendCode) {
                MainActivity.getMainActivity().loadingFragment.show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://edgars.site/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Interface sInterface = retrofit.create(Interface.class);
                String mail = email_layout_email_input.getText().toString();

                Call<String> call = sInterface.authMail(Lists.verifyAuthUrl, mail);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null && response.isSuccessful()) {
                            String result = response.body();
                            codeMail = result;
                            MainActivity.getMainActivity().loadingFragment.hide();
                            InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                            mHandler.post(new Secynds());
                            isSendCode = true;
                         } else {
                            MainActivity.getMainActivity().loadingFragment.hide();
                            Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        main_btn_email.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), main_btn_email));
        main_btn_email.setOnClickListener(view -> {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "В данный момент эта функция находится в разработке", "Понятно", null, new DialogFragment.closeDialog(), null);
            /*regStatus = 2;
            Point point = new Point();
            MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
            auth_layout_main.clearAnimation();
            auth_layout_main.setAlpha(1.0f);
            auth_layout_main.setVisibility(View.VISIBLE);
            auth_layout_main.setTranslationX(0.0f);
            auth_layout_main.animate().alpha(0.0f).translationX(-auth_layout_main.getWidth()).setDuration(300L);
            auth_layout_email.clearAnimation();
            auth_layout_email.setAlpha(0.0f);
            auth_layout_email.setVisibility(View.VISIBLE);
            auth_layout_email.setTranslationX(point.y);
            auth_layout_email.animate().alpha(1.0f).translationX(0.0f).setDuration(300L);
            email_caption_1.setText("Вход");
            email_caption_2.setText("через почту");
            email_info.setText("Введите почтовый адрес чтобы продолжить процесс регистрации или авторизации аккаунта в игре.");
            email_layout_email.setVisibility(View.VISIBLE);
            email_layout_pass.setVisibility(View.GONE);
            email_layout_repeat_pass.setVisibility(View.GONE);
            email_layout_code.setVisibility(View.GONE);*/
        });

        main_btn_google.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), main_btn_google));
        main_btn_google.setOnClickListener(view -> {
            MainActivity.getMainActivity().onClickAuthGoogle();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null) {
                MainActivity.isAuth = true;
            } else {
                MainActivity.isAuth = false;
            }
            //regStatus = 10;
        });

        main_btn_vk.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), main_btn_vk));
        main_btn_vk.setOnClickListener(view -> {
            MainActivity.getMainActivity().onClickAuthGoogle();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null) {
                MainActivity.isAuth = true;
            } else {
                MainActivity.isAuth = false;
            }
            //regStatus = 10;
        });

        email_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), email_layout_continue));

        email_layout_continue.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
            if(regStatus == 2) {
                String textEmail = email_layout_email_input.getText().toString();
                int index = textEmail.indexOf("@");
                if (index != -1) {
                    if (!email_layout_email_input.getText().toString().isEmpty()) {
                        //TODO дабавить проверку почты с фаир базе
                        MainActivity.getMainActivity().loadingFragment.show();

                        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String paramValue = userSnapshot.child("email").getValue(String.class);
                                    // здесь вы можете использовать значение параметра или сохранить его в коллекцию для дальнейшей обработки
                                    if(email_layout_email_input.getText().toString().equals(paramValue.toString())) {
                                        isFreeEmail = false;
                                        regStatus = 5;
                                        email_caption_1.clearAnimation();
                                        email_caption_1.setAlpha(1.0f);
                                        email_caption_1.setVisibility(View.VISIBLE);
                                        email_caption_1.setTranslationX(0.0f);
                                        email_caption_1.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                        email_caption_2.clearAnimation();
                                        email_caption_2.setAlpha(1.0f);
                                        email_caption_2.setVisibility(View.VISIBLE);
                                        email_caption_2.setTranslationX(0.0f);
                                        email_caption_2.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                        email_info.clearAnimation();
                                        email_info.setAlpha(1.0f);
                                        email_info.setVisibility(View.VISIBLE);
                                        email_info.setTranslationX(0.0f);
                                        email_info.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                        email_layout_pass.clearAnimation();
                                        email_layout_pass.setVisibility(View.VISIBLE);
                                        /*email_layout_repeat_pass.clearAnimation();
                                        email_layout_repeat_pass.setVisibility(View.VISIBLE);*/
                                        //email_layout_pass_recover.setVisibility(View.GONE);

                                        email_layout_email_input.setEnabled(false);
                                        email_layout_email_input.setFocusable(false);
                                        email_layout_email_input.setFocusableInTouchMode(false);
                                        email_layout_email_input.setTextColor(872415231);

                                        email_layout_email_image.animate().alpha(0.2f).setDuration(150L).start();

                                        InputMethodManager immm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        immm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

                                        MainActivity.getMainActivity().loadingFragment.hide();

                                        mHandler.postDelayed(new setTextRegistr("Авторизация", "через почту", "Авторизуйтесь чтобы погрузиться в безграничные возможности нашей замечательной игры."), 150L);

                                        return;
                                    }
                                    System.out.println("Parameter value: " + paramValue);
                                }
                                if(isFreeEmail) {
                                    regStatus = 3;
                                    email_caption_1.clearAnimation();
                                    email_caption_1.setAlpha(1.0f);
                                    email_caption_1.setVisibility(View.VISIBLE);
                                    email_caption_1.setTranslationX(0.0f);
                                    email_caption_1.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                    email_caption_2.clearAnimation();
                                    email_caption_2.setAlpha(1.0f);
                                    email_caption_2.setVisibility(View.VISIBLE);
                                    email_caption_2.setTranslationX(0.0f);
                                    email_caption_2.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                    email_info.clearAnimation();
                                    email_info.setAlpha(1.0f);
                                    email_info.setVisibility(View.VISIBLE);
                                    email_info.setTranslationX(0.0f);
                                    email_info.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                    email_layout_pass.clearAnimation();
                                    email_layout_pass.setVisibility(View.VISIBLE);
                                    email_layout_repeat_pass.clearAnimation();
                                    email_layout_repeat_pass.setVisibility(View.VISIBLE);
                                    email_layout_pass_recover.setVisibility(View.GONE);

                                    email_layout_email_input.setEnabled(false);
                                    email_layout_email_input.setFocusable(false);
                                    email_layout_email_input.setFocusableInTouchMode(false);
                                    email_layout_email_input.setTextColor(872415231);

                                    email_layout_email_image.animate().alpha(0.2f).setDuration(150L).start();

                                    InputMethodManager immm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    immm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

                                    MainActivity.getMainActivity().loadingFragment.hide();

                                    mHandler.postDelayed(new setTextRegistr("Регистрация", "через почту", "Создайте профиль чтобы погрузиться в безграничные возможности нашей замечательной игры."), 150L);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
                    } else {
                        email_layout_email_error.setVisibility(View.VISIBLE);
                        email_layout_email_error.clearAnimation();
                        email_layout_email_error.animate().translationY(0.0f).setDuration(300L).start();
                        email_layout_email_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                } else {
                    email_layout_email_error.setVisibility(View.VISIBLE);
                    email_layout_email_error.clearAnimation();
                    email_layout_email_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_email_bg.setImageResource(R.drawable.auth_bg_error);
                }
            } else if(regStatus == 3) {
                if (!email_layout_pass_input.getText().toString().isEmpty()) {
                    if (email_layout_pass_input.getText().toString().length() >= 6) {
                        if (!email_layout_repeat_pass_input.getText().toString().isEmpty()) {
                            if (email_layout_pass_input.getText().toString().equals(email_layout_repeat_pass_input.getText().toString())) {
                                MainActivity.getMainActivity().loadingFragment.show();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://edgars.site/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                Interface sInterface = retrofit.create(Interface.class);
                                String mail = email_layout_email_input.getText().toString();

                                Call<String> call = sInterface.authMail(Lists.verifyAuthUrl, mail);

                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        if (response.body() != null && response.isSuccessful()) {
                                            String result = response.body();
                                            codeMail = result;
                                            MainActivity.getMainActivity().loadingFragment.hide();
                                            InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

                                            email_caption_1.clearAnimation();
                                            email_caption_1.setAlpha(1.0f);
                                            email_caption_1.setVisibility(View.VISIBLE);
                                            email_caption_1.setTranslationX(0.0f);
                                            email_caption_1.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                            email_caption_2.clearAnimation();
                                            email_caption_2.setAlpha(1.0f);
                                            email_caption_2.setVisibility(View.VISIBLE);
                                            email_caption_2.setTranslationX(0.0f);
                                            email_caption_2.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                            email_info.clearAnimation();
                                            email_info.setAlpha(1.0f);
                                            email_info.setVisibility(View.VISIBLE);
                                            email_info.setTranslationX(0.0f);
                                            email_info.animate().alpha(0.0f).translationX(-point1.y).setDuration(150L);

                                            email_layout_code.clearAnimation();
                                            email_layout_code.setVisibility(View.VISIBLE);

                                            email_layout_pass_input.setEnabled(false);
                                            email_layout_pass_input.setFocusable(false);
                                            email_layout_pass_input.setFocusableInTouchMode(false);
                                            email_layout_pass_input.setTextColor(872415231);

                                            email_layout_repeat_pass_input.setEnabled(false);
                                            email_layout_repeat_pass_input.setFocusable(false);
                                            email_layout_repeat_pass_input.setFocusableInTouchMode(false);
                                            email_layout_repeat_pass_input.setTextColor(872415231);

                                            email_layout_pass_image.animate().alpha(0.2f).setDuration(150L).start();

                                            email_layout_repeat_pass_image.animate().alpha(0.2f).setDuration(150L).start();

                                            regStatus = 4;

                                            mHandler.postDelayed(new setTextRegistr("Регистрация", "через почту", "На Ваш почтовый адрес отправлен код, который нужно ввести в поле ниже. (Письмо может быть в спаме.)"), 150L);
                                        } else {
                                            MainActivity.getMainActivity().loadingFragment.hide();
                                            Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
                                email_layout_repeat_pass_error.clearAnimation();
                                email_layout_repeat_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                                email_layout_repeat_pass_error_text.setText("Пароли не совпадают");
                                email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_error);
                            }
                        } else {
                            email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
                            email_layout_repeat_pass_error.clearAnimation();
                            email_layout_repeat_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                            email_layout_repeat_pass_error_text.setText("Повторите пароль");
                            email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_error);
                        }
                    } else {
                        email_layout_pass_error.setVisibility(View.VISIBLE);
                        email_layout_pass_error.clearAnimation();
                        email_layout_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                        email_layout_pass_error_text.setText("Пароль слишком короткий");
                        email_layout_pass_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                } else {
                    email_layout_pass_error.setVisibility(View.VISIBLE);
                    email_layout_pass_error.clearAnimation();
                    email_layout_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_pass_error_text.setText("Введите пароль");
                    email_layout_pass_bg.setImageResource(R.drawable.auth_bg_error);
                }
            } else if(regStatus == 4) {
                if(!email_layout_code_input.getText().toString().isEmpty()) {
                    if(codeMail.toString().equals(email_layout_code_input.getText().toString())) {
                        mAuth.createUserWithEmailAndPassword(email_layout_email_input.getText().toString(), email_layout_repeat_pass_input.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    HashMap<String, String> userInfo = new HashMap<>();
                                    userInfo.put("email", email_layout_email_input.getText().toString());

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo);

                                    mHandler.removeCallbacksAndMessages(null);
                                    Point point = new Point();
                                    MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
                                    viewGroup.clearAnimation();
                                    viewGroup.setAlpha(1.0f);
                                    viewGroup.setVisibility(View.VISIBLE);
                                    viewGroup.animate().alpha(0.0f).setDuration(300L);
                                    mHandler.postDelayed(new ssetVisibility(), 300L);
                                    btn_close.clearAnimation();
                                    btn_close.setTranslationY(0.0f);
                                    btn_close.animate().setDuration(300L).translationY(-point.y).start();
                                    btn_faq.clearAnimation();
                                    btn_faq.setTranslationY(0.0f);
                                    btn_faq.animate().setDuration(300L).translationY(-point.y).start();
                                    auth_bottom.clearAnimation();
                                    auth_bottom.setTranslationY(0.0f);
                                    auth_bottom.animate().setDuration(300L).translationY(point.y).start();
                                    auth_layout_main.clearAnimation();
                                    auth_layout_main.setAlpha(1.0f);
                                    auth_layout_main.setVisibility(View.VISIBLE);
                                    auth_layout_main.setTranslationX(0.0f);
                                    auth_layout_main.animate().alpha(0.0f).translationX(point.y).setDuration(300L);
                                    auth_layout_email.clearAnimation();
                                    auth_layout_email.setAlpha(0.0f);
                                    auth_layout_email.setVisibility(View.GONE);
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if(currentUser != null) {
                                        MainActivity.isAuth = true;
                                    } else {
                                        MainActivity.isAuth = false;
                                    }
                                    FirebaseDatabase.getInstance().getReference().child("Servers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("server-id").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Integer paramInt = snapshot.getValue(Integer.class);
                                            server_id = paramInt;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                    ReturnToZero();
                                    regStatus = 1;
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "Ошибка регистрации через Email!\nПопробуйте ещё раз.", "Понятно", null, new closeRegAuth(true), null);
                                }
                            }
                        });
                    } else {
                        email_layout_code_error.setVisibility(View.VISIBLE);
                        email_layout_code_error.clearAnimation();
                        email_layout_code_error.animate().translationY(0.0f).setDuration(300L).start();
                        email_layout_code_error_text.setText("Введен неправильный код");
                        email_layout_code_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                } else {
                    email_layout_code_error.setVisibility(View.VISIBLE);
                    email_layout_code_error.clearAnimation();
                    email_layout_code_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_code_error_text.setText("Введен неправильный код");
                    email_layout_code_bg.setImageResource(R.drawable.auth_bg_error);
                }
            } else if(regStatus == 5) {
                if(!email_layout_pass_input.getText().toString().isEmpty()) {
                    MainActivity.getMainActivity().loadingFragment.show();
                    mAuth.signInWithEmailAndPassword(email_layout_email_input.getText().toString(), email_layout_pass_input.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        MainActivity.getMainActivity().loadingFragment.hide();
                                        mHandler.removeCallbacksAndMessages(null);
                                        Point point = new Point();
                                        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
                                        viewGroup.clearAnimation();
                                        viewGroup.setAlpha(1.0f);
                                        viewGroup.setVisibility(View.VISIBLE);
                                        viewGroup.animate().alpha(0.0f).setDuration(300L);
                                        mHandler.postDelayed(new ssetVisibility(), 300L);
                                        btn_close.clearAnimation();
                                        btn_close.setTranslationY(0.0f);
                                        btn_close.animate().setDuration(300L).translationY(-point.y).start();
                                        btn_faq.clearAnimation();
                                        btn_faq.setTranslationY(0.0f);
                                        btn_faq.animate().setDuration(300L).translationY(-point.y).start();
                                        auth_bottom.clearAnimation();
                                        auth_bottom.setTranslationY(0.0f);
                                        auth_bottom.animate().setDuration(300L).translationY(point.y).start();
                                        auth_layout_main.clearAnimation();
                                        auth_layout_main.setAlpha(1.0f);
                                        auth_layout_main.setVisibility(View.VISIBLE);
                                        auth_layout_main.setTranslationX(0.0f);
                                        auth_layout_main.animate().alpha(0.0f).translationX(point.y).setDuration(300L);
                                        auth_layout_email.clearAnimation();
                                        auth_layout_email.setAlpha(0.0f);
                                        auth_layout_email.setVisibility(View.GONE);
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if(currentUser != null) {
                                            MainActivity.isAuth = true;
                                        } else {
                                            MainActivity.isAuth = false;
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("Servers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("server-id").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Integer paramInt = snapshot.getValue(Integer.class);
                                                server_id = paramInt;
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        ReturnToZero();
                                        regStatus = 1;
                                    }
                                }
                            });
                } else {
                    email_layout_pass_error.setVisibility(View.VISIBLE);
                    email_layout_pass_error.clearAnimation();
                    email_layout_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_pass_error_text.setText("Введите пароль");
                    email_layout_pass_bg.setImageResource(R.drawable.auth_bg_error);
                }
            } else if(regStatus == 6) {
                MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_alert, "В данный момент эта функция находится в разработке", "Понятно", null, new closeRegAuth(true), null);
                /*String textEmail = email_layout_email_input.getText().toString();
                int index = textEmail.indexOf("@");
                if (index != -1) {
                    if (!email_layout_email_input.getText().toString().isEmpty()) {
                        MainActivity.getMainActivity().loadingFragment.show();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://edgars.site/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Interface sInterface = retrofit.create(Interface.class);
                        String mail = email_layout_email_input.getText().toString();

                        Call<String> call = sInterface.authMail(Lists.verifyAuthUrl, mail);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                if (response.body() != null && response.isSuccessful()) {
                                    String result = response.body();
                                    codeMail = result;
                                    MainActivity.getMainActivity().loadingFragment.hide();
                                    InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
                                    email_layout_code.clearAnimation();
                                    email_layout_code.setVisibility(View.VISIBLE);
                                    email_layout_email_input.setEnabled(false);
                                    email_layout_email_input.setFocusable(false);
                                    email_layout_email_input.setFocusableInTouchMode(false);
                                    email_layout_email_input.setTextColor(872415231);

                                    email_layout_email_image.animate().alpha(0.2f).setDuration(150L).start();
                                    regStatus = 7;
                                    mHandler.postDelayed(new setTextRegistr("Восстановление", "пароля", "На Ваш почтовый адрес отправлен код, который нужно ввести в поле ниже. (Письмо может быть в спаме.)"), 150L);
                                } else {
                                    MainActivity.getMainActivity().loadingFragment.hide();
                                    Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки кода", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        email_layout_email_error.setVisibility(View.VISIBLE);
                        email_layout_email_error.clearAnimation();
                        email_layout_email_error.animate().translationY(0.0f).setDuration(300L).start();
                        email_layout_email_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                } else {
                    email_layout_email_error.setVisibility(View.VISIBLE);
                    email_layout_email_error.clearAnimation();
                    email_layout_email_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_email_bg.setImageResource(R.drawable.auth_bg_error);
                }*/
            } else if(regStatus == 7) {
                if(!email_layout_code_input.getText().toString().isEmpty()) {
                    if(codeMail.toString().equals(email_layout_code_input.getText().toString())) {
                        email_layout_pass.clearAnimation();
                        email_layout_pass.setVisibility(View.VISIBLE);
                        email_layout_repeat_pass.clearAnimation();
                        email_layout_repeat_pass.setVisibility(View.VISIBLE);
                        email_layout_pass_recover.setVisibility(View.GONE);
                        email_layout_code.clearAnimation();
                        email_layout_code.setVisibility(View.GONE);
                        regStatus = 8;
                        mHandler.postDelayed(new setTextRegistr("Восстановление", "пароля", "Задайте новый пароль своему аккаунту."), 150L);
                    } else {
                        email_layout_code_error.setVisibility(View.VISIBLE);
                        email_layout_code_error.clearAnimation();
                        email_layout_code_error.animate().translationY(0.0f).setDuration(300L).start();
                        email_layout_code_error_text.setText("Введен неправильный код");
                        email_layout_code_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                } else {
                    email_layout_code_error.setVisibility(View.VISIBLE);
                    email_layout_code_error.clearAnimation();
                    email_layout_code_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_code_error_text.setText("Введен неправильный код");
                    email_layout_code_bg.setImageResource(R.drawable.auth_bg_error);
                }
            } else if(regStatus == 8) {
                if (!email_layout_pass_input.getText().toString().isEmpty()) {
                    if (email_layout_pass_input.getText().toString().length() >= 6) {
                        if (!email_layout_repeat_pass_input.getText().toString().isEmpty()) {
                            if (email_layout_pass_input.getText().toString().equals(email_layout_repeat_pass_input.getText().toString())) {
                               mAuth.sendPasswordResetEmail(email_layout_email_input.getText().toString())
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   Toast.makeText(MainActivity.getMainActivity(), "Password Reset Email Sent!", Toast.LENGTH_LONG).show();
                                               }
                                               else {
                                                   Toast.makeText(MainActivity.getMainActivity(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       });
                            } else {
                                email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
                                email_layout_repeat_pass_error.clearAnimation();
                                email_layout_repeat_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                                email_layout_repeat_pass_error_text.setText("Пароли не совпадают");
                                email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_error);
                            }
                        } else {
                            email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
                            email_layout_repeat_pass_error.clearAnimation();
                            email_layout_repeat_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                            email_layout_repeat_pass_error_text.setText("Повторите пароль");
                            email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_error);
                        }
                    } else {
                        email_layout_pass_error.setVisibility(View.VISIBLE);
                        email_layout_pass_error.clearAnimation();
                        email_layout_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                        email_layout_pass_error_text.setText("Пароль слишком короткий");
                        email_layout_pass_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                } else {
                    email_layout_pass_error.setVisibility(View.VISIBLE);
                    email_layout_pass_error.clearAnimation();
                    email_layout_pass_error.animate().translationY(0.0f).setDuration(300L).start();
                    email_layout_pass_error_text.setText("Введите пароль");
                    email_layout_pass_bg.setImageResource(R.drawable.auth_bg_error);
                }
            }
        });

        email_layout_email_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                email_layout_email_error.setVisibility(View.VISIBLE);
                email_layout_email_error.clearAnimation();
                email_layout_email_error.animate().translationY(point1.y).setDuration(300L).start();
                email_layout_email_bg.setImageResource(R.drawable.auth_bg_email);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        email_layout_pass_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                email_layout_pass_error.setVisibility(View.VISIBLE);
                email_layout_pass_error.clearAnimation();
                email_layout_pass_error.animate().translationY(point1.y).setDuration(300L).start();
                email_layout_pass_bg.setImageResource(R.drawable.auth_bg_email);
                email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
                email_layout_repeat_pass_error.clearAnimation();
                email_layout_repeat_pass_error.animate().translationY(point1.y).setDuration(300L).start();
                email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_email);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        email_layout_repeat_pass_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
                email_layout_repeat_pass_error.clearAnimation();
                email_layout_repeat_pass_error.animate().translationY(point1.y).setDuration(300L).start();
                email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_email);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        email_layout_code_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                email_layout_code_error.setVisibility(View.VISIBLE);
                email_layout_code_error.clearAnimation();
                email_layout_code_error.animate().translationY(point1.y).setDuration(300L).start();
                email_layout_code_bg.setImageResource(R.drawable.auth_bg_email);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        main_btn_email.setVisibility(View.GONE);
        main_btn_vk.setVisibility(View.GONE);
        email_layout_email_input.setTextCursorDrawable(R.drawable.abc_text_cursor_material);
        email_layout_pass_input.setTextCursorDrawable(R.drawable.abc_text_cursor_material);
        policy_text.setText(Html.fromHtml("Продолжая, вы принимаете <a href='https://matrp.ru/offerta'>Договор-оферту</a> и подтверждаете, что прочли <a href='https://matrp.ru/privacy'>Политику конфиденциальности</a>."));
        policy_text.setMovementMethod(LinkMovementMethod.getInstance());
        policy_text.setLinkTextColor(-1);
        policy_text.setHighlightColor(0);
        f13290a0 = 0;
        auth_layout_main.setVisibility(View.GONE);
        auth_layout_email.setVisibility(View.GONE);
        X = 1;
        Y = 1;
        viewGroup.setVisibility(View.GONE);
    }

    public static class a implements OnCompleteListener<Void> {
        GoogleSignInClient googleSignInClient1;
        public a(GoogleSignInClient googleSignInClient) {
            googleSignInClient1 = googleSignInClient;
        }

        @Override // com.google.android.gms.tasks.OnCompleteListener
        public final void onComplete(@NonNull Task<Void> task) {
            MainActivity.getMainActivity().onClickAuthGoogle();
        }
    }

    public class Secynds implements Runnable {
        public Secynds() {
        }

        @Override
        public final void run() {
            if (secs > 0) {
                SpannableString spannableString = new SpannableString("Не пришёл код? " + secs + " секунд");
                spannableString.setSpan(new ForegroundColorSpan(-5592406), 15, spannableString.length(), 33);
                email_layout_code_send.setText(spannableString);
                secs = secs + (-1);
                mHandler.postDelayed(this, 1000L);
            } else {
                SpannableString spannableString2 = new SpannableString("Не пришёл код? Отправить повторно");
                spannableString2.setSpan(new UnderlineSpan(), 15, spannableString2.length(), 33);
                email_layout_code_send.setText(spannableString2);
                isSendCode = false;
                secs = 60;
            }

        }
    }

    public void ReturnToZero() {
        email_layout_email_error.setVisibility(View.VISIBLE);
        email_layout_email_error.clearAnimation();
        email_layout_email_error.animate().translationY(point1.y).setDuration(300L).start();
        email_layout_email_bg.setImageResource(R.drawable.auth_bg_email);

        email_layout_pass_error.setVisibility(View.VISIBLE);
        email_layout_pass_error.clearAnimation();
        email_layout_pass_error.animate().translationY(point1.y).setDuration(300L).start();
        email_layout_pass_bg.setImageResource(R.drawable.auth_bg_email);

        email_layout_repeat_pass_error.setVisibility(View.VISIBLE);
        email_layout_repeat_pass_error.clearAnimation();
        email_layout_repeat_pass_error.animate().translationY(point1.y).setDuration(300L).start();
        email_layout_repeat_pass_bg.setImageResource(R.drawable.auth_bg_email);

        email_layout_code_error.setVisibility(View.VISIBLE);
        email_layout_code_error.clearAnimation();
        email_layout_code_error.animate().translationY(point1.y).setDuration(300L).start();
        email_layout_code_bg.setImageResource(R.drawable.auth_bg_email);

        email_layout_email_input.setEnabled(true);
        email_layout_email_input.setFocusable(true);
        email_layout_email_input.setFocusableInTouchMode(true);
        email_layout_email_input.setTextColor(-1);
        email_layout_email_input.setText("");

        email_layout_pass_input.setEnabled(true);
        email_layout_pass_input.setFocusable(true);
        email_layout_pass_input.setFocusableInTouchMode(true);
        email_layout_pass_input.setTextColor(-1);
        email_layout_pass_input.setText("");

        email_layout_repeat_pass_input.setEnabled(true);
        email_layout_repeat_pass_input.setFocusable(true);
        email_layout_repeat_pass_input.setFocusableInTouchMode(true);
        email_layout_repeat_pass_input.setTextColor(-1);
        email_layout_repeat_pass_input.setText("");

        email_layout_code_input.setEnabled(true);
        email_layout_code_input.setFocusable(true);
        email_layout_code_input.setFocusableInTouchMode(true);
        email_layout_code_input.setTextColor(-1);
        email_layout_code_input.setText("");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            MainActivity.isAuth = true;
        } else {
            MainActivity.isAuth = false;
        }

        email_layout_email_image.animate().alpha(1.0f).setDuration(150L).start();

        email_layout_pass_image.animate().alpha(1.0f).setDuration(150L).start();

        email_layout_repeat_pass_image.animate().alpha(1.0f).setDuration(150L).start();

        InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

        secs = 60;

        regStatus = 1;
    }

    public void show(){
        mHandler.removeCallbacksAndMessages(null);
        Point point = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L);
        btn_close.clearAnimation();
        btn_close.setTranslationY(-point.y);
        btn_close.animate().setDuration(300L).translationY(0.0f).start();
        btn_faq.clearAnimation();
        btn_faq.setTranslationY(-point.y);
        btn_faq.animate().setDuration(300L).translationY(0.0f).start();
        auth_bottom.clearAnimation();
        auth_bottom.setTranslationY(point.y);
        auth_bottom.animate().setDuration(300L).translationY(0.0f).start();
        auth_layout_main.clearAnimation();
        auth_layout_main.setAlpha(0.0f);
        auth_layout_main.setVisibility(View.VISIBLE);
        auth_layout_main.setTranslationX(-point.y);
        auth_layout_main.animate().alpha(1.0f).translationX(0.0f).setDuration(300L);
    }
    public void hide()
    {
        mHandler.removeCallbacksAndMessages(null);
        Point point = new Point();
        MainActivity.getMainActivity().getWindowManager().getDefaultDisplay().getSize(point);
        if(regStatus == 1) {
            viewGroup.clearAnimation();
            viewGroup.setAlpha(1.0f);
            viewGroup.setVisibility(View.VISIBLE);
            viewGroup.animate().alpha(0.0f).setDuration(300L);
            mHandler.postDelayed(new ssetVisibility(), 300L);
            btn_close.clearAnimation();
            btn_close.setTranslationY(0.0f);
            btn_close.animate().setDuration(300L).translationY(-point.y).start();
            btn_faq.clearAnimation();
            btn_faq.setTranslationY(0.0f);
            btn_faq.animate().setDuration(300L).translationY(-point.y).start();
            auth_bottom.clearAnimation();
            auth_bottom.setTranslationY(0.0f);
            auth_bottom.animate().setDuration(300L).translationY(point.y).start();
            auth_layout_main.clearAnimation();
            auth_layout_main.setAlpha(1.0f);
            auth_layout_main.setVisibility(View.VISIBLE);
            auth_layout_main.setTranslationX(0.0f);
            auth_layout_main.animate().alpha(0.0f).translationX(-point.y).setDuration(300L);
        } else if(regStatus == 2) {
            auth_layout_main.clearAnimation();
            auth_layout_main.setAlpha(0.0f);
            auth_layout_main.setVisibility(View.VISIBLE);
            auth_layout_main.setTranslationX(-auth_layout_main.getWidth());
            auth_layout_main.animate().alpha(1.0f).translationX(0.0f).setDuration(300L);
            auth_layout_email.clearAnimation();
            auth_layout_email.setAlpha(1.0f);
            auth_layout_email.setVisibility(View.VISIBLE);
            auth_layout_email.setTranslationX(0.0f);
            auth_layout_email.animate().alpha(0.0f).translationX(auth_layout_email.getWidth()).setDuration(300L);
            policy_text.setText(Html.fromHtml("Продолжая, вы принимаете <a href='https://matrp.ru/offerta'>Договор-оферту</a> и подтверждаете, что прочли <a href='https://matrp.ru/privacy'>Политику конфиденциальности</a>."));
            policy_text.setMovementMethod(LinkMovementMethod.getInstance());
            policy_text.setLinkTextColor(-1);
            policy_text.setHighlightColor(0);
            email_layout_email_error.setVisibility(View.VISIBLE);
            email_layout_email_error.clearAnimation();
            email_layout_email_error.animate().translationY(point1.y).setDuration(300L).start();
            email_layout_email_bg.setImageResource(R.drawable.auth_bg_email);
            InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
            regStatus = 1;
        } else if(regStatus == 3 || regStatus == 4) {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Вы хотите отменить регистрацию?", "Да", "Нет", new closeRegAuth(true), new DialogFragment.closeDialog());
        } else if(regStatus == 5) {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Вы хотите отменить авторизацию?", "Да", "Нет", new closeRegAuth(true), new DialogFragment.closeDialog());
        } else if(regStatus == 6 || regStatus == 7 || regStatus == 8) {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Вы хотите отменить восстановление пароля?", "Да", "Нет", new closeRegAuth(true), new DialogFragment.closeDialog());
        }
    }

    public class closeRegAuth implements View.OnClickListener {

        boolean isRegg = false;

        public closeRegAuth(boolean isReg) {
            isRegg = isReg;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            if(isRegg) {
                auth_layout_main.clearAnimation();
                auth_layout_main.setAlpha(0.0f);
                auth_layout_main.setVisibility(View.VISIBLE);
                auth_layout_main.setTranslationX(-auth_layout_main.getWidth());
                auth_layout_main.animate().alpha(1.0f).translationX(0.0f).setDuration(300L);
                auth_layout_email.clearAnimation();
                auth_layout_email.setAlpha(1.0f);
                auth_layout_email.setVisibility(View.VISIBLE);
                auth_layout_email.setTranslationX(0.0f);
                auth_layout_email.animate().alpha(0.0f).translationX(auth_layout_email.getWidth()).setDuration(300L);
                MainActivity.getMainActivity().dialogFragment.hide();
                ReturnToZero();
                regStatus = 1;
            }
        }
    }

    public static class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            MainActivity.getMainActivity().authFragment.viewGroup.setVisibility(View.GONE);
        }
    }

    public class setTextRegistr implements Runnable {
        String str10, str11, str12;

        public setTextRegistr(String str1, String str2, String str3) {
            str10 = str1;
            str11 = str2;
            str12 = str3;
        }

        @Override
        public void run() {
            email_caption_1.setText(str10);
            email_caption_2.setText(str11);
            email_info.setText(str12);

            email_caption_1.clearAnimation();
            email_caption_1.setAlpha(0.0f);
            email_caption_1.setVisibility(View.VISIBLE);
            email_caption_1.setTranslationX(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._12sdp));
            email_caption_1.animate().alpha(1.0f).translationX(0.0f).setDuration(150L);

            email_caption_2.clearAnimation();
            email_caption_2.setAlpha(0.0f);
            email_caption_2.setVisibility(View.VISIBLE);
            email_caption_2.setTranslationX(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._12sdp));
            email_caption_2.animate().alpha(1.0f).translationX(0.0f).setDuration(150L);

            email_info.clearAnimation();
            email_info.setAlpha(0.0f);
            email_info.setVisibility(View.VISIBLE);
            email_info.setTranslationX(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._12sdp));
            email_info.animate().alpha(1.0f).translationX(0.0f).setDuration(150L);
        }
    }

}
/*
by EDGAR 3.0
EEE.EDGAR 3.0
*/
