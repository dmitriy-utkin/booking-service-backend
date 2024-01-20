package ru.example.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.example.booking.model.Reservation;
import ru.example.booking.service.ReservationService;
import ru.example.booking.service.RoomService;
import ru.example.booking.service.UserService;
import ru.example.booking.web.model.reservation.ReservationResponse;
import ru.example.booking.web.model.reservation.UpsertReservationRequest;

public abstract class ReservationMapperDelegate implements ReservationMapper {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Override
    public Reservation requestToReservation(UpsertReservationRequest request) {
        return Reservation.builder()
                .room(roomService.findById(request.getRoomId()))
                .checkInDate(roomService.strDateToLocalDate(request.getCheckInDate()))
                .checkOutDate(roomService.strDateToLocalDate(request.getCheckOutDate()))
                .build();
    }

    @Override
    public ReservationResponse reservationToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoom().getId())
                .userId(reservation.getUser().getId())
                .userEmail(reservation.getUser().getEmail())
                .checkInDate(reservationService.localDateToStr(reservation.getCheckInDate()))
                .checkOutDate(reservationService.localDateToStr(reservation.getCheckOutDate()))
                .build();
    }
}
