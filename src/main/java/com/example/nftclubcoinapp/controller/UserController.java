package com.example.nftclubcoinapp.controller;

import com.example.nftclubcoinapp.model.User;
import com.example.nftclubcoinapp.repository.UserRepository;
import com.example.nftclubcoinapp.util.StaticResponseUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping
    public Map<String, Object> getResponse() {
        return StaticResponseUtil.getStaticResponse("user");
    }

    // ✅ GET USER BY ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ CREATE USER (Plain Password)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
