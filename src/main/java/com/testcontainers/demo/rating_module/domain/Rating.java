package com.testcontainers.demo.rating_module.domain;

import java.io.Serializable;

public class Rating implements Serializable {

    Integer ticketId;

    String comment;

    int value;

    public Rating() {}

    public Rating(Integer ticketId, String comment, int value) {
        this.ticketId = ticketId;
        this.comment = comment;
        this.value = value;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public int getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "Rating{" +
                "ticketId=" + ticketId +
                ", comment='" + comment + '\'' +
                ", value=" + value +
                '}';
    }
}
