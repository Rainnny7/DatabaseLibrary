package me.braydon.database.impl.mongo;

import lombok.NonNull;
import me.braydon.database.IDatabase;
import me.braydon.database.properties.URIProperties;

/**
 * @author Braydon
 */
public class MongoDatabase implements IDatabase<URIProperties> {
	/**
	 * Connect to the database server with the given properties
	 *
	 * @param properties the properties to connect with
	 * @param onConnect  the {@link Runnable} that's called when a connection is established with the database server
	 * @return the database instance
	 */
	@Override
	public IDatabase<URIProperties> connect(@NonNull URIProperties properties, Runnable onConnect) {
		return null;
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
	public IDatabase<URIProperties> connect(@NonNull URIProperties properties, @NonNull String uri, Runnable onConnect) {
		return null;
	}
	
	/**
	 * Cleanup the database and close connections
	 */
	@Override
	public void cleanup() {
	
	}
}