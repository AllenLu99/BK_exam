package com.interview.exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExRateDto {
    @JsonProperty("Date")
    private String date;

    @JsonProperty("USD/NTD")
    private String usdNtd;

    @JsonProperty("RMB/NTD")
    private String rmbNtd;

    @JsonProperty("EUR/USD")
    private String eurUsd;

    @JsonProperty("USD/JPY")
    private String usdJpy;

    @JsonProperty("GBP/USD")
    private String gbpUsd;

    @JsonProperty("AUD/USD")
    private String audUsd;

    @JsonProperty("USD/HKD")
    private String usdHkd;

    @JsonProperty("USD/RMB")
    private String usdRmb;

    @JsonProperty("USD/ZAR")
    private String usdZar;

    @JsonProperty("NZD/USD")
    private String nzdUsd;
}
