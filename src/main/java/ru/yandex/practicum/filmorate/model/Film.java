package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Film implements Comparable<Film> {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    @NotNull
    private String description;
    @CustomDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Integer> usersLikes;
    @NotNull
    private Mpa mpa;
    private Collection<Genre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate,
                int duration, Mpa mpa, Collection<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        usersLikes = new HashSet<>();
        this.genres = genres;
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

    public void deleteLike(int id) {
        if (!usersLikes.remove(id)) {
            throw new ObjectNotFoundException("Пользователь " + id + " не лайкал фильм " + getId());
        }
    }

    public void addLike(int id) {
        if (!usersLikes.add(id)) {
            throw new ObjectNotFoundException("Пользователь " + id + " уже лайкал фильм " + getId());
        }
    }

    public void setLikes(Collection<User> users) {
        setUsersLikes(users.stream().map(User::getId).collect(Collectors.toSet()));
    }

}
