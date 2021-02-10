package me.braydon.database.impl.data.impl;

import lombok.Getter;
import me.braydon.database.impl.data.Column;
import me.braydon.database.impl.data.Table;

/**
 * This class represents an {@link Integer} column in a MySQL {@link Table}
 *
 * @author Braydon
 */
@Getter
public class IntegerColumn extends Column<Integer> {
    private final boolean autoIncrement;

    public IntegerColumn(String name, Integer value) {
        super(name, value);
        autoIncrement = false;
    }

    public IntegerColumn(String name, boolean nullable) {
        this(name, 0, false, nullable);
    }

    public IntegerColumn(String name, boolean autoIncrement, boolean nullable) {
        this(name, 0, autoIncrement, nullable);
    }

    public IntegerColumn(String name, int length, boolean autoIncrement, boolean nullable) {
        super(name, length, nullable);
        this.autoIncrement = autoIncrement;
    }

    /**
     * Get the type of the column
     * @return the type
     */
    @Override
    public String getType() {
        return "INT";
    }
}
