package ru.example.booking.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.example.booking.dao.Hotel;
import ru.example.booking.dao.Room;
import ru.example.booking.dao.RoomDescription;
import ru.example.booking.dto.defaults.RoomFilter;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RoomSpecification {

    static Specification<Room> withFilter(RoomFilter filter) {
        return Specification.where(byRoomId(filter.getId()))
                .and(byRoomDescription(filter.getDescription()))
                .and(byRoomPrice(filter.getMinPrice(), filter.getMaxPrice()))
                .and(byRoomCapacity(filter.getCapacity()))
                .and(byCheckInOutDates(filter.getCheckInDate(), filter.getCheckOutDate()))
                .and(byRoomHotelId(filter.getHotelId()));
    }

    static Specification<Room> byCheckInOutDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return (root, query, cb) -> {
            if (checkInDate == null || checkOutDate == null) {
                return null;
            }
            return cb.or(
                    cb.isEmpty(root.get(Room.Fields.bookedDates)),
                    cb.or(
                            cb.greaterThan(root.get(Room.Fields.bookedDates).as(LocalDate.class), checkOutDate),
                            cb.lessThan(root.get(Room.Fields.bookedDates).as(LocalDate.class), checkInDate)
                    )
            );
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
