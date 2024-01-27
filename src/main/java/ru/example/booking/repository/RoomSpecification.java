package ru.example.booking.repository;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.example.booking.dao.Hotel;
import ru.example.booking.dao.Reservation;
import ru.example.booking.dao.Room;
import ru.example.booking.dao.RoomDescription;
import ru.example.booking.dto.defaults.RoomFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public interface RoomSpecification {

    static Specification<Room> withFilter(RoomFilter filter) {
        return Specification.where(byRoomId(filter.getId()))
                .and(byRoomDescription(filter.getDescription()))
                .and(byRoomPrice(filter.getMinPrice(), filter.getMaxPrice()))
                .and(byRoomCapacity(filter.getCapacity()))
                .and(byCheckInOutDates(filter.getCheckInLocalDate(), filter.getCheckOutLocalDate()))
                .and(byRoomHotelId(filter.getHotelId()));
    }

    static Specification<Room> byCheckInOutDates(LocalDate checkInDate, LocalDate checkOutDate) {

        return (root, query, cb) -> {

            if (checkInDate == null && checkOutDate == null) {
                return null;
            }

            Date from = Date.from(checkInDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date to = Date.from(checkOutDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Reservation> reservationRoot = subquery.from(Reservation.class);
            subquery.select(reservationRoot.get(Reservation.Fields.room).get(Room.Fields.id))
                    .where(cb.and(
                            cb.equal(reservationRoot.get(Reservation.Fields.room), root),
                            cb.or(
                                    cb.between(reservationRoot.get(Reservation.Fields.checkInDate), from, to),
                                    cb.between(reservationRoot.get(Reservation.Fields.checkOutDate), from, to)
                            )
                    ));
            return cb.not(cb.exists(subquery));
        };
    }

    static Specification<Room> byRoomHotelId(Long hotelId) {
        return (root, query, cb) -> {
            if (hotelId == null) {
                return null;
            }
            return cb.equal(root.get(Room.Fields.hotel).get(Hotel.Fields.id), hotelId);
        };
    }

    static Specification<Room> byRoomCapacity(Integer capacity) {
        return (root, query, cb) -> {
            if (capacity == null) {
                return null;
            }
            return cb.equal(root.get(Room.Fields.capacity), capacity);
        };
    }

    static Specification<Room> byRoomPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }
            if (minPrice == null) {
                return cb.lessThanOrEqualTo(root.get(Room.Fields.price), maxPrice);
            }
            if (maxPrice == null) {
                return cb.greaterThanOrEqualTo(root.get(Room.Fields.price), minPrice);
            }
            return cb.between(root.get(Room.Fields.price), minPrice, maxPrice);
        };
    }

    static Specification<Room> byRoomDescription(RoomDescription description) {
        return (root, query, cb) -> {
            if (description == null) {
                return null;
            }
            return cb.equal(root.get(Room.Fields.description), description);
        };
    }

    static Specification<Room> byRoomId(Long id) {
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get(Room.Fields.id), id);
        };
    }
}
