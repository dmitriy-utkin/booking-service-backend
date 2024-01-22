package ru.example.booking.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.dao.Reservation;
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.ReservationResponseList;
import ru.example.booking.dto.reservation.UpsertReservationRequest;

import java.util.List;

@DecoratedWith(ReservationMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    Reservation requestToReservation(UpsertReservationRequest request, String datePattern);

    ReservationResponse reservationToResponse(Reservation reservation, String datePattern);

    default ReservationResponseList reservationListToResponseList(List<Reservation> reservations, String datePattern) {
        var responses = reservations.stream()
                .map(reservation -> reservationToResponse(reservation, datePattern))
                .toList();

        return new ReservationResponseList(responses);
    }
}
