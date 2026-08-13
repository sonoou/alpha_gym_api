package com.sonoou.alphagym.config;

import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;

    public DataInitializer(MembershipPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (planRepository.count() == 0) {
            MembershipPlanEntity basicPlan = new MembershipPlanEntity(
                    "Monthly Starter Plan",
                    "Access to all gym equipment & locker facilities for 1 Month",
                    499.00,
                    "INR",
                    1
            );

            MembershipPlanEntity proPlan = new MembershipPlanEntity(
                    "Pro Quarterly Pass",
                    "Gym access + Personal Trainer consultation + Sauna for 3 Months",
                    1299.00,
                    "INR",
                    3
            );

            MembershipPlanEntity vipPlan = new MembershipPlanEntity(
                    "VIP Annual Elite Membership",
                    "Unlimited 24/7 access + Personal Trainer + Diet Plan + Free Merchandise for 12 Months",
                    3999.00,
                    "INR",
                    12
            );

            planRepository.saveAll(List.of(basicPlan, proPlan, vipPlan));
            System.out.println(">>> Seeded 3 default Gym Membership Plans into Database!");
        }
    }
}
