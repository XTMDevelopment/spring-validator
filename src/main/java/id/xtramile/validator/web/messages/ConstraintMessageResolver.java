package id.xtramile.validator.web.messages;

import java.util.Map;

public interface ConstraintMessageResolver {

    boolean supports(Class<?> annotationType);

    String resolve(String field, Class<?> annotationType, Map<String, Object> attrs, Class<?> dtoClass);
}
