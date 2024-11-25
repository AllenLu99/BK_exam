package com.interview.exam.repository;

import com.interview.exam.entity.CurrencyExRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CurrencyExRateRepository extends JpaRepository<CurrencyExRate, Long> {

    boolean existsByDate(LocalDateTime date);

    List<CurrencyExRate> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
