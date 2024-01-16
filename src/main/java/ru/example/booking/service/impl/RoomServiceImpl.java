package ru.example.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.booking.exception.EntityAlreadyExists;
import ru.example.booking.exception.EntityNotFoundException;
import ru.example.booking.model.Room;
import ru.example.booking.repository.RoomRepository;
import ru.example.booking.service.RoomService;
import ru.example.booking.util.BeanUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(Long id) {
        return roomRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Room not found, ID is " + id)
        );
    }

    @Override
    public Room findByName(String name) {
        return roomRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Room not found, name is " + name)
        );
    }

    @Override
    public Room updateById(Long id, Room room) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }

        Room existedRoom = findById(id);
        BeanUtils.copyNonNullProperties(room, existedRoom);

        return roomRepository.save(existedRoom);
    }

    @Override
    public Room save(Room room) {
        if (roomRepository.existsByName(room.getName())) {
            throw new EntityAlreadyExists("Room with name \"" + room.getName() + "\" is already exists");
        }
        return roomRepository.save(room);
    }

    @Override
    public void deleteById(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room not found, ID is " + id);
        }
        roomRepository.deleteById(id);
    }
}
