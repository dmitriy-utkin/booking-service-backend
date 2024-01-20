package ru.example.booking.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.model.Reservation;
import ru.example.booking.web.model.reservation.ReservationResponse;
import ru.example.booking.web.model.reservation.ReservationResponseList;
import ru.example.booking.web.model.reservation.UpsertReservationRequest;

import java.util.List;

@DecoratedWith(ReservationMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    Reservation requestToReservation(UpsertReservationRequest request);

    ReservationResponse reservationToResponse(Reservation reservation);

    default ReservationResponseList reservationListToResponseList(List<Reservation> reservations) {
        var responses = reservations.stream()
                .map(this::reservationToResponse)
                .toList();

        return new ReservationResponseList(responses);
    }
}
