package id.xtramile.validator.web;

import jakarta.validation.constraints.*;

public class ValidationAnnotationTypeRegistry {

    private ValidationAnnotationTypeRegistry() {}
    
    public static Class<?> resolve(String annotationName) {
        if (annotationName == null) {
            return null;
        }
        
        switch (annotationName) {
            case "NotBlank": return NotBlank.class;
            case "NotNull": return NotNull.class;
            case "Null": return Null.class;
            case "NotEmpty": return NotEmpty.class;
            case "Email": return Email.class;
            case "Pattern": return Pattern.class;
            case "Size": return Size.class;
            case "Min": return Min.class;
            case "Max": return Max.class;
            case "DecimalMin": return DecimalMin.class;
            case "DecimalMax": return DecimalMax.class;
            case "Digits": return Digits.class;
            case "Positive": return Positive.class;
            case "PositiveOrZero": return PositiveOrZero.class;
            case "Negative": return Negative.class;
            case "NegativeOrZero": return NegativeOrZero.class;
            case "AssertTrue": return AssertTrue.class;
            case "AssertFalse": return AssertFalse.class;
            case "Past": return Past.class;
            case "PastOrPresent": return PastOrPresent.class;
            case "Future": return Future.class;
            case "FutureOrPresent": return FutureOrPresent.class;

            default: return null;
        }
    }
}
