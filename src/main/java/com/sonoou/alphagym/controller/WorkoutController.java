package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.CategoryRequest;
import com.sonoou.alphagym.dto.PagedResponse;
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

    /**
     * Paginated Workouts API with Category, Difficulty and Search filters.
     * Prevents client/app hanging by loading workouts in clean chunks (e.g. 20 per page).
     */
    @GetMapping("/api/workouts")
    public ResponseEntity<PagedResponse<WorkoutEntity>> getWorkouts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        PagedResponse<WorkoutEntity> pagedWorkouts = workoutService.getWorkoutsPaged(
                category, difficulty, search, page, size, sortBy, sortDirection
        );
        return ResponseEntity.ok(pagedWorkouts);
    }

    /**
     * Get single workout by ID
     */
    @GetMapping("/api/workouts/{id}")
    public ResponseEntity<WorkoutEntity> getWorkoutById(@PathVariable Long id) {
        WorkoutEntity workout = workoutService.getWorkoutById(id);
        if (workout == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(workout);
    }

    /**
     * Legacy / Full list endpoint (optional)
     */
    @GetMapping("/api/workouts/all")
    public ResponseEntity<List<WorkoutEntity>> getAllWorkouts(
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
