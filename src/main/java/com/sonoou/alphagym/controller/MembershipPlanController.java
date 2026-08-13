package com.sonoou.alphagym.controller;

import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.service.MembershipPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    public MembershipPlanController(MembershipPlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<MembershipPlanEntity>> getAllPlans() {
        List<MembershipPlanEntity> plans = planService.getAllActivePlans();
        return ResponseEntity.ok(plans);
    }

    @PostMapping
    public ResponseEntity<MembershipPlanEntity> createPlan(@RequestBody MembershipPlanEntity plan) {
        MembershipPlanEntity created = planService.createPlan(plan);
        return ResponseEntity.ok(created);
    }
}
