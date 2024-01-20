package ru.example.booking.service;

import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id, String username);

    User save(RoleType role, User user);

    User update(Long id, User user, String username);

    User findByUsername(String username, String requesterUsername);

    void deleteById(Long id, String username);

    User findByUsernameWithoutPrivilegeValidation(String username);

    User findByIdWithoutPrivilegeValidation(Long id);

}
