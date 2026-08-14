package com.sonoou.alphagym.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/docs")
public class ApiDocsController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getApiDocumentation() {
        Map<String, Object> docs = new LinkedHashMap<>();
        docs.put("title", "Alpha Veins Gym API Documentation");
        docs.put("version", "1.0.0");
        docs.put("baseUrl", "http://localhost:8080");
        docs.put("openApiJsonUrl", "/api/docs/openapi.json");

        List<Map<String, Object>> endpoints = new ArrayList<>();

        // 1. Health & Docs
        endpoints.add(createEndpoint("Health & System", "GET", "/api/health", "System health check for DB and backend service", false, null, Map.of("status", "UP", "database", "UP", "service", "alpha-veins-backend")));
        endpoints.add(createEndpoint("Health & System", "GET", "/api/docs", "Human-readable API Documentation & route list", false, null, "Summary JSON of endpoints"));
        endpoints.add(createEndpoint("Health & System", "GET", "/api/docs/openapi.json", "Standard OpenAPI 3.0.1 Specification JSON (Import into Postman/Apidog)", false, null, "OpenAPI 3.0 JSON schema"));

        // 2. Authentication
        endpoints.add(createEndpoint("Auth", "POST", "/api/auth/signup", "Register a new user account", false, Map.of("name", "John Doe", "email", "john@example.com", "password", "secret123"), Map.of("token", "jwt_token...", "name", "John Doe", "email", "john@example.com")));
        endpoints.add(createEndpoint("Auth", "POST", "/api/auth/login", "Authenticate user and get Bearer JWT token", false, Map.of("email", "john@example.com", "password", "secret123"), Map.of("token", "jwt_token...", "name", "John Doe", "email", "john@example.com")));

        // 3. Membership Plans
        endpoints.add(createEndpoint("Membership Plans", "GET", "/api/plans", "Fetch all active gym membership plans", false, null, List.of(Map.of("id", 1, "name", "Monthly Starter Plan", "amount", 499.0, "currency", "INR", "durationMonths", 1))));
        endpoints.add(createEndpoint("Membership Plans", "POST", "/api/plans", "Create a new membership plan", true, Map.of("name", "VIP Plan", "amount", 3999.0, "currency", "INR", "durationMonths", 12), Map.of("id", 4, "name", "VIP Plan", "amount", 3999.0)));

        // 4. Payments (Razorpay)
        endpoints.add(createEndpoint("Payments", "POST", "/api/payment/create-order", "Create Razorpay payment order by planId (optional startDate YYYY-MM-DD: today or future)", true, Map.of("planId", 1, "startDate", "2026-08-15"), Map.of("orderId", "order_MZk123456", "keyId", "rzp_test_TPD8uVjfUAQbK1", "amountInPaisa", 49900, "currency", "INR")));
        endpoints.add(createEndpoint("Payments", "POST", "/api/payment/verify", "Verify Razorpay HMAC SHA256 payment signature & activate membership", true, Map.of("razorpayOrderId", "order_MZk123456", "razorpayPaymentId", "pay_MZn876543", "razorpaySignature", "sig...", "planId", 1, "startDate", "2026-08-15"), Map.of("status", "SUCCESS", "message", "Payment verified & membership activated successfully")));
        endpoints.add(createEndpoint("Payments", "GET", "/api/payment/history", "Get user's previous payment transactions history sorted by date DESC", true, null, List.of(Map.of("id", 1, "razorpayOrderId", "order_MZk123456", "razorpayPaymentId", "pay_MZn876543", "amount", 499.0, "currency", "INR", "planName", "Monthly Starter Plan", "status", "SUCCESS", "paymentDate", "2026-08-14T16:20:00"))));

        // 5. User Active Membership & Payments
        endpoints.add(createEndpoint("User Membership", "GET", "/api/user/membership", "Get user's current active membership status, plan name, start date, expiry date & days remaining", true, null, Map.of("isMembershipActive", true, "activePlanName", "Monthly Starter Plan", "planStartDate", "2026-08-15", "planExpiryDate", "2026-09-15T23:59:59", "daysRemaining", 30)));
        endpoints.add(createEndpoint("User Membership", "GET", "/api/user/payments", "Get user payment transaction history", true, null, List.of(Map.of("id", 1, "razorpayOrderId", "order_MZk123456", "amount", 499.0, "status", "SUCCESS"))));

        // 6. Exercise Types (Cleveland Clinic)
        endpoints.add(createEndpoint("Exercise Types", "GET", "/api/exercise-types", "Get all types of exercises (Resistance, Cardio, HIIT, LISS, Flexibility, Balance, Sport-Specific)", false, null, List.of(Map.of("id", 1, "name", "Resistance Training", "description", "Produces tension in muscles...", "examples", "Squats, Bench Press", "benefits", "Builds strength and bone density"))));
        endpoints.add(createEndpoint("Exercise Types", "GET", "/api/types", "Alias for getting all types of exercises", false, null, "List of ExerciseType objects"));
        endpoints.add(createEndpoint("Exercise Types", "GET", "/api/exercise-types/{id}", "Get exercise type details by ID", false, null, Map.of("id", 1, "name", "Resistance Training")));

        // 7. Workouts & Exercise Library (ExRx 565+ Workouts)
        endpoints.add(createEndpoint("Workouts", "GET", "/api/workouts", "Get all workouts (supports ?category=Chest and ?difficulty=Beginner filters)", false, null, List.of(Map.of("id", 142, "name", "Barbell Bench Press", "category", "Chest", "difficulty", "Advanced", "targetMuscles", "Pectoralis Major (Sternal Head), Anterior Deltoid, Triceps"))));
        endpoints.add(createEndpoint("Workouts", "GET", "/api/workouts/{id}", "Get single workout details by ID", false, null, Map.of("id", 142, "name", "Barbell Bench Press", "category", "Chest", "durationMinutes", 15)));
        endpoints.add(createEndpoint("Workouts", "GET", "/api/categories", "Get all 9 workout categories (Neck, Shoulder, Chest, Biceps, Abs, Forearms, Quads, Calves, Back)", false, null, List.of(Map.of("id", 1, "name", "Neck"), Map.of("id", 3, "name", "Chest"), Map.of("id", 9, "name", "Back"))));
        endpoints.add(createEndpoint("Workouts", "POST", "/api/workouts/upload-video", "Upload 9:16 workout video (multipart key 'file')", true, "FormData with video file", Map.of("videoUrl", "/uploads/videos/workout_1.mp4")));

        // 8. Water Intake Tracking
        endpoints.add(createEndpoint("Water Intake", "GET", "/api/user/water", "Get today's water intake & daily target", true, null, Map.of("waterIntakeMl", 1250, "targetWaterMl", 2500, "percentage", 50.0, "date", "2026-08-14")));
        endpoints.add(createEndpoint("Water Intake", "POST", "/api/user/water", "Log or update water intake (actions: ADD, SET, RESET)", true, Map.of("amountMl", 250, "action", "ADD"), Map.of("waterIntakeMl", 1500, "targetWaterMl", 2500, "percentage", 60.0)));

        // 9. User Profile, Routine, Streaks & Schedule
        endpoints.add(createEndpoint("User Profile", "GET", "/api/user/profile", "Get logged-in user profile with membership status", true, null, Map.of("id", 1, "name", "John", "email", "john@example.com", "isMembershipActive", true, "activePlanName", "Monthly Starter Plan", "planStartDate", "2026-08-15")));
        endpoints.add(createEndpoint("User Profile", "PUT", "/api/user/profile", "Update user profile details", true, Map.of("name", "John Doe", "age", 25, "weight", 75.0, "height", 178.0), Map.of("id", 1, "name", "John Doe")));
        endpoints.add(createEndpoint("User Profile", "PATCH", "/api/user/profile/photo", "Upload profile photo (multipart key 'file')", true, "FormData with file", Map.of("profilePhotoUrl", "/uploads/profile_1.jpg")));
        endpoints.add(createEndpoint("User Profile", "PATCH", "/api/user/onboarding", "Set onboarding completion status", true, Map.of("completed", true), Map.of("onboardingCompleted", true)));
        endpoints.add(createEndpoint("User Routine", "GET", "/api/user/routine", "Get user workout routine list", true, null, "List of saved workouts"));
        endpoints.add(createEndpoint("User Routine", "POST", "/api/user/routine", "Add or Remove workout from routine", true, Map.of("workoutId", 5, "action", "ADD"), "Updated routine list"));
        endpoints.add(createEndpoint("User Streaks", "GET", "/api/user/streaks", "Get current workout streaks", true, null, Map.of("currentStreakDays", 7)));
        endpoints.add(createEndpoint("User Streaks", "POST", "/api/user/streaks/complete", "Complete today's workout streak (+1 day)", true, null, Map.of("currentStreakDays", 8)));
        endpoints.add(createEndpoint("User Streaks", "PUT", "/api/user/streaks", "Set custom streak days", true, Map.of("currentStreakDays", 14), Map.of("currentStreakDays", 14)));
        endpoints.add(createEndpoint("User Schedule", "GET", "/api/user/schedule/{dayOfWeek}", "Get user schedule for day (e.g. MONDAY)", true, null, Map.of("dayOfWeek", "MONDAY", "focusArea", "Chest & Triceps")));
        endpoints.add(createEndpoint("User Schedule", "PUT", "/api/user/schedule/{dayOfWeek}", "Update schedule for day", true, Map.of("focusArea", "Legs & Core", "notes", "Heavy Squats"), Map.of("dayOfWeek", "MONDAY", "focusArea", "Legs & Core")));

        // 10. Community Feed
        endpoints.add(createEndpoint("Community Feed", "GET", "/api/feed", "Get community posts feed", true, null, "List of CommunityPost objects"));
        endpoints.add(createEndpoint("Community Feed", "POST", "/api/feed", "Create new post (optional image upload)", true, Map.of("caption", "Morning Leg Day!"), Map.of("id", 1, "caption", "Morning Leg Day!")));
        endpoints.add(createEndpoint("Community Feed", "POST", "/api/feed/{postId}/like", "Toggle like on post", true, null, Map.of("postId", 1, "likesCount", 12)));
        endpoints.add(createEndpoint("Community Feed", "GET", "/api/feed/{postId}/comments", "Get comments on post", true, null, "List of comments"));
        endpoints.add(createEndpoint("Community Feed", "POST", "/api/feed/{postId}/comments", "Add comment to post", true, Map.of("text", "Great form!"), Map.of("id", 1, "text", "Great form!")));

        // 11. Analytics
        endpoints.add(createEndpoint("Analytics", "GET", "/api/analytics/summary", "Get daily & weekly fitness analytics summary", true, null, Map.of("dailySteps", 5000, "dailyCalories", 350, "dailyWaterIntakeMl", 1250, "targetWaterMl", 2500)));
        endpoints.add(createEndpoint("Analytics", "POST", "/api/analytics/daily", "Log daily steps, calories, active minutes & water", true, Map.of("steps", 6000, "caloriesBurned", 400.0, "activeMinutes", 45, "waterIntakeMl", 1500), "HTTP 200 OK"));
        endpoints.add(createEndpoint("Analytics", "GET", "/api/analytics/water", "Get water intake status", true, null, Map.of("waterIntakeMl", 1250, "targetWaterMl", 2500)));
        endpoints.add(createEndpoint("Analytics", "POST", "/api/analytics/water", "Update water intake status", true, Map.of("amountMl", 250, "action", "ADD"), Map.of("waterIntakeMl", 1500, "targetWaterMl", 2500)));

        docs.put("totalEndpoints", endpoints.size());
        docs.put("endpoints", endpoints);

        return ResponseEntity.ok(docs);
    }

    @GetMapping(value = "/openapi.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOpenApiJson() {
        Map<String, Object> openapi = new LinkedHashMap<>();
        openapi.put("openapi", "3.0.1");

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "Alpha Veins Gym Backend API");
        info.put("description", "Comprehensive REST API Specification for Postman, Apidog, Swagger UI & Insomnia Import");
        info.put("version", "1.0.0");
        openapi.put("info", info);

        List<Map<String, String>> servers = List.of(
                Map.of("url", "http://localhost:8080", "description", "Local Server"),
                Map.of("url", "https://thompson-fitted-couples-carlos.trycloudflare.com", "description", "Cloudflare Tunnel Public Server")
        );
        openapi.put("servers", servers);

        Map<String, Object> paths = new LinkedHashMap<>();

        // Health & System
        paths.put("/api/health", Map.of("get", createOperation("System Health Check", "System & DB status health check", false, null, Map.of("200", createResponse("System Health Status", Map.of("status", "UP", "database", "UP", "service", "alpha-veins-backend"))))));
        paths.put("/api/docs", Map.of("get", createOperation("API Documentation", "List of all APIs with usage details", false, null, Map.of("200", createResponse("API List Documentation", Map.of("title", "Alpha Veins Gym API"))))));
        paths.put("/api/docs/openapi.json", Map.of("get", createOperation("OpenAPI JSON Specification", "Standard OpenAPI 3.0.1 JSON Spec for Postman/Apidog Import", false, null, Map.of("200", createResponse("OpenAPI JSON Spec", Map.of("openapi", "3.0.1"))))));

        // Auth
        paths.put("/api/auth/signup", Map.of("post", createOperation("User Signup", "Register a new user account", false, Map.of("name", "John Doe", "email", "john@example.com", "password", "secret123"), Map.of("200", createResponse("JWT Auth Token & User info", Map.of("token", "eyJhbGciOi...", "name", "John Doe", "email", "john@example.com"))))));
        paths.put("/api/auth/login", Map.of("post", createOperation("User Login", "Authenticate user and receive JWT token", false, Map.of("email", "john@example.com", "password", "secret123"), Map.of("200", createResponse("JWT Auth Token & User info", Map.of("token", "eyJhbGciOi...", "name", "John Doe", "email", "john@example.com"))))));

        // Membership Plans
        paths.put("/api/plans", Map.of(
                "get", createOperation("Get Gym Membership Plans", "Fetch all active gym subscription plans", false, null, Map.of("200", createResponse("List of active plans", List.of(Map.of("id", 1, "name", "Monthly Starter Plan", "amount", 499.0, "currency", "INR", "durationMonths", 1))))),
                "post", createOperation("Create Membership Plan", "Create a new membership plan in database", true, Map.of("name", "VIP Plan", "amount", 3999.0, "currency", "INR", "durationMonths", 12), Map.of("200", createResponse("Created plan details", Map.of("id", 4, "name", "VIP Plan", "amount", 3999.0))))
        ));

        // Payments
        paths.put("/api/payment/create-order", Map.of("post", createOperation("Create Razorpay Order", "Generate Razorpay Order ID by planId & optional startDate (today or future)", true, Map.of("planId", 1, "startDate", "2026-08-15"), Map.of("200", createResponse("Razorpay Order Details", Map.of("orderId", "order_MZk123456", "keyId", "rzp_test_TPD8uVjfUAQbK1", "amountInPaisa", 49900, "currency", "INR"))))));
        paths.put("/api/payment/verify", Map.of("post", createOperation("Verify Razorpay Payment", "Verify Razorpay HMAC SHA256 signature & activate membership", true, Map.of("razorpayOrderId", "order_MZk123456", "razorpayPaymentId", "pay_MZn876543", "razorpaySignature", "9f8e7d6c...", "planId", 1, "startDate", "2026-08-15"), Map.of("200", createResponse("Verification Status", Map.of("status", "SUCCESS", "message", "Payment verified & membership activated successfully"))))));
        paths.put("/api/payment/history", Map.of("get", createOperation("Get Payment History", "Get user's previous payment transactions history sorted descending", true, null, Map.of("200", createResponse("Payment History List", List.of(Map.of("id", 1, "razorpayOrderId", "order_MZk123456", "razorpayPaymentId", "pay_MZn876543", "amount", 499.0, "currency", "INR", "planName", "Monthly Starter Plan", "status", "SUCCESS", "paymentDate", "2026-08-14T16:20:00")))))));

        // User Active Membership & Payments
        paths.put("/api/user/membership", Map.of("get", createOperation("Get User Active Membership", "Get user's current active membership status, plan name, start date, expiry date & days remaining", true, null, Map.of("200", createResponse("Membership Status", Map.of("isMembershipActive", true, "activePlanName", "Monthly Starter Plan", "planStartDate", "2026-08-15", "planExpiryDate", "2026-09-15T23:59:59", "daysRemaining", 30))))));
        paths.put("/api/user/payments", Map.of("get", createOperation("Get User Payments", "Get user payment transaction history", true, null, Map.of("200", createResponse("User Payments History", List.of(Map.of("id", 1, "razorpayOrderId", "order_MZk123456", "amount", 499.0, "status", "SUCCESS")))))));

        // Exercise Types
        paths.put("/api/exercise-types", Map.of("get", createOperation("Get Exercise Types", "Fetch all exercise types with descriptions and benefits (from Cleveland Clinic)", false, null, Map.of("200", createResponse("Exercise Types List", List.of(Map.of("id", 1, "name", "Resistance Training", "description", "Produces tension in muscles...", "examples", "Squats, Bicep curls", "benefits", "Increases strength and bone density")))))));
        paths.put("/api/exercise-types/{id}", Map.of("get", createOperation("Get Exercise Type by ID", "Fetch specific exercise type by ID", false, null, Map.of("200", createResponse("Exercise Type Details", Map.of("id", 1, "name", "Resistance Training"))))));
        paths.put("/api/types", Map.of("get", createOperation("Get Types Alias", "Alias route for /api/exercise-types", false, null, Map.of("200", createResponse("Exercise Types List", List.of(Map.of("id", 1, "name", "Resistance Training")))))));

        // Workouts & Categories
        paths.put("/api/workouts", Map.of("get", createOperation("Get Workouts List", "Get 565+ workouts (optional ?category=Chest, ?difficulty=Beginner filters)", false, null, Map.of("200", createResponse("Workouts List", List.of(Map.of("id", 142, "name", "Barbell Bench Press", "category", "Chest", "difficulty", "Advanced", "targetMuscles", "Pectoralis Major, Triceps")))))));
        paths.put("/api/workouts/{id}", Map.of("get", createOperation("Get Workout by ID", "Get workout details by ID", false, null, Map.of("200", createResponse("Workout Details", Map.of("id", 142, "name", "Barbell Bench Press", "category", "Chest"))))));
        paths.put("/api/categories", Map.of("get", createOperation("Get Categories List", "Get all 9 workout categories (Neck, Shoulder, Chest, Biceps, Abs, Forearms, Quads, Calves, Back)", false, null, Map.of("200", createResponse("Categories List", List.of(Map.of("id", 1, "name", "Neck"), Map.of("id", 3, "name", "Chest"), Map.of("id", 9, "name", "Back")))))));
        paths.put("/api/workouts/upload-video", Map.of("post", createOperation("Upload Workout Video", "Upload 9:16 video (multipart key 'file')", true, null, Map.of("200", createResponse("Video Upload Result", Map.of("videoUrl", "/uploads/videos/workout_1.mp4"))))));

        // Water
        paths.put("/api/user/water", Map.of(
                "get", createOperation("Get User Water Intake", "Get today's water intake & target", true, null, Map.of("200", createResponse("Daily Water Intake Details", Map.of("waterIntakeMl", 1250, "targetWaterMl", 2500, "percentage", 50.0)))),
                "post", createOperation("Update User Water Intake", "Log or update water intake (actions: ADD, SET, RESET)", true, Map.of("amountMl", 250, "action", "ADD"), Map.of("200", createResponse("Updated Water Intake Details", Map.of("waterIntakeMl", 1500, "targetWaterMl", 2500, "percentage", 60.0))))
        ));

        // User Profile, Routine, Streaks & Schedule
        paths.put("/api/user/profile", Map.of(
                "get", createOperation("Get User Profile", "Get current user profile details with active membership & start date", true, null, Map.of("200", createResponse("User Profile", Map.of("id", 1, "name", "John Doe", "email", "john@example.com", "isMembershipActive", true, "activePlanName", "Monthly Starter Plan", "planStartDate", "2026-08-15")))),
                "put", createOperation("Update User Profile", "Update user profile fields", true, Map.of("name", "John Doe", "age", 25, "weight", 75.0, "height", 178.0), Map.of("200", createResponse("Updated Profile", Map.of("id", 1, "name", "John Doe"))))
        ));

        paths.put("/api/user/profile/photo", Map.of("patch", createOperation("Update Profile Photo", "Upload profile photo (multipart key 'file')", true, null, Map.of("200", createResponse("Updated Photo URL", Map.of("profilePhotoUrl", "/uploads/profile_1.jpg"))))));
        paths.put("/api/user/onboarding", Map.of("patch", createOperation("Set Onboarding Status", "Set onboarding completion status", true, null, Map.of("200", createResponse("Updated Onboarding Status", Map.of("onboardingCompleted", true))))));

        paths.put("/api/user/routine", Map.of(
                "get", createOperation("Get User Routine", "Get saved workout routine list", true, null, Map.of("200", createResponse("Routine Workouts List", List.of(Map.of("id", 1, "name", "Barbell Bench Press"))))),
                "post", createOperation("Update User Routine", "Add or Remove workout from routine", true, Map.of("workoutId", 5, "action", "ADD"), Map.of("200", createResponse("Updated Routine List", List.of(Map.of("id", 5, "name", "Incline Press")))))
        ));

        paths.put("/api/user/streaks", Map.of(
                "get", createOperation("Get User Streaks", "Get current streak days", true, null, Map.of("200", createResponse("Streaks Details", Map.of("currentStreakDays", 7)))),
                "put", createOperation("Update User Streaks", "Set custom streak days", true, Map.of("currentStreakDays", 14), Map.of("200", createResponse("Updated Streaks", Map.of("currentStreakDays", 14))))
        ));
        paths.put("/api/user/streaks/complete", Map.of("post", createOperation("Complete Today Workout", "Increment streak days (+1 day)", true, null, Map.of("200", createResponse("Updated Streaks", Map.of("currentStreakDays", 8))))));

        paths.put("/api/user/schedule/{dayOfWeek}", Map.of(
                "get", createOperation("Get Daily Schedule", "Get schedule for specific day (e.g. MONDAY)", true, null, Map.of("200", createResponse("Day Schedule", Map.of("dayOfWeek", "MONDAY", "focusArea", "Chest & Triceps")))),
                "put", createOperation("Update Daily Schedule", "Update schedule for specific day", true, Map.of("focusArea", "Legs & Core", "notes", "Squats 4x12"), Map.of("200", createResponse("Updated Day Schedule", Map.of("dayOfWeek", "MONDAY", "focusArea", "Legs & Core"))))
        ));

        // Community Feed
        paths.put("/api/feed", Map.of(
                "get", createOperation("Get Community Feed", "Get community feed posts", true, null, Map.of("200", createResponse("Community Feed Posts", List.of(Map.of("id", 1, "caption", "Morning Workout"))))),
                "post", createOperation("Create Community Post", "Create new post with caption & optional image", true, Map.of("caption", "Hit new PR 100kg!"), Map.of("200", createResponse("Created Post", Map.of("id", 1, "caption", "Hit new PR 100kg!"))))
        ));

        paths.put("/api/feed/{postId}/like", Map.of("post", createOperation("Toggle Post Like", "Like or Unlike community post", true, null, Map.of("200", createResponse("Post Like Status", Map.of("postId", 1, "likesCount", 15))))));
        paths.put("/api/feed/{postId}/comments", Map.of(
                "get", createOperation("Get Post Comments", "Get comments on post", true, null, Map.of("200", createResponse("Comments List", List.of(Map.of("id", 1, "text", "Awesome work!"))))),
                "post", createOperation("Add Post Comment", "Add comment to post", true, Map.of("text", "Great form!"), Map.of("200", createResponse("Created Comment", Map.of("id", 1, "text", "Great form!"))))
        ));

        // Analytics
        paths.put("/api/analytics/summary", Map.of("get", createOperation("Get Analytics Summary", "Get daily & weekly fitness analytics summary", true, null, Map.of("200", createResponse("Analytics Summary", Map.of("dailySteps", 5000, "dailyCalories", 350, "dailyWaterIntakeMl", 1250, "targetWaterMl", 2500))))));
        paths.put("/api/analytics/daily", Map.of("post", createOperation("Log Daily Analytics", "Log daily steps, calories, active minutes & water intake", true, Map.of("steps", 6000, "caloriesBurned", 400.0, "activeMinutes", 45, "waterIntakeMl", 1500), Map.of("200", Map.of("description", "Daily analytics logged")))));
        paths.put("/api/analytics/water", Map.of(
                "get", createOperation("Get Water Intake Status", "Get today's water intake status", true, null, Map.of("200", createResponse("Water Intake Status", Map.of("waterIntakeMl", 1250, "targetWaterMl", 2500)))),
                "post", createOperation("Update Water Intake Status", "Update water intake status", true, Map.of("amountMl", 250, "action", "ADD"), Map.of("200", createResponse("Updated Water Intake Status", Map.of("waterIntakeMl", 1500, "targetWaterMl", 2500))))
        ));

        openapi.put("paths", paths);

        Map<String, Object> components = Map.of(
                "securitySchemes", Map.of(
                        "bearerAuth", Map.of(
                                "type", "http",
                                "scheme", "bearer",
                                "bearerFormat", "JWT"
                        )
                )
        );
        openapi.put("components", components);

        return ResponseEntity.ok(openapi);
    }

    private Map<String, Object> createOperation(String summary, String description, boolean authRequired, Object requestBodyExample, Map<String, Object> responses) {
        Map<String, Object> op = new LinkedHashMap<>();
        op.put("summary", summary);
        op.put("description", description);
        if (authRequired) {
            op.put("security", List.of(Map.of("bearerAuth", List.of())));
        }
        if (requestBodyExample != null) {
            op.put("requestBody", Map.of(
                    "required", true,
                    "content", Map.of("application/json", Map.of("example", requestBodyExample))
            ));
        }
        op.put("responses", responses);
        return op;
    }

    private Map<String, Object> createResponse(String description, Object example) {
        return Map.of(
                "description", description,
                "content", Map.of("application/json", Map.of("example", example))
        );
    }

    private Map<String, Object> createEndpoint(String category, String method, String path, String description, boolean authRequired, Object requestBody, Object responseBody) {
        Map<String, Object> ep = new LinkedHashMap<>();
        ep.put("category", category);
        ep.put("method", method);
        ep.put("path", path);
        ep.put("description", description);
        ep.put("authenticationRequired", authRequired);
        if (requestBody != null) {
            ep.put("sampleRequestBody", requestBody);
        }
        if (responseBody != null) {
            ep.put("sampleResponseBody", responseBody);
        }
        return ep;
    }
}
