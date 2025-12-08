package com.example.nftclubcoinapp.repository;

import com.example.nftclubcoinapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
