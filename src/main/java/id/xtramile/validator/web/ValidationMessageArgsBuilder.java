package id.xtramile.validator.web;

import id.xtramile.validator.util.AnnotationUtils;
import id.xtramile.validator.util.DateUtils;
import id.xtramile.validator.util.MessageUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;

/**
 * Builds message-format arguments from constraint annotation attributes.
 */
public class ValidationMessageArgsBuilder {

    private final ValidationFieldDisplayNames fieldNames;

    /**
     * Creates a builder that uses the given field display name resolver.
     *
     * @param fieldNames resolves display names for cross-field message arguments
     */
    public ValidationMessageArgsBuilder(ValidationFieldDisplayNames fieldNames) {
        this.fieldNames = fieldNames;
    }

    /**
     * Joins string values in sorted order with comma separators.
     *
     * @param values the values to join
     * @return the joined string, or empty if null or empty
     */
    public static String joinSortedComma(String[] values) {
        if (values == null || values.length == 0) {
            return "";
        }
        String[] copy = Arrays.copyOf(values, values.length);
        Arrays.sort(copy);
        return String.join(", ", copy);
    }

    Map<String, Object> constraintAttributesForFieldError(Class<?> dtoClass, String fieldName, Class<?> annotationType) {
        if (dtoClass == null || annotationType == null) {
            return new HashMap<>();
        }

        Annotation onField = AnnotationUtils.findFieldAnnotation(dtoClass, fieldName, annotationType);
        if (onField != null) {
            return AnnotationUtils.extractAnnotationValues(onField);
        }

        Target target = annotationType.getAnnotation(Target.class);
        if (target != null) {
            for (ElementType et : target.value()) {
                if (et == ElementType.TYPE) {
                    @SuppressWarnings("unchecked")
                    Annotation classAnn = dtoClass.getAnnotation((Class<? extends Annotation>) annotationType);

                    if (classAnn != null) {
                        return AnnotationUtils.extractAnnotationValues(classAnn);
                    }
                    break;
                }
            }
        }

        return AnnotationUtils.getAnnotationAttributes(dtoClass, fieldName, annotationType);
    }

    /**
     * Builds message-format arguments for a validation template and constraint attributes.
     *
     * @param fieldName display name of the validated field
     * @param attrs     constraint annotation attributes
     * @param template  the validation message template key
     * @param dtoClass  the validated DTO class
     * @return format arguments for the message template
     */
    public Object[] buildMessageArgs(String fieldName, Map<String, Object> attrs, String template, Class<?> dtoClass) {
        Object[] storedArgs = MessageUtils.getStoredArgs(template);

        if (storedArgs != null && storedArgs.length > 0) {
            if ("validation.cross.field-match".equals(template) && storedArgs.length >= 2) {
                return new Object[]{
                        fieldNames.resolve(dtoClass, (String) storedArgs[0]),
                        fieldNames.resolve(dtoClass, (String) storedArgs[1])
                };
            }

            if ("validation.cross.different-from".equals(template) && storedArgs.length >= 2) {
                return new Object[]{
                        fieldNames.resolve(dtoClass, (String) storedArgs[0]),
                        fieldNames.resolve(dtoClass, (String) storedArgs[1])
                };
            }

            List<Object> args = new ArrayList<>();
            args.add(fieldName);
            args.addAll(Arrays.asList(storedArgs));

            return args.toArray();
        }

        if (attrs != null) {
            Object[] fromAttrs = buildMessageArgsFromAnnotationAttributes(fieldName, attrs, template, dtoClass);

            if (fromAttrs != null) {
                return fromAttrs;
            }
        }

        List<Object> args = new ArrayList<>();
        args.add(fieldName);

        if (attrs != null && attrs.containsKey("length")) {
            args.add(attrs.get("length"));
        }

        if (attrs != null && attrs.containsKey("min")) {
            args.add(attrs.get("min"));
        }

        if (attrs != null && attrs.containsKey("max")) {
            args.add(attrs.get("max"));
        }

        if (attrs != null && attrs.containsKey("value")) {
            args.add(attrs.get("value"));
        }

        if (attrs != null && attrs.containsKey("integer")) {
            args.add(attrs.get("integer"));
        }

        if (attrs != null && attrs.containsKey("fraction")) {
            args.add(attrs.get("fraction"));
        }

        return args.toArray();
    }

    private Object[] buildMessageArgsFromAnnotationAttributes(String fieldName, Map<String, Object> attrs, String template,
                                                              Class<?> dtoClass) {
        if ("validation.common.in-whitelist".equals(template) || "validation.common.in-whitelist.sensitive".equals(template)) {
            if (attrs.containsKey("values")) {
                String joined = joinSortedComma((String[]) attrs.get("values"));
                return new Object[]{fieldName, joined};
            }
        }

        if ("validation.common.not-in-blacklist".equals(template) || "validation.common.not-in-blacklist.sensitive".equals(template)) {
            if (attrs.containsKey("values")) {
                String joined = joinSortedComma((String[]) attrs.get("values"));
                return new Object[]{fieldName, joined};
            }
        }

        if ("validation.contact.email-domain".equals(template) || "validation.contact.email-domain.sensitive".equals(template)) {
            if (attrs.containsKey("allowed")) {
                String joined = joinSortedComma((String[]) attrs.get("allowed"));
                return new Object[]{fieldName, joined};
            }
        }

        if ("validation.common.enum".equals(template) || "validation.common.enum.sensitive".equals(template)) {
            if (attrs.containsKey("enumClass")) {
                @SuppressWarnings("unchecked")
                Class<? extends Enum<?>> ec = (Class<? extends Enum<?>>) attrs.get("enumClass");
                String label = ec != null ? ec.getSimpleName() : "";
                return new Object[]{fieldName, label};
            }
        }

        if ("validation.datetime.datetime".equals(template) || "validation.datetime.date".equals(template)
                || "validation.datetime.time".equals(template)) {
            if (attrs.containsKey("pattern")) {
                return new Object[]{fieldName, attrs.get("pattern")};
            }
        }

        if (template != null && template.startsWith("validation.datetime.") && template.endsWith(".pattern") && attrs.containsKey("pattern")) {
            return new Object[]{fieldName, attrs.get("pattern")};
        }

        if ("validation.datetime.invalid-past-date.tolerance".equals(template) && attrs.containsKey("tolerance")) {
            return new Object[]{fieldName, attrs.get("tolerance")};
        }

        if ("validation.datetime.invalid-future-date.tolerance".equals(template) && attrs.containsKey("toleranceHours")) {
            return new Object[]{fieldName, attrs.get("toleranceHours")};
        }

        if ("validation.datetime.invalid-past-future-date.tolerance".equals(template) && attrs.containsKey("toleranceHours")) {
            return new Object[]{fieldName, attrs.get("toleranceHours")};
        }

        if (dtoClass != null && ("validation.datetime.date-before".equals(template) || "validation.datetime.date-after".equals(template))) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");

            if (first != null && second != null) {
                return new Object[]{fieldNames.resolve(dtoClass, first), fieldNames.resolve(dtoClass, second)};
            }
        }

        if (dtoClass != null && ("validation.datetime.date-before.distance".equals(template) || "validation.datetime.date-after.distance".equals(template))) {
            String first = (String) attrs.get("first");
            String second = (String) attrs.get("second");
            long maxDist = -1L;
            Object md = attrs.get("maxDistance");

            if (md instanceof Number) {
                maxDist = ((Number) md).longValue();
            }

            if (first != null && second != null && maxDist >= 0) {
                Enum<?> precision = (Enum<?>) attrs.get("precision");
                String precisionStr = DateUtils.pluralLabel(precision);

                return new Object[]{
                        fieldNames.resolve(dtoClass, first),
                        fieldNames.resolve(dtoClass, second),
                        maxDist,
                        precisionStr
                };
            }
        }

        if ("validation.finance.payment-reference.pattern".equals(template) && attrs.containsKey("pattern")) {
            return new Object[]{fieldName, attrs.get("pattern")};
        }

        if ("validation.file.image-dimension".equals(template)) {
            if (attrs.containsKey("minWidth") && attrs.containsKey("minHeight")
                    && attrs.containsKey("maxWidth") && attrs.containsKey("maxHeight")) {
                return new Object[]{
                        fieldName,
                        attrs.get("minWidth"),
                        attrs.get("minHeight"),
                        attrs.get("maxWidth"),
                        attrs.get("maxHeight")
                };
            }
        }

        if ("validation.file.file-extension".equals(template) && attrs.containsKey("allowed")) {
            return new Object[]{fieldName, String.join(", ", (String[]) attrs.get("allowed"))};
        }

        if ("validation.file.mime-type".equals(template) && attrs.containsKey("allowed")) {
            return new Object[]{fieldName, String.join(", ", (String[]) attrs.get("allowed"))};
        }

        if ("validation.file.file-size".equals(template) && attrs.containsKey("minBytes") && attrs.containsKey("maxBytes")) {
            return new Object[]{fieldName, attrs.get("minBytes"), attrs.get("maxBytes")};
        }

        return null;
    }
}
