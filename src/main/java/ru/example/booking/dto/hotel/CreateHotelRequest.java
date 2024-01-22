package ru.example.booking.dto.hotel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHotelRequest {

    @NotBlank(message = "Hotel name is required")
    @Size(min = 5, max = 60, message = "Hotel name length should be between 5 and 60 characters")
    private String name;

    @NotBlank(message = "Hotel headline is required")
    @Size(min = 15, max = 160, message = "Hotel headline length should be between 15 and 160 characters")
    private String headline;

    @NotBlank(message = "City of hotel location is required")
    @Size(min = 2, max = 60, message = "City of hotel location length should be between 2 and 60 characters")
    private String city;

    @NotBlank(message = "The hotel address is required")
    @Size(min = 5, max = 160, message = "The hotel address length should be between 5 and 160 characters")
    private String address;

    @NotNull(message = "The distance of hotel to city center is required")
    private Float distance;

    @JsonIgnore
    @Builder.Default
    private Float rating = 0F;

    @JsonIgnore
    @Builder.Default
    private Integer numberOfRatings = 0;

}
