package com.divyansh.apigateway.ratelimiter.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Service
public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity = 3;
    private final int refillRate = 1; // tokens per second

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean allowRequest(String key) {

        Bucket bucket = buckets.computeIfAbsent(key,
                k -> new Bucket(capacity, System.currentTimeMillis()));

        synchronized (bucket) {
            refill(bucket);

            if (bucket.tokens > 0) {
                bucket.tokens--;
                System.out.println("ALLOW → User: " + key + " | Tokens left: " + bucket.tokens);
                return true;
            }

            System.out.println("REJECT → User: " + key + " | Tokens left: " + bucket.tokens);
            return false;
        }
    }

    private void refill(Bucket bucket) {
        long now = System.currentTimeMillis();
        long elapsedTime = now - bucket.lastRefillTime;

        long tokensToAdd = (elapsedTime / 1000) * refillRate;

        if (tokensToAdd > 0) {
            bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;

            System.out.println("REFILL → Tokens requested: " + tokensToAdd + " | New total: " + bucket.tokens);
        }
    }

    static class Bucket {
        long tokens;
        long lastRefillTime;

        Bucket(long tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}