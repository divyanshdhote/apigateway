package com.divyansh.apigateway.ratelimiter.service;

public interface RateLimiter {
    boolean allowRequest(String key);
}