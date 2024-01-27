package ru.example.booking.dto.room;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.example.booking.dao.postrgres.RoomDescription;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertRoomRequest {

    @Size(min = 2, max = 100, message = "Room name length should be between 2 and 100 characters")
    private String name;

    private Long hotelId;

    private RoomDescription description;

    @Positive(message = "Room number can not be negative value")
    private Integer number;

    @Positive(message = "Room price can not be negative value")
    private BigDecimal price;

    @Min(value = 1, message = "Minimal room capacity is 1")
    @Max(value = 15, message = "Maximal room capacity is 15")
    private Integer capacity;
}
