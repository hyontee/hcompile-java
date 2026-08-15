package com.saint.game.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.saint.game.R;
import com.saint.game.core.Utils;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;


import static com.saint.game.core.Config.GAME_PATH;

/*Project made by vk.com/mazan172 */
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private Button btn_play;
    private Button btnSettings;
    private Button btnForum;
    private Button btnVK;

    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.main_activity);
        Init(savedInstanceState);
        super.onCreate(savedInstanceState);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        btnForum = (Button) findViewById(R.id.btnForum);
        btnForum.setOnClickListener(this);

        btnVK = (Button) findViewById(R.id.btnVK);
        btnVK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnForum:
                Intent intent2 = new Intent(this, DonateActivity.class);
                startActivity(intent2);
                break;
            case R.id.btnVK:
                Intent intent3 = new Intent(this, VKActivity.class);
                startActivity(intent3);
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

    private void Init(Bundle savedInstanceState) {
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (IsGameInstalled()) StartClient();
                else StartInstallGame();
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

    private void StartClient()
    {
        Intent intent = new Intent(this, com.saint.game.core.GTASA.class);
        intent.putExtras(getIntent());
        System.out.println("StartActivity GTASA.class");
        startActivity(intent);
        finish();
    }
    private void showMessage(String _s) {
        Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();


    }
    private void StartSetting()
    {
        Intent intent = new Intent(this,com.saint.game.launcher.SettingsActivity.class);
        startActivity(intent);
    }
}
