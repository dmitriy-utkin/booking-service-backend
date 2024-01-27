package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.postrgres.RoleType;
import ru.example.booking.dao.postrgres.User;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ValidationService {

    public void isValidAction(User requester, User owner) {
        boolean isAdmin = requester.getRoles().contains(RoleType.ROLE_ADMIN);
        boolean isOwner = Objects.equals(requester.getId(), owner.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
