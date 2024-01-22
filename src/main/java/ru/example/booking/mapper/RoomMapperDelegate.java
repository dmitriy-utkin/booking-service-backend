package ru.example.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.example.booking.dao.Room;
import ru.example.booking.dto.room.UpsertRoomRequest;
import ru.example.booking.service.HotelService;

public abstract class RoomMapperDelegate implements RoomMapper {

    @Autowired
    private HotelService hotelService;

    @Override
    public Room requestToRoom(UpsertRoomRequest request) {
        return Room.builder()
                .name(request.getName())
                .hotel(request.getHotelId() == null ? null : hotelService.findHotelById(request.getHotelId()))
                .price(request.getPrice())
                .capacity(request.getCapacity())
                .number(request.getNumber())
                .description(request.getDescription())
                .build();
    }
}
