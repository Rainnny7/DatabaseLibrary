package me.braydon.database.properties;

import lombok.Getter;
import lombok.NonNull;

/**
 * Authentication properties for a database hold information such as the host, port, username, and password
 *
 * @author Braydon
 */
@Getter
public class AuthenticationProperties extends DatabaseProperties {
    private final String username, password;

    public AuthenticationProperties(@NonNull String host, int port, String username, String password) {
        super(host, port);
        this.username = username;
        this.password = password;
    }

    @Override
    public AuthenticationProperties withDebugging() {
        debugging = true;
        return this;
    }
}