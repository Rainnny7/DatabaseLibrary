package me.braydon.database.impl.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.braydon.database.impl.data.impl.IntegerColumn;

/**
 * This class represents a table in MySQL
 *
 * @author Braydon
 */
@AllArgsConstructor @RequiredArgsConstructor @Getter
public class Table {
    private final String name;
    private final Column<?>[] columns;
    private String[] primaryKeys;

    /**
     * Get the create query {@link String} for this table
     *
     * @param ignoreExisting whether or not to add the "IF NOT EXISTS" flag to the query
     * @return the query
     */
    public String getCreateQuery(boolean ignoreExisting) {
        // Checking the auto incrementing column count
        int autoIncrementingTables = 0;
        for (Column<?> column : columns) {
            if (column instanceof IntegerColumn && ((IntegerColumn) column).isAutoIncrement()) {
                autoIncrementingTables++;
            }
        }
        if (autoIncrementingTables > 1)
            throw new IllegalArgumentException("Inappropriate amount of auto incrementing tables (" + autoIncrementingTables + ">1)");
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE " + (ignoreExisting ? "IF NOT EXISTS " : "") + "`" + name + "` (");
        for (Column<?> column : columns) {
            boolean autoIncrement = column instanceof IntegerColumn && ((IntegerColumn) column).isAutoIncrement();

            queryBuilder.append("`").append(column.getName()).append("` ").append(column.getType());
            // If the column length is longer than 0, we wanna add the column length to the column type String
            if (column.getLength() > 0)
                queryBuilder.append("(").append(column.getLength()).append(")");
            // If the column isn't nullable, add "NOT NULL" to the query
            if (!column.isNullable())
                queryBuilder.append(" NOT NULL");
            // If the column is set to auto increment, add "AUTO_INCREMENT" to the query
            if (autoIncrement)
                queryBuilder.append(" AUTO_INCREMENT");
            queryBuilder.append(", ");
        }
        StringBuilder query = new StringBuilder(queryBuilder.toString());
        query = new StringBuilder(query.substring(0, query.length() - 2));

        if (autoIncrementingTables > 0 && (primaryKeys == null || (primaryKeys.length < 1)))
            throw new IllegalArgumentException("There must be a primary key set if there is an auto incrementing column");

        // Appending the primary key(s) to the query
        if (primaryKeys != null && (primaryKeys.length > 0)) {
            query.append(", PRIMARY KEY (");
            for (String primaryKey : primaryKeys)
                query.append("`").append(primaryKey).append("`, ");
            query = new StringBuilder(query.substring(0, query.length() - 2) + ")");
        }

        query.append(");");
        return query.toString();
    }
}