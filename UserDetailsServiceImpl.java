package com.shoreexcursions.app.security;

import com.shoreexcursions.app.user.domain.AppUser;
import com.shoreexcursions.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Spring Security UserDetailsService implementation that loads user details from the database.
 *
 * @see US-037, LLD Section 6.2
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPasswordHash())
                .authorities(appUser.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()))
                .accountExpired(false)
                .accountLocked(appUser.getLockedUntil() != null &&
                        appUser.getLockedUntil().isAfter(java.time.OffsetDateTime.now()))
                .credentialsExpired(false)
                .disabled(!appUser.isEnabled())
                .build();
    }
}
