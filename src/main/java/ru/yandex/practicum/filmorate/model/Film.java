package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @Positive
    int duration;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;
        Film film = (Film) o;
        return getDuration() == film.getDuration()
                && getDescription().equals(film.getDescription())
                && getReleaseDate().equals(film.getReleaseDate())
                && getName().equals(film.getName());
    }

}
