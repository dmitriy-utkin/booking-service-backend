package ru.example.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.model.Hotel;
import ru.example.booking.web.model.hotel.CreateHotelRequest;
import ru.example.booking.web.model.hotel.HotelResponse;
import ru.example.booking.web.model.hotel.HotelResponseList;
import ru.example.booking.web.model.hotel.UpdateHotelRequest;

import java.text.DecimalFormat;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelMapper {

    Hotel createRequestToHotel(CreateHotelRequest request);

    Hotel updateRequestToHotel(UpdateHotelRequest request);

    default HotelResponse hotelToResponse(Hotel hotel) {
        DecimalFormat formatter = new DecimalFormat("#.#");
        return HotelResponse.builder()
                .id(hotel.getId())
                .address(hotel.getAddress())
                .name(hotel.getName())
                .city(hotel.getCity())
                .distance(formatter.format(hotel.getDistance()))
                .headline(hotel.getHeadline())
                .numberOfRatings(hotel.getNumberOfRatings())
                .rating(formatter.format(hotel.getRating()))
                .build();
    };

    default HotelResponseList hotelListToResponseList(List<Hotel> hotels) {
        return new HotelResponseList(
                hotels.stream().map(this::hotelToResponse).toList()
        );
    }
}
