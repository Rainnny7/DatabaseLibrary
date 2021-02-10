package me.braydon.database.impl.mysql.data.impl;

import lombok.Getter;
import me.braydon.database.impl.mysql.data.Column;
import me.braydon.database.impl.mysql.data.Table;

/**
 * This class represents a {@link Double} column in a MySQL {@link Table}
 *
 * @author Braydon
 */
@Getter
public class DoubleColumn extends Column<Double> {
    public DoubleColumn(String name, Double value) {
        super(name, value);
    }

    public DoubleColumn(String name, boolean nullable) {
        this(name, 0, nullable);
    }

    public DoubleColumn(String name, int length, boolean nullable) {
        super(name, length, nullable);
    }

    /**
     * Get the type of the column
     * @return the type
     */
    @Override
    public String getType() {
        return "DOUBLE";
    }
}
