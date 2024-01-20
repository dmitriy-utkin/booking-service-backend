package ru.example.booking.service;

import ru.example.booking.model.User;

public interface ValidationService {

    boolean isValidAction(User requester, User owner);
}
