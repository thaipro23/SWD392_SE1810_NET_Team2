package com.pjb.kindergarten_suggestion.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {
    String message() default "Must be 7 characters long and combination of  letters, numbers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
