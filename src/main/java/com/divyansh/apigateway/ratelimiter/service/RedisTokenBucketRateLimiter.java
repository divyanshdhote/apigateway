package com.divyansh.apigateway.ratelimiter.service;

import com.divyansh.apigateway.storage.redis.RedisRateLimiter;
import org.springframework.stereotype.Service;

@Service
public class RedisTokenBucketRateLimiter implements RateLimiter {

    private final RedisRateLimiter redis;

    private final int capacity = 1000;
    private final int refillRate = 500; // tokens per second

    public RedisTokenBucketRateLimiter(RedisRateLimiter redis) {
        this.redis = redis;
    }

    @Override
    public boolean allowRequest(String key) {

        long now = System.currentTimeMillis();

        String value = redis.get(key);

        long tokens;
        long lastRefillTime;

        if (value == null) {
            // First request
            tokens = capacity;
            lastRefillTime = now;
        } else {
            String[] parts = value.split(":");
            tokens = Long.parseLong(parts[0]);
            lastRefillTime = Long.parseLong(parts[1]);
        }

        // 🔥 Refill logic
        long elapsedTime = now - lastRefillTime;
        long tokensToAdd = (elapsedTime / 1000) * refillRate;

        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }

        // 🔥 Consume token
        if (tokens > 0) {
            tokens--;

            redis.set(key, tokens + ":" + lastRefillTime);

            System.out.println("ALLOW → " + key + " | tokens=" + tokens);
            return true;
        }

        redis.set(key, tokens + ":" + lastRefillTime);

        System.out.println("REJECT → " + key + " | tokens=" + tokens);
        return false;
    }
}