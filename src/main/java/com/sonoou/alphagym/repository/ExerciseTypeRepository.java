package com.sonoou.alphagym.repository;

import com.sonoou.alphagym.entity.ExerciseTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseTypeRepository extends JpaRepository<ExerciseTypeEntity, Long> {
    Optional<ExerciseTypeEntity> findByNameIgnoreCase(String name);
}
