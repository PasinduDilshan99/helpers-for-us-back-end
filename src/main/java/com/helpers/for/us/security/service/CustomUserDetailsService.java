package com.you.and.me.security.service;

import com.you.and.me.security.model.CustomUserDetails;
import com.you.and.me.security.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Autowired
    public CustomUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails userNotFound = authRepository.getUserByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        LOGGER.info(userNotFound.toString());
        return userNotFound;
    }

}
