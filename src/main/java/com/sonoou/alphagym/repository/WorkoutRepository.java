package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.WorkoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    List<WorkoutEntity> findByCategoryAndDifficulty(String category, String difficulty);
    List<WorkoutEntity> findByCategory(String category);
    List<WorkoutEntity> findByDifficulty(String difficulty);
    List<WorkoutEntity> findByIdIn(Collection<Long> ids);
}
