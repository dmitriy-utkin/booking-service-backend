package ru.example.booking.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.dao.mongo.ReservationsStatistic;
import ru.example.booking.dao.postrgres.Reservation;
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.ReservationResponseList;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.statistics.event.ReservationEvent;

import java.time.Instant;
import java.util.List;

@DecoratedWith(ReservationMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

    ReservationsStatistic eventRoStatistic(ReservationEvent event);

    ReservationEvent reservationToEvent(Reservation reservation, Instant reservationAt);

    Reservation requestToReservation(UpsertReservationRequest request, String datePattern);

    ReservationResponse reservationToResponse(Reservation reservation, String datePattern);

    default ReservationResponseList reservationListToResponseList(List<Reservation> reservations, String datePattern) {
        var responses = reservations.stream()
                .map(reservation -> reservationToResponse(reservation, datePattern))
                .toList();

        return new ReservationResponseList(responses);
    }
}
