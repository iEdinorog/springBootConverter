package com.example.springBootConverter.map;

import com.example.springBootConverter.model.Currency;
import com.example.springBootConverter.model.Rates;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatesMapper implements RowMapper<Rates> {
    @Override
    public Rates mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rates rates = new Rates();
        rates.setName(rs.getString("name"));
        rates.setRate(rs.getFloat("rate"));
        return rates;
    }
}
