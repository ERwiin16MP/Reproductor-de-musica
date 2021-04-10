package com.erwin16mp.reproductordemusica;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class Win extends AppCompatActivity {
    public static final String ActionBarColor = "#0288D1";
    public static final String StatusBarColor = "#01579B";
    public static final String NavigationBar = "#0288D1";

    public void obtenerImagen(Context context, ImageView ImageView_Album, MediaMetadataRetriever[] retriever, int position) {
        byte[] art = retriever[position].getEmbeddedPicture();
        if (art != null) {
            Glide.with(context).asBitmap().load(art).into(ImageView_Album);
        }
    }

    public String tiempo(int i) {
        String Segundos = String.valueOf(i % 60), Minutos = String.valueOf(i / 60), TotalOut = Minutos + ":" + Segundos, TotalNew = Minutos + ":0" + Segundos;
        if (Segundos.length() == 1)
            return TotalNew;
        else return TotalOut;
    }
}