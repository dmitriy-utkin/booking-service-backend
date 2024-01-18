package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.exception.RoomBookingException;
import ru.example.booking.model.Reservation;
import ru.example.booking.model.Room;
import ru.example.booking.repository.ReservationRepository;
import ru.example.booking.service.ReservationService;
import ru.example.booking.service.RoomService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final RoomService roomService;

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Reservation is not found, ID is " + id)
        );
    }

    @Override
    public Reservation booking(Reservation reservation) {
        roomService.addBookedDates(reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate());
        return reservationRepository.save(reservation);
    }

    @Override
    public void cancel(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation is not found, ID is " + id);
        }

        Reservation reservationForRemoving = findById(id);

        roomService.deleteBookedDates(id, reservationForRemoving.getCheckInDate(), reservationForRemoving.getCheckOutDate());
        reservationRepository.deleteById(id);
    }

    @Override
    public Reservation update(Long id, Reservation reservation) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation is not found, ID is " + id);
        }
        return null;
    }
}
