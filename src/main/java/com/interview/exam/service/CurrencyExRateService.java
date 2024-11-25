package com.interview.exam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.exam.dto.CurrencyExRateDto;
import com.interview.exam.entity.CurrencyExRate;
import com.interview.exam.entity.req.ForexReq;
import com.interview.exam.entity.res.ApiResponse;
import com.interview.exam.entity.res.ForexRes;
import com.interview.exam.repository.CurrencyExRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyExRateService {

    @Autowired
    private CurrencyExRateRepository currencyExRateRepository;

    private final RestTemplate restTemplate;

    public CurrencyExRateService(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    /**
     * 批次排程
     */
    @Scheduled(cron = "0 0 18 * * ?")   // 每日 18:00
    public void fetchAndSaveCurrencyExRates() {
        List<CurrencyExRateDto> forexDataDtoList = this.getForexData();
        if (forexDataDtoList != null) {
            forexDataDtoList.forEach(forexDataDto -> {
                LocalDateTime dateTime = parseStringToLocalDateTime(forexDataDto.getDate());
                if (!currencyExRateRepository.existsByDate(dateTime)) {
                    currencyExRateRepository.save(CurrencyExRate.builder()
                                                                .date(dateTime)
                                                                .usdNtd(new BigDecimal(forexDataDto.getUsdNtd()))
                                                                .rmbNtd(new BigDecimal(forexDataDto.getRmbNtd()))
                                                                .eurUsd(new BigDecimal(forexDataDto.getEurUsd()))
                                                                .usdJpy(new BigDecimal(forexDataDto.getUsdJpy()))
                                                                .gbpUsd(new BigDecimal(forexDataDto.getGbpUsd()))
                                                                .audUsd(new BigDecimal(forexDataDto.getAudUsd()))
                                                                .usdHkd(new BigDecimal(forexDataDto.getUsdHkd()))
                                                                .usdRmb(new BigDecimal(forexDataDto.getUsdRmb()))
                                                                .usdZar(new BigDecimal(forexDataDto.getUsdZar()))
                                                                .nzdUsd(new BigDecimal(forexDataDto.getNzdUsd()))
                                                                .build()
                    );
                }
            });
        }
    }

    /**
     * 取得外匯資料
     */
    public List<CurrencyExRateDto> getForexData() {
        // 拉取 API 回應的 JSON 資料
        String API_URL = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";
        String response = restTemplate.getForObject(API_URL, String.class);
        // 使用 ObjectMapper 來將 JSON 字串解析為 List<ForexDataDto>
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(response, new TypeReference<>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字串轉換 LocalDateTime
     */
    public LocalDateTime parseStringToLocalDateTime(String dateString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(dateString, dateFormatter);
        // 將 LocalDate 轉換為 LocalDateTime 並補上時間部分，默認時間設為 00:00:00
        return date.atStartOfDay();
    }

    /**
     * 取出日期區間內美元/台幣的歷史資料
     */
    public ApiResponse<List<ForexRes>> getForexRates(ForexReq request) {
        ArrayList<ForexRes> list = new ArrayList<>();
        // 使用 DateTimeFormatter 將日期字串轉換為 LocalDate 類型
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        if (startDate.isBefore(oneYearAgo) || endDate.isAfter(today.minusDays(1))) {
            return new ApiResponse<>("E001", "日期區間不符", null);
        }

        // 將 LocalDate 轉換為 LocalDateTime（時間設為 00:00:00）
        LocalDateTime startDateTime = startDate.atStartOfDay();
        // 設定為結束日期的最後一刻
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);
        List<CurrencyExRate> currencyExRateList =
                currencyExRateRepository.findByDateBetween(startDateTime, endDateTime);
        currencyExRateList.forEach(currencyExRate -> {
            DateTimeFormatter formatterStr = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = currencyExRate.getDate().format(formatterStr);
            ForexRes forexRes = ForexRes.builder()
                                        .date(formattedDate)
                                        .usd(currencyExRate.getUsdNtd().toString())
                                        .build();
            list.add(forexRes);
        });
        return new ApiResponse<>("0000", "成功", list);
    }
}
