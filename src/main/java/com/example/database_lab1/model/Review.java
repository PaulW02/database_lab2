package com.example.database_lab1.model;

import java.util.Date;

public class Review {
    private int bookId;

    private int userId;

    private int stars;

    private Date reviewDate;

    private String reviewText = "";

    public Review(int bookId, int userId, int stars, Date reviewDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.stars = stars;
        this.reviewDate = reviewDate;
    }

    public Review(int stars, Date reviewDate) { this(-1,-1,stars,reviewDate);}

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
        return stars + ", " + reviewDate.toString();
    }
}
