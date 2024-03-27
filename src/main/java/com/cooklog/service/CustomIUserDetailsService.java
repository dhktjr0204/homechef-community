package com.cooklog.service;

import com.cooklog.dto.CustomUserDetails;
import com.cooklog.model.User;
import com.cooklog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomIUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        User userData = userRepository.findByEmail(email);

        if(userData == null) {
            throw new UsernameNotFoundException(email);
        }
        return new CustomUserDetails(userData);
    }
}
