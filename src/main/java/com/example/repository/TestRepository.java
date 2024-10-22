package com.example.repository;

import com.example.entity.Test;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.security.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findAllByUser(User user);

}
