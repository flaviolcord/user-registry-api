package com.flaviolcord.user.registry.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SensitiveDataSanitizer {

    public static String sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";

        return Arrays.stream(args)
                .map(SensitiveDataSanitizer::sanitizeArg)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public static String sanitizeArg(Object arg) {
        if (arg == null) return "null";

        if (arg instanceof Collection<?>) {
            return ((Collection<?>) arg).stream()
                    .map(SensitiveDataSanitizer::sanitizeArg)
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        if (arg.getClass().isArray()) {
            return sanitizeArgs((Object[]) arg);
        }

        if (arg instanceof String) {
            return sanitizeString((String) arg);
        }

        if (arg instanceof LocalDate) {
            return sanitizeDate((LocalDate) arg);
        }

        if (isSensitiveObject(arg)) {
            return sanitizeSensitiveObject(arg);
        }

        return truncate(arg.toString(), 100);
    }

    private static String sanitizeString(String str) {
        if ("Male".equalsIgnoreCase(str) || "Female".equalsIgnoreCase(str) || "Other".equalsIgnoreCase(str)) {
            return "***";
        }

        if (str.matches("\\d{10,}")) {
            return maskPhoneNumber(str);
        }

        return str.length() > 2 ? str.substring(0, 2) + "****" : "****";
    }

    private static String maskPhoneNumber(String phoneNumber) {
        return "***" + phoneNumber.substring(Math.max(0, phoneNumber.length() - 4));
    }

    private static String sanitizeDate(LocalDate date) {
        return date == null ? "null" : date.getYear() + "-01-01";
    }

    private static boolean isSensitiveObject(Object obj) {
        return obj.getClass().getSimpleName().contains("User");
    }

    private static String sanitizeSensitiveObject(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.getName() + "=" + sanitizeArg(field.get(obj));
                    } catch (IllegalAccessException e) {
                        return field.getName() + "=<access denied>";
                    }
                })
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private static String truncate(String str, int maxLength) {
        return str.length() > maxLength ? str.substring(0, maxLength) + "..." : str;
    }
}
