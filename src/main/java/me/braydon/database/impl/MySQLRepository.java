package me.braydon.database.impl;

import lombok.NonNull;
import me.braydon.database.DatabaseRepository;
import me.braydon.database.impl.data.Column;

import java.sql.*;
import java.util.function.Consumer;

/**
 * Implementation of the {@link DatabaseRepository} for the {@link MySQLDatabase}
 *
 * @author Braydon
 */
public class MySQLRepository extends DatabaseRepository<MySQLDatabase> {
    public MySQLRepository(MySQLDatabase database) {
        super(database);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and insert the given columns using the provided query
     *
     * @param query the query to execute
     * @param columns the {@link Column} array to insert
     * @return the amount of rows affected
     */
    public int executeInsert(@NonNull String query, @NonNull Column<?>[] columns) {
        return executeInsert(query, columns, null);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and insert the given columns using the provided query
     *
     * @param query the query to execute
     * @param columns the {@link Column} array to insert
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @return the amount of rows affected
     */
    public int executeInsert(@NonNull String query, @NonNull Column<?>[] columns, Consumer<ResultSet> onComplete) {
        return executeInsert(query, columns, onComplete, null);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and insert the given columns using the provided query
     *
     * @param query the query to execute
     * @param columns the {@link Column} array to insert
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     * @return the amount of rows affected
     */
    public int executeInsert(@NonNull String query, @NonNull Column<?>[] columns, Consumer<ResultSet> onComplete,
                                Consumer<SQLException> onException) {
        try (Connection connection = database.getDataSource().getConnection()) {
            return executeInsert(connection, query, columns, onComplete, onException);
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the {@link Column} array to insert
     * @return the amount of rows affected
     */
    public int executeInsert(@NonNull Connection connection, @NonNull String query, @NonNull Column<?>[] columns) {
        return executeInsert(connection, query, columns, null);
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the {@link Column} array to insert
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @return the amount of rows affected
     */
    public int executeInsert(@NonNull Connection connection, @NonNull String query, @NonNull Column<?>[] columns,
                                Consumer<ResultSet> onComplete) {
        return executeInsert(connection, query, columns, onComplete, null);
    }

    /**
     * Insert the given columns using the provided query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the {@link Column} array to insert
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     * @return the amount of rows affected
     */
    public int executeInsert(@NonNull Connection connection, @NonNull String query, @NonNull Column<?>[] columns,
                                Consumer<ResultSet> onComplete, Consumer<SQLException> onException) {
        int questionMarks = 0;
        for (char character : query.toCharArray()) {
            if (character == '?') {
                questionMarks++;
            }
        }
        if (questionMarks != columns.length)
            throw new IllegalArgumentException("Invalid amount of columns for query \"" + query + "\"");
        int affectedRows = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int columnIndex = 1;
            for (Column<?> column : columns)
                statement.setString(columnIndex++, column.getValue() == null ? null : column.getValue().toString());
            affectedRows = statement.executeUpdate();
            if (onComplete != null)
                onComplete.accept(statement.getGeneratedKeys());
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
        return affectedRows;
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     */
    public void executeQuery(@NonNull String query, @NonNull Consumer<ResultSet> onComplete) {
        executeQuery(query, null, onComplete, null);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     * @param columns the {@link Column} array to use in the query
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     */
    public void executeQuery(@NonNull String query, Column<?>[] columns, @NonNull Consumer<ResultSet> onComplete) {
        executeQuery(query, columns, onComplete, null);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     */
    public void executeQuery(@NonNull String query, @NonNull Consumer<ResultSet> onComplete, Consumer<SQLException> onException) {
        executeQuery(query, null, onComplete, onException);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     * @param columns the {@link Column} array to use in the query
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     */
    public void executeQuery(@NonNull String query, Column<?>[] columns, @NonNull Consumer<ResultSet> onComplete,
                             Consumer<SQLException> onException) {
        try (Connection connection = database.getDataSource().getConnection()) {
            executeQuery(connection, query, columns, onComplete, onException);
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     */
    public void executeQuery(@NonNull Connection connection, @NonNull String query, @NonNull Consumer<ResultSet> onComplete,
                             Consumer<SQLException> onException) {
        executeQuery(connection, query, null, onComplete, onException);
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param columns the {@link Column} array to use in the query
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     */
    public void executeQuery(@NonNull Connection connection, @NonNull String query, Column<?>[] columns,
                                @NonNull Consumer<ResultSet> onComplete, Consumer<SQLException> onException) {
        if (columns != null) {
            int questionMarks = 0;
            for (char character : query.toCharArray()) {
                if (character == '?') {
                    questionMarks++;
                }
            }
            if (questionMarks != columns.length)
                throw new IllegalArgumentException("Invalid amount of columns for query \"" + query + "\"");
        }
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (columns != null) {
                int columnIndex = 1;
                for (Column<?> column : columns)
                    statement.setString(columnIndex++, (column.getValue() == null ? null : column.getValue().toString()));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                onComplete.accept(resultSet);
            } catch (SQLException ex) {
                if (onException != null)
                    onException.accept(ex);
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     */
    public void executeQuery(@NonNull String query) {
        executeQuery(query, (Runnable) null);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     */
    public void executeQuery(@NonNull String query, Runnable onComplete) {
        executeQuery(query, onComplete, null);
    }

    /**
     * Open a new connection using the {@link MySQLDatabase} and execute the given query
     *
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     */
    public void executeQuery(@NonNull String query, Runnable onComplete, Consumer<SQLException> onException) {
        try (Connection connection = database.getDataSource().getConnection()) {
            executeQuery(connection, query, onComplete);
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     */
    public void executeQuery(@NonNull Connection connection, @NonNull String query) {
        executeQuery(connection, query, null);
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     */
    public void executeQuery(@NonNull Connection connection, @NonNull String query, Runnable onComplete) {
        executeQuery(connection, query, onComplete, null);
    }

    /**
     * Execute the given query
     *
     * @param connection the connection to execute the query on
     * @param query the query to execute
     * @param onComplete the {@link Consumer} of {@link ResultSet} that gets called when the query has completed
     * @param onException the {@link Consumer} that gets called if an {@link SQLException} is thrown
     */
    public void executeQuery(@NonNull Connection connection, @NonNull String query, Runnable onComplete,
                             Consumer<SQLException> onException) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
            if (onComplete != null)
                onComplete.run();
        } catch (SQLException ex) {
            if (onException != null)
                onException.accept(ex);
            ex.printStackTrace();
        }
    }
}