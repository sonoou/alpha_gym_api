package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.DailyAnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyAnalyticsRepository extends JpaRepository<DailyAnalyticsEntity, Long> {
    Optional<DailyAnalyticsEntity> findByUserIdAndDate(Long userId, LocalDate date);
    List<DailyAnalyticsEntity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
