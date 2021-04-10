package com.erwin16mp.reproductordemusica;

public class Track {
    private String tittle;
    private String artist;
    private int image;

    public Track(String tittle, String artist, int image) {
        this.tittle = tittle;
        this.artist = artist;
        this.image = image;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
