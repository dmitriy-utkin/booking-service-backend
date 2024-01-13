package ru.example.booking.web.hotel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHotelRequest {

    private String name;

    private String headline;

    private String city;

    private String address;

    private Integer distance;

    @JsonIgnore
    @Builder.Default
    private Integer rating = 0;

    @JsonIgnore
    @Builder.Default
    private Integer numberOfRatings = 0;

}
