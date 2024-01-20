package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;
import ru.example.booking.repository.UserRepository;
import ru.example.booking.service.UserService;
import ru.example.booking.service.ValidationService;
import ru.example.booking.util.BeanUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ValidationService validationService;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id, String username) {
        User user = findByIdWithoutPrivilegeValidation(id);
        if (!validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(username), user)) {
            throw new AccessDeniedException("Access denied");
        }
        return user;
    }

    @Override
    public User save(RoleType role, User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityAlreadyExists("User with username \"" + user.getUsername() + "\" is already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EntityAlreadyExists("User with email \"" + user.getEmail() + "\" is already exists");
        }
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user, String username) {
        var existedUser = findById(id, username);

        if (!validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(username), existedUser)) {
            throw new AccessDeniedException("Access denied");
        }

        BeanUtils.copyNonNullProperties(user, existedUser);
        if (user.getPassword() != null) {
            existedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existedUser);
    }

    @Override
    public User findByUsername(String username, String requesterUsername) {

        User user = findByUsernameWithoutPrivilegeValidation(username);

        if (!validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(requesterUsername), user)) {
            throw new AccessDeniedException("Access denied");
        }

        return user;
    }

    @Override
    public void deleteById(Long id, String username) {

        User existedUser = findByIdWithoutPrivilegeValidation(id);

        if (!validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(username), existedUser)) {
            throw new AccessDeniedException("Access denied");
        }

        userRepository.deleteById(id);
    }

    @Override
    public User findByUsernameWithoutPrivilegeValidation(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found, username is " + username)
        );
    }

    @Override
    public User findByIdWithoutPrivilegeValidation(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found, ID is " + id)
        );
    }
}
