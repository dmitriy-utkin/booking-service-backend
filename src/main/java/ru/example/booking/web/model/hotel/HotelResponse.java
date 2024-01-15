package ru.example.booking.web.model.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {

    private String name;

    private String headline;

    private String city;

    private String address;

    private Float distance;

    private Float rating;

    private Integer numberOfRatings;

}
