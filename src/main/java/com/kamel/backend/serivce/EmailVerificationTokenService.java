package com.kamel.backend.serivce;

import com.kamel.backend.exception.TokenCreationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.kamel.backend.model.EmailVerificationToken;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.repo.EmailVerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailVerificationTokenService {

    private final EmailVerificationTokenRepo _emailVerificationTokenRepo;
    private final UserService _userService;

    @Autowired
    public EmailVerificationTokenService(EmailVerificationTokenRepo emailVerificationTokenRepo,
                                         UserService userService) {
        _emailVerificationTokenRepo = emailVerificationTokenRepo;
        _userService = userService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)     // Prevents duplicate tokens in high concurrency
    public EmailVerificationToken createToken(UUID userId) {
        MyUser user = _userService.getUserById(userId);
        if (user == null) {
            throw new TokenCreationException("User not found");
        }
        try {
//            _emailVerificationTokenRepo.deleteByUser(user);
//            deleteUserToken(user.getId());
//            int deleted = _emailVerificationTokenRepo.deleteByUser_Id(user.getId());
//            if (deleted == 0) {
//                throw new EntityNotFoundException("Token not found for user " + userId);
//            }
            int deleted = _emailVerificationTokenRepo.deleteByUser_id(user.getId());
//            if(deleted == 0) {
//                throw new EntityNotFoundException("Token not found for user " + user.getId());
//            }

            EmailVerificationToken token = new EmailVerificationToken();
            token.setUser(user);
            return _emailVerificationTokenRepo.save(token);
        }catch (DataAccessException ex){
            throw new TokenCreationException("Failed to create verification token" + ex.getMessage());
        }
    }

    @Transactional
    public void deleteUserToken(UUID userId) throws EntityNotFoundException {
        int deleted = _emailVerificationTokenRepo.deleteByUser_id(userId);
        if(deleted == 0) {
            throw new EntityNotFoundException("Token not found for user " + userId);
        }
    }

    @Transactional
    public void deleteTokenById(UUID tokenId) {
        _emailVerificationTokenRepo.deleteById(tokenId);
    }

    public List<EmailVerificationToken> getAllTokens(){
        return _emailVerificationTokenRepo.findAll();
    }

    public EmailVerificationToken getTokenById(UUID tokenId) throws EntityNotFoundException {
        EmailVerificationToken token = _emailVerificationTokenRepo.findById(tokenId).orElse(null);
        if(token == null) {
            throw new EntityNotFoundException("Token not found for id " + tokenId);
        }
        return token;
    }
}
