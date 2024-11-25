package com.interview.exam.controller;

import com.interview.exam.entity.req.ForexReq;
import com.interview.exam.entity.res.ApiResponse;
import com.interview.exam.entity.res.ForexRes;
import com.interview.exam.service.CurrencyExRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyExRateController {

    @Autowired
    private CurrencyExRateService currencyExRateService;

    @GetMapping("/takeCurrency")
    public ResponseEntity<ApiResponse<Object>> fetchAndSaveCurrencyExRates() {
        currencyExRateService.fetchAndSaveCurrencyExRates();
        return new ResponseEntity<>(new ApiResponse<>("0000", "成功", null), HttpStatus.OK);
    }

    @PostMapping("/forex")
    public ResponseEntity<ApiResponse<List<ForexRes>>> getForexRates(@RequestBody ForexReq request) {
        ApiResponse<List<ForexRes>> apiResponse = currencyExRateService.getForexRates(request);
        if (apiResponse.getError().getCode().equals("E001")) {
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
