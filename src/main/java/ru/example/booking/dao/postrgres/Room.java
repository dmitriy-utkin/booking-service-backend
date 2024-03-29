package ru.example.booking.dao.postrgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @ToString.Exclude
    private Set<LocalDate> bookedDates = new HashSet<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    @ToString.Exclude
    private List<Reservation> reservations = new ArrayList<>();

}
