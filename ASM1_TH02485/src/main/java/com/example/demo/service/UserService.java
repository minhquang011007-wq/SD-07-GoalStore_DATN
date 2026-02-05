package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void add(User user) {
        repo.save(user);
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public List<User> getAll() {
        return repo.findAll();
    }

    public void remove(String username) {
        User u = repo.findByUsername(username);
        if (u != null) repo.delete(u);
    }

    public void update(User user) {
        repo.save(user);
    }
}
