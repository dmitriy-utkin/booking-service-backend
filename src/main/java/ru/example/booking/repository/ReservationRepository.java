package ru.example.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.booking.dao.Reservation;
import ru.example.booking.dao.Room;
import ru.example.booking.dao.User;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUser(User user);

    boolean existsByRoom(Room room);

    Optional<Reservation> findByUser(User user);

    Optional<Reservation> findByRoom(Room room);

    Optional<Reservation> findByCheckInDate(LocalDate checkInDate);

    Optional<Reservation> findByCheckOutDate(LocalDate checkOutDate);
}
