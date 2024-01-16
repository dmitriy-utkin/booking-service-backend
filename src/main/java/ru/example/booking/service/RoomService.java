package ru.example.booking.service;

import ru.example.booking.model.Room;

import java.util.List;

public interface RoomService {

    List<Room> findAll();

    Room findById(Long id);

    Room findByName(String name);

    Room updateById(Long id, Room room);

    Room save(Room room);

    void deleteById(Long id);

}
