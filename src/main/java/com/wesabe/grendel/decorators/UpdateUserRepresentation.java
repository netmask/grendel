package com.wesabe.grendel.decorators;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.validation.constraints.NotNull;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.fill;

/**
 * A representation of a request to change a user's password.
 * <p>
 * Example JSON:
 * <pre>
 * {
 *   "password": "snoopersneekrit"
 * }
 * </pre>
 * <p>
 * The {@code password} property is required.
 *
 * @author coda
 */
public class UpdateUserRepresentation {

    @NotNull
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

    public void sanitize() {
        fill(password, '\0');
    }
}
