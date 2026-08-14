package com.sonoou.alphagym.config;

import com.sonoou.alphagym.entity.CategoryEntity;
import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.repository.CategoryRepository;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final CategoryRepository categoryRepository;

    public DataInitializer(MembershipPlanRepository planRepository,
                           CategoryRepository categoryRepository) {
        this.planRepository = planRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Membership Plans
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

        // 2. Seed Default Workout Categories
        if (categoryRepository.count() == 0) {
            List<CategoryEntity> defaultCategories = List.of(
                    new CategoryEntity("Neck", "Neck strength and mobility exercises"),
                    new CategoryEntity("Shoulder", "Deltoids, overhead presses, and shoulder conditioning"),
                    new CategoryEntity("Chest", "Pectoral muscles, bench presses, and chest development"),
                    new CategoryEntity("Biceps", "Biceps curls, isolation, and arm building workouts"),
                    new CategoryEntity("Abs", "Core strength, abdominal crunches, and stability training"),
                    new CategoryEntity("Forearms", "Grip strength, forearm curls, and wrist flexors"),
                    new CategoryEntity("Quads", "Quadriceps, squats, leg extensions, and lower body power"),
                    new CategoryEntity("Calves", "Calf raises, lower leg endurance, and definition")
            );
            categoryRepository.saveAll(defaultCategories);
            System.out.println(">>> Seeded 8 default Workout Categories into Database!");
        }
    }
}
