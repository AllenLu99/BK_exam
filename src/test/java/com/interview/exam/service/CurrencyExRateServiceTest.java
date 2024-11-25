package com.interview.exam.service;

import com.interview.exam.dto.CurrencyExRateDto;
import com.interview.exam.entity.CurrencyExRate;
import com.interview.exam.entity.req.ForexReq;
import com.interview.exam.entity.res.ApiResponse;
import com.interview.exam.entity.res.ForexRes;
import com.interview.exam.repository.CurrencyExRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CurrencyExRateServiceTest {

    @Autowired
    private CurrencyExRateService currencyExRateService;

    @MockBean
    private CurrencyExRateRepository currencyExRateRepository;

    @MockBean
    private RestTemplate restTemplate;

    private String mockApiResponse;

    private ForexReq forexReq;

    private CurrencyExRate currencyExRate;


    @BeforeEach
    public void setUp() {
        // 模擬 API 的回應
        mockApiResponse = "[{\"Date\":\"20231120\",\"USD/NTD\":\"30.187\",\"RMB/NTD\":\"4.5\",\"EUR/USD\":\"1.1\",\"USD/JPY\":\"110.5\",\"GBP/USD\":\"1.3\",\"AUD/USD\":\"0.7\",\"USD/HKD\":\"7.8\",\"USD/RMB\":\"7.3\",\"USD/ZAR\":\"18.2\",\"NZD/USD\":\"0.64\"}]";
        forexReq = ForexReq.builder().startDate("2024/11/01").endDate("2024/11/01").build();
        currencyExRate = CurrencyExRate.builder().date(LocalDateTime.of(2024, 11, 1, 0, 0, 0, 0)).usdNtd(new BigDecimal("28.5")).build();
    }


    @Test
    public void testFetchAndSaveCurrencyExchangeRates() {
        // 模擬 RestTemplate 回傳的 JSON 資料
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockApiResponse);
        // 模擬資料庫的查詢結果，假設資料庫中還沒有這個日期的資料
        when(currencyExRateRepository.existsByDate(any(LocalDateTime.class)))
                .thenReturn(false); // 模擬這個日期的外匯資料尚未存在
        // 執行 fetchAndSaveCurrencyExRates 方法
        currencyExRateService.fetchAndSaveCurrencyExRates();
        // 驗證 restTemplate.getForObject 是否正確被呼叫過
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
        // 驗證 currencyExRateRepository.save 是否被呼叫過，表示資料應該被儲存
        verify(currencyExRateRepository, times(1)).save(any(CurrencyExRate.class));
        // 進一步驗證儲存的內容
        ArgumentCaptor<CurrencyExRate> argumentCaptor = ArgumentCaptor.forClass(CurrencyExRate.class);
        verify(currencyExRateRepository, times(1)).save(argumentCaptor.capture());
        CurrencyExRate savedCurrencyExRate = argumentCaptor.getValue();
        assert(savedCurrencyExRate.getUsdNtd()).equals(new BigDecimal("30.187"));
    }

    @Test
    public void testGetForexData() throws Exception {
        // 模擬 API 的回應
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockApiResponse);
        // 測試 getForexData 方法
        List<CurrencyExRateDto> forexDataDtoList = currencyExRateService.getForexData();
        // 驗證返回的 List 是否包含正確的資料
        assert(forexDataDtoList != null && forexDataDtoList.size() == 1);
        assert(forexDataDtoList.get(0).getUsdNtd().equals("30.187"));
    }

    @Test
    public void testParseStringToLocalDateTime() {
        String dateString = "20231120";
        LocalDateTime dateTime = currencyExRateService.parseStringToLocalDateTime(dateString);
        // 驗證日期是否正確解析
        assert(dateTime.getYear() == 2023);
        assert(dateTime.getMonthValue() == 11);
        assert(dateTime.getDayOfMonth() == 20);
    }

    @Test
    void testGetForexRates_Success() {
        List<CurrencyExRate> mockExRateList = Collections.singletonList(currencyExRate);
        // 模擬資料庫返回的結果
        when(currencyExRateRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockExRateList);
        ApiResponse<List<ForexRes>> response = currencyExRateService.getForexRates(forexReq);
        // 檢查返回代碼是否為 "0000"
        assertEquals("0000", response.getError().getCode());
        // 檢查返回訊息是否為 "成功"
        assertEquals("成功", response.getError().getMessage());
        // 確保返回的資料不為 null
        assertNotNull(response.getData());
        // 檢查返回資料的大小
        assertEquals(1, response.getData().size());

        ForexRes forexRes = response.getData().get(0);
        // 檢查日期格式化結果
        assertEquals("20241101", forexRes.getDate());
        // 檢查匯率數據是否正確
        assertEquals("28.5", forexRes.getUsd());
    }

    @Test
    void testGetForexRates_InvalidDateRange() {
        // 修改請求的日期範圍，使其無效
        forexReq.setStartDate("2025/01/01");
        forexReq.setEndDate("2025/01/02");
        ApiResponse<List<ForexRes>> response = currencyExRateService.getForexRates(forexReq);
        // 檢查錯誤代碼
        assertEquals("E001", response.getError().getCode());
        // 檢查錯誤訊息
        assertEquals("日期區間不符", response.getError().getMessage());
        // 資料應該為 null
        assertNull(response.getData());
    }

    @Test
    void testGetForexRates_EmptyResponse() {
        // 模擬空的資料庫返回
        when(currencyExRateRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        ApiResponse<List<ForexRes>> response = currencyExRateService.getForexRates(forexReq);
        // 應該返回成功代碼
        assertEquals("0000", response.getError().getCode());
        // 應該返回成功訊息
        assertEquals("成功", response.getError().getMessage());
        // 返回的資料不為 null
        assertNotNull(response.getData());
        // 資料應該為空
        assertTrue(response.getData().isEmpty());
    }
}
