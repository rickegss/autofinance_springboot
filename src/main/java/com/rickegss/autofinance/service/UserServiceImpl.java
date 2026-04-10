package com.rickegss.autofinance.service;

import com.rickegss.autofinance.entity.User;
import com.rickegss.autofinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(User user){
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
