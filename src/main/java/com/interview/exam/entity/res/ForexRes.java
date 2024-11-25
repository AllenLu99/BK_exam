package com.interview.exam.entity.res;

import com.interview.exam.entity.CurrencyExRate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForexRes {

    private String date;
    private String usd;
}
