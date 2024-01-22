package ru.example.booking.dto.defaults;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindAllSettings {

    @Builder.Default
    private int pageSize = 10;

    @Builder.Default
    private int pageNum = 0;

    @Builder.Default
    private HotelFilter hotelFilter = new HotelFilter();

    @Builder.Default
    private RoomFilter roomFilter = new RoomFilter();

}
