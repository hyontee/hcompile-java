package com.samp.mobile.game;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.joom.paranoid.Obfuscate;
import com.samp.mobile.R;
import com.samp.mobile.game.ui.AttachEdit;
import com.samp.mobile.game.ui.Buttons;
import com.samp.mobile.game.ui.CustomKeyboard;
import com.samp.mobile.game.ui.LoadingScreen;
import com.samp.mobile.game.ui.dialog.DialogManager;
import com.samp.mobile.game.ui.keyboard.KeyboardStandardManager;
import com.samp.mobile.launcher.util.SharedPreferenceCore;
import com.samp.mobile.launcher.util.SignatureChecker;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
@Obfuscate
public class SAMP extends GTASA implements CustomKeyboard.InputListener, HeightProvider.HeightListener {
    private static final String TAG = "SAMP";
    private static SAMP instance;

    private CustomKeyboard mKeyboard;
    private DialogManager mDialog;
    private HeightProvider mHeightProvider;
    public KeyboardStandardManager mKeyboardStandardManager = null;
    private AttachEdit mAttachEdit;
    private LoadingScreen mLoadingScreen;
    private Buttons mButtons;
    public native void sendDialogResponse(int i, int i2, int i3, byte[] str);

    public static SAMP getInstance() {
        return instance;
    }
    @Override
    public FrameLayout getParentLayout() {
        return getmAndroidUI();
    }
    private void showTab()
    {

    }

    private void hideTab()
    {

    }

    private void setTab(int id, String name, int score, int ping)
    {

    }

    private void clearTab()
    {

    }

    private void showLoadingScreen()
    {

    }

    private void hideLoadingScreen()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingScreen.hide();
            }
        });
    }

    public void exitGame(){
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);

        finishAndRemoveTask();
        System.exit(0);
    }

    public void showDialog(int dialogId, int dialogTypeId, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        final String caption = new String(bArr, StandardCharsets.UTF_8);
        final String content = new String(bArr2, StandardCharsets.UTF_8);
        final String leftBtnText = new String(bArr3, StandardCharsets.UTF_8);
        final String rightBtnText = new String(bArr4, StandardCharsets.UTF_8);
        runOnUiThread(() -> { this.mDialog.show(dialogId, dialogTypeId, caption, content, leftBtnText, rightBtnText); });
    }

    private native void onInputEnd(byte[] str);
    @Override
    public void OnInputEnd(String str)
    {
        byte[] toReturn = null;
        try
        {
            toReturn = str.getBytes("windows-1251");
        }
        catch(UnsupportedEncodingException e)
        {

        }

        try {
            onInputEnd(toReturn);
        }
        catch (UnsatisfiedLinkError e5) {
            Log.e(TAG, e5.getMessage());
        }
    }

    private void showKeyboard()
    {

    }

    private void hideKeyboard()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mKeyboard.HideInputLayout();
            }
        });
    }

    private void showEditObject()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAttachEdit.show();
            }
        });
    }

    private void hideEditObject()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAttachEdit.hide();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "**** onCreate");
        super.onCreate(savedInstanceState);

        //if(!SignatureChecker.isSignatureValid(this, getPackageName()))
        //{
            //Toast.makeText(this, "Use original launcher! No remake", Toast.LENGTH_LONG).show();
            //return;
        //}

        //mHeightProvider = new HeightProvider(this);

        mKeyboard = new CustomKeyboard(this);
        mDialog = new DialogManager(this);
        mAttachEdit = new AttachEdit(this);
        mLoadingScreen = new LoadingScreen(this);
        mKeyboardStandardManager = new KeyboardStandardManager(this);
        mKeyboardStandardManager.selectLang(KeyboardStandardManager.KEYBOARD_LANG_RU);
        mButtons = new Buttons(this);
        instance = this;

        try {
            initializeSAMP();
        } catch (UnsatisfiedLinkError e5) {
            Log.e(TAG, e5.getMessage());
        }

    }

    private native void initializeSAMP();
    public native void specialCall(int id);
    @Override
    public void onStart() {
        Log.i(TAG, "**** onStart");
        super.onStart();
    }

    @Override
    public void onRestart() {
        Log.i(TAG, "**** onRestart");
        super.onRestart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "**** onResume");
        super.onResume();
        //mHeightProvider.init(view);
    }

    public native void onEventBackPressed();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onEventBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            onEventBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "**** onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "**** onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "**** onDestroy");
        super.onDestroy();
    }

    @Override
    public void onHeightChanged(int orientation, int height) {

    }
    public void SetVisibleKeyboardStandard(int active, int type)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mKeyboardStandardManager.setCallableClose(null);
                mKeyboardStandardManager.setVisible(active, type, false);
                mKeyboardStandardManager.setCallableEnter(new Runnable() {
                    @Override
                    public void run() {
                        String originalText = mKeyboardStandardManager.keyboardTextInput.getText().toString();
                        OnInputEnd(originalText);

                        if (originalText.length() > 0) {
                            mKeyboardStandardManager.addHistoryText(originalText);
                        }
                        mKeyboardStandardManager.keyboardTextInput.setText("");
                    }
                });

                mKeyboardStandardManager.setCallableClose(new Runnable() {
                    @Override
                    public void run() {
                        mKeyboardStandardManager.setVisible(0, 0, false);
                        OnInputEnd("");
                    }
                });
            }
        });
    }
    public void toggleCustomButtons(boolean status)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mButtons.toggleVisibility(status);
            }
        });
    }
    @Override
    public void setPauseState(boolean pause) {
        super.setPauseState(pause);
    }
}
