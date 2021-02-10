package me.braydon.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.braydon.database.IDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link IDatabase} for MySQL
 *
 * @author Braydon
 */
@RequiredArgsConstructor @Getter
public class MySQLDatabase implements IDatabase<MySQLProperties, MySQLRepository> {
    private final Map<String, String> dataSourceProperties;
    private MySQLProperties properties;
    private HikariDataSource dataSource;

    public MySQLDatabase() {
        this(new HashMap<>() {{
            put("cachePrepStmts", "true");
            put("prepStmtCacheSize", "250");
            put("prepStmtCacheSqlLimit", "2048");
        }});
    }

    /**
     * Connect to the database server with the given properties
     *
     * @param properties the properties to connect with
     * @param onConnect  the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    @Override
    public IDatabase<MySQLProperties, MySQLRepository> connect(@NonNull MySQLProperties properties, Runnable onConnect) {
        connect(properties, "jdbc:mysql://" + properties.getHost() + ":" + properties.getPort() + "/" + properties.getDatabase(), onConnect);
        return this;
    }

    /**
     * Connect to the database server with the given properties and uri
     *
     * @param properties the properties to connect with
     * @param uri        the uri to use to make a connection to the database server
     * @param onConnect  the {@link Runnable} that's called when a connection is established with the database server
     * @return the database instance
     */
    @Override
    public IDatabase<MySQLProperties, MySQLRepository> connect(@NonNull MySQLProperties properties, @NonNull String uri, Runnable onConnect) {
        this.properties = properties;

        long started = System.currentTimeMillis();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(uri);
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        for (Map.Entry<String, String> entry : dataSourceProperties.entrySet())
            config.addDataSourceProperty(entry.getKey(), entry.getValue());
        dataSource = new HikariDataSource(config);
        if (properties.isDebugging())
            debug("Connection established in " + (System.currentTimeMillis() - started) + "ms");
        if (onConnect != null)
            onConnect.run();
        return this;
    }

    /**
     * Get a dummy connection of the repository for this database type
     *
     * @return the repository
     * @apiNote This will create a new instance of a repository each time, it's recommended to save a reference
     * of the repository for future use
     */
    @Override
    public MySQLRepository getDummyRepository() {
        return new MySQLRepository(this);
    }
}