package com.example.springBootConverter.controller;

import com.example.springBootConverter.model.Conversion;
import com.example.springBootConverter.model.Currency;
import com.example.springBootConverter.model.Rates;
import com.example.springBootConverter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/convert")
    public Conversion convert(@RequestParam(value = "date") String date, @RequestParam(value = "from") String from,
                              @RequestParam(value = "to") String to,
                              @RequestParam(value = "sum", required = false) String sum) {
        return currencyService.convert(date, from, to, sum);
    }

    @GetMapping("/rates")
    public Map<String, List<Rates>> rates(@RequestParam(value = "from") String from,
                                          @RequestParam(value = "to") String to) {
        return currencyService.getRates(from, to);
    }

}
