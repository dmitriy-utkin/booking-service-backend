package ru.example.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.SimpleRoomResponse;
import ru.example.booking.dto.room.UpsertRoomRequest;
import ru.example.booking.service.RoomService;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public RoomResponseList findAll() {
        return roomService.findAll();
    }

    @GetMapping("/filter")
    public RoomResponseList findAllWithFilter(@RequestBody FindAllSettings settings) {
        return roomService.findAll(settings);
    }

    @GetMapping("/{id}")
    public SimpleRoomResponse findById(@PathVariable("id") Long id) {
        return roomService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody UpsertRoomRequest request) {
        roomService.save(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SimpleRoomResponse update(@PathVariable("id") Long id, @RequestBody UpsertRoomRequest request) {
        return roomService.updateById(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        roomService.deleteById(id);
    }
}
