package com.erwin16mp.reproductordemusica;

public class Canciones {
    private final String Titulo;
    private final String Artista;
    private final String Url;
    private final String Duracion;

    public Canciones(String titulo, String artista, String url, String duracion) {
        Titulo = titulo;
        Artista = artista;
        Url = url;
        Duracion = duracion;
    }

    public String getTitulo() {
        return Titulo;
    }

    public String getArtista() {
        return Artista;
    }

    public String getUrl() {
        return Url;
    }

    public String getDuracion() {
        return Duracion;
    }
}