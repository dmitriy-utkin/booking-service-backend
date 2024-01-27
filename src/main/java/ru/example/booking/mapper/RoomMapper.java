package ru.example.booking.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.example.booking.dao.postrgres.Room;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.SimpleRoomResponse;
import ru.example.booking.dto.room.UpsertRoomRequest;

import java.util.List;

@DecoratedWith(RoomMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {

    Room requestToRoom(UpsertRoomRequest request);

    SimpleRoomResponse roomToSimpleResponse(Room room);


    default RoomResponseList roomListToResponseList(List<Room> rooms) {
        return new RoomResponseList(
                rooms.stream().map(this::roomToSimpleResponse).toList()
        );
    }
}
