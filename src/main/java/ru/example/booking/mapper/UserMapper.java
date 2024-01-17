package ru.example.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.model.User;
import ru.example.booking.web.model.user.CreateUserRequest;
import ru.example.booking.web.model.user.UpdateUserRequest;
import ru.example.booking.web.model.user.UserResponse;
import ru.example.booking.web.model.user.UserResponseList;

import java.util.List;

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
}
