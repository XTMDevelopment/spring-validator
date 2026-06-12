package id.xtramile.validator.validator.finance;

import id.xtramile.validator.annotation.finance.ValidTransactionAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionAmountValidatorTest {

    private TransactionAmountValidator validator;

    private static ValidTransactionAmount getAnnotation(String fieldName) {
        try {
            Field f = TransactionAmountDummy.class.getDeclaredField(fieldName);
            return f.getAnnotation(ValidTransactionAmount.class);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        validator = new TransactionAmountValidator();
    }

    @Test
    void testDefaultValidation() {
        validator.initialize(getAnnotation("defaultAmount"));

        assertTrue(validator.isValid(null, null)); // Null allowed
        assertTrue(validator.isValid(0L, null)); // Zero allowed by default
        assertTrue(validator.isValid(1_000_000L, null)); // Valid amount
        assertTrue(validator.isValid(500_000_000L, null)); // Large amount
        assertTrue(validator.isValid(Long.MAX_VALUE, null)); // Maximum long value
    }

    @Test
    void testRangeValidation() {
        validator.initialize(getAnnotation("rangeAmount"));

        // Valid cases
        assertTrue(validator.isValid(500_000L, null)); // Minimum boundary
        assertTrue(validator.isValid(2_500_000_000L, null)); // Middle range
        assertTrue(validator.isValid(5_000_000_000L, null)); // Maximum boundary

        // Invalid cases
        assertFalse(validator.isValid(499_999L, null)); // Below minimum
        assertFalse(validator.isValid(5_000_000_001L, null)); // Above maximum
        assertFalse(validator.isValid(0L, null)); // Zero amount
        assertFalse(validator.isValid(-1L, null)); // Negative amount
    }

    @Test
    void testNoZeroValidation() {
        validator.initialize(getAnnotation("noZeroAmount"));

        assertTrue(validator.isValid(1_000_000L, null)); // Minimum
        assertTrue(validator.isValid(100_000_000L, null)); // Valid amount
        assertTrue(validator.isValid(5_000_000_000L, null)); // Large amount
        assertFalse(validator.isValid(0L, null)); // Zero not allowed
        assertFalse(validator.isValid(999_999L, null)); // Below minimum
    }

    @Test
    void testBoundedAmounts() {
        validator.initialize(getAnnotation("boundedAmount"));

        assertTrue(validator.isValid(0L, null)); // Minimum (zero allowed)
        assertTrue(validator.isValid(500_000_000L, null)); // Middle range
        assertTrue(validator.isValid(1_000_000_000L, null)); // Maximum
        assertFalse(validator.isValid(1_000_000_001L, null)); // Above maximum
        assertFalse(validator.isValid(-1L, null)); // Below minimum
    }

    @Test
    void testNegativeAmounts() {
        validator.initialize(getAnnotation("defaultAmount"));

        assertFalse(validator.isValid(-1L, null)); // Negative amount (below min of 0)
        assertFalse(validator.isValid(-1_000_000L, null)); // Large negative amount
    }

    @Test
    void testLargeNumberRange() {
        validator.initialize(getAnnotation("rangeAmount"));

        assertTrue(validator.isValid(500_000L, null)); // Minimum (500k)
        assertTrue(validator.isValid(10_000_000L, null)); // 10M
        assertTrue(validator.isValid(100_000_000L, null)); // 100M
        assertTrue(validator.isValid(1_000_000_000L, null)); // 1B
        assertTrue(validator.isValid(5_000_000_000L, null)); // 5B (maximum)
    }

    private static class TransactionAmountDummy {
        @ValidTransactionAmount
        Long defaultAmount;

        @ValidTransactionAmount(min = 500_000L, max = 5_000_000_000L)
        Long rangeAmount;

        @ValidTransactionAmount(min = 1_000_000L, allowZero = false)
        Long noZeroAmount;

        @ValidTransactionAmount(max = 1_000_000_000L)
        Long boundedAmount;
    }
}
