package me.braydon.database.impl.mysql.data.impl;

import lombok.Getter;
import me.braydon.database.impl.mysql.data.Column;
import me.braydon.database.impl.mysql.data.Table;

/**
 * This class represents a {@link Boolean} column in a MySQL {@link Table}
 *
 * @author Braydon
 */
@Getter
public class BooleanColumn extends Column<Boolean> {
    public BooleanColumn(String name, Boolean value) {
        super(name, value);
    }

    public BooleanColumn(String name, boolean nullable) {
        super(name, 0, nullable);
    }

    /**
     * Get the type of the column
     * @return the type
     */
    @Override
    public String getType() {
        return "BOOLEAN";
    }
}