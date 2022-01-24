package com.example.springBootConverter.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JsonData {
    private Date date;
    private List<Currency> listCurrency;
}

