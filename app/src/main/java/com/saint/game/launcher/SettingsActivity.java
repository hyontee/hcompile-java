package com.saint.game.launcher;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.saint.game.R;
import com.saint.game.core.Utils;
import com.saint.game.launcher.MainActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.ini4j.Wini;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import static com.saint.game.core.Config.GAME_PATH;
import static com.saint.game.core.Config.GAME_DIR;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText nickname;
    private Button btnVK;
    private Button btnForum;
    private Button btnHome;
    private Button btnRein;
    private Button btnPlaying;

    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.settings_activity);
        Init(savedInstanceState);
        Play(savedInstanceState);
        Init2(savedInstanceState);
        super.onCreate(savedInstanceState);

        btnForum = (Button) findViewById(R.id.btnForum);
        btnForum.setOnClickListener(this);

        btnVK = (Button) findViewById(R.id.btnVK);
        btnVK.setOnClickListener(this);

        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnForum:
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://vk.com/mazan172"));
                startActivity(intent);
                break;
            case R.id.btnVK:
                Intent intent2 = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://vk.com/mazan172"));
                startActivity(intent2);
                break;
            case R.id.btnHome:
                Intent intent7 = new Intent(this, MainActivity.class);
                startActivity(intent7);
                break;
            default:
                break;
        }
    }

    private boolean IsGameInstalled()
    {
        String GetGamePath = GAME_PATH + "texdb/gta3.img";
        File file = new File(GetGamePath);
        return file.exists();
    }

    private void Play(Bundle savedInstanceState) {
        btnPlaying = (Button) findViewById(R.id.btn_playing);
        btnPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (IsGameInstalled()) StartClient();
                else StartInstallGame();
            }
        });
    }


    private void Init2(Bundle savedInstanceState) {
        btnRein = (Button) findViewById(R.id.btnRein);
        btnRein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                UninstallFilesGame();
                StartInstallGame();
            }
        });
    }

    private void StartInstallGame()
    {
        Intent intent = new Intent(this, com.saint.game.launcher.InstallGame.class);
        Utils.setType(0);
        intent.putExtras(getIntent());
        startActivity(intent);
    }


    private void Init(Bundle savedInstanceState)
    {
        nickname = (EditText) findViewById(R.id.nick_edit);
//findViewById(R.id.nick_edit)
        ((EditText)nickname).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    //if (!event.isShiftPressed())
                    //{
                    try {
                        File f = new File(GAME_PATH+"SAMP/settings.ini");
                        if(!f.exists()){ f.createNewFile();f.mkdirs();}
                        Wini w = new Wini(new File(GAME_PATH+"SAMP/settings.ini"));
                        w.put("client", "name", nickname.getText().toString());
                        w.store();
                        showMessage("Ваш никнейм успешно поставлен!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        InitLogic();
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                boolean success = deleteDir(child);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private boolean UninstallFilesGame() {
        try {
            File dir = new File(GAME_DIR+"Matreshka/");
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i=0; i<children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                    }
                }
                return false;
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Файлы игры удалены").
                setMessage("Файлы игры успешно удалены с вашего телефона!")
                //.setIcon(R.drawable.)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }});
        return false;
    }

    private void StartClient()
    {
        Intent intent = new Intent(this, com.saint.game.core.GTASA.class);
        intent.putExtras(getIntent());
        System.out.println("StartActivity GTASA.class");
        startActivity(intent);
        finish();
    }



    private void InitLogic() {
        try{
            Wini w = new Wini(new File(GAME_PATH+"SAMP/settings.ini"));
            nickname.setText(w.get("client", "name"));
            w.store();
            //CheckUpdateLauncher();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String _s) {
        Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
    }
}
