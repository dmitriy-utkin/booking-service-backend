package ru.example.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "hotels")
@Data
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

    private Integer distance;

    private Integer rating;

    @Column(name = "number_of_ratings")
    private Integer numberOfRatings;

}
