package com.brilliant.game.ui.auth;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.brilliant.game.R;
import com.brilliant.game.core.Samp;
import com.brilliant.game.launcher.util.Util;

import java.util.ArrayList;

public class SpawnSelector {

    private final Activity mActivity;
    private final ConstraintLayout mInputLayout;

    private final ArrayList<ConstraintLayout> mItems;

    Drawable[] DRAWABLES;

    String[] TITLES = new String[]{
            "Место выхода", "Ваш дом", "Вокзал", "Организация"
    };

    String[] DESCRIPTIONS = new String[]{
            "Появиться на том месте, \nгде вы были перед выходом",
            "Появиться в вашем купленном\nили арендованном доме",
            "Появиться на вокзале",
            "Появиться в здании \nорганизации"
    };

    public SpawnSelector(Activity activity) {
        this.mActivity = activity;
        this.mInputLayout = activity.findViewById(R.id.brp_ss_main);

        this.mItems = new ArrayList<>();

        if (this.mInputLayout == null) {
            DRAWABLES = new Drawable[0];
            return;
        }

        LinearLayout itemsLL = activity.findViewById(R.id.brp_ss_items);

        DRAWABLES = new Drawable[]{
                activity.getDrawable(R.drawable.ic_clock), activity.getDrawable(R.drawable.ic_home),
                activity.getDrawable(R.drawable.ic_train), activity.getDrawable(R.drawable.ic_fraction)
        };

        for (int i = 0; i < itemsLL.getChildCount(); i++)
        {
            ConstraintLayout item = (ConstraintLayout) itemsLL.getChildAt(i);

            TextView title = item.findViewById(R.id.brp_ss_item_title);
            TextView desc = item.findViewById(R.id.brp_ss_item_desc);

            ImageView img = item.findViewById(R.id.brp_ss_item_icon);

            title.setText(TITLES[i]);
            desc.setText(DESCRIPTIONS[i]);

            img.setImageDrawable(DRAWABLES[i]);

            mItems.add(item);
        }

        Util.HideLayout(this.mInputLayout, false);
    }

    public void show(boolean isHouse, boolean isFraction) {
        if (mInputLayout == null) return;

        for (int i = 0; i < this.mItems.size(); i++)
        {
            ConstraintLayout item = this.mItems.get(i);

            TextView title = item.findViewById(R.id.brp_ss_item_not_fount_text);
            ImageView bg = item.findViewById(R.id.brp_ss_item_not_fount_bg);

            Button btn = item.findViewById(R.id.brp_ss_item_btn);

            int visibility = View.GONE;
            View.OnClickListener clickListener = selectSpawn(i);

            item.animate().alpha(0.0f).setDuration(0).translationYBy(500).start();
            item.animate().alpha(1.0f).setDuration(500).setStartDelay(400 * i).translationY(0).start();

            if (i == 1) {
                visibility = !isHouse ? View.VISIBLE : View.GONE;
                clickListener = !isHouse ? null : selectSpawn(i);

                title.setText(!isHouse ? "У вас нет\nдома" : "");
            } else if (i == 3){
                visibility = !isFraction ? View.VISIBLE : View.GONE;
                clickListener = !isFraction ? null : selectSpawn(i);

                title.setText(!isFraction ? "Вы не состоите\nв организации" : "");
            }

            title.setVisibility(visibility);
            bg.setVisibility(visibility);

            btn.setOnClickListener(clickListener);
        }

        Util.ShowLayout(this.mInputLayout, true);
    }

    public void hide() {
        if (mInputLayout == null) return;

        for (int i = 0; i < this.mItems.size(); i++) {
            ConstraintLayout item = this.mItems.get(i);

            item.animate().alpha(1.0f).setDuration(500).setStartDelay(400 * i).translationYBy(500).start();
        }

        Util.HideLayout(this.mInputLayout, true);
    }

    private View.OnClickListener selectSpawn(int id) {
        return v -> {
            Samp.getInstance().sendSpawnClick(id);

            v.startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.btn_click));

            hide();
        };
    }

    public ConstraintLayout getInputLayout() {
        return mInputLayout;
    }

}
