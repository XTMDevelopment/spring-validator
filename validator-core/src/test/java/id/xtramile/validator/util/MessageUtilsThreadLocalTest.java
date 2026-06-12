package id.xtramile.validator.util;

import id.xtramile.validator.enums.Group;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression tests for Phase 4 {@link MessageUtils} ThreadLocal cleanup
 * ({@code ARG_STORAGE.remove()} on {@link MessageUtils#clearStoredArgs()}).
 */
class MessageUtilsThreadLocalTest {

    private static ConstraintValidatorContext mockContext() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
        return context;
    }

    @SuppressWarnings("unchecked")
    private static ThreadLocal<Map<String, Object[]>> argStorage() throws Exception {
        Field field = MessageUtils.class.getDeclaredField("ARG_STORAGE");
        field.setAccessible(true);
        return (ThreadLocal<Map<String, Object[]>>) field.get(null);
    }

    @AfterEach
    void tearDown() {
        MessageUtils.clearStoredArgs();
    }

    @Test
    void storeAndRetrieveArgs_removesEntryOnGet() {
        ConstraintValidatorContext context = mockContext();

        MessageUtils.buildViolation(context, Group.DATA, "test.key", "arg1", "arg2");

        String key = "validation.data.test.key";
        Object[] args = MessageUtils.getStoredArgs(key);

        assertThat(args).containsExactly("arg1", "arg2");
        assertThat(MessageUtils.getStoredArgs(key)).isNull();
    }

    @Test
    void clearStoredArgs_removesStoredEntries() {
        ConstraintValidatorContext context = mockContext();

        MessageUtils.buildViolation(context, Group.DATA, "test.key", "arg1");

        MessageUtils.clearStoredArgs();

        assertThat(MessageUtils.getStoredArgs("validation.data.test.key")).isNull();
    }

    @Test
    void clearStoredArgs_removesThreadLocalEntry_preventingPoolLeak() throws Exception {
        ConstraintValidatorContext context = mockContext();
        ThreadLocal<Map<String, Object[]>> storage = argStorage();

        MessageUtils.buildViolation(context, Group.DATA, "test.key", "arg1");
        Map<String, Object[]> mapBeforeClear = storage.get();
        assertThat(mapBeforeClear).isNotEmpty();

        MessageUtils.clearStoredArgs();

        Map<String, Object[]> mapAfterClear = storage.get();
        assertThat(mapAfterClear).isNotSameAs(mapBeforeClear);
        assertThat(mapAfterClear).isEmpty();
    }
}
