package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private final int id;
    private String name;

    public void setName(String name) {
        this.name = name;
    }
}


