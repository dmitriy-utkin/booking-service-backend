package ru.example.booking.web.model.hotel;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHotelRequest {

    @Size(min = 5, max = 60, message = "Hotel name length should be between 5 and 60 characters")
    private String name;

    @Size(min = 15, max = 160, message = "Hotel headline length should be between 15 and 160 characters")
    private String headline;

    @Size(min = 2, max = 60, message = "City of hotel location length should be between 2 and 60 characters")
    private String city;

    @Size(min = 5, max = 160, message = "The hotel address length should be between 5 and 160 characters")
    private String address;

    private Float distance;

}
