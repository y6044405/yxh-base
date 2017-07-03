package com.tzg.tool.kit.asserts;

import org.apache.commons.lang3.StringUtils;

/**
 * 断言
 */
public abstract class Assert {

    protected Assert() {
    }
    public static void isNull(Object object, String message)   {
        if (object == null) {
             throw new IllegalArgumentException(message);
        }
    }
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void areNotNull(String message, Object... obj) {
        notNull(obj, message);
        for (Object object : obj) {
            if (object == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    public static void equals(Object obj, Object obj2, String message) {
        if (obj == null && obj2 == null) {
            return;
        }
        if (obj == null || obj2 == null) {
            throw new IllegalArgumentException(message);
        }
        if (!obj.equals(obj2)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText(String text) {
        hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    public static void hasText(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notTrue(boolean expression, String message)  {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
