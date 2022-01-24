package com.example.springBootConverter.model;

import lombok.Data;

import java.sql.Date;


@Data
public class Conversion {
    private String date;
    private String from;
    private String to;
    private Float sum;

    public Conversion(String date, String from, String to, Float sum) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.sum = sum;
    }
}
