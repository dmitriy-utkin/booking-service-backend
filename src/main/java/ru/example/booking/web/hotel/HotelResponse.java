package ru.example.booking.web.hotel;

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

    private Integer distance;

    private Integer rating;

    private Integer numberOfRatings;

}
