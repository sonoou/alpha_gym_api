package com.sonoou.alphagym.service;

import com.sonoou.alphagym.dto.*;
import com.sonoou.alphagym.entity.ScheduleEntity;
import com.sonoou.alphagym.entity.UserEntity;
import com.sonoou.alphagym.entity.WorkoutEntity;
import com.sonoou.alphagym.repository.ScheduleRepository;
import com.sonoou.alphagym.repository.UserRepository;
import com.sonoou.alphagym.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final ScheduleRepository scheduleRepository;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository,
                       WorkoutRepository workoutRepository,
                       ScheduleRepository scheduleRepository,
                       FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.scheduleRepository = scheduleRepository;
        this.fileStorageService = fileStorageService;
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public UserProfileResponse getProfile(String email) {
        UserEntity user = getUserByEmail(email);
        return mapToProfileResponse(user);
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        UserEntity user = getUserByEmail(email);
        if (request.getName() != null) user.setName(request.getName());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getWeight() != null) user.setWeight(request.getWeight());
        if (request.getHeight() != null) user.setHeight(request.getHeight());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getFitnessGoal() != null) user.setFitnessGoal(request.getFitnessGoal());
        if (request.getOnboardingCompleted() != null) user.setOnboardingCompleted(request.getOnboardingCompleted());

        UserEntity updated = userRepository.save(user);
        return mapToProfileResponse(updated);
    }

    public UserProfileResponse updatePhoto(String email, MultipartFile file) {
        UserEntity user = getUserByEmail(email);
        String photoUrl = fileStorageService.storeFile(file);
        user.setProfilePhotoUrl(photoUrl);
        UserEntity updated = userRepository.save(user);
        return mapToProfileResponse(updated);
    }

    public UserProfileResponse setOnboardingStatus(String email, boolean completed) {
        UserEntity user = getUserByEmail(email);
        user.setOnboardingCompleted(completed);
        UserEntity updated = userRepository.save(user);
        return mapToProfileResponse(updated);
    }

    public List<WorkoutEntity> getRoutine(String email) {
        UserEntity user = getUserByEmail(email);
        Set<Long> workoutIds = user.getBookmarkedWorkoutIds();
        if (workoutIds.isEmpty()) {
            return new ArrayList<>();
        }
        return workoutRepository.findByIdIn(workoutIds);
    }

    public List<WorkoutEntity> updateRoutine(String email, RoutineActionRequest request) {
        UserEntity user = getUserByEmail(email);
        Set<Long> bookmarked = user.getBookmarkedWorkoutIds();
        if ("ADD".equalsIgnoreCase(request.getAction())) {
            bookmarked.add(request.getWorkoutId());
        } else if ("REMOVE".equalsIgnoreCase(request.getAction())) {
            bookmarked.remove(request.getWorkoutId());
        }
        userRepository.save(user);
        return getRoutine(email);
    }

    public StreakResponse getStreaks(String email) {
        UserEntity user = getUserByEmail(email);
        return new StreakResponse(user.getCurrentStreakDays(), user.getTotalWorkoutsCompleted());
    }

    public StreakResponse completeStreak(String email) {
        UserEntity user = getUserByEmail(email);
        user.setCurrentStreakDays(user.getCurrentStreakDays() + 1);
        user.setTotalWorkoutsCompleted(user.getTotalWorkoutsCompleted() + 1);
        UserEntity saved = userRepository.save(user);
        return new StreakResponse(saved.getCurrentStreakDays(), saved.getTotalWorkoutsCompleted());
    }

    public StreakResponse updateStreaks(String email, UpdateStreakRequest request) {
        UserEntity user = getUserByEmail(email);
        if (request.getStreakDays() != null) {
            user.setCurrentStreakDays(request.getStreakDays());
        }
        if (request.getTotalWorkoutsCompleted() != null) {
            user.setTotalWorkoutsCompleted(request.getTotalWorkoutsCompleted());
        }
        UserEntity saved = userRepository.save(user);
        return new StreakResponse(saved.getCurrentStreakDays(), saved.getTotalWorkoutsCompleted());
    }

    public ScheduleDto getSchedule(String email, String dayOfWeek) {
        UserEntity user = getUserByEmail(email);
        ScheduleEntity schedule = scheduleRepository.findByUserIdAndDayOfWeekIgnoreCase(user.getId(), dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return new ScheduleDto(dayOfWeek, new ArrayList<>(), "", "");
        }
        return new ScheduleDto(schedule.getDayOfWeek(), schedule.getWorkoutIds(), schedule.getFocusArea(), schedule.getNotes());
    }

    public ScheduleDto saveSchedule(String email, String dayOfWeek, ScheduleDto dto) {
        UserEntity user = getUserByEmail(email);
        ScheduleEntity schedule = scheduleRepository.findByUserIdAndDayOfWeekIgnoreCase(user.getId(), dayOfWeek)
                .orElseGet(() -> {
                    ScheduleEntity newSchedule = new ScheduleEntity();
                    newSchedule.setUser(user);
                    newSchedule.setDayOfWeek(dayOfWeek);
                    return newSchedule;
                });

        if (dto.getWorkoutIds() != null) schedule.setWorkoutIds(dto.getWorkoutIds());
        if (dto.getFocusArea() != null) schedule.setFocusArea(dto.getFocusArea());
        if (dto.getNotes() != null) schedule.setNotes(dto.getNotes());

        ScheduleEntity saved = scheduleRepository.save(schedule);
        return new ScheduleDto(saved.getDayOfWeek(), saved.getWorkoutIds(), saved.getFocusArea(), saved.getNotes());
    }

    public UserMembershipResponse getUserMembership(String email) {
        UserEntity user = getUserByEmail(email);
        boolean isActive = user.getMembershipActive();
        String planName = user.getActivePlanName();
        String expiry = user.getPlanExpiryDate() != null ? user.getPlanExpiryDate().toString() : null;
        
        long daysRemaining = 0;
        if (user.getPlanExpiryDate() != null) {
            daysRemaining = java.time.Duration.between(java.time.LocalDateTime.now(), user.getPlanExpiryDate()).toDays();
            if (daysRemaining < 0) daysRemaining = 0;
        }

        return new UserMembershipResponse(isActive, planName, expiry, daysRemaining);
    }

    private UserProfileResponse mapToProfileResponse(UserEntity user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setAge(user.getAge());
        response.setWeight(user.getWeight());
        response.setHeight(user.getHeight());
        response.setGender(user.getGender());
        response.setFitnessGoal(user.getFitnessGoal());
        response.setOnboardingCompleted(user.getOnboardingCompleted());
        response.setProfilePhotoUrl(user.getProfilePhotoUrl());
        response.setCurrentStreakDays(user.getCurrentStreakDays());
        response.setTotalWorkoutsCompleted(user.getTotalWorkoutsCompleted());
        response.setIsMembershipActive(user.getMembershipActive());
        response.setActivePlanName(user.getActivePlanName());
        response.setPlanExpiryDate(user.getPlanExpiryDate() != null ? user.getPlanExpiryDate().toString() : null);
        return response;
    }
}
