package ru.example.booking.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.mapper.HotelMapper;
import ru.example.booking.service.HotelService;
import ru.example.booking.web.model.hotel.*;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponse> save(@Valid @RequestBody CreateHotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                hotelMapper.hotelToResponse(
                        hotelService.save(hotelMapper.createRequestToHotel(request))
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponse> update(@PathVariable("id") Long id,
                                                @Valid @RequestBody UpdateHotelRequest request) {
        return ResponseEntity.ok(
                hotelMapper.hotelToResponse(
                        hotelService.updateById(id, hotelMapper.updateRequestToHotel(request))
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        hotelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rate/{id}")
    public ResponseEntity<HotelResponse> updateRating(@PathVariable("id") Long id,
                                                      @RequestParam @Min(1) @Max(5) Integer newRating) {
        return ResponseEntity.ok(hotelMapper.hotelToResponse(hotelService.updateRating(id, newRating)));
    }
}
