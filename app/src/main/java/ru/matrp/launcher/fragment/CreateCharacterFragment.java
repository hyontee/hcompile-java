package ru.matrp.launcher.fragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.other.Interface;
import ru.matrp.launcher.other.Lists;
import ru.matrp.bonus.R;

public class CreateCharacterFragment extends MainActivity {

    public static final int[] maleSkins = {98, 97, 35, 71, 113};

    public static final int[] femaleSkins = {40, 63};

    public int vibran_male = 0;

    public int vibran_female = 0;

    public Point point1 = new Point();
    public String nickName;
    public int male = 0;
    public int female = 0;
    public int createCharacter = 1;
    public EditText char_layout_pass_input;
    public ViewGroup char_layout_pass_error;
    public TextView char_layout_pass_error_text;
    public TextView char_layout_pass_recover;
    public ViewGroup char_layout_code;
    public ImageView char_layout_code_bg;
    public ImageView char_layout_code_image;
    public EditText char_layout_code_input;
    public ViewGroup char_layout_code_error;
    public TextView char_layout_code_error_text;
    public TextView char_layout_code_send;
    public ViewGroup char_layout_promo;
    public ImageView char_layout_promo_bg;
    public ImageView char_layout_promo_image;
    public EditText char_layout_promo_input;
    public ViewGroup char_layout_promo_error;
    public TextView char_layout_promo_error_text;
    public ViewGroup char_layout_sex;
    public ViewGroup char_layout_sex_male;
    public ImageView char_layout_sex_bg_male;
    public ViewGroup char_layout_sex_female;
    public ImageView char_layout_sex_bg_female;
    public ViewGroup char_layout_skin;
    public ImageView char_layout_skin_image;
    public ViewGroup char_layout_skin_left;
    public ViewGroup char_layout_skin_right;
    public ViewGroup char_layout_rules;
    public CheckBox char_layout_rules_checkbox;
    public ViewGroup char_layout_continue;
    public LinearLayout header_layout;
    public ImageView btn_close;
    public ImageView btn_faq;
    public LinearLayout character_layout;
    public TextView char_caption_1;
    public TextView char_caption_2;
    public ViewGroup char_layout_nickname;
    public ImageView char_layout_nickname_bg;
    public ImageView char_layout_nickname_bg2;
    public ImageView char_layout_nickname_image;
    public EditText char_layout_nickname_input;
    public EditText char_layout_nickname_input2;
    public ViewGroup char_layout_nickname_error;
    public TextView char_layout_nickname_error_text;
    public ViewGroup char_layout_pass;
    public ImageView char_layout_pass_bg;
    public ImageView char_layout_pass_image;

    public CreateCharacterFragment() {
        super();
        createCharacterInit();
    }

    public void createCharacterInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_create_character, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        point1 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        header_layout = (LinearLayout) viewGroup.findViewById(R.id.header_layout);
        btn_close = (ImageView) viewGroup.findViewById(R.id.btn_close);
        btn_faq = (ImageView) viewGroup.findViewById(R.id.btn_faq);
        character_layout = (LinearLayout) viewGroup.findViewById(R.id.character_layout);
        char_caption_1 = (TextView) viewGroup.findViewById(R.id.char_caption_1);
        char_caption_2 = (TextView) viewGroup.findViewById(R.id.char_caption_2);
        char_layout_nickname = (ViewGroup) viewGroup.findViewById(R.id.char_layout_nickname);
        char_layout_nickname_bg = (ImageView) viewGroup.findViewById(R.id.char_layout_nickname_bg);
        char_layout_nickname_bg2 = (ImageView) viewGroup.findViewById(R.id.char_layout_nickname_bg2);
        char_layout_nickname_image = (ImageView) viewGroup.findViewById(R.id.char_layout_nickname_image);
        char_layout_nickname_input = (EditText) viewGroup.findViewById(R.id.char_layout_nickname_input);
        char_layout_nickname_input2 = (EditText) viewGroup.findViewById(R.id.char_layout_nickname_input2);
        char_layout_nickname_error = (ViewGroup) viewGroup.findViewById(R.id.char_layout_nickname_error);
        char_layout_nickname_error_text = (TextView) viewGroup.findViewById(R.id.char_layout_nickname_error_text);
        char_layout_pass = (ViewGroup) viewGroup.findViewById(R.id.char_layout_pass);
        char_layout_pass_bg = (ImageView) viewGroup.findViewById(R.id.char_layout_pass_bg);
        char_layout_pass_image = (ImageView) viewGroup.findViewById(R.id.char_layout_pass_image);
        char_layout_pass_input = (EditText) viewGroup.findViewById(R.id.char_layout_pass_input);
        char_layout_pass_error = (ViewGroup) viewGroup.findViewById(R.id.char_layout_pass_error);
        char_layout_pass_error_text = (TextView) viewGroup.findViewById(R.id.char_layout_pass_error_text);
        char_layout_pass_recover = (TextView) viewGroup.findViewById(R.id.char_layout_pass_recover);
        char_layout_code = (ViewGroup) viewGroup.findViewById(R.id.char_layout_code);
        char_layout_code_bg = (ImageView) viewGroup.findViewById(R.id.char_layout_code_bg);
        char_layout_code_image = (ImageView) viewGroup.findViewById(R.id.char_layout_code_image);
        char_layout_code_input = (EditText) viewGroup.findViewById(R.id.char_layout_code_input);
        char_layout_code_error = (ViewGroup) viewGroup.findViewById(R.id.char_layout_code_error);
        char_layout_code_error_text = (TextView) viewGroup.findViewById(R.id.char_layout_code_error_text);
        char_layout_code_send = (TextView) viewGroup.findViewById(R.id.char_layout_code_send);
        char_layout_promo = (ViewGroup) viewGroup.findViewById(R.id.char_layout_promo);
        char_layout_promo_bg = (ImageView) viewGroup.findViewById(R.id.char_layout_promo_bg);
        char_layout_promo_image = (ImageView) viewGroup.findViewById(R.id.char_layout_promo_image);
        char_layout_promo_input = (EditText) viewGroup.findViewById(R.id.char_layout_promo_input);
        char_layout_promo_error = (ViewGroup) viewGroup.findViewById(R.id.char_layout_promo_error);
        char_layout_promo_error_text = (TextView) viewGroup.findViewById(R.id.char_layout_promo_error_text);
        char_layout_sex = (ViewGroup) viewGroup.findViewById(R.id.char_layout_sex);
        char_layout_sex_male = (ViewGroup) viewGroup.findViewById(R.id.char_layout_sex_male);
        char_layout_sex_bg_male = (ImageView) viewGroup.findViewById(R.id.char_layout_sex_bg_male);
        char_layout_sex_female = (ViewGroup) viewGroup.findViewById(R.id.char_layout_sex_female);
        char_layout_sex_bg_female = (ImageView) viewGroup.findViewById(R.id.char_layout_sex_bg_female);
        char_layout_skin = (ViewGroup) viewGroup.findViewById(R.id.char_layout_skin);
        char_layout_skin_image = (ImageView) viewGroup.findViewById(R.id.char_layout_skin_image);
        char_layout_skin_left = (ViewGroup) viewGroup.findViewById(R.id.char_layout_skin_left);
        char_layout_skin_right = (ViewGroup) viewGroup.findViewById(R.id.char_layout_skin_right);
        char_layout_rules = (ViewGroup) viewGroup.findViewById(R.id.char_layout_rules);
        char_layout_rules_checkbox = (CheckBox) viewGroup.findViewById(R.id.char_layout_rules_checkbox);
        char_layout_continue = (ViewGroup) viewGroup.findViewById(R.id.char_layout_continue);
        btn_close.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_close));
        btn_close.setOnClickListener(view -> {
            MainActivity.getMainActivity().dialogFragment.show(R.drawable.ic_launcher_question, "Вы хотите отменить создание персонажа?", "Да", "Нет", new closeCreateCharater(), new DialogFragment.closeDialog());
        });
        btn_faq.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_faq));
        btn_faq.setOnClickListener(view -> {
            MainActivity.getMainActivity().faqFragment.show();
        });
        char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));
        char_layout_continue.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
            if(createCharacter == 1) {
                if (char_layout_nickname_input.length() <= 0 && char_layout_nickname_input2.length() <= 0) {
                    char_layout_nickname_error.setVisibility(View.VISIBLE);
                    char_layout_nickname_error.clearAnimation();
                    char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                    char_layout_nickname_error_text.setText("Введите имя и фамилию!");
                    char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_error);
                    char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_error);
                } else {
                    if (!char_layout_nickname_input.getText().toString().isEmpty()) {
                        if (!char_layout_nickname_input2.getText().toString().isEmpty()) {
                            if (char_layout_nickname_input.getText().toString().matches("^[a-zA-Z]*$") && char_layout_nickname_input2.getText().toString().matches("^[a-zA-Z]*$")) {
                                nickName = char_layout_nickname_input.getText().toString() + "_" + char_layout_nickname_input2.getText().toString();
                                if (nickName.length() >= 4) {
                                    if (nickName.length() >= 19) {
                                        char_layout_nickname_error.setVisibility(View.VISIBLE);
                                        char_layout_nickname_error.clearAnimation();
                                        char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                                        char_layout_nickname_error_text.setText("Слишком длинные имя и фамилия!");
                                        char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_error);
                                        char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_error);
                                    } else {
                                        MainActivity.getMainActivity().loadingFragment.show();
                                        char_layout_nickname_input.setText(char_layout_nickname_input.getText().toString().substring(0, 1).toUpperCase() + char_layout_nickname_input.getText().toString().substring(1));
                                        char_layout_nickname_input2.setText(char_layout_nickname_input2.getText().toString().substring(0, 1).toUpperCase() + char_layout_nickname_input2.getText().toString().substring(1));
                                        char_layout_nickname_input.setSelection(char_layout_nickname_input.length());
                                        char_layout_nickname_input2.setSelection(char_layout_nickname_input2.length());

                                        nickName = char_layout_nickname_input.getText().toString() + "_" + char_layout_nickname_input2.getText().toString();
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl("http://api-free.edgars.site/")
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();

                                        Interface sInterface = retrofit.create(Interface.class);

                                        Call<String> call = sInterface.getIsAcc(Lists.isAccUrl, nickName);

                                        call.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {

                                                if (response.body() != null && response.isSuccessful()) {
                                                    if (response.body().length() == 2) {
                                                        System.out.println("ne zanet");
                                                        MainActivity.getMainActivity().loadingFragment.hide();

                                                        char_caption_1.clearAnimation();
                                                        char_caption_1.setTranslationX(0.0f);
                                                        char_caption_1.setAlpha(1.0f);
                                                        char_caption_1.animate().alpha(0.0f).translationX(-char_caption_1.getWidth()).setDuration(150L).withEndAction(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                char_caption_1.setText("Никнейм свободен,");
                                                                char_caption_1.clearAnimation();
                                                                char_caption_1.setTranslationX(char_caption_1.getWidth());
                                                                char_caption_1.setAlpha(0.0f);
                                                                char_caption_1.animate().alpha(1.0f).translationX(0.0f).setDuration(150L).start();
                                                            }
                                                        }).start();

                                                        char_caption_2.clearAnimation();
                                                        char_caption_2.setTranslationX(0.0f);
                                                        char_caption_2.setAlpha(1.0f);
                                                        char_caption_2.animate().alpha(0.0f).translationX(-char_caption_2.getWidth()).setDuration(150L).withEndAction(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                char_caption_2.setText("заполните все поля");
                                                                char_caption_2.clearAnimation();
                                                                char_caption_2.setTranslationX(char_caption_2.getWidth());
                                                                char_caption_2.setAlpha(0.0f);
                                                                char_caption_2.animate().alpha(1.0f).translationX(0.0f).setDuration(150L).start();
                                                            }
                                                        }).start();

                                                        Animka(char_layout_nickname);
                                                        Animka(char_layout_promo);
                                                        Animka(char_layout_sex);
                                                        Animka(char_layout_skin);
                                                        Animka(char_layout_rules);

                                                        char_layout_nickname_input.setEnabled(false);
                                                        char_layout_nickname_input.setFocusable(false);
                                                        char_layout_nickname_input.setFocusableInTouchMode(false);
                                                        char_layout_nickname_input.setTextColor(872415231);

                                                        char_layout_nickname_input2.setEnabled(false);
                                                        char_layout_nickname_input2.setFocusable(false);
                                                        char_layout_nickname_input2.setFocusableInTouchMode(false);
                                                        char_layout_nickname_input2.setTextColor(872415231);

                                                        char_layout_nickname_image.animate().alpha(0.2f).setDuration(150L).start();

                                                        char_layout_continue.animate().alpha(0.5f).setDuration(300L).start();

                                                        char_layout_continue.setOnTouchListener(null);

                                                        createCharacter = 2;
                                                    } else {
                                                        System.out.println("zanet");
                                                        char_layout_nickname_error.setVisibility(View.VISIBLE);
                                                        char_layout_nickname_error.clearAnimation();
                                                        char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                                                        char_layout_nickname_error_text.setText("Имя и фамилия заняты введите другие!");
                                                        char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_error);
                                                        char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_error);
                                                    }
                                                    MainActivity.getMainActivity().loadingFragment.hide();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки данных", Toast.LENGTH_SHORT).show();
                                                MainActivity.getMainActivity().loadingFragment.hide();
                                            }
                                        });
                                    }
                                } else {
                                    char_layout_nickname_error.setVisibility(View.VISIBLE);
                                    char_layout_nickname_error.clearAnimation();
                                    char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                                    char_layout_nickname_error_text.setText("Слишком короткие имя и фамилия!");
                                    char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_error);
                                    char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_error);
                                }
                            } else {
                                char_layout_nickname_error.setVisibility(View.VISIBLE);
                                char_layout_nickname_error.clearAnimation();
                                char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                                char_layout_nickname_error_text.setText("Используйте английские символы!");
                                char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_error);
                                char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_error);
                            }
                        } else {
                            char_layout_nickname_error.setVisibility(View.VISIBLE);
                            char_layout_nickname_error.clearAnimation();
                            char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                            char_layout_nickname_error_text.setText("Введите фамилию!");
                            char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_error);
                        }
                    } else {
                        char_layout_nickname_error.setVisibility(View.VISIBLE);
                        char_layout_nickname_error.clearAnimation();
                        char_layout_nickname_error.animate().translationY(0.0f).setDuration(300L).start();
                        char_layout_nickname_error_text.setText("Введите имя!");
                        char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_error);
                    }
                }
            } else if (createCharacter == 2) {
                if (male != 0 || female != 0) {
                    if (char_layout_rules_checkbox.isChecked()) {
                        MainActivity.getMainActivity().loadingFragment.show();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://api-free.edgars.site/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Interface sInterface = retrofit.create(Interface.class);

                        String sex; //0 female / 1 male

                        if (male == 0) {
                            sex = "0";
                        } else {
                            sex = "1";
                        }

                        String skin;

                        if (sex == "0") {
                            skin = String.valueOf(femaleSkins[vibran_female]);
                        } else {
                            skin = String.valueOf(maleSkins[vibran_male]);
                        }

                        String promo;

                        if (char_layout_promo_input.getText().toString().length() <= 0) {
                            promo = null;
                        } else {
                            promo = char_layout_promo_input.getText().toString();
                        }

                        Call<String> call = sInterface.createCharacter(Lists.createCharacterUrl, nickName, sex, skin, promo);

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                if (response.body() != null && response.isSuccessful()) {
                                    if (response.body().length() == 1) {
                                        HashMap<String, String> serversInfo = new HashMap<>();
                                        serversInfo.put("nick", nickName);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child("User-servers").child("Server_" + MainActivity.server_id).child(FirebaseAuth.getInstance().getUid()).setValue(serversInfo);
                                        MainActivity.getMainActivity().mainFragment.show();
                                        ReturnToZero();
                                        hide();
                                    }
                                    System.out.println(response.body());
                                }
                                MainActivity.getMainActivity().loadingFragment.hide();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(MainActivity.getMainActivity(), "Ошибка при отправки данных", Toast.LENGTH_SHORT).show();
                                MainActivity.getMainActivity().loadingFragment.hide();
                            }
                        });
                    } else {
                        char_layout_rules_checkbox.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                            @Override
                            public void run() {
                                char_layout_rules_checkbox.setBackground(MainActivity.getMainActivity().getResources().getDrawable(R.drawable.auth_bg_error));
                                char_layout_rules_checkbox.animate().alpha(1.0f).setDuration(300L).withEndAction(new Runnable() {

                                    @Override
                                    public void run() {
                                        char_layout_rules_checkbox.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                                            @Override
                                            public void run() {
                                                char_layout_rules_checkbox.setBackground(null);
                                                char_layout_rules_checkbox.animate().alpha(1.0f).setDuration(300L).start();
                                            }
                                        }).start();
                                    }
                                }).start();
                            }
                        }).start();
                    }
                } else {
                    char_layout_sex_bg_male.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_error);
                            char_layout_sex_bg_male.animate().alpha(1.0f).setDuration(300L).withEndAction(new Runnable() {

                                @Override
                                public void run() {
                                    char_layout_sex_bg_male.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                                        @Override
                                        public void run() {
                                            char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_email);
                                            char_layout_sex_bg_male.animate().alpha(1.0f).setDuration(300L).start();
                                        }
                                    }).start();
                                }
                            }).start();
                        }
                    }).start();
                    char_layout_sex_bg_female.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_error);
                            char_layout_sex_bg_female.animate().alpha(1.0f).setDuration(300L).withEndAction(new Runnable() {

                                @Override
                                public void run() {
                                    char_layout_sex_bg_female.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                                        @Override
                                        public void run() {
                                            char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_email);
                                            char_layout_sex_bg_female.animate().alpha(1.0f).setDuration(300L).start();
                                        }
                                    }).start();
                                }
                            }).start();
                        }
                    }).start();
                    char_layout_skin_image.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            char_layout_skin_image.setImageResource(R.drawable.auth_bg_error);
                            char_layout_skin_image.animate().alpha(1.0f).setDuration(300L).withEndAction(new Runnable() {

                                @Override
                                public void run() {
                                    char_layout_skin_image.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                                        @Override
                                        public void run() {
                                            char_layout_skin_image.setImageResource(R.drawable.auth_bg_email);
                                            char_layout_skin_image.animate().alpha(1.0f).setDuration(300L).start();
                                        }
                                    }).start();
                                }
                            }).start();
                        }
                    }).start();

                    if (!char_layout_rules_checkbox.isChecked()) {
                        char_layout_rules_checkbox.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                            @Override
                            public void run() {
                                char_layout_rules_checkbox.setBackground(MainActivity.getMainActivity().getResources().getDrawable(R.drawable.auth_bg_error));
                                char_layout_rules_checkbox.animate().alpha(1.0f).setDuration(300L).withEndAction(new Runnable() {

                                    @Override
                                    public void run() {
                                        char_layout_rules_checkbox.animate().alpha(0.2f).setDuration(300L).withEndAction(new Runnable() {

                                            @Override
                                            public void run() {
                                                char_layout_rules_checkbox.setBackground(null);
                                                char_layout_rules_checkbox.animate().alpha(1.0f).setDuration(300L).start();
                                            }
                                        }).start();
                                    }
                                }).start();
                            }
                        }).start();
                    }
                }
            }
        });

        char_layout_nickname_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                char_layout_nickname_error.setVisibility(View.VISIBLE);
                char_layout_nickname_error.clearAnimation();
                char_layout_nickname_error.animate().translationY(point1.y).setDuration(300L).start();
                char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_email);
                char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_email);
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

        char_layout_nickname_input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                char_layout_nickname_error.setVisibility(View.VISIBLE);
                char_layout_nickname_error.clearAnimation();
                char_layout_nickname_error.animate().translationY(point1.y).setDuration(300L).start();
                char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_email);
                char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_email);
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

        char_layout_nickname.setVisibility(View.GONE);
        char_layout_pass.setVisibility(View.GONE);
        char_layout_code.setVisibility(View.GONE);
        char_layout_promo.setVisibility(View.GONE);
        char_layout_sex.setVisibility(View.GONE);
        char_layout_skin.setVisibility(View.GONE);
        char_layout_rules.setVisibility(View.GONE);
        char_layout_rules_checkbox.setText(Html.fromHtml("Согласен с <a href='https://forum.matrp.ru/index.php?threads/%D0%9E%D0%B1%D1%89%D0%B8%D0%B5-%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D0%BB%D0%B0-%D1%81%D0%B5%D1%80%D0%B2%D0%B5%D1%80%D0%BE%D0%B2.20/'>правилами сервера</a>"));
        char_layout_rules_checkbox.setMovementMethod(LinkMovementMethod.getInstance());
        char_layout_rules_checkbox.setLinkTextColor(-1);
        char_layout_rules_checkbox.setHighlightColor(0);
        char_layout_rules_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (male != 0 || female != 0) {
                    if (isChecked) {
                        char_layout_continue.animate().alpha(1.0f).setDuration(300L).start();
                        char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));
                    } else {
                        char_layout_continue.animate().alpha(0.5f).setDuration(300L).start();
                        char_layout_continue.setOnTouchListener(null);
                    }
                }
            }
        });
        SpannableString spannableString = new SpannableString("Забыли пароль?");
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 33);
        char_layout_pass_recover.setText(spannableString);
        SpannableString spannableString2 = new SpannableString("Не пришёл код? Отправить повторно");
        spannableString2.setSpan(new UnderlineSpan(), 15, spannableString2.length(), 33);
        char_layout_code_send.setText(spannableString2);
        char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_email);
        char_layout_sex_male.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_sex_male));
        char_layout_sex_male.setOnClickListener(view -> {
            if (male == 0 && female == 0) {
                if (char_layout_rules_checkbox.isChecked()) {
                    char_layout_continue.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));
                }
                char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_selected);
                male++;
                char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                        Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + maleSkins[vibran_male] + ".png?edgar=1829top09").into(char_layout_skin_image);
                    }
                }).start();
                if (vibran_male == 0) {
                    char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                } else if (vibran_male == maleSkins.length - 1) {
                    char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                } else {
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                }

            } else if (male == 0 && female == 1) {
                if (char_layout_rules_checkbox.isChecked()) {
                    char_layout_continue.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));
                }
                char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_selected);
                char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_email);
                female--;
                male++;
                char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                        Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + maleSkins[vibran_male] + ".png?edgar=1829top09").into(char_layout_skin_image);
                    }
                }).start();
                if (vibran_male == 0) {
                    char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                } else if (vibran_male == maleSkins.length - 1) {
                    char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                } else {
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                }

            } else {
                char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_email);
                char_layout_skin_image.animate().alpha(0.0f).setDuration(300L).start();
                char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                char_layout_skin_left.setOnTouchListener(null);
                char_layout_skin_right.setOnTouchListener(null);
                char_layout_continue.animate().alpha(0.5f).setDuration(300L).start();
                char_layout_continue.setOnTouchListener(null);
                male--;
            }
        });
        char_layout_sex_female.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_sex_female));
        char_layout_sex_female.setOnClickListener(view -> {
            if (female == 0 && male == 0) {
                if (char_layout_rules_checkbox.isChecked()) {
                    char_layout_continue.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));
                }
                char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_selected);
                female++;
                char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                        Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + femaleSkins[vibran_female] + ".png?edgar=1829top09").into(char_layout_skin_image);

                    }
                }).start();
                if (vibran_female == 0) {
                    char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                } else if (vibran_female == femaleSkins.length - 1) {
                    char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                } else {
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                }

            } else if (female == 0 && male == 1) {
                if (char_layout_rules_checkbox.isChecked()) {
                    char_layout_continue.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));
                }
                char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_selected);
                char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_email);
                female++;
                male--;
                char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                        Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + femaleSkins[vibran_female] + ".png?edgar=1829top09").into(char_layout_skin_image);
                    }
                }).start();
                if (vibran_female == 0) {
                    char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                } else if (vibran_female == femaleSkins.length - 1) {
                    char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                } else {
                    char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                    char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                    char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                    char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                }

            } else {
                char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_email);
                char_layout_skin_image.animate().alpha(0.0f).setDuration(300L).start();
                char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                char_layout_skin_left.setOnTouchListener(null);
                char_layout_skin_right.setOnTouchListener(null);
                char_layout_continue.animate().alpha(0.5f).setDuration(300L).start();
                char_layout_continue.setOnTouchListener(null);
                female--;
            }
        });
        char_layout_skin_left.setAlpha(0.2f);
        char_layout_skin_left.setOnTouchListener(null);
        char_layout_skin_left.setOnClickListener(view -> {
            if (male == 1) {
                if (vibran_male != 0) {
                     if (vibran_male == maleSkins.length - 1) {
                         char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                         char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                    }
                    vibran_male--;
                    char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                            Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + maleSkins[vibran_male] + ".png?edgar=1829top09").into(char_layout_skin_image);
                        }
                    }).start();
                    if (vibran_male == 0) {
                        char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                        char_layout_skin_left.setOnTouchListener(null);
                    }
                }
            } else if (female == 1) {
                if (vibran_female != 0) {
                    if (vibran_female == femaleSkins.length - 1) {
                        char_layout_skin_right.animate().alpha(1.0f).setDuration(300L).start();
                        char_layout_skin_right.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_right));
                    }
                    vibran_female--;
                    char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                            Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + femaleSkins[vibran_female] + ".png?edgar=1829top09").into(char_layout_skin_image);
                        }
                    }).start();
                    if (vibran_female == 0) {
                        char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
                        char_layout_skin_left.setOnTouchListener(null);
                    }
                }
            }
        });
        char_layout_skin_right.setAlpha(0.2f);
        char_layout_skin_right.setOnTouchListener(null);
        char_layout_skin_right.setOnClickListener(view -> {
            if (male == 1) {
                if (vibran_male != maleSkins.length - 1) {
                    if (vibran_male == 0) {
                        char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                        char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                    }
                    vibran_male++;
                    char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                            Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + maleSkins[vibran_male] + ".png?edgar=1829top09").into(char_layout_skin_image);
                        }
                    }).start();
                    if (vibran_male == maleSkins.length - 1) {
                        char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                        char_layout_skin_right.setOnTouchListener(null);
                    }
                }
            } else if (female == 1) {
                if (vibran_female != femaleSkins.length - 1) {
                    if (vibran_female == 0) {
                        char_layout_skin_left.animate().alpha(1.0f).setDuration(300L).start();
                        char_layout_skin_left.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_skin_left));
                    }
                    vibran_female++;
                    char_layout_skin_image.animate().alpha(0.0f).setDuration(150L).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            char_layout_skin_image.animate().alpha(1.0f).setDuration(150L).start();
                            Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + femaleSkins[vibran_female] + ".png?edgar=1829top09").into(char_layout_skin_image);
                        }
                    }).start();
                    if (vibran_female == femaleSkins.length - 1) {
                        char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
                        char_layout_skin_right.setOnTouchListener(null);
                    }
                }
            }
        });
        createCharacter = 1;
        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        header_layout.clearAnimation();
        header_layout.setTranslationY(-f10.y);
        header_layout.animate().setDuration(300L).translationY(0.0f).start();
        character_layout.clearAnimation();
        character_layout.setTranslationY(f10.y);
        character_layout.animate().setDuration(300L).translationY(0.0f).start();
        char_caption_1.setText("Введите имя и фамилию");
        char_caption_2.setText("персонажа");
        char_layout_nickname.setVisibility(View.VISIBLE);
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        header_layout.clearAnimation();
        header_layout.setTranslationY(0.0f);
        header_layout.animate().setDuration(300L).translationY(-f10.y).start();
        character_layout.clearAnimation();
        character_layout.setTranslationY(0.0f);
        character_layout.animate().setDuration(300L).translationY(f10.y).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
        mHandler.postDelayed(new ssetVisibility(), 300L);
    }

    public void ReturnToZero() {
        InputMethodManager imm = (InputMethodManager) MainActivity.getMainActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);

        char_layout_nickname.setVisibility(View.GONE);
        char_layout_pass.setVisibility(View.GONE);
        char_layout_code.setVisibility(View.GONE);
        char_layout_promo.setVisibility(View.GONE);
        char_layout_sex.setVisibility(View.GONE);
        char_layout_skin.setVisibility(View.GONE);
        char_layout_rules.setVisibility(View.GONE);
        char_layout_rules_checkbox.setText(Html.fromHtml("Согласен с <a href='https://forum.matrp.ru/index.php?threads/%D0%9E%D0%B1%D1%89%D0%B8%D0%B5-%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D0%BB%D0%B0-%D1%81%D0%B5%D1%80%D0%B2%D0%B5%D1%80%D0%BE%D0%B2.20/'>правилами сервера</a>"));
        char_layout_rules_checkbox.setMovementMethod(LinkMovementMethod.getInstance());
        char_layout_rules_checkbox.setLinkTextColor(-1);
        char_layout_rules_checkbox.setHighlightColor(0);
        char_layout_rules_checkbox.setChecked(false);

        char_layout_nickname_error.setVisibility(View.VISIBLE);
        char_layout_nickname_error.clearAnimation();
        char_layout_nickname_error.animate().translationY(point1.y).setDuration(300L).start();
        char_layout_nickname_bg.setImageResource(R.drawable.auth_bg_email);
        char_layout_nickname_bg2.setImageResource(R.drawable.auth_bg_email);

        char_layout_nickname_input.setEnabled(true);
        char_layout_nickname_input.setFocusable(true);
        char_layout_nickname_input.setFocusableInTouchMode(true);
        char_layout_nickname_input.setTextColor(-1);
        char_layout_nickname_input.setText("");
        char_layout_nickname_input.setSelection(char_layout_nickname_input.length());

        char_layout_nickname_input2.setEnabled(true);
        char_layout_nickname_input2.setFocusable(true);
        char_layout_nickname_input2.setFocusableInTouchMode(true);
        char_layout_nickname_input2.setTextColor(-1);
        char_layout_nickname_input2.setText("");
        char_layout_nickname_input2.setSelection(char_layout_nickname_input2.length());

        char_layout_promo_input.setText("");
        char_layout_promo_input.setSelection(char_layout_promo_input.length());

        char_layout_sex_bg_female.setImageResource(R.drawable.auth_bg_email);
        char_layout_skin_image.animate().alpha(0.0f).setDuration(300L).start();
        char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
        char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
        char_layout_skin_left.setOnTouchListener(null);
        char_layout_skin_right.setOnTouchListener(null);
        char_layout_continue.animate().alpha(0.5f).setDuration(300L).start();
        char_layout_continue.setOnTouchListener(null);
        vibran_female = 0;
        female = 0;
        char_layout_sex_bg_male.setImageResource(R.drawable.auth_bg_email);
        char_layout_skin_image.animate().alpha(0.0f).setDuration(300L).start();
        char_layout_skin_right.animate().alpha(0.2f).setDuration(300L).start();
        char_layout_skin_left.animate().alpha(0.2f).setDuration(300L).start();
        char_layout_skin_left.setOnTouchListener(null);
        char_layout_skin_right.setOnTouchListener(null);
        char_layout_continue.animate().alpha(0.5f).setDuration(300L).start();
        char_layout_continue.setOnTouchListener(null);
        vibran_male = 0;
        male = 0;

        char_layout_nickname_image.animate().alpha(1.0f).setDuration(150L).start();

        char_layout_continue.animate().alpha(1.0f).setDuration(300L).start();

        char_layout_continue.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), char_layout_continue));

        createCharacter = 1;
    }

    public class closeCreateCharater implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MainActivity.getMainActivity().dialogFragment.hide();
            MainActivity.getMainActivity().mainFragment.show();
            ReturnToZero();
            hide();
        }
    }

    public final class anim extends Animation {

        public final View f12986a;
        public final int f12987b = 0;
        public final int c;
        public final boolean f12988d;
        public final int f12989e;

        public anim(View view, int i10, int i11, boolean z6) {
            this.f12986a = view;
            this.c = i11;
            this.f12988d = z6;
            this.f12989e = i11 + 0 + i10 + 0;
            if (z6) {
                ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = i11;
                ((LinearLayout.LayoutParams) view.getLayoutParams()).height = i10;
            } else {
                ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = 0;
                ((LinearLayout.LayoutParams) view.getLayoutParams()).height = 0;
            }
            view.requestLayout();
        }

        @Override // android.view.animation.Animation
        public final void applyTransformation(float f10, Transformation transformation) {
            if (f10 == 1.0f) {
                ((LinearLayout.LayoutParams) this.f12986a.getLayoutParams()).topMargin = this.c;
                this.f12986a.getLayoutParams().height = -2;
                this.f12986a.requestLayout();
                this.f12986a.setVisibility(this.f12988d ? 8 : 0);
                return;
            }
            if (this.f12988d) {
                f10 = 1.0f - f10;
            }
            int i10 = (int) (this.f12989e * f10);
            int i11 = this.f12987b;
            int min = Math.min(this.c - i11, i10) + i11;
            ((LinearLayout.LayoutParams) this.f12986a.getLayoutParams()).topMargin = min;
            ((LinearLayout.LayoutParams) this.f12986a.getLayoutParams()).height = i10 - min;
            this.f12986a.requestLayout();
        }

        @Override // android.view.animation.Animation
        public final void initialize(int i10, int i11, int i12, int i13) {
            super.initialize(i10, i11, i12, i13);
        }

        @Override // android.view.animation.Animation
        public final boolean willChangeBounds() {
            return true;
        }
    }

    public final void Animka(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.getAnimation().cancel();
        }
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        boolean z6 = view instanceof TextView;
        if (z6) {
            view.measure(-2, -2);
        } else {
            view.measure(-1, -2);
        }
        anim aVar = new anim(view, view.getMeasuredHeight(), z6 ? 0 : MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._12sdp), false);
        aVar.setDuration(z6 ? 150L : 300L);
        aVar.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(aVar);
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
