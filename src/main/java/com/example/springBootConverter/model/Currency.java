package com.example.springBootConverter.model;

import lombok.Data;

import java.sql.Date;


@Data
public class Currency {
    private Date date;
    private String name;
    private float rate;

    public Currency() {}

    public Currency(String name, float rate) {
        this.name = name;
        this.rate = rate;
    }
}
