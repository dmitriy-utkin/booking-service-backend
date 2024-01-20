package ru.example.booking.service.impl;

import org.springframework.stereotype.Service;
import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;
import ru.example.booking.service.ValidationService;

import java.util.Objects;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public boolean isValidAction(User requester, User owner) {
        boolean isAdmin = requester.getRoles().contains(RoleType.ROLE_ADMIN);
        boolean isOwner = Objects.equals(requester.getId(), owner.getId());
        return isOwner || isAdmin;
    }
}
