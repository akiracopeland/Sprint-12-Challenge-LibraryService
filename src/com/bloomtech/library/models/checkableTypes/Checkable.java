package com.bloomtech.library.models.checkableTypes;

import java.util.HashSet;
import java.util.Objects;

public abstract class Checkable {
    private String isbn;
    private String title;

    public Checkable() {
    }

    public Checkable(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checkable checkable = (Checkable) o;
        return Objects.equals(isbn, checkable.isbn) && Objects.equals(title, checkable.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title);
    }
}
