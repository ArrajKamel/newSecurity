package com.example.demo.serivce;

import com.example.demo.dto.SignupRequest;
import com.example.demo.exception.EmailExistsException;
import com.example.demo.exception.ExpiredTokenException;
import com.example.demo.model.EmailVerificationToken;
import com.example.demo.model.MyUser;
import com.example.demo.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepo _userRepo;
    private final EmailVerificationTokenService _emailVerificationTokenService;
    private final EmailService _emailService;
    private final PasswordEncoder _passwordEncoder;

    @Autowired
    public AuthService(UserRepo userRepo,
                       EmailVerificationTokenService emailVerificationTokenService,
                       EmailService emailService) {
        _userRepo = userRepo;
        _emailVerificationTokenService = emailVerificationTokenService;
        _emailService = emailService;
        _passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public void handleSignup(@Valid SignupRequest request) {
//        // check if the user is existed or not
//        if(_userRepo.existsByEmail(request.getEmail())){
//            throw new EmailExistsException(request.getEmail());
//        }
//        //if the email is not existed
//        //create a new user and store it as a "disable" user
//
//        MyUser user = new MyUser();
//        user.setEmail(request.getEmail());
//        user.setPassword(request.getPassword());
//        user.setEmailScanConsent(request.isEmailScanConsent());
//        user.setEnabled(false);
//        _userRepo.save(user);
        MyUser user = createUser(request);
        System.out.println("user is created : " + user.getEmail());
        System.out.println("with id : " + user.getId());

        //now we have the user, but we need to activate his/her account
        // create a token and send it via an Email
        EmailVerificationToken token = _emailVerificationTokenService.createToken(user);
        System.out.println("token is created : " + token.getTokenId());
        _emailService.sendVerificationEmail(user, token);
    }

    @Transactional
    public MyUser createUser(@Valid SignupRequest signupRequest) throws EmailExistsException {
        if(_userRepo.existsByEmail(signupRequest.getEmail())){
            throw new EmailExistsException(signupRequest.getEmail());
        }

        MyUser user = new MyUser();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(_passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmailScanConsent(signupRequest.isEmailScanConsent());
        user.setEnabled(false);
        return _userRepo.save(user);
    }

    @Transactional
    public void confirmVerification(UUID tokenId) throws ExpiredTokenException {
        try {
            EmailVerificationToken token = _emailVerificationTokenService.getTokenById(tokenId);
            if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new ExpiredTokenException();
            }

            // Enable user
            MyUser user = token.getUser();
            user.setEnabled(true);
            _userRepo.save(user);

            // Cleanup
            _emailVerificationTokenService.deleteTokenById(tokenId);
        }catch (EntityNotFoundException ex){
            throw new EntityNotFoundException("Token not found for id " + tokenId);
        }
    }


    @Transactional
    public void deleteUserById(UUID id) {
        _userRepo.deleteById(id);
    }


    public List<MyUser> getAllUsers() {
        return _userRepo.findAll();
    }

    public MyUser getUserById(UUID userId) {
        return _userRepo.findById(userId).orElse(null);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<MyUser> user = _userRepo.findByEmail(email);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
