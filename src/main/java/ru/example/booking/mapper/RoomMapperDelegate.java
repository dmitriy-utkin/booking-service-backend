package ru.example.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.example.booking.model.Room;
import ru.example.booking.service.HotelService;
import ru.example.booking.web.model.room.UpsertRoomRequest;

public abstract class RoomMapperDelegate implements RoomMapper {

    @Autowired
    private HotelService hotelService;

    @Override
    public Room requestToRoom(UpsertRoomRequest request) {
        return Room.builder()
                .name(request.getName())
                .hotel(request.getHotelId() == null ? null : hotelService.findById(request.getHotelId()))
                .price(request.getPrice())
                .capacity(request.getCapacity())
                .number(request.getNumber())
                .description(request.getDescription())
                .build();
    }
}
