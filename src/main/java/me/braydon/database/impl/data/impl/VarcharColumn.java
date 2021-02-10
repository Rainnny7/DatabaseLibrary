package me.braydon.database.impl.data.impl;

import lombok.Getter;
import me.braydon.database.impl.data.Column;
import me.braydon.database.impl.data.Table;

/**
 * This class represents a {@link String} column in a MySQL {@link Table}
 *
 * @author Braydon
 */
@Getter
public class VarcharColumn extends Column<String> {
    public VarcharColumn(String name, String value) {
        super(name, value);
    }

    public VarcharColumn(String name, int length, boolean nullable) {
        super(name, length, nullable);
    }

    /**
     * Get the type of the column
     * @return the type
     */
    @Override
    public String getType() {
        return "VARCHAR";
    }
}