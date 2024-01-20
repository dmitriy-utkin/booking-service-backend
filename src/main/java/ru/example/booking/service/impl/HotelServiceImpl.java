package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.model.Hotel;
import ru.example.booking.repository.HotelRepository;
import ru.example.booking.service.HotelService;
import ru.example.booking.util.BeanUtils;

import java.text.DecimalFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    @Override
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel findById(Long id) {
        return hotelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Hotel not found, ID is " + id)
        );
    }

    @Override
    public Hotel findByName(String name) {
        return hotelRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Hotel not found, name is " + name)
        );
    }

    @Override
    public Hotel updateById(Long id, Hotel hotel) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel not found, ID is " + id);
        }

        Hotel existedHotel = findById(id);
        BeanUtils.copyNonNullProperties(hotel, existedHotel);

        return hotelRepository.save(existedHotel);
    }

    @Override
    public Hotel save(Hotel hotel) {
        if (hotelRepository.existsByName(hotel.getName())) {
            throw new EntityAlreadyExists("Hotel with name \"" + hotel.getName() + "\" is already exists");
        }
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteById(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel not found, ID is " + id);
        }
        hotelRepository.deleteById(id);
    }

    @Override
    public Hotel updateRating(Long hotelId, int newRating) {
        Hotel existedHotel = findById(hotelId);

        float newTotalRating = existedHotel.getRating() * existedHotel.getNumberOfRatings() + newRating;
        int newNumberOfRatings = existedHotel.getNumberOfRatings() + 1;
        float newHotelRating = newTotalRating / (float) newNumberOfRatings;

        existedHotel.setNumberOfRatings(newNumberOfRatings);
        existedHotel.setRating(newHotelRating);

        return updateById(hotelId, existedHotel);
    }
}
