package com.wesabe.grendel.representations;

import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonSetter;

import java.util.Arrays;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.fill;
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
 * 
 * The {@code password} property is required.
 * 
 * @author coda
 */
public class UpdateUserRepresentation implements Validatable {
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
	
	@Override
	public void validate() throws ValidationException {
		final ValidationException error = new ValidationException();
		
		if ((password == null) || (password.length == 0)) {
			error.missingRequiredProperty("password");
		}
		
		if (error.hasReasons()) {
			throw error;
		}
	}
	
	public void sanitize() {
		fill(password, '\0');
	}
}
