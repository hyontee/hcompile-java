package com.saint.game.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.saint.game.R;
import android.annotation.SuppressLint;
/*Project made by vk.com/mazan172 */
public class VKActivity extends AppCompatActivity {
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/mazan172"));
        startActivity(intent);
    }
}
