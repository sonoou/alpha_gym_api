package com.sonoou.alphagym.service;

import com.sonoou.alphagym.dto.CategoryRequest;
import com.sonoou.alphagym.dto.PagedResponse;
import com.sonoou.alphagym.dto.WorkoutRequest;
import com.sonoou.alphagym.entity.CategoryEntity;
import com.sonoou.alphagym.entity.WorkoutEntity;
import com.sonoou.alphagym.repository.CategoryRepository;
import com.sonoou.alphagym.repository.WorkoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public WorkoutService(WorkoutRepository workoutRepository,
                          CategoryRepository categoryRepository,
                          FileStorageService fileStorageService) {
        this.workoutRepository = workoutRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    public PagedResponse<WorkoutEntity> getWorkoutsPaged(
            String category,
            String difficulty,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        int pageNumber = Math.max(0, page);
        int pageSize = (size <= 0) ? 20 : Math.min(size, 100);

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String validSortBy = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
        Sort sort = Sort.by(direction, validSortBy);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<WorkoutEntity> workoutPage = workoutRepository.findWorkoutsWithFilters(category, difficulty, search, pageable);

        return new PagedResponse<>(
                workoutPage.getContent(),
                workoutPage.getNumber(),
                workoutPage.getSize(),
                workoutPage.getTotalElements(),
                workoutPage.getTotalPages(),
                workoutPage.isLast()
        );
    }

    public List<WorkoutEntity> getWorkouts(String category, String difficulty) {
        if (category != null && !category.isBlank() && difficulty != null && !difficulty.isBlank()) {
            return workoutRepository.findByCategoryAndDifficulty(category, difficulty);
        } else if (category != null && !category.isBlank()) {
            return workoutRepository.findByCategory(category);
        } else if (difficulty != null && !difficulty.isBlank()) {
            return workoutRepository.findByDifficulty(difficulty);
        }
        return workoutRepository.findAll();
    }

    public WorkoutEntity getWorkoutById(Long id) {
        return workoutRepository.findById(id).orElse(null);
    }

    public WorkoutEntity createWorkout(WorkoutRequest request) {
        WorkoutEntity workout = new WorkoutEntity();
        workout.setName(request.getName());
        workout.setDescription(request.getDescription());
        workout.setCategory(request.getCategory());
        workout.setDifficulty(request.getDifficulty());
        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setVideoUrl(request.getVideoUrl());
        workout.setImageUrl(request.getImageUrl());
        workout.setTargetMuscles(request.getTargetMuscles());
        return workoutRepository.save(workout);
    }

    public String uploadVideo(MultipartFile file) {
        return fileStorageService.storeFile(file);
    }

    public List<CategoryEntity> getCategories() {
        return categoryRepository.findAll();
    }

    public CategoryEntity createCategory(CategoryRequest request) {
        CategoryEntity category = new CategoryEntity(request.getName(), request.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
