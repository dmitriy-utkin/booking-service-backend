package ru.example.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.dao.mongo.UserStatistic;
import ru.example.booking.dao.postrgres.User;
import ru.example.booking.dto.user.CreateUserRequest;
import ru.example.booking.dto.user.UpdateUserRequest;
import ru.example.booking.dto.user.UserResponse;
import ru.example.booking.dto.user.UserResponseList;
import ru.example.booking.statistics.event.UserEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User createRequestToUser(CreateUserRequest request);

    User updateRequestToUser(UpdateUserRequest request);

    UserResponse userToUserResponse(User user);

    default UserResponseList userListToResponseList(List<User> users) {
        return new UserResponseList(
                users.stream().map(this::userToUserResponse).toList()
        );
    }

    UserStatistic eventToStatistic(UserEvent event);

    default UserEvent userToEvent(User user, Instant registrationAt) {
        return UserEvent.builder()
                .userId(user.getId())
                .registrationAt(registrationAt)
                .build();
    }
}
