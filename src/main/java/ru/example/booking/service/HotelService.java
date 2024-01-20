package ru.example.booking.service;

import ru.example.booking.model.Hotel;

import java.util.List;

public interface HotelService {

    List<Hotel> findAll();

    Hotel findById(Long id);

    Hotel findByName(String name);

    Hotel updateById(Long id, Hotel hotel);

    Hotel save(Hotel hotel);

    void deleteById(Long id);

    Hotel updateRating(Long hotelId, int newRating);
}
