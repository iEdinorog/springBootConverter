package com.example.springBootConverter.map;

import com.example.springBootConverter.model.Currency;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyMapper implements RowMapper<Currency> {
    @Override
    public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
        Currency currency = new Currency();
        currency.setDate(rs.getDate("date"));
        currency.setName(rs.getString("name"));
        currency.setRate(rs.getFloat("rate"));
        return currency;
    }
}
