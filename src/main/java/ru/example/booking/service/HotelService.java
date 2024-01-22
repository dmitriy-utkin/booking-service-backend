package ru.example.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.booking.dao.Hotel;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.hotel.CreateHotelRequest;
import ru.example.booking.dto.hotel.HotelResponse;
import ru.example.booking.dto.hotel.HotelResponseList;
import ru.example.booking.dto.hotel.UpdateHotelRequest;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.mapper.HotelMapper;
import ru.example.booking.repository.HotelRepository;
import ru.example.booking.repository.HotelSpecification;
import ru.example.booking.util.BeanUtils;


@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    private final HotelMapper hotelMapper;

    public HotelResponseList findAll(FindAllSettings settings) {
        return hotelMapper.hotelListToResponseList(
                hotelRepository.findAll(HotelSpecification.withFilter(settings.getHotelFilter()),
                PageRequest.of(settings.getPageNum(), settings.getPageSize())).getContent()
        );
    }

    public HotelResponseList findAll() {
        return hotelMapper.hotelListToResponseList(hotelRepository.findAll());
    }

    public HotelResponse findById(Long id) {
        return hotelMapper.hotelToResponse(findHotelById(id));
    }

    public HotelResponse findByName(String name) {
        var hotel = hotelRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Hotel not found, name is " + name)
        );
        return hotelMapper.hotelToResponse(hotel);
    }

    public HotelResponse updateById(Long id, UpdateHotelRequest hotel) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel not found, ID is " + id);
        }
        Hotel existedHotel = findHotelById(id);
        BeanUtils.copyNonNullProperties(hotelMapper.updateRequestToHotel(hotel), existedHotel);

        return hotelMapper.hotelToResponse(hotelRepository.save(existedHotel));
    }

    public HotelResponse save(CreateHotelRequest hotel) {
        if (hotelRepository.existsByName(hotel.getName())) {
            throw new EntityAlreadyExists("Hotel with name \"" + hotel.getName() + "\" is already exists");
        }
        return hotelMapper.hotelToResponse(hotelRepository.save(hotelMapper.createRequestToHotel(hotel)));
    }

    public void deleteById(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel not found, ID is " + id);
        }
        hotelRepository.deleteById(id);
    }

    public HotelResponse updateRating(Long hotelId, int newRating) {
        Hotel existedHotel = findHotelById(hotelId);

        float newTotalRating = existedHotel.getRating() * existedHotel.getNumberOfRatings() + newRating;
        int newNumberOfRatings = existedHotel.getNumberOfRatings() + 1;
        float newHotelRating = newTotalRating / (float) newNumberOfRatings;

        existedHotel.setNumberOfRatings(newNumberOfRatings);
        existedHotel.setRating(newHotelRating);

        return hotelMapper.hotelToResponse(hotelRepository.save(existedHotel));
    }

    public Hotel findHotelById(Long id) {
        return hotelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Hotel not found, ID is " + id)
        );
    }
}
