package com.erwin16mp.reproductordemusica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.erwin16mp.reproductordemusica.services.OnClearFromRecentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.erwin16mp.reproductordemusica.Index.Archivos;

public class Reproductor extends AppCompatActivity implements MediaPlayer.OnCompletionListener, Playable {

    private static final String CANCION_REPRODUCIENDO = "CancionReproduciendo";
    private static final String SALIDA = "Salida";
    private static final String DIRECTO = "Directo";
    private static final String ALEATORIO = "Aleatorio";
    private static final String REPETIR = "Repetir";
    private static final String REPRODUCIENDO = "Reproduciendo";
    private static final String Canciones_Canciones = "Canciones_Canciones";
    private static final String TEMA = "Tema";
    private final Win win = new Win();
    private static int CancionReproduciendo;
    private ImageView Album;
    private TextView Label_Titulo, Label_Artista, Label_Cancion, Label_Tiempo1, Label_Tiempo2;
    private SeekBar BarraDeProgreso;
    public static MediaPlayer Musica = new MediaPlayer();
    private FloatingActionButton Button_PlayPause;
    private final Handler handler = new Handler();
    private Thread PlayThread, AnteriorThread, SiguienteThread;
    private Button Button_Aleatorio, Button_Anterior, Button_Siguiente, Button_Repetir;
    private Boolean Aleatorio, Repetir;
    public static NotificationManager notificationManager;
    static ArrayList<Canciones> Canciones;
    private int TiempoActual, NumeroDeCanciones;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTema();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reproductor);

        inicializarVistas();
        cargarConfiguraciones();
        recuperarDatos();
        reloj();

        BarraDeProgreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Musica != null && fromUser) {
                    Musica.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        switch (getIntent().getExtras().getString(SALIDA, null)) {
            case DIRECTO:
                CancionReproduciendo = getIntent().getExtras().getInt(CANCION_REPRODUCIENDO, 0);
                if (Musica != null) {
                    Musica.stop();
                    Musica.release();
                }
                Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
                Musica.start();
                Button_PlayPause.setImageResource(R.drawable.pause);
                CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
                break;
            case REPRODUCIENDO:
                if (Musica.isPlaying()) {
                    Button_PlayPause.setImageResource(R.drawable.pause);
                    Musica.setOnCompletionListener(this);
                    BarraDeProgreso.setMax(Musica.getDuration() / 1000);
                    CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
                } else {
                    Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
                    CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.play);
                }
                break;
            case ALEATORIO:
                CancionReproduciendo = getIntent().getExtras().getInt(CANCION_REPRODUCIENDO, 0);
                if (Musica != null) {
                    Musica.stop();
                    Musica.release();
                }
                Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
                Musica.start();
                Button_PlayPause.setImageResource(R.drawable.pause);
                CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
                Aleatorio = true;
                guardarBoolean(ALEATORIO, true);
                Button_Aleatorio.setBackgroundResource(R.drawable.aleatorio_1);
                break;
        }
        Musica.setOnCompletionListener(this);
        BarraDeProgreso.setMax(Musica.getDuration() / 1000);
        setDatos();
        
    }

    private void reloj() {
        Reproductor.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Musica != null) {
                    TiempoActual = Musica.getCurrentPosition() / 1000;
                    BarraDeProgreso.setProgress(TiempoActual);
                    Label_Tiempo1.setText(win.tiempo(TiempoActual));
                }
                handler.postDelayed(this, 1);
            }
        });
    }

    private void cargarConfiguraciones() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Aleatorio = getConfiguracionBoolean(ALEATORIO, false);
        Repetir = getConfiguracionBoolean(REPETIR, false);
        if (Aleatorio)
            Button_Aleatorio.setBackgroundResource(R.drawable.aleatorio_1);
        if (Repetir)
            Button_Repetir.setBackgroundResource(R.drawable.repetir_one_1);
    }

    public void aleatorio(View view) {
        if (Aleatorio) {
            Aleatorio = false;
            Button_Aleatorio.setBackgroundResource(R.drawable.aleatorio);
        } else {
            Aleatorio = true;
            Button_Aleatorio.setBackgroundResource(R.drawable.aleatorio_1);
        }
        guardarBoolean(ALEATORIO, Aleatorio);
    }

    public void repetir(View view) {
        if (Repetir) {
            Repetir = false;
            Button_Repetir.setBackgroundResource(R.drawable.repetir);
        } else {
            Repetir = true;
            Button_Repetir.setBackgroundResource(R.drawable.repetir_one_1);
        }
        guardarBoolean(REPETIR, Repetir);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayThreadButton();
        SiguienteThreadButton();
        AnteriorThreadButton();
    }

    private void AnteriorThreadButton() {
        AnteriorThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Button_Anterior.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Anterior();
                    }
                });
            }
        };
        AnteriorThread.start();
    }

    private void Anterior() {
        if (Musica.isPlaying()) {
            if (Aleatorio && !Repetir)
                CancionReproduciendo = new Random().nextInt(NumeroDeCanciones + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo - 1) < 0 ? (NumeroDeCanciones - 1) : (CancionReproduciendo - 1));
            Musica.stop();
            Musica.release();
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
            Button_PlayPause.setBackgroundResource(R.drawable.pause);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
            Musica.start();
        } else {
            if (Aleatorio && !Repetir)
                CancionReproduciendo = new Random().nextInt(NumeroDeCanciones + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo - 1) < 0 ? (NumeroDeCanciones - 1) : (CancionReproduciendo - 1));
            Musica.stop();
            Musica.release();
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
            Button_PlayPause.setBackgroundResource(R.drawable.play);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.play);
        }
        BarraDeProgreso.setMax(Musica.getDuration() / 1000);
        Musica.setOnCompletionListener(this);
        reloj();
        setDatos();
    }

    private void SiguienteThreadButton() {
        SiguienteThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Button_Siguiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Siguiente();
                    }
                });
            }
        };
        SiguienteThread.start();
    }

    private void Siguiente() {
        if (Musica.isPlaying()) {
            Musica.stop();
            Musica.release();
            if (Aleatorio && !Repetir)
                CancionReproduciendo = new Random().nextInt(NumeroDeCanciones + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo + 1) % NumeroDeCanciones);
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
            Button_PlayPause.setBackgroundResource(R.drawable.pause);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
            Musica.start();
        } else {
            Musica.stop();
            Musica.release();
            if (Aleatorio && !Repetir)
                CancionReproduciendo = new Random().nextInt(NumeroDeCanciones + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo + 1) % NumeroDeCanciones);
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
            Button_PlayPause.setBackgroundResource(R.drawable.play);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.play);
        }
        BarraDeProgreso.setMax(Musica.getDuration() / 1000);
        Musica.setOnCompletionListener(this);
        reloj();
        setDatos();
    }

    private void PlayThreadButton() {
        PlayThread = new Thread() {
            @Override
            public void run() {
                super.run();
                Button_PlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayPause();
                    }
                });
            }
        };
        PlayThread.start();
    }

    private void PlayPause() {
        if (Musica.isPlaying()) {
            Button_PlayPause.setImageResource(R.drawable.play);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.play);
            Musica.pause();
        } else {
            Button_PlayPause.setImageResource(R.drawable.pause);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
            Musica.start();
        }
        BarraDeProgreso.setMax(Musica.getDuration() / 1000);
        reloj();
        Musica.setOnCompletionListener(this);
    }

    private void setDatos() {
        Label_Tiempo2.setText(win.tiempo(Musica.getDuration() / 1000));
        Label_Titulo.setText(Canciones.get(CancionReproduciendo).getTitulo());
        Label_Artista.setText(Canciones.get(CancionReproduciendo).getArtista());
        Label_Cancion.setText(getString(R.string.Cancion) + ": " + (CancionReproduciendo + 1) + "/" + NumeroDeCanciones);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(Canciones.get(CancionReproduciendo).getUrl());
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            Glide.with(getApplicationContext()).asBitmap().load(art).into(Album);
        } else Glide.with(getApplicationContext()).asBitmap().load(R.mipmap.musica).into(Album);
        Label_Titulo.setSelected(true);
        Label_Artista.setSelected(true);
        if (Musica.isPlaying())
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
        guardarInt(CANCION_REPRODUCIENDO, CancionReproduciendo);
    }

    private void recuperarDatos() {
        CancionReproduciendo = getIntent().getIntExtra(CANCION_REPRODUCIENDO, 0);
        Canciones = Archivos;
        NumeroDeCanciones = Canciones.size();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter(Canciones_Canciones));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
        Aleatorio = getConfiguracionBoolean(ALEATORIO, false);
        Repetir = getConfiguracionBoolean(REPETIR, false);
        CancionReproduciendo = getConfiguracionInt(CANCION_REPRODUCIENDO, 0);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, this.getString(R.string.Notificacion), NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    private void inicializarVistas() {
        Album = findViewById(R.id.ImageView_Album);
        Label_Titulo = findViewById(R.id.TextView_Titulo);
        Label_Artista = findViewById(R.id.TextView_Artista);
        Label_Cancion = findViewById(R.id.TextView_Cancion);
        Label_Tiempo1 = findViewById(R.id.TextView_Tiempo1);
        Label_Tiempo2 = findViewById(R.id.TextView_Tiempo2);
        BarraDeProgreso = findViewById(R.id.SeekBar_Barra);
        Button_PlayPause = findViewById(R.id.Button_PlayPause);
        Button_Aleatorio = findViewById(R.id.Button_Aleatorio);
        Button_Anterior = findViewById(R.id.Button_Anterior);
        Button_Siguiente = findViewById(R.id.Button_Siguiente);
        Button_Repetir = findViewById(R.id.Repetir);
        Label_Titulo.setSelected(true);
        Label_Artista.setSelected(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Siguiente();
        if (Musica != null) {
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Canciones.get(CancionReproduciendo).getUrl())))));
            Musica.start();
            Musica.setOnCompletionListener(this);
            CreateNotification.createNotification(Reproductor.this, Canciones.get(CancionReproduciendo), R.drawable.pause);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancion_reproduciendo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Detalles:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.Detalles) + ": " + Canciones.get(CancionReproduciendo).getTitulo())
                        .setMessage(this.getString(R.string.Titulo) + ": " + Canciones.get(CancionReproduciendo).getTitulo() + "\n\n" + this.getString(R.string.Artista) + ": " + Canciones.get(CancionReproduciendo).getArtista() + "\n\n" + this.getString(R.string.Url) + ": " + Canciones.get(CancionReproduciendo).getUrl())
                        .setPositiveButton(R.string.Vale, null).create().show();
                break;
            case R.id.BuscarLetra:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://www.google.com/search?q=" + Canciones.get(CancionReproduciendo).getTitulo() + " - " + Canciones.get(CancionReproduciendo).getArtista() + " letra").replace(" ", "+"))));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NotNull Intent intent) {
            switch (intent.getExtras().getString("actionname")) {
                case CreateNotification.ACTION_PREVIUOS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    onTrackPlay();
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    @Override
    public void onTrackPrevious() {
        Anterior();
    }

    @Override
    public void onTrackPlay() {
        PlayPause();
    }

    @Override
    public void onTrackNext() {
        Siguiente();
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
} // 562 -> 471