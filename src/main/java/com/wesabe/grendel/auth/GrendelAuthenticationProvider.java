package com.wesabe.grendel.auth;

import com.google.common.collect.Lists;
import com.wesabe.grendel.entities.User;
import com.wesabe.grendel.entities.dao.UserRepository;
import com.wesabe.grendel.openpgp.CryptographicException;
import com.wesabe.grendel.openpgp.UnlockedKeySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 4/11/14 Created by Jonathan Garay
 */
@Component
public class GrendelAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        authentication.setAuthenticated(false);

        final User user = userRepository.findById((String) authentication.getPrincipal());


        if (user != null) {
            try {
                final UnlockedKeySet keySet = user.getKeySet()
                        .unlock(((String) authentication.getCredentials()).toCharArray());

                return new UsernamePasswordAuthenticationToken(new Session(user, keySet),
                        ((String) authentication.getCredentials()).toCharArray(),
                        grantedAuthorities()
                );

            } catch (CryptographicException e) {
                throw new AuthenticationCredentialsNotFoundException("Username or credentials invalid");
            }
        } else {
            throw new UsernameNotFoundException("Username or credentials invalid");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication
                .getName()
                .equals(UsernamePasswordAuthenticationToken.class.getName());
    }


    private List<GrantedAuthority> grantedAuthorities() {
        List<GrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(new SimpleGrantedAuthority("USER_ACTIONS"));
        authorities.add(new SimpleGrantedAuthority("DOCUMENT_ACTIONS"));

        return authorities;
    }
}
