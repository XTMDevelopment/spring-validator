package id.xtramile.validator.util;

import id.xtramile.validator.enums.Group;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageUtilsTest {

    @AfterEach
    void tearDown() {
        MessageUtils.clearStoredArgs();
    }

    @Test
    void testBuildViolationWithGroupAndKey() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        MessageUtils.buildViolation(context, Group.DATA, "test.key");

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("validation.data.test.key");
        verify(builder).addConstraintViolation();
    }

    @Test
    void testBuildViolationWithGroupKeyAndArgs() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        MessageUtils.buildViolation(context, Group.CONTACT, "email.invalid", "min", 5);

        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("validation.contact.email.invalid");
        verify(builder).addConstraintViolation();

        Object[] args = MessageUtils.getStoredArgs("validation.contact.email.invalid");
        assertNotNull(args);
        assertEquals(2, args.length);
        assertEquals("min", args[0]);
        assertEquals(5, args[1]);
    }

    @Test
    void testBuildViolationWithNullContext() {
        assertDoesNotThrow(() -> MessageUtils.buildViolation(null, Group.DATA, "test.key"));
        assertDoesNotThrow(() -> MessageUtils.buildViolation(null, Group.DATA, "test.key", "arg1", "arg2"));
    }

    @Test
    void testBuildViolationWithNullArgs() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        MessageUtils.buildViolation(context, Group.DATA, "test.key", (Object[]) null);

        verify(context).buildConstraintViolationWithTemplate("validation.data.test.key");

        Object[] args = MessageUtils.getStoredArgs("validation.data.test.key");
        assertNull(args);
    }

    @Test
    void testGetStoredArgs() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        String key = "validation.data.test.key";
        MessageUtils.buildViolation(context, Group.DATA, "test.key", "arg1", "arg2");

        Object[] args = MessageUtils.getStoredArgs(key);
        assertNotNull(args);
        assertEquals(2, args.length);
        assertEquals("arg1", args[0]);
        assertEquals("arg2", args[1]);

        Object[] argsAgain = MessageUtils.getStoredArgs(key);
        assertNull(argsAgain);
    }

    @Test
    void testGetStoredArgsNotFound() {
        assertNull(MessageUtils.getStoredArgs("nonexistent.key"));
    }

    @Test
    void testClearStoredArgs() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        MessageUtils.buildViolation(context, Group.DATA, "test.key", "arg1");

        MessageUtils.clearStoredArgs();

        Object[] args = MessageUtils.getStoredArgs("validation.data.test.key");
        assertNull(args);
    }

    @Test
    void testJoin() {
        Set<String> set = Set.of("a", "b", "c");
        String result = MessageUtils.join(set);
        assertNotNull(result);
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
        assertTrue(result.contains(", "));
    }

    @Test
    void testJoinEmptySet() {
        Set<String> emptySet = Set.of();
        String result = MessageUtils.join(emptySet);
        assertEquals("", result);
    }

    @Test
    void testBuildKey() {
        assertEquals("validation.data.test", MessageUtils.buildKey(Group.DATA, "test"));
        assertEquals("validation.contact.email", MessageUtils.buildKey(Group.CONTACT, "email"));
        assertEquals("validation.finance.amount", MessageUtils.buildKey(Group.FINANCE, "amount"));
    }
}
