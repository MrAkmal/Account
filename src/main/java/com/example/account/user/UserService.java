package com.example.account.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public Long create(String username) {
        User user = new User();
        user.setName(username);
        User createdUser = repository.save(user);
        return createdUser.getId();
    }

    public List<User> getAll() {
        return repository.findAll();
    }
}
