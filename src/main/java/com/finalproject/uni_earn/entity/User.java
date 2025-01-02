package com.finalproject.uni_earn.entity;

import com.finalproject.uni_earn.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public abstract class User {
    @Id
    @Column(name = "user_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;
    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}
