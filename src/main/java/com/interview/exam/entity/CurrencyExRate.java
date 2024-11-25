package com.interview.exam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "currency_ex_rate")
public class CurrencyExRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 主鍵 ID

    @Column(name = "date")
    private LocalDateTime date;  // 匯率日期

    @Column(name = "usd_ntd")
    private BigDecimal usdNtd;  // 美元對台幣匯率

    @Column(name = "rmb_ntd")
    private BigDecimal rmbNtd;  // 人民幣對台幣匯率

    @Column(name = "eur_usd")
    private BigDecimal eurUsd;  // 歐元對美元匯率

    @Column(name = "usd_jpy")
    private BigDecimal usdJpy;  // 美元對日圓匯率

    @Column(name = "gbp_usd")
    private BigDecimal gbpUsd;  // 英鎊對美元匯率

    @Column(name = "aud_usd")
    private BigDecimal audUsd;  // 澳元對美元匯率

    @Column(name = "usd_hkd")
    private BigDecimal usdHkd;  // 美元對港幣匯率

    @Column(name = "usd_rmb")
    private BigDecimal usdRmb;  // 美元對人民幣匯率

    @Column(name = "usd_zar")
    private BigDecimal usdZar;  // 美元對南非幣匯率

    @Column(name = "nzd_usd")
    private BigDecimal nzdUsd;  // 新西蘭元對美元匯率

    @Column(name = "create_time")
    private LocalDateTime createdTime;  // 資料創建時間

    @PrePersist
    public void prePersist() {
        if (createdTime == null) {
            createdTime = LocalDateTime.now();  // 設置為當前時間
        }
    }
}
