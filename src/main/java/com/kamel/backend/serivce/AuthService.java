package com.kamel.backend.serivce;

import com.kamel.backend.dto.LoginRequest;
import com.kamel.backend.dto.SignupRequest;
import com.kamel.backend.exception.EmailExistsException;
import com.kamel.backend.exception.ExpiredTokenException;
import com.kamel.backend.model.EmailVerificationToken;
import com.kamel.backend.model.MyUser;
import com.kamel.backend.repo.RoleRepo;
import com.kamel.backend.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepo _userRepo;
    private final RoleRepo _roleRepo;
    private final EmailVerificationTokenService _emailVerificationTokenService;
    private final EmailService _emailService;
    private final PasswordEncoder _passwordEncoder;
    private final AuthenticationManager _authenticationManager;
    private final JwtService _jwtService;


    @Autowired
    public AuthService(UserRepo userRepo,
                       EmailVerificationTokenService emailVerificationTokenService,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder,
                       RoleRepo roleRepo,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        _userRepo = userRepo;
        _emailVerificationTokenService = emailVerificationTokenService;
        _emailService = emailService;
        _passwordEncoder = passwordEncoder;
        _roleRepo = roleRepo;
        _authenticationManager = authenticationManager;
        _jwtService = jwtService;
    }


    public ResponseEntity<?> handleLogin(LoginRequest loginRequest) {
        // Authenticate the user
        try {
            Authentication authentication = _authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // handle updating the lastLoginAt attribute
            MyUser user = (MyUser) authentication.getPrincipal();
            user.setLastLoginAt(LocalDateTime.now());
            _userRepo.save(user);

            String jwt = _jwtService.generateToken(user);
            return ResponseEntity.ok(Map.of("token", jwt));

//            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
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

    private MyUser createUser(@Valid SignupRequest signupRequest) throws EmailExistsException {
        if(_userRepo.existsByEmail(signupRequest.getEmail())){
            throw new EmailExistsException(signupRequest.getEmail());
        }

        MyUser user = new MyUser();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(_passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmailScanConsent(signupRequest.isEmailScanConsent());
        user.setRoles(Set.of(_roleRepo.findByName(signupRequest.getRole()).orElseThrow(EntityNotFoundException::new)));
        user.setFirstname(signupRequest.getFirstname());
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





}
