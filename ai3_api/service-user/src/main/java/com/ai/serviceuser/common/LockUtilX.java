package com.ai.serviceuser.common;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class LockUtilX {

    private final Cache<String, Object> lockCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public Object getLock(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Lock key must not be null");
        }
        return lockCache.get(key, k -> new Object());
    }

    public Object getLock(Long key) {
        if (key == null) {
            throw new IllegalArgumentException("Lock key must not be null");
        }
        return getLock(key.toString());
    }

}
