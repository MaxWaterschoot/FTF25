package utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FestivalCodesValidator.class)
public @interface ValidFestivalCodes {
    String message() default "{festival.codes.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
