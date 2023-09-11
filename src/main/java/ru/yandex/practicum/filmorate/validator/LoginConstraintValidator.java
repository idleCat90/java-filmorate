package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginConstraintValidator implements ConstraintValidator<LoginConstraint, String> {

    @Override
    public void initialize(LoginConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return (login != null && !login.isBlank() && !login.contains(" "));
    }
}
