package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FilmorateApplicationTests {

	private static Validator validator;

	static {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.usingContext().getValidator();
	}

	@Test
	public void testValidators() {
		final Film film = new Film();
		film.setName("Test film");
		film.setDescription("Description");
		film.setDuration(200);
		film.setReleaseDate(LocalDate.of(1500, 1, 1));

		Set<ConstraintViolation<Film>> validates = validator.validate(film);

		assertFalse(validates.isEmpty());
		validates.stream().map(ConstraintViolation::getMessage)
				.forEach(System.out::println);
	}
}
