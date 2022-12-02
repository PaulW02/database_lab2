package com.example.database_lab1.model;

public class Genre {
    private int genreId;
    private String genreName;

    public Genre(int genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Genre(String genreName) {this (-1,genreName);}

    public int getGenreId() {return genreId;}

    public String getGenreName() {return genreName;}

    @Override
    public String toString() {
        return genreName + ", ";
    }
}
