package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginConstraintValidator.class)
public @interface LoginConstraint {
    String message() default "login не может содержать пробелы.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}