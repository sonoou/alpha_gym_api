package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    Optional<ScheduleEntity> findByUserIdAndDayOfWeekIgnoreCase(Long userId, String dayOfWeek);
}
