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

        // Health & Docs
        endpoints.add(createEndpoint("Health & System", "GET", "/api/health", "System health check for DB and backend service", false, null, Map.of("status", "UP", "database", "UP", "service", "alpha-veins-backend")));
        endpoints.add(createEndpoint("Health & System", "GET", "/api/docs", "Human-readable API Documentation & route list", false, null, "Summary JSON of endpoints"));
        endpoints.add(createEndpoint("Health & System", "GET", "/api/docs/openapi.json", "Standard OpenAPI 3.0.1 JSON Specification", false, null, "OpenAPI 3.0 JSON schema"));

        // Authentication
        endpoints.add(createEndpoint("Auth", "POST", "/api/auth/signup", "Register a new user account", false, Map.of("name", "John Doe", "email", "john@example.com", "password", "secret123"), Map.of("token", "jwt_token...", "name", "John Doe", "email", "john@example.com")));
        endpoints.add(createEndpoint("Auth", "POST", "/api/auth/login", "Authenticate user and get Bearer JWT token", false, Map.of("email", "john@example.com", "password", "secret123"), Map.of("token", "jwt_token...", "name", "John Doe", "email", "john@example.com")));

        // Membership Plans
        endpoints.add(createEndpoint("Membership Plans", "GET", "/api/plans", "Fetch all active gym membership plans", false, null, List.of(Map.of("id", 1, "name", "Monthly Starter Plan", "amount", 499.0, "currency", "INR", "durationMonths", 1))));
        endpoints.add(createEndpoint("Membership Plans", "POST", "/api/plans", "Create a new membership plan", true, Map.of("name", "VIP Plan", "amount", 3999.0, "currency", "INR", "durationMonths", 12), Map.of("id", 4, "name", "VIP Plan", "amount", 3999.0)));

        // Payments (Razorpay)
        endpoints.add(createEndpoint("Payments", "POST", "/api/payment/create-order", "Create Razorpay payment order by planId or amount", true, Map.of("planId", 1), Map.of("orderId", "order_MZk123456", "keyId", "rzp_test_...", "amountInPaisa", 49900, "currency", "INR")));
        endpoints.add(createEndpoint("Payments", "POST", "/api/payment/verify", "Verify Razorpay HMAC SHA256 payment signature", true, Map.of("razorpayOrderId", "order_MZk123456", "razorpayPaymentId", "pay_MZn876543", "razorpaySignature", "sig..."), Map.of("status", "SUCCESS", "message", "Payment verified successfully")));

        // Water Intake
        endpoints.add(createEndpoint("Water Intake", "GET", "/api/user/water", "Get today's water intake & daily target", true, null, Map.of("waterIntakeMl", 1250, "targetWaterMl", 2500, "percentage", 50.0, "date", "2026-08-13")));
        endpoints.add(createEndpoint("Water Intake", "POST", "/api/user/water", "Log or update water intake (actions: ADD, SET, RESET)", true, Map.of("amountMl", 250, "action", "ADD"), Map.of("waterIntakeMl", 1500, "targetWaterMl", 2500, "percentage", 60.0)));

        // User Profile & Routine
        endpoints.add(createEndpoint("User Profile", "GET", "/api/user/profile", "Get logged-in user profile", true, null, Map.of("id", 1, "name", "John", "email", "john@example.com")));
        endpoints.add(createEndpoint("User Profile", "PUT", "/api/user/profile", "Update user profile details", true, Map.of("name", "John Doe", "age", 25, "weight", 75.0, "height", 178.0), Map.of("id", 1, "name", "John Doe")));
        endpoints.add(createEndpoint("User Profile", "PATCH", "/api/user/profile/photo", "Upload profile photo (multipart/form-data key 'file')", true, "FormData with file", Map.of("profilePhotoUrl", "/uploads/profile_1.jpg")));
        endpoints.add(createEndpoint("User Profile", "GET", "/api/user/routine", "Get user workout routine list", true, null, "List of saved workouts"));
        endpoints.add(createEndpoint("User Profile", "POST", "/api/user/routine", "Add/Remove workout to routine (action: ADD or REMOVE)", true, Map.of("workoutId", 5, "action", "ADD"), "Updated routine list"));
        endpoints.add(createEndpoint("User Profile", "GET", "/api/user/streaks", "Get current workout streaks", true, null, Map.of("currentStreakDays", 7)));
        endpoints.add(createEndpoint("User Profile", "GET", "/api/user/schedule/{dayOfWeek}", "Get schedule for specific day (e.g. MONDAY)", true, null, Map.of("dayOfWeek", "MONDAY", "focusArea", "Chest & Triceps")));

        // Workouts & Categories
        endpoints.add(createEndpoint("Workouts", "GET", "/api/workouts", "Get workouts list (optional ?category=Chest filter)", false, null, "List of Workout objects"));
        endpoints.add(createEndpoint("Workouts", "GET", "/api/categories", "Get workout categories list", false, null, "List of Category objects"));
        endpoints.add(createEndpoint("Workouts", "POST", "/api/workouts/upload-video", "Upload 9:16 workout video (multipart key 'file')", true, "FormData with video file", Map.of("videoUrl", "/uploads/videos/workout_1.mp4")));

        // Community Feed
        endpoints.add(createEndpoint("Community Feed", "GET", "/api/feed", "Get community posts feed", true, null, "List of CommunityPost objects"));
        endpoints.add(createEndpoint("Community Feed", "POST", "/api/feed", "Create new post (optional image upload)", true, Map.of("caption", "Morning Leg Day!"), Map.of("id", 1, "caption", "Morning Leg Day!")));

        // Analytics
        endpoints.add(createEndpoint("Analytics", "GET", "/api/analytics/summary", "Get daily & weekly fitness analytics summary", true, null, Map.of("dailySteps", 5000, "dailyCalories", 350, "dailyWaterIntakeMl", 1250, "targetWaterMl", 2500)));

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
        info.put("description", "Comprehensive REST APIs for Alpha Veins Gym Flutter Mobile Application");
        info.put("version", "1.0.0");
        openapi.put("info", info);

        List<Map<String, String>> servers = List.of(
                Map.of("url", "http://localhost:8080", "description", "Local Server"),
                Map.of("url", "https://thompson-fitted-couples-carlos.trycloudflare.com", "description", "Cloudflare Tunnel Server")
        );
        openapi.put("servers", servers);

        Map<String, Object> paths = new LinkedHashMap<>();

        // Health
        paths.put("/api/health", Map.of("get", Map.of("summary", "System Health Check", "responses", Map.of("200", Map.of("description", "System & DB status")))));
        paths.put("/api/docs", Map.of("get", Map.of("summary", "API Documentation", "responses", Map.of("200", Map.of("description", "API list JSON")))));
        paths.put("/api/docs/openapi.json", Map.of("get", Map.of("summary", "OpenAPI Specification", "responses", Map.of("200", Map.of("description", "OpenAPI 3.0.1 JSON")))));
        
        // Auth
        paths.put("/api/auth/signup", Map.of("post", Map.of("summary", "User Registration", "responses", Map.of("200", Map.of("description", "Registered successfully with JWT token")))));
        paths.put("/api/auth/login", Map.of("post", Map.of("summary", "User Login", "responses", Map.of("200", Map.of("description", "Authenticated with JWT token")))));

        // Plans
        paths.put("/api/plans", Map.of(
                "get", Map.of("summary", "Get Membership Plans", "responses", Map.of("200", Map.of("description", "List of active membership plans"))),
                "post", Map.of("summary", "Create Membership Plan", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Created plan")))
        ));

        // Payments
        paths.put("/api/payment/create-order", Map.of("post", Map.of("summary", "Create Razorpay Order", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Razorpay order created")))));
        paths.put("/api/payment/verify", Map.of("post", Map.of("summary", "Verify Razorpay Payment", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Verification result")))));

        // Water
        paths.put("/api/user/water", Map.of(
                "get", Map.of("summary", "Get Water Intake", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Daily water intake details"))),
                "post", Map.of("summary", "Update Water Intake", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Updated water intake details")))
        ));

        // Profile
        paths.put("/api/user/profile", Map.of(
                "get", Map.of("summary", "Get Profile", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "User profile"))),
                "put", Map.of("summary", "Update Profile", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Updated user profile")))
        ));

        // Workouts
        paths.put("/api/workouts", Map.of("get", Map.of("summary", "Get Workouts", "responses", Map.of("200", Map.of("description", "List of workouts")))));
        paths.put("/api/categories", Map.of("get", Map.of("summary", "Get Workout Categories", "responses", Map.of("200", Map.of("description", "List of categories")))));

        // Analytics
        paths.put("/api/analytics/summary", Map.of("get", Map.of("summary", "Get Analytics Summary", "security", List.of(Map.of("bearerAuth", List.of())), "responses", Map.of("200", Map.of("description", "Daily & weekly summary")))));

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
