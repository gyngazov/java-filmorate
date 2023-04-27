package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Comparable<Film> {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Integer> usersLikes;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        usersLikes = new HashSet<>();
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

    /**
     * Сорт по убыванию.
     */
    @Override
    public int compareTo(Film film) {
        return Integer.compare(film.getUsersLikes().size(), usersLikes.size());
    }

    public void deleteLike(int id) throws ObjectNotFoundException {
        if (!isUserInLikes(id)) {
            throw new ObjectNotFoundException("Пользователь " + id + " не лайкал фильм " + getId());
        }
        usersLikes.remove(id);
    }

    public void addLike(int id) throws ObjectNotFoundException {
        if (isUserInLikes(id)) {
            throw new ObjectNotFoundException("Пользователь " + id + " уже лайкал фильм " + getId());
        }
        usersLikes.add(id);
    }

    private boolean isUserInLikes(int userId) {
        return usersLikes.contains(userId);
    }
}
