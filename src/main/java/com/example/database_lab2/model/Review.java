package com.example.database_lab2.model;

import java.util.Date;

public class Review {
    private int bookId;

    private int userId;

    private int stars;

    private Date reviewDate;

    private String reviewText = "";

    public Review(int bookId, int userId, int stars, Date reviewDate, String reviewText) {
        this.bookId = bookId;
        this.userId = userId;
        this.stars = stars;
        this.reviewDate = reviewDate;
        this.reviewText = reviewText;
    }

    public Review(int stars, Date reviewDate, String reviewText) { this(-1,-1,stars,reviewDate, reviewText);}

    public int getBookId() {
        return bookId;
    }

    public int getUserId() {
        return userId;
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
        return userId + ", " + reviewText + ", " + stars + ", " + reviewDate.toString();
    }
}
