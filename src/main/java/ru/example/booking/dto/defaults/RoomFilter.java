package ru.example.booking.dto.defaults;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String checkInDate;

    private String checkOutDate;

    @JsonIgnore
    private LocalDate checkInLocalDate;

    @JsonIgnore
    private LocalDate checkOutLocalDate;

    private Long hotelId;
}
