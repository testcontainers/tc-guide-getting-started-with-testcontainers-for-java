package com.testcontainers.demo.rating_module.domain;

import java.io.Serializable;

public class Rating implements Serializable {

    Integer ticketId;

    String comment;

    int stars;

    public Rating() {}

    public Rating(Integer ticketId, String comment, int stars) {
        this.ticketId = ticketId;
        this.comment = comment;
        this.stars = stars;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "ticketId=" + ticketId +
                ", comment='" + comment + '\'' +
                ", stars=" + stars +
                '}';
    }
}
