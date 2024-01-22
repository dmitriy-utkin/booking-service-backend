package ru.example.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.service.RoomService;
import ru.example.booking.dto.room.RoomResponse;
import ru.example.booking.dto.room.RoomResponseList;
import ru.example.booking.dto.room.UpsertRoomRequest;

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
    public RoomResponse findById(@PathVariable("id") Long id) {
        return roomService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> save(@Valid @RequestBody UpsertRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.save(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse update(@PathVariable("id") Long id, @RequestBody UpsertRoomRequest request) {
        return roomService.updateById(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        roomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
