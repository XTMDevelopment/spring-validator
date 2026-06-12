package id.xtramile.validator.validator.network;

import id.xtramile.validator.annotation.network.ValidPort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static id.xtramile.validator.util.ValidatorUtils.isBlank;

/**
 * Validator implementation for {@link ValidPort} annotation.
 * <p>
 * Validates network port numbers for both numeric and string values.
 * This validator ensures the port number is within the valid range
 * of 1-65535 for network communication.
 * 
 * <p>The validator performs the following operations:
 * <ul>
 * <li>Accepts null values as valid</li>
 * <li>Handles Number and CharSequence types</li>
 * <li>Validates port range (1-65535)</li>
 * <li>Handles parsing exceptions gracefully</li>
 * <li>Ignores unsupported types (returns true)</li>
 * </ul>
 * 
 * @see ValidPort
 */
public class PortValidator implements ConstraintValidator<ValidPort, Object> {

    /**
     * Validates the port number against the valid range.
     * @param value the port number to validate
     * @param context the constraint validator context
     * @return true if the port number is valid or is null
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        int port;

        try {
            if (value instanceof Number) {
                Number n = (Number) value;
                port = n.intValue();

            } else if (value instanceof CharSequence) {
                CharSequence cs = (CharSequence) value;
                String str = cs.toString().trim();
                if (isBlank(str)) return true;

                port = Integer.parseInt(str);
            } else {
                return true;
            }

        } catch (NumberFormatException e) {
            return false;
        }

        return port >= 1 && port <= 65535;
    }
}
