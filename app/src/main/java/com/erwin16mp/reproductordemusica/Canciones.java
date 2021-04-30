package com.erwin16mp.reproductordemusica;

public class Canciones {
    private final String Url;
    private final String Titulo;
    private final String Artista;
    private final String Album;
    private final String Duracion;
    private final int Imagen;

    public Canciones(String url, String titulo, String artista, String album, String duracion, int imagen) {
        Url = url;
        Titulo = titulo;
        Artista = artista;
        Album = album;
        Duracion = duracion;
        Imagen = imagen;
    }

    public String getUrl() {
        return Url;
    }

    public String getTitulo() {
        return Titulo;
    }

    public String getArtista() {
        return Artista;
    }

    public String getAlbum() {
        return Album;
    }

    public String getDuracion() {
        return Duracion;
    }

    public int getImagen() {
        return Imagen;
    }
}