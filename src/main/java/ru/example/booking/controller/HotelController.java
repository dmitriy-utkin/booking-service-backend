package ru.example.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.example.booking.dto.defaults.FindAllSettings;
import ru.example.booking.dto.hotel.CreateHotelRequest;
import ru.example.booking.dto.hotel.HotelResponse;
import ru.example.booking.dto.hotel.HotelResponseList;
import ru.example.booking.dto.hotel.UpdateHotelRequest;
import ru.example.booking.service.HotelService;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public HotelResponseList findAll() {
        return hotelService.findAll();
    }

    @GetMapping("/filter")
    public HotelResponseList findAllWithFilter(@RequestBody FindAllSettings settings) {
        return hotelService.findAll(settings);
    }

    @GetMapping("/{id}")
    public HotelResponse findById(@PathVariable("id") Long id) {
        return hotelService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponse> save(@Valid @RequestBody CreateHotelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                hotelService.save(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponse update(@PathVariable("id") Long id,
                                @Valid @RequestBody UpdateHotelRequest request) {
        return hotelService.updateById(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        hotelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rate/{id}")
    public HotelResponse updateRating(@PathVariable("id") Long id,
                                      @RequestParam @Min(1) @Max(5) Integer newRating) {
        return hotelService.updateRating(id, newRating);
    }
}
