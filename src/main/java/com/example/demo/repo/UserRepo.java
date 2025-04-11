package com.example.demo.repo;

import com.example.demo.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<MyUser, UUID> {
    boolean existsByEmail(String email);

    Optional<MyUser> findByEmail(String email);
}
