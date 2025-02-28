package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @Column(name = "user_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(name = "user_name", unique = true, nullable = false)
    @Size(min = 3, max = 50)
    private String userName;

    private String profilePictureUrl; // Stores the S3 URL of the profile picture

    @Column(name = "email", unique = true, nullable = false)
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    //@ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    private boolean verified = false; // Email verification status

    private String verificationToken; // Token for email verification

    private boolean isDeleted = false; // Soft delete status
}
