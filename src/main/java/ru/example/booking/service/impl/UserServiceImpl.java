package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.booking.aop.Validation;
import ru.example.booking.aop.ValidationType;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;
import ru.example.booking.repository.UserRepository;
import ru.example.booking.service.UserService;
import ru.example.booking.util.BeanUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Validation(type = ValidationType.USER_FIND_BY)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found, ID is " + id)
        );
    }

    @Override
    public User save(RoleType role, User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityAlreadyExists("User with username \"" + user.getUsername() +"\" is already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExists("User with email \"" + user.getEmail() +"\" is already exists");
        }
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Validation(type = ValidationType.USER_UPDATE)
    public User update(Long id, User user) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found, ID is " + id);
        }
        var existedUser = findById(id);
        BeanUtils.copyNonNullProperties(user, existedUser);
        if (user.getPassword() != null) {
            existedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existedUser);
    }

    @Override
    @Validation(type = ValidationType.USER_FIND_BY)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found, username is " + username)
        );
    }

    @Override
    @Validation(type = ValidationType.USER_DELETE)
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found, ID is " + id);
        }
        userRepository.deleteById(id);
    }
}
