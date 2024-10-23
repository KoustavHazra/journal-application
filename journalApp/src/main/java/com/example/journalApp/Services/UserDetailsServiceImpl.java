package com.example.journalApp.Services;

import com.example.journalApp.Entity.User;
import com.example.journalApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User authenticatedUser = userRepository.findByUsername(username);
        if (authenticatedUser != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(authenticatedUser.getUsername())
                    .password(authenticatedUser.getPassword())
                    .roles(authenticatedUser.getRoles().toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException("User with username '" + username + "' was not found.");
    }
}
