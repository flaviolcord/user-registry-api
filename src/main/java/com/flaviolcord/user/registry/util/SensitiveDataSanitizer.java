package com.flaviolcord.user.registry.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Utility class for sanitizing sensitive data from objects, strings, collections, and arrays.
 * It provides methods to mask or truncate sensitive information, such as personal details,
 * to ensure data security and privacy.
 * <p>
 * This class is particularly useful for logging or displaying data without exposing sensitive
 * information such as phone numbers, user details, or gender.
 * </p>
 *
 * <p><strong>Examples of sanitization:</strong></p>
 * <ul>
 * <li>Phone numbers are masked to hide all but the last four digits.</li>
 * <li>Strings are truncated or replaced with placeholders when sensitive.</li>
 * <li>Custom objects containing sensitive fields are sanitized recursively.</li>
 * </ul>
 *
 * <p><strong>Thread Safety:</strong> This class is thread-safe as it operates only on provided
 * inputs without maintaining any state.</p>
 */
public class SensitiveDataSanitizer {

    /**
     * Default constructor for {@code SensitiveDataSanitizer}.
     * <p>
     * This constructor is intentionally private, as this class is a utility class with only
     * static methods and does not require any instance initialization.
     * </p>
     */
    private SensitiveDataSanitizer() { }

    /**
     * Sanitizes an array of objects by applying appropriate sanitization rules to each element.
     *
     * @param args the array of objects to sanitize; can contain {@code null}, collections,
     *             arrays, or other objects
     * @return a string representation of the sanitized array, with elements separated by commas
     *         and enclosed in square brackets
     */
    public static String sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";

        return Arrays.stream(args)
                .map(SensitiveDataSanitizer::sanitizeArg)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * Sanitizes a single object by applying sanitization rules based on its type.
     * <p>
     * Supported types include:
     * <ul>
     * <li>{@code String}: Strings are masked or truncated based on their content.</li>
     * <li>{@code Collection}: Elements in the collection are sanitized recursively.</li>
     * <li>Arrays: Elements are sanitized recursively.</li>
     * <li>{@code LocalDate}: Dates are normalized to display only the year.</li>
     * <li>Custom objects: Fields of sensitive objects are sanitized recursively.</li>
     * </ul>
     * </p>
     *
     * @param arg the object to sanitize
     * @return a sanitized string representation of the object
     */
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

        if (arg instanceof String str) {
            return sanitizeString(str);
        }

        if (arg instanceof LocalDate date) {
            return sanitizeDate(date);
        }

        if (isSensitiveObject(arg)) {
            return sanitizeSensitiveObject(arg);
        }

        return truncate(arg.toString());
    }

    /**
     * Masks sensitive strings.
     * <ul>
     * <li>Gender strings ("Male", "Female", "Other") are replaced with {@code "***"}.</li>
     * <li>Phone numbers with 10 or more digits are masked, showing only the last 4 digits.</li>
     * <li>Other strings are truncated or replaced with placeholders.</li>
     * </ul>
     *
     * @param str the string to sanitize
     * @return a sanitized version of the string
     */
    private static String sanitizeString(String str) {
        if ("Male".equalsIgnoreCase(str) || "Female".equalsIgnoreCase(str) || "Other".equalsIgnoreCase(str)) {
            return "***";
        }

        if (str.matches("\\d{10,}")) {
            return maskPhoneNumber(str);
        }

        return str.length() > 2 ? str.substring(0, 2) + "****" : "****";
    }

    /**
     * Masks a phone number, leaving only the last four digits visible.
     *
     * @param phoneNumber the phone number to mask
     * @return the masked phone number
     */
    private static String maskPhoneNumber(String phoneNumber) {
        return "***" + phoneNumber.substring(Math.max(0, phoneNumber.length() - 4));
    }

    /**
     * Normalizes a {@link LocalDate} by displaying only the year, with the month and day set to
     * {@code 01-01}.
     *
     * @param date the date to sanitize
     * @return a sanitized string representation of the date
     */
    private static String sanitizeDate(LocalDate date) {
        return date == null ? "null" : date.getYear() + "-01-01";
    }

    /**
     * Checks if an object is considered sensitive based on its class name.
     * <p>
     * Objects are considered sensitive if their class name contains the term "User".
     * </p>
     *
     * @param obj the object to check
     * @return {@code true} if the object is considered sensitive; {@code false} otherwise
     */
    private static boolean isSensitiveObject(Object obj) {
        return obj.getClass().getSimpleName().contains("User");
    }

    /**
     * Sanitizes a custom object by sanitizing all its fields recursively.
     *
     * @param obj the object to sanitize
     * @return a sanitized string representation of the object, with fields and their values
     */
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

    /**
     * Truncates a string to the specified maximum length and appends {@code "..."} if the string
     * exceeds the maximum length (100).
     *
     * @param str the string to truncate
     * @return the truncated string
     */
    private static String truncate(String str) {
        return str.length() > 100 ? str.substring(0, 100) + "..." : str;
    }
}
