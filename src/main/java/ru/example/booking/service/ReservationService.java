package ru.example.booking.service;

import ru.example.booking.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    List<Reservation> findAll();

    Reservation findById(Long id, String username);

    Reservation booking(Reservation reservation, String username);

    void cancel(Long id, String username);

    Reservation update(Long id, Reservation reservation, String username);

    String localDateToStr(LocalDate date);

}
