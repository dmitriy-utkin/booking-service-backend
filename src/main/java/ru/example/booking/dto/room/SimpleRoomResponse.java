package ru.example.booking.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.dao.RoomDescription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;

    private String name;

    private Long hotelId;

    private RoomDescription description;

    private Integer number;

    private BigDecimal price;

    private Integer capacity;

    @Builder.Default
    private Set<LocalDate> bookedDates = new HashSet<>();
}
