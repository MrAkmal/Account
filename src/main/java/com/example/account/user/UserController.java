package com.example.account.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @PostMapping(value = "/add-user/{username}")
    public ResponseEntity<Long> create(@PathVariable String username) {
        Long savedId = service.create(username);
        return ResponseEntity.ok(savedId);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<User>> getAll() {
        List<User> users = service.getAll();
        return ResponseEntity.ok(users);
    }

}
