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

public class Reproductor extends AppCompatActivity implements MediaPlayer.OnCompletionListener, Playable {

    private static final String TITULOS = "Titulos";
    private static final String ARTISTAS = "Artistas";
    private static final String URLS = "Urls";
    private static final String DURACIONES = "Duraciones";
    private static final String CANCION_REPRODUCIENDO = "CancionReproduciendo";
    private static final String SALIDA = "Salida";
    private static final String DIRECTO = "Directo";
    private static final String ALEATORIO = "Aleatorio";
    private static final String REPETIR = "Repetir";
    private static final String REPRODUCIENDO = "Reproduciendo";
    private static final String canciones_canciones = "canciones_canciones";
    private static final String TEMA = "Tema";
    private final Win win = new Win();
    private static int NumeroDeTitulos;
    private static String[] Titulos, Artistas, Urls, Duraciones;
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
    private static List<Canciones> canciones;
    private int TiempoActual;

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
                Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
                Musica.start();
                Button_PlayPause.setImageResource(R.drawable.pause);
                CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
                break;
            case REPRODUCIENDO:
                if (Musica.isPlaying()) {
                    Button_PlayPause.setImageResource(R.drawable.pause);
                    Musica.setOnCompletionListener(this);
                    BarraDeProgreso.setMax(Musica.getDuration() / 1000);
                    CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
                } else {
                    Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
                    CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.play);
                }
                break;
            case ALEATORIO:
                CancionReproduciendo = getIntent().getExtras().getInt(CANCION_REPRODUCIENDO, 0);
                if (Musica != null) {
                    Musica.stop();
                    Musica.release();
                }
                Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
                Musica.start();
                Button_PlayPause.setImageResource(R.drawable.pause);
                CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
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
                CancionReproduciendo = new Random().nextInt(NumeroDeTitulos + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo - 1) < 0 ? (NumeroDeTitulos - 1) : (CancionReproduciendo - 1));
            Musica.stop();
            Musica.release();
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
            Button_PlayPause.setBackgroundResource(R.drawable.pause);
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
            Musica.start();
        } else {
            if (Aleatorio && !Repetir)
                CancionReproduciendo = new Random().nextInt(NumeroDeTitulos + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo - 1) < 0 ? (NumeroDeTitulos - 1) : (CancionReproduciendo - 1));
            Musica.stop();
            Musica.release();
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
            Button_PlayPause.setBackgroundResource(R.drawable.play);
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.play);
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
                CancionReproduciendo = new Random().nextInt(NumeroDeTitulos + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo + 1) % NumeroDeTitulos);
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
            Button_PlayPause.setBackgroundResource(R.drawable.pause);
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
            Musica.start();
        } else {
            Musica.stop();
            Musica.release();
            if (Aleatorio && !Repetir)
                CancionReproduciendo = new Random().nextInt(NumeroDeTitulos + 1);
            else if (!Aleatorio && !Repetir)
                CancionReproduciendo = ((CancionReproduciendo + 1) % NumeroDeTitulos);
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
            Button_PlayPause.setBackgroundResource(R.drawable.play);
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.play);
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
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.play);
            Musica.pause();
        } else {
            Button_PlayPause.setImageResource(R.drawable.pause);
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
            Musica.start();
        }
        BarraDeProgreso.setMax(Musica.getDuration() / 1000);
        reloj();
        Musica.setOnCompletionListener(this);
    }

    private void setDatos() {
        Label_Tiempo2.setText(win.tiempo(Musica.getDuration() / 1000));
        Label_Titulo.setText(Titulos[CancionReproduciendo]);
        Label_Artista.setText(Artistas[CancionReproduciendo]);
        Label_Cancion.setText(getString(R.string.Cancion) + ": " + (CancionReproduciendo + 1) + "/" + NumeroDeTitulos);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(Urls[CancionReproduciendo]);
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            Glide.with(getApplicationContext()).asBitmap().load(art).into(Album);
        }
        Label_Titulo.setSelected(true);
        Label_Artista.setSelected(true);
        if (Musica.isPlaying())
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
        guardarInt(CANCION_REPRODUCIENDO, CancionReproduciendo);
    }

    private void recuperarDatos() {
        Titulos = getIntent().getExtras().getStringArray(TITULOS);
        Artistas = getIntent().getExtras().getStringArray(ARTISTAS);
        Urls = getIntent().getExtras().getStringArray(URLS);
        Duraciones = getIntent().getExtras().getStringArray(DURACIONES);
        NumeroDeTitulos = Urls.length;
        canciones = new ArrayList<>();
        for (int i = 0; i < NumeroDeTitulos; i++)
            canciones.add(new Canciones(Urls[CancionReproduciendo], Titulos[CancionReproduciendo], Artistas[CancionReproduciendo], "", "", R.drawable.reproduciendo));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter(canciones_canciones));
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
            Musica = MediaPlayer.create(Reproductor.this, Uri.fromFile(new File(String.valueOf(Uri.parse(Urls[CancionReproduciendo])))));
            Musica.start();
            Musica.setOnCompletionListener(this);
            CreateNotification.createNotification(Reproductor.this, canciones.get(CancionReproduciendo), R.drawable.pause);
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
                builder.setTitle(getString(R.string.Detalles) + ": " + Titulos[CancionReproduciendo])
                        .setMessage(this.getString(R.string.Titulo) + ": " + Titulos[CancionReproduciendo] + "\n\n" + this.getString(R.string.Artista) + ": " + Artistas[CancionReproduciendo] + "\n\n" + this.getString(R.string.Url) + ": " + Urls[CancionReproduciendo])
                        .setPositiveButton(R.string.Vale, null).create().show();
                break;
            case R.id.BuscarLetra:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://www.google.com/search?q=" + Titulos[CancionReproduciendo] + " - " + Artistas[CancionReproduciendo] + " letra").replace(" ", "+"))));
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (preferences.getString(TEMA, "Rojo")) {
            case "Rojo":
                setTheme(R.style.Rojo);
                break;
            case "Morado":
                setTheme(R.style.Morado);
                break;
            case "Azul":
                break;
            case "Verde":
                break;
            case "Amarillo":
                break;
            case "Naranja":
                break;
            case "Café":
                break;
        }
    }
} // 562 -> 471