package ru.example.booking.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.HotelMapper;
import ru.example.booking.service.HotelService;
import ru.example.booking.web.model.hotel.CreateHotelRequest;
import ru.example.booking.web.model.hotel.HotelResponse;
import ru.example.booking.web.model.hotel.HotelResponseList;
import ru.example.booking.web.model.hotel.UpdateHotelRequest;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    private final HotelMapper hotelMapper;

    @GetMapping
    public ResponseEntity<HotelResponseList> findAll() {
        return ResponseEntity.ok(hotelMapper.hotelListToResponseList(hotelService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(hotelMapper.hotelToResponse(hotelService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<HotelResponse> save(@Valid @RequestBody CreateHotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                hotelMapper.hotelToResponse(
                        hotelService.save(hotelMapper.createRequestToHotel(request))
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> update(@PathVariable("id") Long id,
                                                @Valid @RequestBody UpdateHotelRequest request) {
        return ResponseEntity.ok(
                hotelMapper.hotelToResponse(
                        hotelService.update(id, hotelMapper.updateRequestToHotel(request))
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        hotelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
