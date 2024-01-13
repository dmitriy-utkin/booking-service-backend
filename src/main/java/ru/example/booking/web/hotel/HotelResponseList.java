package ru.example.booking.web.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponseList {

    private List<HotelResponse> hotels = new ArrayList<>();

}
