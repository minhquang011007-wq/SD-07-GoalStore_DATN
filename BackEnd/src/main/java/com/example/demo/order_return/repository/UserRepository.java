package com.example.demo.order_return.repository;

import com.example.demo.order_return.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}