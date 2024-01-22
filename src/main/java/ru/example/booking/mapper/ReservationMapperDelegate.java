package ru.example.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.example.booking.dao.Reservation;
import ru.example.booking.dto.reservation.ReservationResponse;
import ru.example.booking.dto.reservation.UpsertReservationRequest;
import ru.example.booking.service.RoomService;
import ru.example.booking.service.UserService;
import ru.example.booking.util.LocalDatesUtil;

public abstract class ReservationMapperDelegate implements ReservationMapper {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;


    @Override
    public Reservation requestToReservation(UpsertReservationRequest request, String datePattern) {
        return Reservation.builder()
                .room(roomService.findRoomById(request.getRoomId()))
                .checkInDate(LocalDatesUtil.strDateToLocalDate(request.getCheckInDate(), datePattern))
                .checkOutDate(LocalDatesUtil.strDateToLocalDate(request.getCheckOutDate(), datePattern))
                .build();
    }

    @Override
    public ReservationResponse reservationToResponse(Reservation reservation, String datePattern) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoom().getId())
                .userId(reservation.getUser().getId())
                .userEmail(reservation.getUser().getEmail())
                .checkInDate(LocalDatesUtil.localDateToStr(reservation.getCheckInDate(), datePattern))
                .checkOutDate(LocalDatesUtil.localDateToStr(reservation.getCheckOutDate(), datePattern))
                .build();
    }
}
