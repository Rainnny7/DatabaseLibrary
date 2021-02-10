package me.braydon.database;

import lombok.AllArgsConstructor;

/**
 * This class represents a repository for the given {@link IDatabase}
 *
 * @author Braydon
 */
@AllArgsConstructor
public class DatabaseRepository<T extends IDatabase<?, ?>> {
    protected final T database;
}