package com.erwin16mp.reproductordemusica;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private Animation topAnimation, bottonAnimation;
    private ImageView Logo;
    private TextView Erwin, Correo, Version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setNavigationBarColor(Color.parseColor("#000000"));

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottonAnimation = AnimationUtils.loadAnimation(this, R.anim.botton_animation);

        inicializarVistas();

        Logo.setAnimation(topAnimation);
        Erwin.setAnimation(bottonAnimation);
        Correo.setAnimation(bottonAnimation);
        Version.setAnimation(bottonAnimation);

        vereficarpermiso();
    }

    private void vereficarpermiso() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        else
            cambioDeActivity();
    }

    private void cambioDeActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Index.class));
                finish();
            }
        }, 2000);
    }

    private void inicializarVistas() {
        Logo = findViewById(R.id.logo);
        Erwin = findViewById(R.id.erwin);
        Correo = findViewById(R.id.correo);
        Version = findViewById(R.id.version);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                cambioDeActivity();
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.SolicitudDePermiso));
                builder.setMessage(getString(R.string.LaAplicacionNo));
                builder.setPositiveButton(getString(R.string.Vale), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
            }
    }
}