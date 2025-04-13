package com.kamel.backend.serivce;

import com.kamel.backend.dto.SignupRequest;
import com.kamel.backend.exception.EmailExistsException;
import com.kamel.backend.exception.ExpiredTokenException;
import com.kamel.backend.model.EmailVerificationToken;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepo _userRepo;
    private final EmailVerificationTokenService _emailVerificationTokenService;
    private final EmailService _emailService;
    private final PasswordEncoder _passwordEncoder;

    @Autowired
    public AuthService(UserRepo userRepo,
                       EmailVerificationTokenService emailVerificationTokenService,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder) {
        _userRepo = userRepo;
        _emailVerificationTokenService = emailVerificationTokenService;
        _emailService = emailService;
        _passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void handleSignup(@Valid SignupRequest request) {
        MyUser user = createUser(request);
        System.out.println("user is created :" + user.getEmail());
        System.out.println("with id : " +user.getId());

        //now we have the user, but we need to activate his/her account
        // create a token and send it via an Email
        EmailVerificationToken token = _emailVerificationTokenService.createToken(user.getId());
        System.out.println("token is created : " +token.getTokenId());
        _emailService.sendVerificationEmail(user, token);
    }

//    @Transactional
    private MyUser createUser(@Valid SignupRequest signupRequest) throws EmailExistsException {
        if(_userRepo.existsByEmail(signupRequest.getEmail())){
            throw new EmailExistsException(signupRequest.getEmail());
        }

        MyUser user = new MyUser();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(_passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmailScanConsent(signupRequest.isEmailScanConsent());
//        user.setEnabled(false);
        return _userRepo.save(user);
    }

    @Transactional
    public void confirmVerification(UUID tokenId) throws ExpiredTokenException {
        try {
            EmailVerificationToken token = _emailVerificationTokenService.getTokenById(tokenId);
            if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new ExpiredTokenException();
            }

            // verify the user by setting "EmailVerified" attribute to ture
            MyUser user = token.getUser();
            user.setEmailVerified(true);
            _userRepo.save(user);

            // Cleanup
            _emailVerificationTokenService.deleteTokenById(tokenId);
        }catch (EntityNotFoundException ex){
            throw new EntityNotFoundException("Token not found for id " + tokenId);
        }
    }


    public void setLoginDate(MyUser user) {
        user.setLastLoginAt(LocalDateTime.now());
        _userRepo.save(user);
    }
}
