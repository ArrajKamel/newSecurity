package com.kamel.backend.serivce;

import com.kamel.backend.model.MyUser;
import com.kamel.backend.repo.UserRepo;
import com.kamel.backend.security.CostumeUserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepo _userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        _userRepo = userRepo;
    }

    @Transactional
    public void deleteUserById(UUID id) {
        // TODO handle tokens deletion before user deletion to not violate the FK constraints
        _userRepo.deleteById(id);
    }

    protected UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CostumeUserPrincipal principal = (CostumeUserPrincipal) authentication.getPrincipal();
        return principal.getId();
    }

    protected MyUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CostumeUserPrincipal principal = (CostumeUserPrincipal) authentication.getPrincipal();
        return _userRepo.findById(principal.getId()).orElse(null);
    }


    public List<MyUser> getAllUsers() {
        return _userRepo.findAll();
    }

    public MyUser getUserById(UUID userId) {
        return _userRepo.findById(userId).orElse(null);
    }

    public void disableUser(UUID userId) {
        MyUser user = getUserById(userId);
        if(user == null) {
            throw new EntityNotFoundException("User not found for id " + userId);
        }
        user.setEnabled(false);
        _userRepo.save(user);
    }

    public void enableUser(UUID userId) {
        MyUser user = getUserById(userId);
        if(user == null) {
            throw new EntityNotFoundException("User not found for id " + userId);
        }
        user.setEnabled(true);
        _userRepo.save(user);
    }




}
