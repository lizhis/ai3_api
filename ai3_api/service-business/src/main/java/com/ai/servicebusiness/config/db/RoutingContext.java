package com.ai.servicebusiness.config.db;

public class RoutingContext {
    private static final ThreadLocal<Boolean> isRead = ThreadLocal.withInitial(() -> false);

    public static void markRead() {
        isRead.set(true);
    }

    public static void markWrite() {
        isRead.set(false);
    }

    public static boolean isRead() {
        return Boolean.TRUE.equals(isRead.get());
    }

    public static void clear() {
        isRead.remove();
    }
}
