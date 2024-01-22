package ru.example.booking.dto.defaults;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.dao.RoomDescription;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomFilter {

    private Long id;

    private RoomDescription description;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer capacity;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Long hotelId;
}
