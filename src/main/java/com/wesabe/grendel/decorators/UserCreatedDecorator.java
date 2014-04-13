package com.wesabe.grendel.decorators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.constraints.NotNull;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.fill;


/**
 * A representation of a request to create a new user.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "id": "Example User",
 *   "password": "snoopersneekrit"
 * }
 * </pre>
 * <p>
 * Both {@code id} and {@code password} properties are required.
 *
 * @author coda
 */
public class UserCreatedDecorator {

    @NotNull
    private String id;

    private char[] password;

    @JsonGetter("password")
    public char[] getPassword() {
        return password;
    }

    @JsonSetter("password")
    public void setPassword(char[] password) {
        this.password = copyOf(password, password.length);
        fill(password, '\0');
    }

    @JsonGetter("id")
    public String getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(String username) {
        this.id = username;
    }

    public void sanitize() {
        fill(password, '\0');
    }
}
