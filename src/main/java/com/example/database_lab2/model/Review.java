package com.example.database_lab2.model;

import java.util.Date;

public class Review {
    private String isbn;

    private String username;

    private int stars;

    private Date reviewDate;

    private String reviewText = "";


    public Review(String isbn, String username, int stars, Date reviewDate, String reviewText) {
        this.isbn = isbn;
        this.username = username;
        this.stars = stars;
        this.reviewDate = reviewDate;
        this.reviewText = reviewText;
    }

    public Review(int stars, Date reviewDate, String reviewText) { this("","", stars, reviewDate, reviewText);}

    public String getIsbn() {
        return isbn;
    }

    public String getUsername() {
        return username;
    }

    public int getStars() {
        return stars;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {this.reviewText = reviewText;}

    @Override
    public String toString() {
        return username + ", " + reviewText + ", " + stars + ", " + reviewDate.toString();
    }
}
