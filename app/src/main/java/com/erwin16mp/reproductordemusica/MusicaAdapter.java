package com.erwin16mp.reproductordemusica;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Canciones> canciones;

    MusicaAdapter(Context context, ArrayList<Canciones> canciones) {
        this.canciones = canciones;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cancion, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Label_Titulo.setText(canciones.get(position).getTitulo());
        holder.Label_Artista.setText(canciones.get(position).getArtista());
        holder.Label_Duracion.setText(canciones.get(position).getDuracion());
        byte[] image = getImagenAlbum(canciones.get(position).getUrl());
        if (image!=null)
            Glide.with(context).asBitmap().load(image).into(holder.Album);
    }

    @Override
    public int getItemCount() {
        return canciones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Label_Titulo, Label_Artista, Label_Duracion;
        ImageView Album;

        public MyViewHolder(@NonNull View view) {
            super(view);
            Label_Titulo = view.findViewById(R.id.TextView_TituloLista);
            Label_Artista = view.findViewById(R.id.TextView_ArtistaLista);
            Label_Duracion = view.findViewById(R.id.TextView_Duracion);
            Album = view.findViewById(R.id.ImageView_Imagen);
        }
    }

    private byte[] getImagenAlbum(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
