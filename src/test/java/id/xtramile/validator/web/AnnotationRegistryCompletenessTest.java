package id.xtramile.validator.web;

import jakarta.validation.Constraint;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationRegistryCompletenessTest {

    private static final String ANNOTATION_BASE_PACKAGE = "id.xtramile.validator.annotation";

    @Test
    void everyCustomConstraintAnnotationIsRegistered() throws Exception {
        Set<Class<?>> constraintAnnotations = findConstraintAnnotations();

        assertThat(constraintAnnotations).isNotEmpty();

        Set<String> unregistered = constraintAnnotations.stream()
                .filter(type -> AnnotationRegistry.get(type) == null)
                .map(Class::getSimpleName)
                .collect(Collectors.toCollection(TreeSet::new));

        assertThat(unregistered)
                .as("Custom @Constraint annotations under annotation/** must be registered in AnnotationRegistry")
                .isEmpty();
    }

    private Set<Class<?>> findConstraintAnnotations() throws Exception {
        String packagePath = ANNOTATION_BASE_PACKAGE.replace('.', '/');
        Enumeration<URL> resources = getClass().getClassLoader().getResources(packagePath);
        Set<Class<?>> constraintAnnotations = new TreeSet<>((a, b) -> a.getName().compareTo(b.getName()));

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (!"file".equals(resource.getProtocol())) {
                continue;
            }

            Path packageRoot = Paths.get(resource.toURI());
            if (!Files.isDirectory(packageRoot)) {
                continue;
            }

            try (Stream<Path> classFiles = Files.walk(packageRoot)) {
                classFiles.filter(path -> path.toString().endsWith(".class"))
                        .forEach(path -> addConstraintAnnotation(packageRoot, path, constraintAnnotations));
            }
        }

        return constraintAnnotations;
    }

    private void addConstraintAnnotation(Path packageRoot, Path classFile, Set<Class<?>> constraintAnnotations) {
        String className = toClassName(packageRoot, classFile);

        try {
            Class<?> type = Class.forName(className);
            if (type.isAnnotation() && type.isAnnotationPresent(Constraint.class)) {
                constraintAnnotations.add(type);
            }
        } catch (ClassNotFoundException | LinkageError ignored) {
            // Skip unloadable generated or auxiliary types.
        }
    }

    private String toClassName(Path packageRoot, Path classFile) {
        String relativePath = packageRoot.relativize(classFile).toString()
                .replace('\\', '/');
        if (relativePath.endsWith(".class")) {
            relativePath = relativePath.substring(0, relativePath.length() - ".class".length());
        }
        return ANNOTATION_BASE_PACKAGE + "." + relativePath.replace('/', '.');
    }
}
