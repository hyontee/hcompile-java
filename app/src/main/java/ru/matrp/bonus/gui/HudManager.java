package ru.matrp.bonus.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nvidia.devtech.NvEventQueueActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.bonus.InterfacesManager;
import ru.matrp.bonus.R;
import ru.matrp.bonus.gui.chatedgar.ChatManager;
import ru.matrp.bonus.gui.keyboard.KeyBoard;

public class HudManager {
    public NvEventQueueActivity nvEventQueueActivity = null;
    public ViewGroup viewGroup = null;
    public FrameLayout hud_layout, btn_shop;
    public TextView hud_money, quest_btn_optional, edgar;
    public LinearLayout hud_layou, quest_layout, quest_btn_hide;

    public FrameLayout btn_quest, btn_bp, btn_1, hud_chat_btn;

    public ImageView hud_weapon, chat_icon;

    public ProgressBar progressHP;

    public HudManager(NvEventQueueActivity nvEventQueueActivity, int guiId) {
        this.nvEventQueueActivity = nvEventQueueActivity;
        viewGroup = InterfacesManager.getInterfacesManager().viewGroup[guiId];
        show();
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
    }

    public void show() {
        if (viewGroup != null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) NvEventQueueActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.hud, (ViewGroup) null);
        NvEventQueueActivity.getInstance().getBackUILayout().addView(viewGroup, -1, -1);

        hud_layout = viewGroup.findViewById(R.id.hud_main);
        hud_layou = viewGroup.findViewById(R.id.hud_layout);

        progressHP = viewGroup.findViewById(R.id.stat_progress);

        edgar = viewGroup.findViewById(R.id.quest_text);
        edgar.setText(MainActivity.getMainActivity().text6);

        hud_money = viewGroup.findViewById(R.id.money_text);
        quest_btn_optional = viewGroup.findViewById(R.id.quest_btn_optional);
        quest_btn_hide = viewGroup.findViewById(R.id.quest_btn_hide);
        hud_weapon = viewGroup.findViewById(R.id.weapon_melee_image);

        btn_quest = viewGroup.findViewById(R.id.btn_quest);
        quest_layout = viewGroup.findViewById(R.id.quest_layout);
        btn_shop = viewGroup.findViewById(R.id.btn_shop);
        btn_bp = viewGroup.findViewById(R.id.btn_bp);
        btn_1 = viewGroup.findViewById(R.id.btn_1);
        hud_chat_btn = viewGroup.findViewById(R.id.hud_chat_btn);
        chat_icon = viewGroup.findViewById(R.id.chat_icon);

        btn_shop.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
            NvEventQueueActivity.getInstance().sendCommand("/mm".getBytes(StandardCharsets.UTF_8));
        });

        btn_bp.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
            NvEventQueueActivity.getInstance().sendCommand("/mm".getBytes(StandardCharsets.UTF_8));
        });

        btn_1.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
            NvEventQueueActivity.getInstance().sendCommand("/mm".getBytes(StandardCharsets.UTF_8));
            //NvEventQueueActivity.getInstance().togglePlayer(1);
        });

        quest_btn_hide.setOnClickListener(v -> {
            hud_layou.setVisibility(View.VISIBLE);
            quest_layout.setVisibility(View.GONE);
        });

        quest_btn_optional.setOnClickListener(v -> {
            hud_layou.setVisibility(View.VISIBLE);
            quest_layout.setVisibility(View.GONE);
        });

        btn_quest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hud_layou.setVisibility(View.GONE);
                quest_layout.setVisibility(View.VISIBLE);
            }
        });

        hud_chat_btn.setOnClickListener(v -> {

            int i;
            if (ChatManager.statusChat == 3) {
                i = 1;
            } else i = ChatManager.statusChat + 2;
            InterfacesManager.getInterfacesManager().getChatManager().setChatStatys(i);
            chat_icon.setImageResource(i == 1 ? R.drawable.ic_hud_chat_active : i == 2 ? R.drawable.ic_hud_chat_middle : R.drawable.ic_hud_chat_inactive);
        });
        hud_chat_btn.setOnTouchListener(new KeyBoard.clicabilel(nvEventQueueActivity, hud_chat_btn));

            /*String str;
            float dimensionPixelSize = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._9sdp);
            float dimensionPixelSize2 = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._9sdp);
            float dimensionPixelSize3 = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._92sdp) + dimensionPixelSize;
            float dimensionPixelSize4 = nvEventQueueActivity.getResources().getDimensionPixelSize(R.dimen._92sdp) + dimensionPixelSize2;
//CHUD::hud_radar
            setRadarSizes(0.0f + dimensionPixelSize, dimensionPixelSize2 + 0.0f, dimensionPixelSize3 - 0.0f, dimensionPixelSize4 - 0.0f);
            float f1 = dimensionPixelSize4 - 0.0f;
            Log.i("edgar", "float 1 = " + f1);*/

    }

    public Bitmap createBitmapFromByteArray(byte[] byteArray) {
        final Bitmap createBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        createBitmap.setHasAlpha(true);
        IntBuffer asIntBuffer = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        Log.i("edgar", "asIntBuffer = " + asIntBuffer);
        int[] iArr = new int[asIntBuffer.remaining()];
        //Log.i("edgar", "length = " + iArr.length);
        asIntBuffer.get(iArr);
        createBitmap.setPixels(iArr, 0, 512, 0, 0, 512, 512);
        return createBitmap;
    }

    public void UpdateHudInfo(int health, int armour, int hunger, int weaponidweik, int ammo, int playerid, int money, int wanted)
    {
        if(viewGroup == null)
        {
            return;
        }
        progressHP.setProgress(health);

        DecimalFormat formatter=new DecimalFormat();
        DecimalFormatSymbols symbols= DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String s= formatter.format(money);
        hud_money.setText(s);

        int id = viewGroup.getResources().getIdentifier(new Formatter().format("weapon_%d", Integer.valueOf(weaponidweik)).toString(), "drawable", nvEventQueueActivity.getPackageName());
        hud_weapon.setImageResource(id);

        hud_weapon.setOnClickListener(v -> NvEventQueueActivity.getInstance().onWeaponChanged());

    }

    public void ShowGps()
    {

    }

    public void HideGps()
    {

    }

    public void ShowX2()
    {

    }

    public void HideX2()
    {

    }

    public void ShowZona()
    {

    }

    public void HideZona()
    {

    }

    public void ShowRadar()
    {

    }

    public void HideRadar()
    {

    }

    public void ShowHud()
    {
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.VISIBLE);
    }

    public void HideHud()
    {
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
    }

}
