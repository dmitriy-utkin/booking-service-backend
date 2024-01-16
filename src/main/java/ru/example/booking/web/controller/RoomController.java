package ru.example.booking.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.RoomMapper;
import ru.example.booking.service.RoomService;
import ru.example.booking.web.model.room.RoomResponse;
import ru.example.booking.web.model.room.RoomResponseList;
import ru.example.booking.web.model.room.UpsertRoomRequest;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    private final RoomMapper roomMapper;

    @GetMapping
    public ResponseEntity<RoomResponseList> findAll() {
        return ResponseEntity.ok(roomMapper.roomListToResponseList(roomService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomMapper.roomToResponse(roomService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> save(@Valid @RequestBody UpsertRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                roomMapper.roomToResponse(
                        roomService.save(roomMapper.requestToRoom(request))
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(@PathVariable("id") Long id, @RequestBody UpsertRoomRequest request) {
        return ResponseEntity.ok(
                roomMapper.roomToResponse(
                        roomService.updateById(id, roomMapper.requestToRoom(request))
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        roomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
