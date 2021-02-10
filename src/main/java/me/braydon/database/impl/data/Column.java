package me.braydon.database.impl.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This class represents a column in a MySQL {@link Table}
 *
 * @author Braydon
 */
@AllArgsConstructor @RequiredArgsConstructor @Setter @Getter
public abstract class Column<T> {
    private final String name;
    private T value;
    private final int length;
    private final boolean nullable;

    /**
     * Construct a new column with a name and a value. This is used when executing
     * queries in a repository.
     *
     * @param name the name of the column
     * @param value the value in the column
     */
    public Column(String name, T value) {
        this(name, value, -1, false);
    }

    /**
     * Get the type of the column
     * @return the type
     */
    public abstract String getType();
}
