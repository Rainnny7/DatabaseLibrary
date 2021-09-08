package me.braydon.database.properties;

import lombok.Getter;
import lombok.NonNull;

/**
 * @author Braydon
 */
@Getter
public class URIProperties extends DatabaseProperties {
	private final String uri;
	
	public URIProperties(@NonNull String host, String uri) {
		super("", -1);
		this.uri = uri;
	}
}