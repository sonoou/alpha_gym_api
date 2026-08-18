# 🏋️‍♂️ Alpha Veins Gym API Reference & Usage Guide

> Complete, Swagger-style API Documentation for Alpha Veins Gym Backend.  
> Compatible with **Postman**, **Apidog**, **Insomnia**, and **Mobile App Integration (Flutter / React Native / Swift / Kotlin)**.

---

## 🌐 Server Base URLs

| Environment | Base URL | Description |
|:---|:---|:---|
| **Local Development** | `http://localhost:8080` | Local Spring Boot Server |
| **Cloudflare Tunnel (Public)** | `https://thompson-fitted-couples-carlos.trycloudflare.com` | Live SSL Public Domain |
| **Live JSON Doc** | `/api/docs` | Human-readable endpoints list |
| **OpenAPI 3.0.1 Spec** | `/api/docs/openapi.json` | Import directly into Postman / Apidog / Swagger UI |

---

## 🔐 Authentication Scheme

All protected routes require a JWT Bearer Token in the `Authorization` header:

```http
Authorization: Bearer <YOUR_JWT_TOKEN>
```

---

## 📑 Table of Contents

1. [Authentication (`/api/auth`)](#1-authentication)
2. [User Profile & Onboarding (`/api/user`)](#2-user-profile--onboarding)
3. [Membership Plans (`/api/plans`)](#3-membership-plans)
4. [Razorpay Payments & PDF Receipts (`/api/payment`)](#4-razorpay-payments--pdf-receipts)
5. [Workouts & Exercise Library (`/api/workouts`)](#5-workouts--exercise-library)
6. [Exercise Types (`/api/exercise-types`)](#6-exercise-types)
7. [User Routines (`/api/user/routine`)](#7-user-routines)
8. [User Streaks (`/api/user/streaks`)](#8-user-streaks)
9. [Weekly Workout Schedule (`/api/user/schedule`)](#9-weekly-workout-schedule)
10. [Water Intake Tracker (`/api/user/water`)](#10-water-intake-tracker)
11. [Fitness Analytics (`/api/analytics`)](#11-fitness-analytics)
12. [Community Feed & Comments (`/api/feed`)](#12-community-feed--social)
13. [System Health Check (`/api/health`)](#13-system-health-check)
14. [Static Asset Hosting (`/uploads`)](#14-static-asset-hosting)

---

## 1. Authentication

### 🔹 Register / Signup
`POST /api/auth/signup`  
*Creates a new user account.*

* **Auth Required**: `No`
* **Request Body** (`application/json`):
```json
{
  "name": "Sonu (Sanjeev)",
  "email": "sonu@example.com",
  "password": "password123",
  "age": 24,
  "weight": 72.5,
  "height": 178.0,
  "gender": "MALE",
  "fitnessGoal": "Muscle Gain & Hypertrophy"
}
```
* **Response** (`200 OK`):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Sonu (Sanjeev)",
  "email": "sonu@example.com"
}
```

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Sonu","email":"sonu@example.com","password":"password123"}'
```

---

### 🔹 Login
`POST /api/auth/login`  
*Authenticate user and obtain JWT token.*

* **Auth Required**: `No`
* **Request Body** (`application/json`):
```json
{
  "email": "sonu@example.com",
  "password": "password123"
}
```
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Sonu (Sanjeev)",
  "email": "sonu@example.com",
  "role": "ROLE_USER"
}
```

---

### 🔹 Admin Login
`POST /api/auth/admin/login`  
*Authenticate Gym Owner / Admin account and obtain Admin JWT token.*

* **Auth Required**: `No`
* **Default Credentials**:
  * **Email**: `admin@alphagym.com`
  * **Password**: `admin123`
* **Request Body** (`application/json`):
```json
{
  "email": "admin@alphagym.com",
  "password": "admin123"
}
```
* **Response** (`200 OK`):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Alpha Veins Admin",
  "email": "admin@alphagym.com",
  "onboardingCompleted": true,
  "role": "ROLE_ADMIN"
}
```

```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@alphagym.com","password":"admin123"}'
```

---

## 2. User Profile & Onboarding

### 🔹 Get User Profile
`GET /api/user/profile`  
*Fetch profile details, stats, and active membership status.*

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "id": 1,
  "name": "Sonu (Sanjeev)",
  "email": "sonu@example.com",
  "age": 24,
  "weight": 72.5,
  "height": 178.0,
  "gender": "MALE",
  "fitnessGoal": "Muscle Gain & Hypertrophy",
  "profilePhotoUrl": "/uploads/profile_1.jpg",
  "onboardingCompleted": true,
  "isMembershipActive": true,
  "activePlanName": "Pro Quarterly Pass",
  "planStartDate": "2026-08-18T00:00:00",
  "planExpiryDate": "2026-11-18T23:59:59",
  "daysRemaining": 92,
  "currentStreakDays": 7,
  "totalWorkoutsCompleted": 15
}
```

---

### 🔹 Update Profile Details
`PUT /api/user/profile`

* **Auth Required**: `Yes (Bearer)`
* **Request Body** (`application/json`):
```json
{
  "name": "Sonu Kumar",
  "age": 25,
  "weight": 74.0,
  "height": 178.0,
  "gender": "MALE",
  "fitnessGoal": "Strength & Endurance"
}
```

---

### 🔹 Upload Profile Photo
`PATCH /api/user/profile/photo`

* **Auth Required**: `Yes (Bearer)`
* **Content-Type**: `multipart/form-data`
* **Form Key**: `file` (Image file: JPG, PNG, WEBP)
* **Response** (`200 OK`):
```json
{
  "profilePhotoUrl": "/uploads/profile_1_1723948200.jpg"
}
```

---

### 🔹 Set Onboarding Completed
`PATCH /api/user/onboarding`

* **Auth Required**: `Yes (Bearer)`
* **Request Body** (`application/json`):
```json
{
  "completed": true
}
```

---

### 🔹 Check Active Membership Status
`GET /api/user/membership`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "isMembershipActive": true,
  "activePlanName": "Pro Quarterly Pass",
  "planStartDate": "2026-08-18T00:00:00",
  "planExpiryDate": "2026-11-18T23:59:59",
  "daysRemaining": 92
}
```

---

## 3. Membership Plans

### 🔹 Get All Active Plans
`GET /api/plans`

* **Auth Required**: `No`
* **Response** (`200 OK`):
```json
[
  {
    "id": 1,
    "name": "Monthly Starter Plan",
    "description": "Full access to gym equipment & lockers",
    "amount": 499.0,
    "currency": "INR",
    "durationMonths": 1,
    "active": true
  },
  {
    "id": 2,
    "name": "Pro Quarterly Pass",
    "description": "3 Months + Free Custom Diet Plan + Trainer Support",
    "amount": 1299.0,
    "currency": "INR",
    "durationMonths": 3,
    "active": true
  },
  {
    "id": 3,
    "name": "Elite Annual VIP",
    "description": "12 Months Unlimited + Free Personal Training Sessions",
    "amount": 3999.0,
    "currency": "INR",
    "durationMonths": 12,
    "active": true
  }
]
```

---

### 🔹 Create New Membership Plan (Admin)
`POST /api/plans`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "name": "Semi-Annual Power Pass",
  "description": "6 Months gym access with steam bath",
  "amount": 2399.0,
  "currency": "INR",
  "durationMonths": 6,
  "active": true
}
```

---

## 4. Razorpay Payments & PDF Receipts

### 🔹 Step 1: Create Razorpay Order
`POST /api/payment/create-order`

* **Auth Required**: `Yes (Bearer)`
* **Request Body** (`application/json`):
```json
{
  "planId": 2,
  "startDate": "2026-08-20"
}
```
*(Note: `startDate` is optional. If omitted, today's date is used. Cannot be in the past).*

* **Response** (`200 OK`):
```json
{
  "orderId": "order_TR08cesn10gukh",
  "keyId": "rzp_test_TPD8uVjfUAQbK1",
  "amountInPaisa": 129900,
  "currency": "INR",
  "status": "created"
}
```

---

### 🔹 Step 2: Verify Payment & Activate Membership
`POST /api/payment/verify`

* **Auth Required**: `Yes (Bearer)`
* **Request Body** (`application/json`):
```json
{
  "razorpayOrderId": "order_TR08cesn10gukh",
  "razorpayPaymentId": "pay_TPE_SUCCESS99",
  "razorpaySignature": "9f8e7d6c5b4a3a2b1c0d...",
  "planId": 2,
  "startDate": "2026-08-20"
}
```
* **Response** (`200 OK`):
```json
{
  "status": "SUCCESS",
  "message": "Payment verified & membership activated successfully starting on 2026-08-20",
  "paymentId": "pay_TPE_SUCCESS99"
}
```

---

### 🔹 Step 3: Get Payment History (With Receipt URLs)
`GET /api/payment/history` *(or alias `GET /api/user/payments`)*

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
[
  {
    "id": 2,
    "razorpayOrderId": "order_TR08cesn10gukh",
    "razorpayPaymentId": "pay_TPE_SUCCESS99",
    "amount": 1299.0,
    "currency": "INR",
    "planId": 2,
    "planName": "Pro Quarterly Pass",
    "status": "SUCCESS",
    "paymentDate": "2026-08-18T03:56:22",
    "receiptDownloadUrl": "/api/payment/receipt/2/download"
  }
]
```

---

### 🔹 Step 4: Preview / View Receipt PDF
`GET /api/payment/receipt/{transactionId}`

* **Auth Required**: `Yes (Bearer)`
* **Returns**: Binary PDF Stream (`application/pdf`, `Content-Disposition: inline`)

---

### 🔹 Step 5: Download Receipt PDF File
`GET /api/payment/receipt/{transactionId}/download`

* **Auth Required**: `Yes (Bearer)`
* **Returns**: PDF Attachment Download (`AlphaVeins_Receipt_{id}.pdf`)

```bash
curl -X GET http://localhost:8080/api/payment/receipt/2/download \
  -H "Authorization: Bearer <TOKEN>" \
  -o AlphaVeins_Receipt.pdf
```

---

### 🔹 Download Receipt by Razorpay Order ID
`GET /api/payment/receipt/order/{orderId}`

* **Auth Required**: `Yes (Bearer)`
* **Returns**: PDF Attachment Download (`AlphaVeins_Receipt_{orderId}.pdf`)

---

## 5. Workouts & Exercise Library

### 🔹 Get Paginated Workouts
`GET /api/workouts`

* **Auth Required**: `No`
* **Query Parameters**:
  * `page` (default: `0`)
  * `size` (default: `20`)
  * `category` (optional, e.g. `Chest`, `Biceps`, `Triceps`, `Back`, `Quads`, `Abs`, `Shoulder`, `Forearms`, `Calves`, `Neck`)
  * `difficulty` (optional, e.g. `Beginner`, `Intermediate`, `Advanced`)
  * `search` (optional keyword search, e.g. `bench`, `squat`, `curl`)
  * `sortBy` (default: `id`)
  * `sortDirection` (default: `asc`)

* **Example Request**:
```http
GET /api/workouts?page=0&size=2&category=Chest&search=press
```

* **Response** (`200 OK`):
```json
{
  "content": [
    {
      "id": 142,
      "name": "Barbell Bench Press",
      "description": "Targeted exercise for Pectoralis Major, Anterior Deltoid, Triceps. Execute with controlled tempo and full range of motion.",
      "category": "Chest",
      "difficulty": "Intermediate",
      "durationMinutes": 15,
      "imageUrl": "/uploads/muscles/pectoralis_major.gif",
      "videoUrl": "/uploads/exercises/Barbell_Bench_Press.gif",
      "targetMuscles": "Pectoralis Major, Anterior Deltoid, Triceps",
      "createdAt": "2026-08-18T07:12:30"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 436,
  "totalPages": 218,
  "last": false
}
```

---

### 🔹 Get Workout By ID
`GET /api/workouts/{id}`

* **Auth Required**: `No`

---

### 🔹 Get All Categories (10 Target Muscle Groups)
`GET /api/categories`

* **Auth Required**: `No`
* **Response** (`200 OK`):
```json
[
  { "id": 1, "name": "Neck" },
  { "id": 2, "name": "Shoulder" },
  { "id": 3, "name": "Chest" },
  { "id": 4, "name": "Biceps" },
  { "id": 5, "name": "Triceps" },
  { "id": 6, "name": "Abs" },
  { "id": 7, "name": "Forearms" },
  { "id": 8, "name": "Quads" },
  { "id": 9, "name": "Calves" },
  { "id": 10, "name": "Back" }
]
```

---

### 🔹 Upload 9:16 Workout Video (Admin)
`POST /api/workouts/upload-video`

* **Auth Required**: `Yes (Bearer)`
* **Content-Type**: `multipart/form-data`
* **Form Key**: `file` (MP4 / GIF / WEBM)
* **Response** (`200 OK`):
```json
{
  "videoUrl": "/uploads/exercises/custom_exercise_1.mp4"
}
```

---

## 6. Exercise Types

Based on **Cleveland Clinic** guidelines:

### 🔹 Get All Exercise Types
`GET /api/exercise-types` *(or alias `GET /api/types`)*

* **Auth Required**: `No`
* **Response** (`200 OK`):
```json
[
  {
    "id": 1,
    "name": "Resistance Training (Strength)",
    "description": "Produces tension in muscles through bodyweight, free weights, or machines.",
    "examples": "Barbell Squat, Bench Press, Dumbbell Rows, Deadlifts",
    "benefits": "Builds skeletal muscle, boosts metabolism, enhances bone density"
  },
  {
    "id": 2,
    "name": "High-Intensity Interval Training (HIIT)",
    "description": "Short bursts of intense anaerobic exercise with recovery periods.",
    "examples": "Sprint intervals, Tabata, Burpee circuits",
    "benefits": "Maximizes calorie burn, improves VO2 max and insulin sensitivity"
  }
]
```

---

## 7. User Routines

### 🔹 Get Saved User Routines
`GET /api/user/routine`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`): List of workout objects.

---

### 🔹 Add or Remove Workout in Routine
`POST /api/user/routine`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "workoutId": 142,
  "action": "ADD"
}
```
*(Action can be `"ADD"` or `"REMOVE"`).*

---

## 8. User Streaks

### 🔹 Get Current Streak
`GET /api/user/streaks`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "currentStreakDays": 7
}
```

---

### 🔹 Mark Today's Workout Complete (+1 Streak)
`POST /api/user/streaks/complete`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "currentStreakDays": 8
}
```

---

## 9. Weekly Workout Schedule

### 🔹 Get Schedule for Specific Day
`GET /api/user/schedule/{dayOfWeek}`  
*(Valid `dayOfWeek`: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`).*

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "dayOfWeek": "MONDAY",
  "focusArea": "Chest & Triceps",
  "notes": "Flat bench 4x10, Incline DB 3x12, Dips 3x15"
}
```

---

### 🔹 Update Schedule for Specific Day
`PUT /api/user/schedule/{dayOfWeek}`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "focusArea": "Back & Biceps (Pull Day)",
  "notes": "Deadlift 4x6, Lat Pulldown 4x12, Preacher Curls 3x12"
}
```

---

## 10. Water Intake Tracker

### 🔹 Get Today's Water Intake
`GET /api/user/water`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "waterIntakeMl": 1500,
  "targetWaterMl": 3000,
  "percentage": 50.0,
  "date": "2026-08-18"
}
```

---

### 🔹 Log Water Intake
`POST /api/user/water`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "amountMl": 250,
  "action": "ADD"
}
```
*(Actions: `"ADD"`, `"SET"`, `"RESET"`).*

---

## 11. Fitness Analytics

### 🔹 Get Today's Analytics Summary
`GET /api/analytics/today` *(or `GET /api/analytics/summary`)*

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "date": "2026-08-18",
  "steps": 7500,
  "caloriesBurned": 480.0,
  "activeMinutes": 55,
  "waterIntakeMl": 2000,
  "targetWaterMl": 3000
}
```

---

### 🔹 Log Daily Analytics
`POST /api/analytics/daily`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "steps": 8500,
  "caloriesBurned": 520.0,
  "activeMinutes": 60,
  "waterIntakeMl": 2500
}
```

---

## 12. Community Feed & Social

### 🔹 Get Feed Posts
`GET /api/feed`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`): List of community post objects with like count and author details.

---

### 🔹 Create Post
`POST /api/feed`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "caption": "Hit a new Bench Press PR today: 110 kg! 💪🔥",
  "imageUrl": "/uploads/posts/pr_photo.jpg"
}
```

---

### 🔹 Like / Unlike Post
`POST /api/feed/{postId}/like`

* **Auth Required**: `Yes (Bearer)`
* **Response** (`200 OK`):
```json
{
  "postId": 1,
  "likesCount": 24,
  "liked": true
}
```

---

### 🔹 Get Comments on Post
`GET /api/feed/{postId}/comments`

* **Auth Required**: `Yes (Bearer)`

---

### 🔹 Add Comment to Post
`POST /api/feed/{postId}/comments`

* **Auth Required**: `Yes (Bearer)`
* **Request Body**:
```json
{
  "text": "Awesome form and incredible progress! Keep it up! 👏"
}
```

---

## 13. System Health Check

### 🔹 Health Endpoint
`GET /api/health`

* **Auth Required**: `No`
* **Response** (`200 OK`):
```json
{
  "status": "UP",
  "database": "UP",
  "service": "alpha-veins-backend"
}
```

---

## 14. Static Asset Hosting

All downloaded muscle highlight diagrams, exercise video demonstrations, and user uploads are served statically without authentication:

| Asset Type | URL Format | Example URL |
|:---|:---|:---|
| **Target Muscle Diagrams** | `/uploads/muscles/{file}` | `http://localhost:8080/uploads/muscles/latissimus_dorsi.gif` |
| **Exercise Demo Videos / GIFs** | `/uploads/exercises/{file}` | `http://localhost:8080/uploads/exercises/Barbell_Bench_Press.gif` |
| **User Profile Photos** | `/uploads/{file}` | `http://localhost:8080/uploads/profile_1.jpg` |

---

*© 2026 Alpha Veins Gym. All Rights Reserved.*
