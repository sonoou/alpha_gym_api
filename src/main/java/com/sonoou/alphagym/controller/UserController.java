package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.dto.*;
import com.sonoou.alphagym.entity.WorkoutEntity;
import com.sonoou.alphagym.service.AnalyticsService;
import com.sonoou.alphagym.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AnalyticsService analyticsService;

    public UserController(UserService userService, AnalyticsService analyticsService) {
        this.userService = userService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserProfileResponse profile = userService.getProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(Authentication authentication,
                                                             @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile/photo")
    public ResponseEntity<UserProfileResponse> updateProfilePhoto(Authentication authentication,
                                                                   @RequestParam("file") MultipartFile file) {
        UserProfileResponse profile = userService.updatePhoto(authentication.getName(), file);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/onboarding")
    public ResponseEntity<UserProfileResponse> setOnboardingStatus(Authentication authentication,
                                                                    @RequestParam(value = "completed", defaultValue = "true") Boolean completed) {
        UserProfileResponse profile = userService.setOnboardingStatus(authentication.getName(), completed);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/routine")
    public ResponseEntity<List<WorkoutEntity>> getRoutine(Authentication authentication) {
        List<WorkoutEntity> routine = userService.getRoutine(authentication.getName());
        return ResponseEntity.ok(routine);
    }

    @PostMapping("/routine")
    public ResponseEntity<List<WorkoutEntity>> updateRoutine(Authentication authentication,
                                                             @RequestBody RoutineActionRequest request) {
        List<WorkoutEntity> updatedRoutine = userService.updateRoutine(authentication.getName(), request);
        return ResponseEntity.ok(updatedRoutine);
    }

    @GetMapping("/streaks")
    public ResponseEntity<StreakResponse> getStreaks(Authentication authentication) {
        StreakResponse streaks = userService.getStreaks(authentication.getName());
        return ResponseEntity.ok(streaks);
    }

    @PostMapping("/streaks/complete")
    public ResponseEntity<StreakResponse> completeStreak(Authentication authentication) {
        StreakResponse streaks = userService.completeStreak(authentication.getName());
        return ResponseEntity.ok(streaks);
    }

    @PutMapping("/streaks")
    public ResponseEntity<StreakResponse> updateStreaks(Authentication authentication,
                                                        @RequestBody UpdateStreakRequest request) {
        StreakResponse streaks = userService.updateStreaks(authentication.getName(), request);
        return ResponseEntity.ok(streaks);
    }

    @GetMapping("/schedule/{dayOfWeek}")
    public ResponseEntity<ScheduleDto> getSchedule(Authentication authentication,
                                                   @PathVariable String dayOfWeek) {
        ScheduleDto schedule = userService.getSchedule(authentication.getName(), dayOfWeek);
        return ResponseEntity.ok(schedule);
    }

    @PutMapping("/schedule/{dayOfWeek}")
    public ResponseEntity<ScheduleDto> updateSchedule(Authentication authentication,
                                                      @PathVariable String dayOfWeek,
                                                      @RequestBody ScheduleDto dto) {
        ScheduleDto schedule = userService.saveSchedule(authentication.getName(), dayOfWeek, dto);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/water")
    public ResponseEntity<WaterIntakeResponse> getWaterIntake(Authentication authentication) {
        WaterIntakeResponse response = analyticsService.getWaterIntake(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/water")
    public ResponseEntity<WaterIntakeResponse> updateWaterIntake(Authentication authentication,
                                                                   @RequestBody WaterIntakeRequest request) {
        WaterIntakeResponse response = analyticsService.updateWaterIntake(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/membership")
    public ResponseEntity<UserMembershipResponse> getUserMembership(Authentication authentication) {
        UserMembershipResponse response = userService.getUserMembership(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
