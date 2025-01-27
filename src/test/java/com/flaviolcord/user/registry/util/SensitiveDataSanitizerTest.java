package com.flaviolcord.user.registry.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SensitiveDataSanitizerTest {

    @Test
    void testSanitizeArgs_nullArgs() {
        assertEquals("[]", SensitiveDataSanitizer.sanitizeArgs(null));
    }

    @Test
    void testSanitizeArgs_emptyArgs() {
        assertEquals("[]", SensitiveDataSanitizer.sanitizeArgs(new Object[]{}));
    }

    @Test
    void testSanitizeArgs_mixedArgs() {
        Object[] args = {
                "SensitiveData",
                LocalDate.of(2023, 1, 1),
                12345,
                List.of("Male", "1234567890", "RegularData"),
                new String[]{"ArrayElement1", "ArrayElement2"}
        };

        String expected = "[Se****, 2023-01-01, 12345, [***, ***7890, Re****], [Ar****, Ar****]]";
        assertEquals(expected, SensitiveDataSanitizer.sanitizeArgs(args));
    }

    @Test
    void testSanitizeArg_nullArg() {
        assertEquals("null", SensitiveDataSanitizer.sanitizeArg(null));
    }

    @Test
    void testSanitizeArg_collectionArg() {
        List<String> collection = List.of("Male", "1234567890", "NormalText");
        String expected = "[***, ***7890, No****]";
        assertEquals(expected, SensitiveDataSanitizer.sanitizeArg(collection));
    }

    @Test
    void testSanitizeArg_arrayArg() {
        Object[] array = {"Array1", "1234567890", LocalDate.of(2023, 5, 15)};
        String expected = "[Ar****, ***7890, 2023-01-01]";
        assertEquals(expected, SensitiveDataSanitizer.sanitizeArg(array));
    }

    @Test
    void testSanitizeArg_sensitiveString() {
        assertEquals("***", SensitiveDataSanitizer.sanitizeArg("Male"));
        assertEquals("***", SensitiveDataSanitizer.sanitizeArg("Female"));
        assertEquals("***", SensitiveDataSanitizer.sanitizeArg("Other"));
    }

    @Test
    void testSanitizeArg_regularString() {
        assertEquals("Se****", SensitiveDataSanitizer.sanitizeArg("SensitiveData"));
        assertEquals("****", SensitiveDataSanitizer.sanitizeArg("A"));
    }

    @Test
    void testSanitizeArg_phoneNumber() {
        assertEquals("***7890", SensitiveDataSanitizer.sanitizeArg("1234567890"));
    }

    @Test
    void testSanitizeArg_localDate() {
        assertEquals("2023-01-01", SensitiveDataSanitizer.sanitizeArg(LocalDate.of(2023, 5, 15)));
    }

    @Test
    void testSanitizeArg_sensitiveObject() {
        // sensitive object, since contains "user" in the name
        record UserTest(
                String username,
                String password,
                LocalDate birthdate
        ){}

        UserTest user = new UserTest("SensitiveUser", "123456", LocalDate.of(1990, 1, 1));
        String expected = "{username=Se****, password=12****, birthdate=1990-01-01}";
        assertEquals(expected, SensitiveDataSanitizer.sanitizeArg(user));
    }

    @Test
    void testSanitizeArg_nonSensitiveObject() {
        // non-sensitive object
        record Product(String name, double price){}
        Product product = new Product("Product1", 99.99);

        // Expecting the default toString() output for Product since it will be ignored
        String expected = product.toString();
        assertEquals(expected, SensitiveDataSanitizer.sanitizeArg(product));
    }

    @Test
    void testSanitizeArg_truncateString() {
        String longString = "This is a very long string that needs to be truncated.";
        String expected = "Th****";;
        assertEquals(expected, SensitiveDataSanitizer.sanitizeArg(longString));
    }
}