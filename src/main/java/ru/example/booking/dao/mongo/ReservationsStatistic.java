package ru.example.booking.dao.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
@Document(collection = "reservation_stat")
public class ReservationsStatistic {

    @Id
    private String id;

    private Instant reservationDate;

    private Long userId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Long roomId;
}
