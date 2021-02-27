package me.braydon.database.impl.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.braydon.database.IDatabase;
import me.braydon.database.IRepositoryDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link IDatabase} for MySQL
 *
 * @author Braydon
 */
@RequiredArgsConstructor @Getter @Slf4j(topic = "MySQLDatabase")
public class MySQLDatabase implements IDatabase<MySQLProperties>, IRepositoryDatabase<MySQLRepository> {
    private static final Object LOCK = new Object();

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
    public IDatabase<MySQLProperties> connect(@NonNull MySQLProperties properties, Runnable onConnect) {
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
    public IDatabase<MySQLProperties> connect(@NonNull MySQLProperties properties, @NonNull String uri, Runnable onConnect) {
        if (dataSource != null)
            throw new IllegalStateException("Already connected");
        synchronized (LOCK) {
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
                log.debug("Connection established in " + (System.currentTimeMillis() - started) + "ms");
            if (onConnect != null)
                onConnect.run();
            return this;
        }
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
        synchronized (LOCK) {
            return new MySQLRepository(this);
        }
    }

    /**
     * Cleanup the database and close connections
     */
    @Override
    public void cleanup() {
        synchronized (LOCK) {
            dataSourceProperties.clear();
            properties = null;
            if (dataSource != null && (!dataSource.isClosed()))
                dataSource.close();
            dataSource = null;
        }
    }
}