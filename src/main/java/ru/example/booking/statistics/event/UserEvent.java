package ru.example.booking.statistics.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEvent {

    private String id;

    private Instant registrationAt;

    private Long userId;
}
