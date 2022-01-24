package com.example.springBootConverter.service;

import com.example.springBootConverter.map.CurrencyMapper;
import com.example.springBootConverter.map.RatesMapper;
import com.example.springBootConverter.model.Conversion;
import com.example.springBootConverter.model.Currency;
import com.example.springBootConverter.model.JsonData;
import com.example.springBootConverter.model.Rates;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableScheduling
public class CurrencyService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CurrencyService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public JSONObject getJson(String date) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL("https://nationalbank.kz/rss/get_rates.cfm?fdate=" + date + "&switch=russian");
            URLConnection urlConn = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return XML.toJSONObject(content.toString());
    }

    public void jsonParser(String date) {
        JSONObject jsonObject = getJson(date);
        JsonData jsonData = new JsonData();

        String stringDate = jsonObject.getJSONObject("rates").getString("date");
        JSONArray mCurrency = jsonObject.getJSONObject("rates").getJSONArray("item");

        Iterator<Object> arrIterator = mCurrency.iterator();
        jsonData.setListCurrency(new ArrayList<>());
        while (arrIterator.hasNext()) {
            JSONObject nObject = (JSONObject) arrIterator.next();
            jsonData.getListCurrency().add(
                    new Currency(nObject.get("title").toString(), Float.parseFloat(nObject.get("description").toString())));
        }


        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        stringDate = stringDate.replace(".", "-");
        try {
            jsonData.setDate(formatter.parse(stringDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Currency currency : jsonData.getListCurrency()) {
            jdbcTemplate.update("insert into currency values(?,?,?)", jsonData.getDate(),
                    currency.getName(), currency.getRate());
        }
    }

    @Scheduled(fixedRate = 28800000)
    public void fillBase() {
        jdbcTemplate.execute("TRUNCATE currency");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String[] days = new String[7];
        days[0] = sdf.format(date);

        for (int i = 1; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            date = cal.getTime();
            days[i] = sdf.format(date);
        }

        for (String d : days) {
            jsonParser(d);
        }
        System.out.println("База заполнена");
    }

    public Conversion convert(String stringDate, String from, String to, String sum) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        float firstCur = 0;
        float secCur = 0;
        float res;

        if (!from.equals("KZT")) {
            firstCur = Objects.requireNonNull(jdbcTemplate.query("select * from currency where date = ? AND name = ?",
                    new Object[]{date, from}, new CurrencyMapper()).stream().findAny().orElse(null)).getRate();
        }
        if (!to.equals("KZT")) {
            secCur = Objects.requireNonNull(jdbcTemplate.query("select * from currency where date = ? AND name = ?",
                    new Object[]{date, to}, new CurrencyMapper()).stream().findAny().orElse(null)).getRate();
        }

        if (from.equals("KZT")) {
            res = 1 / secCur;
        } else if (to.equals("KZT")) {
            res = firstCur;
        } else {
            res = firstCur / secCur;
        }

        if (sum != null && !sum.equals("")) {
            res *= Float.parseFloat(sum);
        }

        res = (float) (Math.ceil(res * Math.pow(10, 4)) / Math.pow(10, 4));

        return new Conversion(formatter.format(date), from, to, res);
    }

    public Map<String, List<Rates>> getRates(String stringFrom, String stringTo) {
        Map<String, List<Rates>> listMap = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date from = null, to =null;

        try {
            from = formatter.parse(stringFrom);
            to = formatter.parse(stringTo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        listMap.put(formatter.format(from), (jdbcTemplate.query("select name,rate from currency where date = ?",
                new Object[]{from}, new RatesMapper())));

        listMap.put(formatter.format(to), (jdbcTemplate.query("select name,rate from currency where date = ?",
                new Object[]{to}, new RatesMapper())));

        return listMap;
    }

}
