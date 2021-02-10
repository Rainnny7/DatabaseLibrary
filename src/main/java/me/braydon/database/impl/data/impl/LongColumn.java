package me.braydon.database.impl.data.impl;

import lombok.Getter;
import me.braydon.database.impl.data.Column;
import me.braydon.database.impl.data.Table;

/**
 * This class represents a {@link Long} column in a MySQL {@link Table}
 *
 * @author Braydon
 */
@Getter
public class LongColumn extends Column<Long> {
    public LongColumn(String name, Long value) {
        super(name, value);
    }

    public LongColumn(String name, boolean nullable) {
        this(name, 0, nullable);
    }

    public LongColumn(String name, int length, boolean nullable) {
        super(name, length, nullable);
    }

    /**
     * Get the type of the column
     * @return the type
     */
    @Override
    public String getType() {
        return "LONG";
    }
}
