package com.samp.mobile.game.ui;

import android.app.Activity;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.samp.mobile.R;
import com.samp.mobile.game.SAMP;

public class LoadingScreen {

    private Activity activity;
    private ConstraintLayout mainLayout;

    public LoadingScreen(Activity activity)
    {
        this.activity = activity;

        mainLayout = (ConstraintLayout) activity.getLayoutInflater().inflate(R.layout.loadingscreen, null);
        ((SAMP) activity).getParentLayout().addView(mainLayout, new ConstraintLayout.LayoutParams(-1, -1));
    }

    public void hide()
    {
        mainLayout.setVisibility(View.INVISIBLE);
    }
}
