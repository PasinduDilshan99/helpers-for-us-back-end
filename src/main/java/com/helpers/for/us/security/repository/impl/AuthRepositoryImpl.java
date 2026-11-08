package com.you.and.me.security.repository.impl;


import com.you.and.me.exception.InternalServerErrorExceptionHandler;
import com.you.and.me.exception.UserRegisterFailedErrorExceptionHandler;
import com.you.and.me.security.model.RegisterUser;
import com.you.and.me.security.model.User;
import com.you.and.me.security.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class AuthRepositoryImpl implements AuthRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void signup(RegisterUser registerUser) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsInserted = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO user (username, password, first_name, last_name, email) " +
                                "VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, registerUser.getUsername());
                ps.setString(2, registerUser.getPassword());
                ps.setString(3, registerUser.getFirstName());
                ps.setString(5, registerUser.getLastName());
                ps.setString(6, registerUser.getEmail());
                return ps;
            }, keyHolder);

            if (rowsInserted == 0 || keyHolder.getKey() == null) {
                throw new UserRegisterFailedErrorExceptionHandler("User registration failed.");
            }

            registerUser.setUserId(keyHolder.getKey().longValue());
            Integer roleId = getRoleIdByName("ROLE_USER");
            if (roleId != null) {
                jdbcTemplate.update(
                        "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)",
                        registerUser.getUserId(),
                        roleId
                );

                registerUser.setRoles(fetchRoles(registerUser.getUserId()));
                registerUser.setPrivileges(fetchPrivileges(registerUser.getUserId()));
            }

        } catch (UserRegisterFailedErrorExceptionHandler ex) {
            throw ex;

        } catch (DataAccessException sqlEx) {
            throw new UserRegisterFailedErrorExceptionHandler("Database error: " + sqlEx.getMessage());

        } catch (Exception ex) {
            throw new InternalServerErrorExceptionHandler("Unexpected error: " + ex.getMessage());
        }
    }

    private Integer getRoleIdByName(String roleName) {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = ?", Integer.class, roleName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private Set<String> fetchRoles(Long userId) {
        String sql = """
                SELECT r.name FROM role r 
                INNER JOIN user_role ur ON r.id = ur.role_id 
                WHERE ur.user_id = ?
                """;
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), userId));
    }

    private Set<String> fetchPrivileges(Long userId) {
        String sql = """
                SELECT DISTINCT p.name FROM privileges p 
                INNER JOIN role_privilege rp ON p.id = rp.privilege_id
                INNER JOIN user_role ur ON rp.role_id = ur.role_id
                WHERE ur.user_id = ?
                """;
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), userId));
    }

    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> User.builder()
                    .id(rs.getLong("id"))
                    .username(rs.getString("username"))
                    .password(rs.getString("password"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .email(rs.getString("email"))
                    .build());

            if (user != null) {
                user.setRoles(fetchRoles(user.getId()));
                user.setPrivileges(fetchPrivileges(user.getId()));
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
