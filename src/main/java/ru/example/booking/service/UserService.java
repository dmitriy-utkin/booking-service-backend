package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dao.postrgres.User;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.dto.user.UpdateUserRequest;
import ru.example.booking.dto.user.UserResponse;
import ru.example.booking.dto.user.UserResponseList;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.mapper.UserMapper;
import ru.example.booking.repository.postgres.UserRepository;
import ru.example.booking.util.BeanUtils;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ValidationService validationService;

    private final UserMapper userMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.userTopic}")
    private String kafkaTopic;

    public UserResponseList findAll() {
        return userMapper.userListToResponseList(userRepository.findAll());
    }

    public UserResponse findById(Long id, String username) {
        User user = findByIdWithoutPrivilegeValidation(id);
        validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(username), user);
        return userMapper.userToUserResponse(user);
    }

    public UserResponse save(RoleType role, CreateUserRequest request) {

        validateUsernameEmail(request.getUsername(), request.getEmail());

        var userForSaving = userMapper.createRequestToUser(request);
        userForSaving.setRoles(Set.of(role));
        userForSaving.setPassword(passwordEncoder.encode(userForSaving.getPassword()));
        var savedUser = userRepository.save(userForSaving);

        kafkaTemplate.send(kafkaTopic, userMapper.userToEvent(savedUser, Instant.now()));

        return userMapper.userToUserResponse(savedUser);
    }

    public UserResponse update(Long id, UpdateUserRequest request, String username) {

        validateUsernameEmail(request.getUsername(), request.getEmail());

        var existedUser = findByIdWithoutPrivilegeValidation(id);

        validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(username), existedUser);

        var updatedUser = userMapper.updateRequestToUser(request);

        BeanUtils.copyNonNullProperties(updatedUser, existedUser);
        if (updatedUser.getPassword() != null) {
            existedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userMapper.userToUserResponse(userRepository.save(existedUser));
    }

    public UserResponse findByUsername(String username, String requesterUsername) {

        User user = findByUsernameWithoutPrivilegeValidation(username);

        validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(requesterUsername), user);

        return userMapper.userToUserResponse(user);
    }

    public void deleteById(Long id, String username) {

        User existedUser = findByIdWithoutPrivilegeValidation(id);

        validationService.isValidAction(findByUsernameWithoutPrivilegeValidation(username), existedUser);

        userRepository.deleteById(id);
    }

    public User findByUsernameWithoutPrivilegeValidation(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found, username is " + username)
        );
    }

    public User findByIdWithoutPrivilegeValidation(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found, ID is " + id)
        );
    }

    private void validateUsernameEmail(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new EntityAlreadyExists("User with username \"" + username + "\" is already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new EntityAlreadyExists("User with email \"" + email + "\" is already exists");
        }
    }

}
