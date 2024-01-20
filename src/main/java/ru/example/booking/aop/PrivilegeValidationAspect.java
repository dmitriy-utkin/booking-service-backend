package ru.example.booking.aop;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.example.booking.exception.IllegalArguments;
import ru.example.booking.model.Reservation;
import ru.example.booking.model.RoleType;
import ru.example.booking.model.User;
import ru.example.booking.service.ReservationService;
import ru.example.booking.service.UserService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Aspect
@Component
@Slf4j
@ConditionalOnProperty(prefix = "app.validation", name = "enable", havingValue = "true")
public class PrivilegeValidationAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Before("@annotation(Validation)")
    public void validateCredential(JoinPoint joinPoint) {

        ValidationType type = getValidationType(joinPoint);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!validate(type, userDetails.getUsername(), joinPoint.getArgs())) {
            throw new AccessDeniedException("Access denied, please contact administrator");
        }
    }

    @SneakyThrows
    private ValidationType getValidationType(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?>[] parameters = methodSignature.getMethod().getParameterTypes();
        Validation validationAnnotation = joinPoint.getTarget().getClass()
                .getMethod(joinPoint.getSignature().getName(), parameters)
                .getAnnotation(Validation.class);
        if (validationAnnotation != null) {
            return validationAnnotation.type();
        }
        throw new IllegalArguments("Validation type is unexpected");
    }

    private boolean validate(ValidationType validationType, String username, Object[] args) {

        User requester = userService.findByUsername(username);
        Set<RoleType> requesterRoles = requester.getRoles();

        Object objectToGetOwnerId = Arrays.stream(args).findFirst().orElse(null);

        if (objectToGetOwnerId == null) {
            return false;
        }

        switch (validationType) {
            case USER_UPDATE, USER_FIND_BY, USER_DELETE,
                    RESERVATION_DELETE, RESERVATION_UPDATE, RESERVATION_FIND_BY -> {
                if (isNotAdmin(requesterRoles) && isNotOwnerOfEntity(validationType,
                        objectToGetOwnerId,
                        requester.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNotAdmin(Set<RoleType> requesterRoles) {
        return requesterRoles.stream().noneMatch(role -> role.equals(RoleType.ROLE_ADMIN));
    }

    private boolean isNotOwnerOfEntity(ValidationType validationType,
                                       Object objectToGetOwnerId,
                                       Long requesterId) {

        switch (validationType) {
            case USER_DELETE, USER_FIND_BY -> {
                return !Objects.equals(objectToGetOwnerId, requesterId);
            }
            case USER_UPDATE -> {
                return !Objects.equals(getOwnerIdForUpdate(objectToGetOwnerId, true, false), requesterId);
            }
            case RESERVATION_DELETE, RESERVATION_FIND_BY -> {
                return !Objects.equals(reservationService.findById((Long) objectToGetOwnerId).getUser().getId(), requesterId);
            }
            case RESERVATION_UPDATE -> {
                return !Objects.equals(getOwnerIdForUpdate(objectToGetOwnerId, false, true), requesterId);
            }
            default -> {
                return true;
            }
        }
    }

    @SneakyThrows
    private Long getOwnerIdForUpdate(Object object, boolean user, boolean reservation) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (user && field.getName().equals(User.Fields.id)) {
                return (Long) field.get(object);
            }
            if(reservation && (field.getName().equals(Reservation.Fields.id))) {
                Long reservationId = (Long) field.get(object);
                return reservationService.findById(reservationId).getUser().getId();
            }
        }
        return null;
    }
}
