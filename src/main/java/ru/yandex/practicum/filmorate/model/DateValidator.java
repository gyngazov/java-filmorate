package ru.yandex.practicum.filmorate.model;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<CustomDate, LocalDate> {

    private static final LocalDate FILM_EPOCH = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(CustomDate customDate) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        if (releaseDate == null) {
            return false;
        }
        return !releaseDate.isBefore(FILM_EPOCH);
    }

}
