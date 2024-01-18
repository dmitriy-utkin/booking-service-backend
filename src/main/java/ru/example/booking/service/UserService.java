package ru.example.booking.service;

import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User save(RoleType role, User user);

    User update(Long id, User user);

    User findByUsername(String username);

    void deleteById(Long id);

}
