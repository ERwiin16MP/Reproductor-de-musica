package com.erwin16mp.reproductordemusica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Random;
import static com.erwin16mp.reproductordemusica.Reproductor.notificationManager;

public class Index extends AppCompatActivity {

    private static final String CANCION_REPRODUCIENDO = "CancionReproduciendo";
    private static final String SALIDA = "Salida";
    private static final String ALEATORIO = "Aleatorio";
    private static final String REPRODUCIENDO = "Reproduciendo";
    private static final String OCULTAR_AUD = "OCULTAR_AUD";
    private static final String TEMA = "Tema";
    private static final int AJUSTES_CODIGO = 111;
    private RecyclerView ListaDeCanciones;
    private AdView mAdView;
    private static Boolean AUD;
    private InterstitialAd mInterstitialAd;
    static ArrayList<Canciones> Archivos;
    private MusicaAdapter adapter;
    private static String album, titulo, duracion, url, artista;
    SharedPreferences preferences;
    private static String Aux;
    private FloatingActionButton Boton_Aleatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        inicializarVistas();
        cargarConfiguraciones();
        cargarAnuncios();
        Archivos = buscarCanciones(this);
        if (!(Archivos.size() < 1)) {
            adapter = new MusicaAdapter(getApplicationContext(), Archivos);
            ListaDeCanciones.setAdapter(adapter);
            ListaDeCanciones.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        } else Toast.makeText(this, R.string.NoTieneCanciones, Toast.LENGTH_SHORT).show();
    }

    private void cargarConfiguraciones() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        AUD = preferences.getBoolean(OCULTAR_AUD, false);
    }

    private void cargarAnuncios() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2386705468697450/1308765909");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public static @NotNull ArrayList<Canciones> buscarCanciones(@NotNull Context context) {
        ArrayList<Canciones> canciones = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Media.TITLE);
        if (cursor != null) {
            if (AUD) {
                while (cursor.moveToNext()) {
                    url = cursor.getString(3);
                    Aux = String.valueOf(url.charAt(url.length() - 1));
                    if (!("a".equalsIgnoreCase(Aux) || ("s".equalsIgnoreCase(Aux)))){
                        album = cursor.getString(0);
                        titulo = cursor.getString(1);
                        duracion = cursor.getString(2);
                        artista = cursor.getString(4);
                        canciones.add(new Canciones(url, titulo, artista, album, duracion, R.drawable.reproduciendo));
                    }
                }
            }else {while (cursor.moveToNext()) {
                album = cursor.getString(0);
                titulo = cursor.getString(1);
                duracion = cursor.getString(2);
                url = cursor.getString(3);
                artista = cursor.getString(4);
                canciones.add(new Canciones(url, titulo, artista, album, duracion, R.drawable.reproduciendo));
            }}
            cursor.close();
        }
        return canciones;
    }

    private Intent enviarDatos(int CancionReproduciendo, String Salida) {
        return new Intent(this, Reproductor.class)
                .putExtra(SALIDA, Salida)
                .putExtra(CANCION_REPRODUCIENDO, CancionReproduciendo);
    }

    private void inicializarVistas() {
        ListaDeCanciones = findViewById(R.id.ListaDeCanciones);
        ListaDeCanciones.setHasFixedSize(true);
        Boton_Aleatorio = findViewById(R.id.Button_Aleatorio_Index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Compartir:
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, getString(R.string.UnSimple) + "http://play.google.com/store/apps/details?id=com.erwin16mp.reproductordemusica"), "Compartir"));
                break;
            case R.id.Mas:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/dev?id=4896053393646672481")));
                break;
            case R.id.Acerca_de:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(getLayoutInflater().inflate(R.layout.acerca_de, null)).create().show();
                break;
            case R.id.Calificar:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.erwin16mp.reproductordemusica")));
                break;
            case R.id.Apoyar:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle(getString(R.string.Apoyar))
                        .setMessage(getString(R.string.ParaApoyar))
                        .setNegativeButton(getString(R.string.No), null)
                        .setPositiveButton(getString(R.string.Vale), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mInterstitialAd.isLoaded())
                                    mInterstitialAd.show();
                                else
                                    Toast.makeText(Index.this, R.string.Error, Toast.LENGTH_LONG).show();
                            }
                        }).create().show();
                break;
            case R.id.Reproduciendo:
                startActivity(enviarDatos(0, REPRODUCIENDO));
                break;
            case R.id.Ajustes:
                startActivityForResult(new Intent(this, Ajustes.class), AJUSTES_CODIGO);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void aleatorio(View view) {
        startActivity(enviarDatos(new Random().nextInt(Archivos.size()), ALEATORIO));
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if (notificationManager != null) {
                Reproductor.notificationManager.cancelAll();
            }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AJUSTES_CODIGO) {
            this.recreate();
        }
    }

    private void guardarString(String Key, String Value) {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(Key, Value).commit();
    }

    private void guardarInt(String Key, int Value) {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(Key, Value).commit();
    }

    private void guardarBoolean(String Key, Boolean Value) {
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(Key, Value).commit();
    }

    private String getConfiguracionString(String Key, String defValue) {
        return getPreferences(Context.MODE_PRIVATE).getString(Key, defValue);
    }

    private int getConfiguracionInt(String Key, int defValue) {
        return getPreferences(Context.MODE_PRIVATE).getInt(Key, defValue);
    }

    private boolean getConfiguracionBoolean(String Key, Boolean defValue) {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(Key, defValue);
    }

    private void setTema() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (preferences.getString(TEMA, "Rojo")) {
            case "Rojo":
                setTheme(R.style.Rojo);
                break;
            case "Morado":
                setTheme(R.style.Morado);
                break;
            case "Azul":
                setTheme(R.style.Azul);
                break;
            case "Verde":
                setTheme(R.style.Verde);
                break;
            case "Amarillo":
                setTheme(R.style.Amarillo);
                break;
            case "Naranja":
                setTheme(R.style.Naranja);
                break;
            case "Café":
                setTheme(R.style.Cafe);
                break;
        }
    }
}// 304 -> 256 -> 239