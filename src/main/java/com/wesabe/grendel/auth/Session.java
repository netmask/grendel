package com.wesabe.grendel.auth;

import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A {@link User} and their {@link UnlockedKeySet}.
 *
 * @author coda
 */
@Component
@Scope("session")
public class Session {
    private final User user;
    private final UnlockedKeySet keySet;

    public Session(User user, UnlockedKeySet keySet) {
        this.user = user;
        this.keySet = keySet;
    }

    public UnlockedKeySet getKeySet() {
        return keySet;
    }

    public User getUser() {
        return user;
    }
}
