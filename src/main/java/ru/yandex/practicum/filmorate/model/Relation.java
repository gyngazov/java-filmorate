package ru.yandex.practicum.filmorate.model;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Для обмена данными из friends между storage и service.
 */

public class Relation extends Pair<Integer, Integer> {
    private final int left;
    private final int right;

    public Relation(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Integer getLeft() {
        return left;
    }

    @Override
    public Integer getRight() {
        return right;
    }

    @Override
    public Integer setValue(Integer value) {
        return null;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
