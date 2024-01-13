package ru.example.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.model.Hotel;
import ru.example.booking.web.hotel.CreateHotelRequest;
import ru.example.booking.web.hotel.HotelResponse;
import ru.example.booking.web.hotel.HotelResponseList;
import ru.example.booking.web.hotel.UpdateHotelRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelMapper {

    Hotel createRequestToHotel(CreateHotelRequest request);

    Hotel updateRequestToHotel(UpdateHotelRequest request);

    HotelResponse hotelToResponse(Hotel hotel);

    default HotelResponseList hotelListToResponseList(List<Hotel> hotels) {
        List<HotelResponse> hotelResponses = hotels.stream().map(this::hotelToResponse).toList();
        return new HotelResponseList(hotelResponses);
    }
}
