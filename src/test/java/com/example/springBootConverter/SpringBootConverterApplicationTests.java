package com.example.springBootConverter;

import com.example.springBootConverter.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
class SpringBootConverterApplicationTests {

	private CurrencyService currencyService;
	private JdbcTemplate jdbcTemplate;
	@Autowired
	public SpringBootConverterApplicationTests(CurrencyService currencyService, JdbcTemplate jdbcTemplate) {
		this.currencyService = currencyService;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Test
	void convert(){
		String date18 = "2022-01-18";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(date18);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		jdbcTemplate.update("insert into currency values(?,?,?)", date,"EUR", 496.46);
		jdbcTemplate.update("insert into currency values(?,?,?)", date,"USD", 434.58);

		currencyService.convert("2022-01-18","EUR","USD","");
	}

	@Test
	void getRates(){
		String date19 = "2022-01-19";
		String date16 = "2022-01-16";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date fDate = null, sDate = null;

		try {
			fDate = formatter.parse(date19);
			sDate = formatter.parse(date16);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		jdbcTemplate.update("insert into currency values(?,?,?)", fDate,"EUR", 494.45);
		jdbcTemplate.update("insert into currency values(?,?,?)", fDate,"USD", 434.07);

		jdbcTemplate.update("insert into currency values(?,?,?)", sDate,"USD", 434.84);
		jdbcTemplate.update("insert into currency values(?,?,?)", sDate,"EUR", 498.2);
		jdbcTemplate.update("insert into currency values(?,?,?)", sDate,"AMD", 9.07);

		currencyService.getRates("2022-01-16", "2022-01-19");
	}
}
