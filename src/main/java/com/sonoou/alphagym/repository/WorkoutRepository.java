package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.WorkoutEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<WorkoutEntity, Long> {
    List<WorkoutEntity> findByCategoryAndDifficulty(String category, String difficulty);
    List<WorkoutEntity> findByCategory(String category);
    List<WorkoutEntity> findByDifficulty(String difficulty);
    List<WorkoutEntity> findByIdIn(Collection<Long> ids);

    @Query("SELECT w FROM WorkoutEntity w WHERE " +
           "(:category IS NULL OR :category = '' OR LOWER(w.category) = LOWER(:category)) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR LOWER(w.difficulty) = LOWER(:difficulty)) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(w.targetMuscles) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<WorkoutEntity> findWorkoutsWithFilters(
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("search") String search,
            Pageable pageable);
}
