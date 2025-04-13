package com.kamel.backend.repo;


import com.kamel.backend.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerificationToken, UUID> {
    int deleteByUser_id(UUID userId);

//    void deleteByUser(MyUser user);
//
//    void deleteByUser_Id(UUID userId);
}
