package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateConstraintValidator.class)
public @interface ReleaseDateConstraint {
    String message() default "Дата релиза не может быть ранее 28.12.1895.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}