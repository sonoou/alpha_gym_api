package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.entity.ExerciseTypeEntity;
import com.sonoou.alphagym.repository.ExerciseTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ExerciseTypeController {

    private final ExerciseTypeRepository exerciseTypeRepository;

    public ExerciseTypeController(ExerciseTypeRepository exerciseTypeRepository) {
        this.exerciseTypeRepository = exerciseTypeRepository;
    }

    @GetMapping({"/api/exercise-types", "/api/types"})
    public ResponseEntity<List<ExerciseTypeEntity>> getAllExerciseTypes() {
        return ResponseEntity.ok(exerciseTypeRepository.findAll());
    }

    @GetMapping({"/api/exercise-types/{id}", "/api/types/{id}"})
    public ResponseEntity<ExerciseTypeEntity> getExerciseTypeById(@PathVariable Long id) {
        return exerciseTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
