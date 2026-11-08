package com.you.and.me.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    @Builder.Default
    private Set<String> roles = new HashSet<>();
    @Builder.Default
    private Set<String> privileges = new HashSet<>();
}
