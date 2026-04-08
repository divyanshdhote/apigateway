# 🚀 API Gateway with Distributed Rate Limiting

A lightweight **API Gateway** built using Spring WebFlux that enforces **per-user rate limiting** using the **Token Bucket algorithm**, with support for **distributed state via Redis**.

---

## 📌 Overview

This project implements a minimal yet production-relevant API Gateway that:

* Intercepts incoming HTTP requests using a custom WebFlux filter
* Applies **rate limiting per user**
* Uses **Token Bucket algorithm with time-based refill**
* Stores state in **Redis** to support horizontal scaling
* Supports multiple users via request-based identity (header/JWT-ready design)

---

## ⚙️ Tech Stack

* Java 17
* Spring Boot (WebFlux)
* Redis
* Maven

---

## 🧠 Architecture

```
Client Request
     ↓
RateLimitingFilter (WebFlux)
     ↓
Extract User ID (Header/JWT-ready)
     ↓
RateLimiter (Token Bucket)
     ↓
Redis (tokens + timestamp)
     ↓
Allow ✅ / Reject ❌ (HTTP 429)
```

---

## 🔥 Features

### ✅ Reactive Gateway

* Built using **Spring WebFlux**
* Non-blocking request handling

### ✅ Token Bucket Rate Limiting

* Configurable:

  * Capacity (max burst)
  * Refill rate (tokens/sec)
* Supports burst traffic with controlled throttling

### ✅ Distributed State (Redis)

* Stores:

  ```
  key → tokens:lastRefillTimestamp
  ```
* Enables consistent rate limiting across multiple instances

### ✅ Per-User Isolation

* Each user has an independent token bucket
* Identity extracted from request headers (JWT-ready design)

---

## 🧪 How It Works

### Example Flow

1. User sends request with header:

   ```
   X-User-Id: user-1
   ```

2. Gateway:

   * Reads current tokens from Redis
   * Calculates refill based on elapsed time
   * Consumes a token if available

3. Response:

   * ✅ Allowed → `200 OK`
   * ❌ Rate limited → `429 TOO MANY REQUESTS`

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd api-gateway
```

---

### 2. Start Redis

Using Docker:

```bash
docker run -p 6379:6379 redis
```

---

### 3. Run the application

```bash
mvn spring-boot:run
```

---

### 4. Test the API

#### 🔹 Single User

```bash
curl -H "X-User-Id: user-1" http://localhost:8080/test
```

#### 🔹 Multiple Users

```bash
# user-1
curl -H "X-User-Id: user-1" http://localhost:8080/test

# user-2
curl -H "X-User-Id: user-2" http://localhost:8080/test
```

---

### 🔁 Load Testing

```bash
for i in {1..10}; do 
  curl -H "X-User-Id: user-1" -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/test
done
```

---

## ⚙️ Configuration

### Rate Limiter Parameters

```java
capacity = 3          // max tokens
refillRate = 1        // tokens per second
```

---

## ⚠️ Limitations

* Not fully atomic (race conditions possible under high concurrency)
* Uses simple Redis GET/SET (can be improved with Lua scripts)
* Fixed TTL-based key expiration
* No dynamic configuration (static limits)

---

## 🚧 Future Improvements

* 🔄 Atomic updates using Redis Lua scripts
* 📊 Metrics & monitoring (Prometheus)
* 🔐 Full JWT authentication integration
* ⚙️ Dynamic rate limit configuration
* 🌐 API-level + user-level throttling

---

## 💡 Key Learnings

* Designing a **non-blocking API Gateway** using WebFlux
* Implementing **Token Bucket algorithm** correctly with time-based refill
* Handling **distributed state using Redis**
* Understanding trade-offs between simplicity and correctness

---

## 📄 License

This project is for learning and demonstration purposes.
