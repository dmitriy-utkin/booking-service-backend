package ru.example.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

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

}
