package com.samp.mobile.game.ui.keyboard;

import android.content.Context;
import android.util.AttributeSet;

public class KeyboardInputTextView extends androidx.appcompat.widget.AppCompatEditText {

    public KeyboardInputTextView(Context context) {
        super(context);
    }

    public KeyboardInputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardInputTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    boolean canPaste() {
        return false;
    }
}
