package com.you.and.me.security.repository;


import com.you.and.me.security.model.RegisterUser;
import com.you.and.me.security.model.User;

import java.util.Optional;

public interface AuthRepository {

    void signup(RegisterUser registerUser);

    Optional<User> getUserByUsername(String username);


}
