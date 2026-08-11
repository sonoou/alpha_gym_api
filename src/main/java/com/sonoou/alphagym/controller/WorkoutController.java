package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.CategoryRequest;
import com.sonoou.alphagym.dto.WorkoutRequest;
import com.sonoou.alphagym.entity.CategoryEntity;
import com.sonoou.alphagym.entity.WorkoutEntity;
import com.sonoou.alphagym.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/api/workouts")
    public ResponseEntity<List<WorkoutEntity>> getWorkouts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        List<WorkoutEntity> workouts = workoutService.getWorkouts(category, difficulty);
        return ResponseEntity.ok(workouts);
    }

    @PostMapping("/api/workouts")
    public ResponseEntity<WorkoutEntity> createWorkout(@Valid @RequestBody WorkoutRequest request) {
        WorkoutEntity workout = workoutService.createWorkout(request);
        return ResponseEntity.ok(workout);
    }

    @PostMapping("/api/workouts/upload-video")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        String videoUrl = workoutService.uploadVideo(file);
        return ResponseEntity.ok(videoUrl);
    }

    @GetMapping("/api/categories")
    public ResponseEntity<List<CategoryEntity>> getCategories() {
        List<CategoryEntity> categories = workoutService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/api/categories")
    public ResponseEntity<CategoryEntity> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryEntity category = workoutService.createCategory(request);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/api/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        workoutService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
