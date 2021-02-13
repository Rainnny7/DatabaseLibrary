package me.braydon.database;

/**
 * @author Braydon
 */
public interface IRepositoryDatabase<E> {
    /**
     * Get a dummy connection of the repository for this database type
     * @return the repository
     * @apiNote This will create a new instance of a repository each time, it's recommended to save a reference
     *          of the repository for future use
     */
    E getDummyRepository();
}