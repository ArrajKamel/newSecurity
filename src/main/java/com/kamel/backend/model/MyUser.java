package com.kamel.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * No Separate username: Email is the identifier.<br>
 * No Plaintext Passwords: Hashing handled by PasswordEncoder.<br>
 * Minimal Roles: Start with ROLE_USER only (scale later).<br>
 * GDPR Built-In: emailScanConsent enforced at registration.<br>
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

//    @Column(nullable = false)
    private String firstname;

    private String lastname;

    private String phoneNumber;

    /**
     * OAuth and local users share the same table -> unique email
     */
    @Column(nullable = false, unique = true)
    private String email;


//    critical for password handling
//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private MyAuthProvider provider;
    /**
     * for local users only -> nullable for OAuth users
     */
    private String password;

    /**
     * OAuth users: emailVerified=true immediately (Google verifies emails). <br>
     * Local users: emailVerified=false until email confirmation link clicked. <br>
     */
    @Column(nullable = false)
    private boolean emailVerified = false;
    /**
     * GDPR toggle
     */
    @Column(nullable = false)
    private boolean emailScanConsent;

    /**
     * For OAuth users only (nullable for LOCAL). <br>
     * Google's unique user ID
     */
//    private String oauth2ProviderId;
    /**
     * set to "true" after email verification is done successfully <br>
     * Can disable accounts
     */
    @Column(nullable = false)
    private boolean enabled = true;


    /**
     *     Timestamps (Auditing): <br>
     * createdAt/updatedAt: Automatic with Hibernate annotations.<br>
     * lastLoginAt: Detect inactive accounts.
     */
    @CreationTimestamp
    @Column(nullable = true)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Updated on every auth
     */
    private LocalDateTime lastLoginAt;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;


    /**
     * Implements UserDetails for Spring Security. <br>
     * Default role ROLE_USER assigned to all (customize later).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals("ROLE_" + roleName));
    }

    @Override public String getUsername() { return this.email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return this.enabled; }
}
