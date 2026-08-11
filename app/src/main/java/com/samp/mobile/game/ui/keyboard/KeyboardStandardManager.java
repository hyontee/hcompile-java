package com.samp.mobile.game.ui.keyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.samp.mobile.R;
import com.samp.mobile.game.SAMP;

public class KeyboardStandardManager implements View.OnClickListener {
    SAMP root;
    AppCompatActivity activity;

    ConstraintLayout keyboardStandardView;

    LinearLayout keyboardCharsLineUp;
    LinearLayout keyboardCharsLineMiddle;
    LinearLayout keyboardCharsLineDown;

    ImageView ButtonSymbolShift;
    ImageView ButtonSymbolBackspace;
    TextView ButtonSymbolLang;
    TextView ButtonSymbolSpace;
    ImageView keyboardHistoryButtonUp;
    ImageView keyboardHistoryButtonDown;
    ImageView ButtonSymbolEnter;
    public FrameLayout visibleZone;

    public KeyboardInputTextView keyboardTextInput;

    public static final int KEYBOARD_LANG_ENG = 0;
    public static final int KEYBOARD_LANG_SPEC = 1;
    public static final int KEYBOARD_LANG_RU = 2;

    int selectedLang;

    boolean activeShift;
    boolean activePasswordSecurity;
    int useType = 0;

    Runnable callableEnter;
    Runnable callableClose;

    String keyboardsCharLang[][] = {
            {

            },
            {

            },
            {

            }
    };

    Button keyboardSpecSymbols[] = {
            null, null, null, null
    };

    final int TYPE_USE_KEYBOARD_CHAT = 0;
    final int TYPE_USE_KEYBOARD_DIALOG = 1;
    final int MAX_TYPES_OF_USE_KEYBOARD = 2;

    String HistoryTexts[] = new String[30];
    String LastTexts[] = new String[MAX_TYPES_OF_USE_KEYBOARD];
    int idSelectedHistoryTexts = -1;

    public KeyboardStandardManager(SAMP root) {
        this.root = root;
        this.activity = (AppCompatActivity) root;

        View inflatedViewkeyboardStandard = activity.getLayoutInflater().inflate(R.layout.wn_keyboard_standard, null, false);
        keyboardStandardView = (ConstraintLayout) inflatedViewkeyboardStandard.findViewById(R.id.keyboardStandard);
        ((SAMP) activity).getParentLayout().addView(inflatedViewkeyboardStandard, new ConstraintLayout.LayoutParams(-1, -1));
        keyboardCharsLineUp = (LinearLayout) keyboardStandardView.findViewById(R.id.contentButtonsListLineUp);
        keyboardCharsLineMiddle = (LinearLayout) keyboardStandardView.findViewById(R.id.contentButtonsListLineMiddle);
        keyboardCharsLineDown = (LinearLayout) keyboardStandardView.findViewById(R.id.contentButtonsListLineDown);

        ButtonSymbolShift = (ImageView) keyboardStandardView.findViewById(R.id.contentButtonSymbolShift);
        ButtonSymbolBackspace = (ImageView) keyboardStandardView.findViewById(R.id.contentButtonSymbolBackspace);
        ButtonSymbolSpace = (TextView) keyboardStandardView.findViewById(R.id.contentButtonSymbolSpace);
        ButtonSymbolLang = (TextView) keyboardStandardView.findViewById(R.id.contentButtonSymbolSpecLang);
        ButtonSymbolEnter = (ImageView) keyboardStandardView.findViewById(R.id.contentButtonSymbolEnter);
        visibleZone = (FrameLayout) keyboardStandardView.findViewById(R.id.visibleZone);

        keyboardTextInput = (KeyboardInputTextView) keyboardStandardView.findViewById(R.id.contentText);

        keyboardSpecSymbols[0] = (Button) keyboardStandardView.findViewById(R.id.contentButtonSymbolSpec1);
        keyboardSpecSymbols[1] = (Button) keyboardStandardView.findViewById(R.id.contentButtonSymbolSpec2);
        keyboardSpecSymbols[2] = (Button) keyboardStandardView.findViewById(R.id.contentButtonSymbolSpec3);
        keyboardSpecSymbols[3] = (Button) keyboardStandardView.findViewById(R.id.contentButtonSymbolSpec4);

        keyboardHistoryButtonUp = (ImageView) keyboardStandardView.findViewById(R.id.buttonHistoryUP);
        keyboardHistoryButtonDown = (ImageView) keyboardStandardView.findViewById(R.id.buttonHistoryDown);

        keyboardTextInput.setFocusable(true);
        keyboardTextInput.setFocusableInTouchMode(true);

        keyboardHistoryButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newSelectedIndexHistory = idSelectedHistoryTexts + 1;

                while (newSelectedIndexHistory < HistoryTexts.length) {
                    if (HistoryTexts[newSelectedIndexHistory].length() > 0) {
                        idSelectedHistoryTexts = newSelectedIndexHistory;
                        keyboardTextInput.setText(HistoryTexts[newSelectedIndexHistory]);
                        keyboardTextInput.setSelection(keyboardTextInput.getText().toString().length());
                        keyboardTextInput.requestFocus();
                        return;
                    }

                    newSelectedIndexHistory++;
                }
            }
        });
        keyboardHistoryButtonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newSelectedIndexHistory = idSelectedHistoryTexts - 1;

                if (newSelectedIndexHistory == -1) {
                    keyboardTextInput.setText("");
                    idSelectedHistoryTexts = -1;
                    return;
                }

                while (newSelectedIndexHistory >= 0) {
                    if (HistoryTexts[newSelectedIndexHistory].length() > 0) {
                        idSelectedHistoryTexts = newSelectedIndexHistory;
                        keyboardTextInput.setText(HistoryTexts[newSelectedIndexHistory]);
                        keyboardTextInput.setSelection(keyboardTextInput.getText().toString().length());
                        keyboardTextInput.requestFocus();
                        return;
                    }
                    newSelectedIndexHistory--;
                }

                keyboardTextInput.setText("");
                idSelectedHistoryTexts = -1;
            }
        });


        for (int i = 0; i < LastTexts.length; i++) {
            LastTexts[i] = "";
        }
        for (int i = 0; i < HistoryTexts.length; i++) {
            HistoryTexts[i] = "";
        }

        for (int i = 0; i < keyboardSpecSymbols.length; i++) {
            keyboardSpecSymbols[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String oldText = keyboardTextInput.getText().toString();
                    int selectionCursorStart = keyboardTextInput.getSelectionStart();
                    if(selectionCursorStart - keyboardTextInput.getSelectionEnd() != 0){
                        String startString = oldText.substring(0, selectionCursorStart);
                        String endString = oldText.substring(keyboardTextInput.getSelectionEnd(), oldText.length());
                        keyboardTextInput.setText(startString + ((TextView) v).getText() + endString);
                        keyboardTextInput.setSelection(selectionCursorStart+1);
                        return;
                    }

                    if (!keyboardTextInput.isFocused()) {
                        selectionCursorStart = keyboardTextInput.getText().toString().length();
                    }

                    String textPart1 = oldText.substring(0, selectionCursorStart);
                    String textPart2 = oldText.substring(selectionCursorStart);

                    keyboardTextInput.setText(textPart1 + ((TextView) v).getText() + textPart2);
                    keyboardTextInput.setSelection(selectionCursorStart + 1);
                }
            });
        }

        keyboardsCharLang[KEYBOARD_LANG_ENG] = new String[] {
                "qwertyuiop", "asdfghjkl", "zxcvbnm"
        };
        keyboardsCharLang[KEYBOARD_LANG_RU] = new String[] {
                "йцукенгшщзхъ", "фывапролджэ", "ячсмитьбю"
        };
        keyboardsCharLang[KEYBOARD_LANG_SPEC] = new String[] {
                "1234567890", "@#$%\"*()-_", ".:;+=<>[]"
        };

        ButtonSymbolShift.setOnClickListener(this);
        ButtonSymbolBackspace.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            keyboardTextInput.setShowSoftInputOnFocus(false);
        } else {
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(keyboardTextInput.getWindowToken(), 0);
        }

        keyboardTextInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                if (menu != null) {
                    menu.clear();
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        ButtonSymbolSpace.setOnClickListener(this);
        ButtonSymbolLang.setOnClickListener(this);
        ButtonSymbolEnter.setOnClickListener(this);
    }

    public void setCallableEnter(Runnable callable) {
        this.callableEnter = callable;
    }

    public void setCallableClose(Runnable callable) {
        this.callableClose = callable;
        if(callable != null){
            visibleZone.setOnTouchListener(null);
            visibleZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(KeyboardStandardManager.this.callableClose != null){
                        KeyboardStandardManager.this.callableClose.run();
                    }
                }
            });

            visibleZone.setClickable(true);
            visibleZone.setFocusable(false);
        }else{
            visibleZone.setOnClickListener(null);
            visibleZone.setOnTouchListener(null);
            visibleZone.setClickable(false);
            visibleZone.setFocusable(false);
        }
    }

    public void setVisible(int active, int type, boolean isPassword) {
        activePasswordSecurity = isPassword;

        useType = type;

        if(isPassword) {
            keyboardTextInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            keyboardTextInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        }

        if (active == 1) {
            idSelectedHistoryTexts = -1;

            keyboardTextInput.setText(LastTexts[type]);
            keyboardStandardView.setVisibility(View.VISIBLE);
            keyboardTextInput.setSelection(keyboardTextInput.getText().toString().length());
            keyboardTextInput.requestFocus();
        } else {
            keyboardStandardView.setVisibility(View.GONE);
            LastTexts[type] = keyboardTextInput.getText().toString();
        }
    }

    public boolean isVisible() {
        return keyboardStandardView != null && keyboardStandardView.getVisibility() == View.VISIBLE;
    }

    public void setTextLastOnType(String text, int type){
        LastTexts[type] = text;
    }

    public int getFreeSlotOfHistory(){
        for (int i = 0; i < HistoryTexts.length; i++) {
            if (HistoryTexts[i].length() == 0) {
                return i;
            }
        }

        return -1;
    }

    public void addHistoryText(String text){
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        if (HistoryTexts[0] != null && HistoryTexts[0].equals(text)) {
            return;
        }

        offsetSLotsInHistory();
        HistoryTexts[0] = text;
    }

    public void offsetSLotsInHistory(){
        for (int i = HistoryTexts.length - 1; i > 0; i--) {
            HistoryTexts[i] = HistoryTexts[i - 1];
        }

        HistoryTexts[0] = "";
    }

    public void deleteLastSlotInHistory(){
        for (int i = 1; i < HistoryTexts.length; i++) {
            HistoryTexts[i-1] = HistoryTexts[i];
        }
        HistoryTexts[HistoryTexts.length-1] = "";
    }

    public void selectLang(int lang) {
        selectedLang = lang;
        if (lang == KEYBOARD_LANG_SPEC) {
            for (int i = 0; i < keyboardCharsLineUp.getChildCount(); i++) {
                if (keyboardCharsLineUp.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineUp.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardCharsLineMiddle.getChildCount(); i++) {
                if (keyboardCharsLineMiddle.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineMiddle.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardCharsLineDown.getChildCount(); i++) {
                if (keyboardCharsLineDown.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineDown.removeViewAt(i);
                    i--;
                }
            }

            for (int i = 0; i < keyboardsCharLang[KEYBOARD_LANG_SPEC].length; i++) {
                String charsOnLine = keyboardsCharLang[KEYBOARD_LANG_SPEC][i];

                for (int j = 0; j < charsOnLine.length(); j++) {
                    if (i == 0) {
                        keyboardCharsLineUp.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()));
                    }
                    if (i == 1) {
                        keyboardCharsLineMiddle.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()));
                    }
                    if (i == 2) {
                        keyboardCharsLineDown.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()), keyboardCharsLineDown.getChildCount() - 1);
                    }
                }
                keyboardCharsLineUp.setWeightSum(keyboardCharsLineUp.getChildCount());
                keyboardCharsLineMiddle.setWeightSum(keyboardCharsLineMiddle.getChildCount());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) keyboardCharsLineMiddle.getLayoutParams();
                params.setMargins(0, 0, 0, 5);
                keyboardCharsLineMiddle.setLayoutParams(params);
                keyboardCharsLineDown.setWeightSum(keyboardCharsLineDown.getChildCount() - 2 + (1f * 1));


                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ButtonSymbolShift.getLayoutParams();
                params1.weight = 1f;
                ButtonSymbolShift.setLayoutParams(params1);
                ButtonSymbolShift.setVisibility(View.GONE);

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) ButtonSymbolBackspace.getLayoutParams();
                params2.weight = 1f;
                ButtonSymbolBackspace.setLayoutParams(params2);
            }
        }
        if (lang == KEYBOARD_LANG_ENG) {
            for (int i = 0; i < keyboardCharsLineUp.getChildCount(); i++) {
                if (keyboardCharsLineUp.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineUp.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardCharsLineMiddle.getChildCount(); i++) {
                if (keyboardCharsLineMiddle.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineMiddle.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardCharsLineDown.getChildCount(); i++) {
                if (keyboardCharsLineDown.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineDown.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardsCharLang[KEYBOARD_LANG_ENG].length; i++) {
                String charsOnLine = keyboardsCharLang[KEYBOARD_LANG_ENG][i];

                for (int j = 0; j < charsOnLine.length(); j++) {
                    if (i == 0) {
                        keyboardCharsLineUp.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()));
                    }
                    if (i == 1) {
                        keyboardCharsLineMiddle.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()));
                    }
                    if (i == 2) {
                        keyboardCharsLineDown.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()), keyboardCharsLineDown.getChildCount() - 1);
                    }
                }
                keyboardCharsLineUp.setWeightSum(keyboardCharsLineUp.getChildCount());
                keyboardCharsLineMiddle.setWeightSum(keyboardCharsLineMiddle.getChildCount());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) keyboardCharsLineMiddle.getLayoutParams();
                params.setMargins((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        activity.getResources().getDisplayMetrics()
                ), 0, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        activity.getResources().getDisplayMetrics()
                ), 5);
                keyboardCharsLineMiddle.setLayoutParams(params);
                keyboardCharsLineDown.setWeightSum(keyboardCharsLineDown.getChildCount() - 2 + (1.4f * 2));


                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ButtonSymbolShift.getLayoutParams();
                params1.weight = 1.4f;
                ButtonSymbolShift.setLayoutParams(params1);
                ButtonSymbolShift.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) ButtonSymbolBackspace.getLayoutParams();
                params2.weight = 1.4f;
                ButtonSymbolBackspace.setLayoutParams(params2);
            }
        }
        if (lang == KEYBOARD_LANG_RU) {
            for (int i = 0; i < keyboardCharsLineUp.getChildCount(); i++) {
                if (keyboardCharsLineUp.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineUp.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardCharsLineMiddle.getChildCount(); i++) {
                if (keyboardCharsLineMiddle.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineMiddle.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardCharsLineDown.getChildCount(); i++) {
                if (keyboardCharsLineDown.getChildAt(i) instanceof TextView) {
                    keyboardCharsLineDown.removeViewAt(i);
                    i--;
                }
            }
            for (int i = 0; i < keyboardsCharLang[KEYBOARD_LANG_RU].length; i++) {
                String charsOnLine = keyboardsCharLang[KEYBOARD_LANG_RU][i];

                for (int j = 0; j < charsOnLine.length(); j++) {
                    if (i == 0) {
                        keyboardCharsLineUp.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()));
                    }
                    if (i == 1) {
                        keyboardCharsLineMiddle.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()));
                    }
                    if (i == 2) {
                        keyboardCharsLineDown.addView(createCharButton(new Character(charsOnLine.charAt(j)).toString()), keyboardCharsLineDown.getChildCount() - 1);
                    }
                }
                keyboardCharsLineUp.setWeightSum(keyboardCharsLineUp.getChildCount());
                keyboardCharsLineMiddle.setWeightSum(keyboardCharsLineMiddle.getChildCount());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) keyboardCharsLineMiddle.getLayoutParams();
                params.setMargins((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        activity.getResources().getDisplayMetrics()
                ), 0, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40,
                        activity.getResources().getDisplayMetrics()
                ), 5);
                keyboardCharsLineMiddle.setLayoutParams(params);
                keyboardCharsLineDown.setWeightSum(keyboardCharsLineDown.getChildCount() - 2 + (1.4f * 2));


                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) ButtonSymbolShift.getLayoutParams();
                params1.weight = 1.4f;
                ButtonSymbolShift.setLayoutParams(params1);
                ButtonSymbolShift.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) ButtonSymbolBackspace.getLayoutParams();
                params2.weight = 1.4f;
                ButtonSymbolBackspace.setLayoutParams(params2);
            }
        }
    }

    public TextView createCharButton(String symb) {
        TextView button = new TextView(activity);

        Typeface font = ResourcesCompat.getFont(activity, R.font.arial_bold);
        button.setTypeface(font);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        params.setMargins(0,0, activity.getResources().getDimensionPixelSize(R.dimen._2sdp),0);
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);

        button.setText(activeShift ? symb.toUpperCase() : symb.toLowerCase()); 
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        button.setBackgroundResource(R.drawable.selector_keyboard_key_default_background);

        button.setAllCaps(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldText = keyboardTextInput.getText().toString();
                int selectionCursorStart = keyboardTextInput.getSelectionStart();
                int selectionCursorEnd = keyboardTextInput.getSelectionEnd();

                String charToInsert = ((TextView) v).getText().toString();

                if (selectionCursorStart != selectionCursorEnd) {
                    String startString = oldText.substring(0, selectionCursorStart);
                    String endString = oldText.substring(selectionCursorEnd);
                    keyboardTextInput.setText(startString + charToInsert + endString);
                    keyboardTextInput.setSelection(selectionCursorStart + charToInsert.length());
                } else {
                    String textPart1 = oldText.substring(0, selectionCursorStart);
                    String textPart2 = oldText.substring(selectionCursorStart);
                    keyboardTextInput.setText(textPart1 + charToInsert + textPart2);
                    keyboardTextInput.setSelection(selectionCursorStart + charToInsert.length());
                }
            }
        });

        return button;
    }

    public ConstraintLayout getView() {
        return keyboardStandardView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == ButtonSymbolBackspace.getId()) {
            String oldText = keyboardTextInput.getText().toString();
            int selectionCursorStart = keyboardTextInput.getSelectionStart();
            if(selectionCursorStart - keyboardTextInput.getSelectionEnd() != 0){
                String startString = oldText.substring(0, selectionCursorStart);
                String endString = oldText.substring(keyboardTextInput.getSelectionEnd(), oldText.length());
                keyboardTextInput.setText(startString + endString);
                keyboardTextInput.setSelection(selectionCursorStart);
                return;
            }

            if (!keyboardTextInput.isFocused()) {
                selectionCursorStart = keyboardTextInput.getText().toString().length();
            }
            if (selectionCursorStart == 0) {
                return;
            }

            String textPart1 = oldText.substring(0, selectionCursorStart - 1);
            String textPart2 = oldText.substring(selectionCursorStart);


            keyboardTextInput.setText(textPart1 + textPart2);
            keyboardTextInput.setSelection(selectionCursorStart - 1);
        }else if (v.getId() == ButtonSymbolSpace.getId()) {
            String oldText = keyboardTextInput.getText().toString();
            int selectionCursorStart = keyboardTextInput.getSelectionStart();
            if(selectionCursorStart - keyboardTextInput.getSelectionEnd() != 0){
                String startString = oldText.substring(0, selectionCursorStart);
                String endString = oldText.substring(keyboardTextInput.getSelectionEnd(), oldText.length());
                keyboardTextInput.setText(startString + " " + endString);
                keyboardTextInput.setSelection(selectionCursorStart+1);
                return;
            }

            if (!keyboardTextInput.isFocused()) {
                selectionCursorStart = keyboardTextInput.getText().toString().length();
            }

            String textPart1 = oldText.substring(0, selectionCursorStart);
            String textPart2 = oldText.substring(selectionCursorStart);

            keyboardTextInput.setText(textPart1 + " " + textPart2);
            keyboardTextInput.setSelection(selectionCursorStart + 1);
        }else if (v.getId() == ButtonSymbolShift.getId()) {

            if (activeShift) {
                activeShift = false;

                for (int i = 0; i < keyboardCharsLineUp.getChildCount(); i++) {
                    if (keyboardCharsLineUp.getChildAt(i) instanceof TextView) {
                        TextView symbView = ((TextView) keyboardCharsLineUp.getChildAt(i));
                        symbView.setText(symbView.getText().toString().toLowerCase());
                    }
                }
                for (int i = 0; i < keyboardCharsLineMiddle.getChildCount(); i++) {
                    if (keyboardCharsLineMiddle.getChildAt(i) instanceof TextView) {
                        TextView symbView = ((TextView) keyboardCharsLineMiddle.getChildAt(i));
                        symbView.setText(symbView.getText().toString().toLowerCase());
                    }
                }
                for (int i = 0; i < keyboardCharsLineDown.getChildCount(); i++) {
                    if (keyboardCharsLineDown.getChildAt(i) instanceof TextView) {
                        TextView symbView = ((TextView) keyboardCharsLineDown.getChildAt(i));
                        symbView.setText(symbView.getText().toString().toLowerCase());
                    }
                }
            } else {
                activeShift = true;

                for (int i = 0; i < keyboardCharsLineUp.getChildCount(); i++) {
                    if (keyboardCharsLineUp.getChildAt(i) instanceof TextView) {
                        TextView symbView = ((TextView) keyboardCharsLineUp.getChildAt(i));
                        symbView.setText(symbView.getText().toString().toUpperCase());
                    }
                }
                for (int i = 0; i < keyboardCharsLineMiddle.getChildCount(); i++) {
                    if (keyboardCharsLineMiddle.getChildAt(i) instanceof TextView) {
                        TextView symbView = ((TextView) keyboardCharsLineMiddle.getChildAt(i));
                        symbView.setText(symbView.getText().toString().toUpperCase());
                    }
                }
                for (int i = 0; i < keyboardCharsLineDown.getChildCount(); i++) {
                    if (keyboardCharsLineDown.getChildAt(i) instanceof TextView) {
                        TextView symbView = ((TextView) keyboardCharsLineDown.getChildAt(i));
                        symbView.setText(symbView.getText().toString().toUpperCase());
                    }
                }

            }
        } else if (v.getId() == ButtonSymbolLang.getId()) {
            if (this.selectedLang == KEYBOARD_LANG_ENG) {
                this.selectLang(KEYBOARD_LANG_SPEC);
                return;
            }
            if (this.selectedLang == KEYBOARD_LANG_SPEC) {
                this.selectLang(KEYBOARD_LANG_RU);
                return;
            }
            if (this.selectedLang == KEYBOARD_LANG_RU) {
                this.selectLang(KEYBOARD_LANG_ENG);
                return;
            }
        } else if (v.getId() == ButtonSymbolEnter.getId()) {
            if(keyboardTextInput.getText().toString().length() > 0 && useType == TYPE_USE_KEYBOARD_CHAT) {
                addHistoryText(keyboardTextInput.getText().toString());
            }
            idSelectedHistoryTexts = -1;

            if(callableEnter != null) {
                callableEnter.run();
                setCallableEnter(null);
            } else {
                keyboardStandardView.setVisibility(View.INVISIBLE);
            }

            keyboardTextInput.setText("");
            if(useType != -1){
                LastTexts[useType] = "";
            }
        }
    }
}
