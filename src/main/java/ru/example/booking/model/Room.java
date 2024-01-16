package ru.example.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "rooms", uniqueConstraints = @UniqueConstraint(name = "room_name", columnNames = "name"))
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldNameConstants
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    private RoomDescription description;

    private Integer number;

    private BigDecimal price;

    private Integer capacity;

    @Builder.Default
    private Set<Date> bookedDates = new HashSet<>();

}
