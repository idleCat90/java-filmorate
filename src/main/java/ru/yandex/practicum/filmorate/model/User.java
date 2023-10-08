package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.LoginConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @LoginConstraint
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}