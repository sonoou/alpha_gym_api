package com.sonoou.alphagym.config;

import com.sonoou.alphagym.entity.CategoryEntity;
import com.sonoou.alphagym.entity.ExerciseTypeEntity;
import com.sonoou.alphagym.entity.MembershipPlanEntity;
import com.sonoou.alphagym.repository.CategoryRepository;
import com.sonoou.alphagym.repository.ExerciseTypeRepository;
import com.sonoou.alphagym.repository.MembershipPlanRepository;
import com.sonoou.alphagym.repository.WorkoutRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final CategoryRepository categoryRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final WorkoutRepository workoutRepository;
    private final DataSource dataSource;

    public DataInitializer(MembershipPlanRepository planRepository,
                           CategoryRepository categoryRepository,
                           ExerciseTypeRepository exerciseTypeRepository,
                           WorkoutRepository workoutRepository,
                           DataSource dataSource) {
        this.planRepository = planRepository;
        this.categoryRepository = categoryRepository;
        this.exerciseTypeRepository = exerciseTypeRepository;
        this.workoutRepository = workoutRepository;
        this.dataSource = dataSource;
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
                    new CategoryEntity("Calves", "Calf raises, lower leg endurance, and definition"),
                    new CategoryEntity("Back", "Lats, upper/lower traps, rhomboids, and spinal erector workouts")
            );
            categoryRepository.saveAll(defaultCategories);
            System.out.println(">>> Seeded 9 default Workout Categories into Database!");
        }

        // 3. Seed Types of Exercises (Extracted from Cleveland Clinic)
        if (exerciseTypeRepository.count() == 0) {
            List<ExerciseTypeEntity> exerciseTypes = List.of(
                    new ExerciseTypeEntity(
                            "Resistance Training",
                            "Produces tension in your muscles by using weights, resistance bands, kettlebells, or bodyweight. Encompasses isotonic exercises (push, pull, lift) and isometric exercises (holding positions) to build muscle and increase overall strength.",
                            "Bicep curls, squats, bench presses, push-ups, pull-ups, planks, wall sits, glute bridges",
                            "Builds muscle, improves muscular endurance, increases bone density, assists fall prevention, and enhances mental clarity."
                    ),
                    new ExerciseTypeEntity(
                            "Cardio (Cardiovascular Exercise)",
                            "Workouts that get your blood pumping harder and faster, increasing heart rate and oxygen consumption to strengthen heart and lungs.",
                            "Running, brisk walking, cycling, swimming, rowing, stair climbing, elliptical training",
                            "Improves heart health, lowers risk of heart disease, diabetes, and high blood pressure, and boosts daily energy."
                    ),
                    new ExerciseTypeEntity(
                            "High-Intensity Interval Training (HIIT)",
                            "A cardio format featuring short, high-effort bursts of intense activity followed by brief periods of lower-intensity recovery.",
                            "Squat jumps, burpees, sprint intervals, jumping jacks, calisthenics circuits, stationary bike sprints",
                            "Conditions the body for big bursts of energy, builds athletic strength, and burns calories rapidly in a short duration."
                    ),
                    new ExerciseTypeEntity(
                            "Low-Intensity Steady-State Cardio (LISS)",
                            "Long-duration aerobic activity performed at a continuous, low-to-moderate intensity pace.",
                            "Long-distance walking, hiking, steady jogging, lap swimming, rowing machine, cross-country skiing",
                            "Builds aerobic stamina and endurance for everyday activities, is joint-friendly, and supports cardiovascular health."
                    ),
                    new ExerciseTypeEntity(
                            "Flexibility Training",
                            "Stretching exercises that lengthen muscles and connective tissues to prevent stiffness, improve joint range of motion, and protect against injury.",
                            "Static stretching (holding 30-90s), dynamic stretching (leg swings, arm circles), active and passive stretching, yoga",
                            "Decreases muscle tightness, improves range of motion, prevents workout injuries, and enhances functional daily mobility."
                    ),
                    new ExerciseTypeEntity(
                            "Balance Exercises",
                            "Exercises that challenge and strengthen core and stabilizing muscles to maintain postural control and body equilibrium.",
                            "Single-leg stands, heel-to-toe walking, balance board drills, stability ball exercises, tai chi",
                            "Prevents falls, improves postural stability, enhances body coordination, and supports healthy longevity."
                    ),
                    new ExerciseTypeEntity(
                            "Sport-Specific Training",
                            "Targeted training regimens focused on drills, agility, and movement patterns tailored directly to a specific athletic sport.",
                            "Agility ladder drills, plyometrics, shuttle runs, sport-specific skill drills (basketball footwork, soccer drills)",
                            "Maximizes athletic performance, refines technique and reaction time, and builds resilience for sport movements."
                    )
            );
            exerciseTypeRepository.saveAll(exerciseTypes);
            System.out.println(">>> Seeded 7 Exercise Types into Database from Cleveland Clinic!");
        }

        // 4. Seed ExRx Workouts if table is empty
        if (workoutRepository.count() == 0) {
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("workouts_seed.sql"));
                populator.execute(dataSource);
                System.out.println(">>> Seeded 565 ExRx Workouts into Database!");
            } catch (Exception e) {
                System.err.println(">>> Error executing workouts_seed.sql: " + e.getMessage());
            }
        }
    }
}
