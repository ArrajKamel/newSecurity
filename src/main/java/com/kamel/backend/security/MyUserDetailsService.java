package com.kamel.backend.security;

import com.kamel.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepo _userRepo;

    @Autowired
    public MyUserDetailsService(UserRepo userRepo) {
        _userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return _userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
