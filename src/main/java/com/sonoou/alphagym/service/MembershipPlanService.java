package com.sonoou.alphagym.service;

import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;

    public MembershipPlanService(MembershipPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<MembershipPlanEntity> getAllActivePlans() {
        return planRepository.findByActiveTrue();
    }

    public MembershipPlanEntity createPlan(MembershipPlanEntity plan) {
        return planRepository.save(plan);
    }
}
