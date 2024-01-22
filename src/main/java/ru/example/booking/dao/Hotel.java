package ru.example.booking.dao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "hotels", uniqueConstraints = {@UniqueConstraint(name = "hotel_name", columnNames = "name")})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldNameConstants
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String headline;

    private String city;

    private String address;

    private Float distance;

    private Float rating;

    @Column(name = "number_of_ratings")
    private Integer numberOfRatings;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

}
